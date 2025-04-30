package com.app.server.user_challenge.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.application.dto.CertificationDataDto
import com.app.server.challenge_certification.application.service.constant.EConsecutiveState
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.ReceiveReportResponseDto
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.event.ReportCreatedEvent
import com.app.server.user_challenge.infra.ReportInfraService
import com.app.server.user_challenge.ui.dto.SendToReportServerRequestDto
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class UserChallengeCommandService (
    private val userChallengeService: UserChallengeService,
    private val challengeService: ChallengeService,
    private val userChallengeHistoryCommandService: UserChallengeHistoryCommandService,
    private val reportInfraService: ReportInfraService,
    private val eventPublisher : ApplicationEventPublisher
) : ParticipantChallengeUseCase, UsingIceUseCase {

    override fun execute(
        challengeParticipantDto: ChallengeParticipantDto,
    ) : UserChallenge {
        validateUserCanParticipateInChallenge(challengeParticipantDto)
        val userChallenge = createUserChallenge(challengeParticipantDto)
        saveUserChallengeWithHistory(userChallenge, challengeParticipantDto.participantsTotalDays)
        return userChallenge
    }

    fun processAfterCertificateSuccess(
        userId : Long, userChallengeId : Long, certificationDto: CertificationDataDto
    ): UserChallenge {
        val userChallenge = userChallengeService.findById(userChallengeId)

        // 날짜 인증 상태 Success로 변경
        userChallenge.updateCertificationStateIsSuccess(
            certificationDate = certificationDto.certificationDate,
            certificatedImageUrl = certificationDto.imageUrl
        )

        // 전체 참여 일수 증가
        userChallenge.increaseTotalParticipatedDays(
            certificationDate = certificationDto.certificationDate
        )

        // 연속 참여 일수 증가
        readyForValidateCanIncreaseConsecutiveParticipantDays(userChallenge, certificationDto.certificationDate)

        // 얼리기 조건 확인 후 업데이트
        userChallenge.validateIncreaseIceCount()

        // 챌린지 종료 여부 확인
        if (userChallenge.checkIsDone(certificationDto.certificationDate)){
            makeReport(userId, userChallenge)
        }

        return userChallenge
    }

    private fun makeReport(userId: Long, userChallenge: UserChallenge) {
        val sendToReportServerRequestDto = SendToReportServerRequestDto.from(
            userId = userId,
            challengeTitle = userChallenge.challenge.title,
            progress = userChallenge.getSuccessDayCount(),
            totalDay = userChallenge.participantDays
        )

        val report: ReceiveReportResponseDto =
            reportInfraService.receiveReportMessage(sendToReportServerRequestDto)

        saveReportMessageInUserChallenge(report, userChallenge)

        eventPublisher.publishEvent(
            ReportCreatedEvent(
                userChallengeId = userChallenge.id!!,
                content = report.message,
            )
        )
    }

    private fun saveReportMessageInUserChallenge(
        report: ReceiveReportResponseDto,
        userChallenge: UserChallenge
    ) {
        when (report.status) {
            EUserReportResultCode.RECEIVE_REPORT_SUCCESS -> {
                userChallenge.updateReportMessage(report.message)
                updateUserChallengeStatusToPending(userChallenge)
            }

            EUserReportResultCode.RECEIVE_REPORT_FAILED -> {
                // TODO: 레포트 발급에 실패한 챌린지들은 따로 모아 배치작업으로 다시 재발급 필요
                userChallenge.updateStatus(EUserChallengeStatus.DEAD)
                throw InternalServerErrorException(
                    UserChallengeException.CANNOT_MAKE_REPORT
                )
            }

            EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER -> {
                userChallenge.updateStatus(EUserChallengeStatus.DEAD)
                throw InternalServerErrorException(
                    UserChallengeException.ERROR_IN_REPORT_SERVER
                )
            }
        }
    }

    private fun updateUserChallengeStatusToPending(userChallenge: UserChallenge) {
        userChallenge.updateStatus(EUserChallengeStatus.PENDING)
    }

    override fun processAfterCertificateIce(
        iceDto: UserChallengeIceRequestDto,
        certificationDate: LocalDate
    ): UserChallenge {
        val userChallenge = userChallengeService.findById(userChallengeId = iceDto.userChallengeId)

        // 얼리기 가능 여부 판단
        userChallenge.validateCanIceAndUse()
        // 날짜 인증 상태 Ice로 변경
        userChallenge.updateCertificationStateIsIce(certificationDate)
        // 연속 참여 일수 증가
        readyForValidateCanIncreaseConsecutiveParticipantDays(userChallenge, certificationDate)

        return userChallenge
    }

    private fun readyForValidateCanIncreaseConsecutiveParticipantDays(
        userChallenge: UserChallenge,
        certificationDate: LocalDate
    ) {
        val userChallengeHistory: UserChallengeHistory? = userChallenge.getUserChallengeHistoryWhen(certificationDate)
        val pastUserChallengeHistory: UserChallengeHistory? =
            userChallenge.getUserChallengeHistoryWhen(certificationDate.minusDays(1))

        validateUserChallenge(
            userChallenge = userChallenge,
            pastUserChallengeHistory = pastUserChallengeHistory,
            userChallengeHistory = userChallengeHistory!!
        )
    }

    private fun validateUserChallenge(
        userChallenge: UserChallenge,
        pastUserChallengeHistory: UserChallengeHistory?,
        userChallengeHistory: UserChallengeHistory
    ) {
        // 첫 날인 경우
        val nowCount : Long = userChallenge.nowConsecutiveParticipationDayCount
        var consecutiveState: EConsecutiveState

        if (pastUserChallengeHistory == null && userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED) {
            consecutiveState = EConsecutiveState.FIRST_DAY
        }
        // 연속 일자 증가
        else if (userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED &&
            pastUserChallengeHistory!!.status != EUserChallengeCertificationStatus.FAILED
        ) {

            consecutiveState = EConsecutiveState.CONSECUTIVE_ONLY

        } else if (
            userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED &&
            pastUserChallengeHistory!!.status != EUserChallengeCertificationStatus.FAILED &&
            userChallenge.nowConsecutiveParticipationDayCount > userChallenge.maxConsecutiveParticipationDayCount
        ) {
            consecutiveState = EConsecutiveState.CONSECUTIVE_MAX
        }
        // 연속 일자 초기화
        else if (userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED && pastUserChallengeHistory!!.status == EUserChallengeCertificationStatus.FAILED) {
            consecutiveState = EConsecutiveState.CONSECUTIVE_RESET
        } else {
            throw InternalServerErrorException(UserChallengeException.CANNOT_UPDATE_CONSECUTIVE_PARTICIPATION_DAY_COUNT)
        }

        updateParticipantDaysState(
            userChallenge = userChallenge,
            consecutiveState = consecutiveState,
            nowCount = nowCount
        )
    }

    private fun updateParticipantDaysState(
        userChallenge: UserChallenge,
        consecutiveState: EConsecutiveState,
        nowCount: Long
    ) {
        when (consecutiveState) {
            EConsecutiveState.FIRST_DAY -> {
                userChallenge.updateNowConsecutiveParticipationDayCount(nowCount + 1)
                userChallenge.updateMaxConsecutiveParticipationDayCount(nowCount + 1)
            }

            EConsecutiveState.CONSECUTIVE_ONLY -> {
                userChallenge.updateNowConsecutiveParticipationDayCount(nowCount + 1)
            }

            EConsecutiveState.CONSECUTIVE_MAX -> {
                userChallenge.updateMaxConsecutiveParticipationDayCount(nowCount + 1)
            }

            EConsecutiveState.CONSECUTIVE_RESET -> {
                userChallenge.updateNowConsecutiveParticipationDayCount(0)
            }
        }
    }

    private fun validateUserCanParticipateInChallenge(challengeParticipantDto: ChallengeParticipantDto) {
        val existingUserChallenge = userChallengeService.findByUserIdAndChallengeId(
            userId = challengeParticipantDto.userId,
            challengeId = challengeParticipantDto.challengeId
        )

        existingUserChallenge?.validateCanParticipants()
    }

    private fun createUserChallenge(
        challengeParticipantDto: ChallengeParticipantDto
    ): UserChallenge {
        val challenge = challengeService.findById(challengeParticipantDto.challengeId)
        return UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = challengeParticipantDto.userId,
                challenge = challenge,
                participantsDate = challengeParticipantDto.participantsTotalDays,
                status =challengeParticipantDto.status
            )
        )
    }

    private fun saveUserChallengeWithHistory(userChallenge: UserChallenge, participantsDate: Int) {
        // UserChallengeHistory를 생성하고 UserChallenge에 연결
        userChallengeHistoryCommandService.createUserChallengeHistory(
            userChallenge = userChallenge,
            participantsDate = participantsDate
        )

        // UserChallenge와 연결된 모든 히스토리가 함께 저장됨
        userChallengeService.save(userChallenge)
    }
}