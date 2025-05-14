package com.app.server.user_challenge.application.service.command

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.application.dto.CertificationDataDto
import com.app.server.challenge_certification.ui.dto.request.UserChallengeIceRequestDto
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.infra.api.report.dto.request.SendToReportServerRequestDto
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto
import com.app.server.user_challenge.application.dto.response.UserChallengeResponseDto
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.event.ReportCreatedEvent
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserChallengeParticipantState
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.port.outbound.ReportPort
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class UserChallengeCommandServiceImpl(
    private val userChallengeService: UserChallengeService,
    private val challengeService: ChallengeService,
    private val userChallengeHistoryCommandService: UserChallengeHistoryCommandService,
    private val eventPublisher: ApplicationEventPublisher,
    private val reportPort: ReportPort
) : UserChallengeCommandService, UsingIceUseCase {

    override fun execute(
        challengeParticipantDto: ChallengeParticipantDto,
    ): UserChallengeResponseDto {
        val userCanParticipantInChallenge: EUserChallengeParticipantState? =
            validateUserCanParticipateInChallenge(challengeParticipantDto)

        if (userCanParticipantInChallenge == null ||
            userCanParticipantInChallenge == EUserChallengeParticipantState.NEW_CHALLENGE_START
        ) {
            val userChallenge = createUserChallenge(challengeParticipantDto)

            saveUserChallengeWithHistory(
                userChallenge,
                challengeParticipantDto.participantsTotalDays,
                challengeParticipantDto.participantsStartDate
            )

            return UserChallengeResponseDto(
                userChallengeId = userChallenge.id!!,
                challengeTitle = userChallenge.challenge.title,
                userId = userChallenge.userId,
                participantsDate = userChallenge.participantDays,
                status = userChallenge.status,
                maxConsecutiveDays = userChallenge.maxConsecutiveParticipationDayCount,
                nowConsecutiveDays = userChallenge.nowConsecutiveParticipationDayCount,
                totalConsecutiveDays = userChallenge.totalParticipationDayCount,
                iceCount = userChallenge.iceCount,
                challengeStartDate = userChallenge.getUserChallengeHistories().first().date.toString(),
                progressFromTotal = userChallenge.calculateProgressFromTotalParticipationDays(
                    challengeParticipantDto.participantsStartDate
                ),
                progressFromElapsed = userChallenge.calculateProgressFromElapsedDays(
                    challengeParticipantDto.participantsStartDate
                ),
                reportMessage = userChallenge.reportMessage,
                challengeHistories = userChallenge.getUserChallengeHistories().map {
                    UserChallengeResponseDto.ChallengeHistoryResponseDto(
                        date = it.date,
                        isCertificated = it.status,
                        imageUrl = it.certificatedImageUrl
                    )
                }
            )
        }
        // 이미 참여 중인 챌린지인 경우
        // if (userCanParticipantInChallenge == EUserChallengeParticipantState.EXISTING_CHALLENGE_CONTINUE)
        val userChallenge = userChallengeService.findByUserIdAndChallengeId(
            userId = challengeParticipantDto.userId,
            challengeId = challengeParticipantDto.challengeId
        )

        userChallenge!!.plusParticipatedDays(
            participantsDate = challengeParticipantDto.participantsTotalDays,
        )

        saveUserChallengeWithHistory(
            userChallenge,
            challengeParticipantDto.participantsTotalDays,
            participantsStartDay = challengeParticipantDto.participantsStartDate
        )

        userChallenge.increaseIceCountWhenUserChallengeContinues()

        userChallenge.updateReportMessage(null)

        userChallenge.updateStatus(EUserChallengeStatus.RUNNING)

        return UserChallengeResponseDto(
            userChallengeId = userChallenge.id!!,
            challengeTitle = userChallenge.challenge.title,
            userId = userChallenge.userId,
            participantsDate = userChallenge.participantDays,
            status = userChallenge.status,
            maxConsecutiveDays = userChallenge.maxConsecutiveParticipationDayCount,
            nowConsecutiveDays = userChallenge.nowConsecutiveParticipationDayCount,
            totalConsecutiveDays = userChallenge.totalParticipationDayCount,
            iceCount = userChallenge.iceCount,
            challengeStartDate = userChallenge.getUserChallengeHistories().first().date.toString(),
            progressFromTotal = userChallenge.calculateProgressFromTotalParticipationDays(
                challengeParticipantDto.participantsStartDate
            ),
            progressFromElapsed = userChallenge.calculateProgressFromElapsedDays(
                challengeParticipantDto.participantsStartDate
            ),
            reportMessage = userChallenge.reportMessage,
            challengeHistories = userChallenge.getUserChallengeHistories().map {
                UserChallengeResponseDto.ChallengeHistoryResponseDto(
                    date = it.date,
                    isCertificated = it.status,
                    imageUrl = it.certificatedImageUrl
                )
            }
        )
    }

    fun processAfterCertificateSuccess(
        userChallengeId: Long, certificationDto: CertificationDataDto
    ): SavedTodayUserChallengeCertificationEvent {
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

        userChallengeService.saveAndFlush(userChallenge)

        val event = SavedTodayUserChallengeCertificationEvent(
            userChallengeId = userChallenge.id!!,
            totalParticipationDayCount = userChallenge.totalParticipationDayCount,
            maxConsecutiveParticipationDayCount = userChallenge.maxConsecutiveParticipationDayCount
        )

        eventPublisher.publishEvent(event)

        // 챌린지 종료 여부 확인
        if (userChallenge.checkIsNotRunning(certificationDto.certificationDate)) {
            makeReport(userChallenge)
        }

        return event
    }

    private fun makeReport(userChallenge: UserChallenge) {
        val sendToReportServerRequestDto = SendToReportServerRequestDto.from(
            challengeTitle = userChallenge.challenge.title,
            progress = (userChallenge.totalParticipationDayCount * 100)
                .floorDiv(userChallenge.participantDays)
                .toInt(),
            totalDay = userChallenge.participantDays
        )

        val report: GetReportResponseDto = reportPort.getReportMessage(sendToReportServerRequestDto)

        saveReportMessageInUserChallenge(report, userChallenge)

        eventPublisher.publishEvent(
            ReportCreatedEvent(
                userChallengeId = userChallenge.id!!,
                content = report.message,
            )
        )
    }

    private fun saveReportMessageInUserChallenge(
        report: GetReportResponseDto,
        userChallenge: UserChallenge
    ) {
        when (report.status) {
            EUserReportResultCode.RECEIVE_REPORT_SUCCESS -> {
                userChallenge.updateReportMessage(report.message)
                userChallenge.updateStatus(EUserChallengeStatus.PENDING)
            }

            EUserReportResultCode.RECEIVE_REPORT_FAILED -> {
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

    override fun processAfterCertificateIce(
        iceDto: UserChallengeIceRequestDto,
        certificationDate: LocalDate
    ): UserChallenge {
        val userChallenge = userChallengeService.findById(userChallengeId = iceDto.userChallengeId)

        // 얼리기 가능 여부 판단
        userChallenge.useIce()
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
        val pastUserChallengeHistory: UserChallengeHistory? =
            userChallenge.getUserChallengeHistoryWhen(certificationDate.minusDays(1))

        validateUserChallenge(
            userChallenge = userChallenge,
            pastUserChallengeHistory = pastUserChallengeHistory,
        )
    }

    private fun validateUserChallenge(
        userChallenge: UserChallenge,
        pastUserChallengeHistory: UserChallengeHistory?,
    ) {

        val nowCount = userChallenge.nowConsecutiveParticipationDayCount

        // 이전 기록이 없는 경우 (첫 날인 경우)
        if (pastUserChallengeHistory == null || userChallenge.maxConsecutiveParticipationDayCount == 0L) {
            userChallenge.updateNowConsecutiveParticipationDayCount(nowCount + 1)
            userChallenge.updateMaxConsecutiveParticipationDayCount(
                maxOf(nowCount + 1, userChallenge.maxConsecutiveParticipationDayCount)
            )
            return
        }

        // 이전 기록이 실패한 경우 (연속 깨짐, 다시 시작)
        if (pastUserChallengeHistory.status == EUserChallengeCertificationStatus.FAILED) {
            userChallenge.updateNowConsecutiveParticipationDayCount(1)
            return
        }

        // 이전 기록도 성공이고 현재도 성공인 경우 (연속 유지)
        val newConsecutiveCount = userChallenge.nowConsecutiveParticipationDayCount + 1
        userChallenge.updateNowConsecutiveParticipationDayCount(newConsecutiveCount)

        // 현재 연속 일수가 최대 연속 일수보다 크면 최대값 업데이트
        if (newConsecutiveCount > userChallenge.maxConsecutiveParticipationDayCount) {
            userChallenge.updateMaxConsecutiveParticipationDayCount(newConsecutiveCount)
        }
    }

    private fun validateUserCanParticipateInChallenge(challengeParticipantDto: ChallengeParticipantDto)
            : EUserChallengeParticipantState? {
        val existingUserChallenge: UserChallenge? = userChallengeService.findByUserIdAndChallengeId(
            userId = challengeParticipantDto.userId,
            challengeId = challengeParticipantDto.challengeId
        )

        return existingUserChallenge?.validateCanParticipants()
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
                status = challengeParticipantDto.status
            )
        )
    }

    private fun saveUserChallengeWithHistory(
        userChallenge: UserChallenge,
        participantsDate: Int,
        participantsStartDay: LocalDate
    ) {
        // UserChallengeHistory를 생성하고 UserChallenge에 연결
        userChallengeHistoryCommandService.createUserChallengeHistory(
            userChallenge = userChallenge,
            participantsDate = participantsDate,
            startDay = participantsStartDay
        )

        // UserChallenge와 연결된 모든 히스토리가 함께 저장됨
        userChallengeService.save(userChallenge)
    }

    fun batchUpdateChallengeStatusFromRunningToPending(validateToday: LocalDate) {
        val runningUserChallengeList: List<UserChallenge> =
            userChallengeService.findAllByStatus(EUserChallengeStatus.RUNNING)

        runningUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsNotRunning(validateToday)) {
                makeReport(userChallenge)
            }
        }
    }

    fun batchUpdateChallengeStatusFromPendingToCompleted(validateToday: LocalDate) {
        val pendingUserChallengeList: List<UserChallenge> =
            userChallengeService.findAllByStatus(EUserChallengeStatus.PENDING)
        pendingUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsCompleted(validateToday)) {
                userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
            }
        }
    }

    fun batchUpdateChallengeStatusFromWaitingToCompleted(validateToday: LocalDate) {
        val waitingUserChallengeList: List<UserChallenge> =
            userChallengeService.findAllByStatus(EUserChallengeStatus.WAITING)
        waitingUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsCompleted(validateToday)) {
                userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
            }
        }
    }

    override fun updateUserChallengeStatus(
        userChallenge: UserChallenge,
        todayDate: LocalDate
    ) {
        val endDate: LocalDate = userChallenge.getUserChallengeHistories().last().date

        if (userChallenge.status == EUserChallengeStatus.PENDING &&
            todayDate.isBefore(endDate.plusDays(2))
        ) {
            userChallenge.updateStatus(EUserChallengeStatus.WAITING)
        } else if (userChallenge.status == EUserChallengeStatus.PENDING &&
            todayDate.isAfter(endDate.plusDays(1))
        ) {
            userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
        }
    }
}