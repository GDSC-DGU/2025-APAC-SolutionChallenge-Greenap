package com.app.server.user_challenge.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.challenge_certification.enums.EConsecutiveState
import com.app.server.challenge_certification.dto.application.business.CertificationDataDto
import com.app.server.challenge_certification.dto.ui.request.UserChallengeIceRequestDto
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto
import com.app.server.user_challenge.application.dto.raw.request.SendToReportServerRequestDto
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.event.ReportCreatedEvent
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserChallengeParticipantState
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.infra.api.report.ReportAdaptor
import com.app.server.user_challenge.port.outbound.ReportPort
import com.app.server.user_challenge.ui.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class UserChallengeCommandService(
    private val userChallengeService: UserChallengeService,
    private val challengeService: ChallengeService,
    private val userChallengeHistoryCommandService: UserChallengeHistoryCommandService,
    private val eventPublisher: ApplicationEventPublisher,
    private val reportPort: ReportPort
) : ParticipantChallengeUseCase, UsingIceUseCase {

    override fun execute(
        challengeParticipantDto: ChallengeParticipantDto,
    ): UserChallenge {
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

            return userChallenge
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

        return userChallenge

    }

    suspend fun processAfterCertificateSuccess(
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

        // 챌린지 종료 여부 확인
        if (userChallenge.checkIsNotRunning(certificationDto.certificationDate)) {
            makeReport(userChallenge)
        }

        val event = SavedTodayUserChallengeCertificationEvent(
            userChallengeId = userChallenge.id!!,
            maxConsecutiveParticipationDayCount = userChallenge.maxConsecutiveParticipationDayCount,
            totalParticipationDayCount = userChallenge.totalParticipationDayCount
        )
        eventPublisher.publishEvent(event)

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
        val nowCount: Long = userChallenge.nowConsecutiveParticipationDayCount
        var consecutiveState: EConsecutiveState

        // 첫 날인 경우
        if (pastUserChallengeHistory == null && userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED) {
            consecutiveState = EConsecutiveState.FIRST_DAY
        }
        // 연속 일자 증가
        else if (
            userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED &&
            pastUserChallengeHistory!!.status != EUserChallengeCertificationStatus.FAILED &&
            userChallenge.nowConsecutiveParticipationDayCount == userChallenge.maxConsecutiveParticipationDayCount
        ) {
            consecutiveState = EConsecutiveState.CONSECUTIVE_MAX
        } else if (
            userChallengeHistory.status != EUserChallengeCertificationStatus.FAILED &&
            pastUserChallengeHistory!!.status != EUserChallengeCertificationStatus.FAILED
        ) {
            consecutiveState = EConsecutiveState.CONSECUTIVE_ONLY
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
                userChallenge.updateNowConsecutiveParticipationDayCount(nowCount + 1)
                userChallenge.updateMaxConsecutiveParticipationDayCount(nowCount + 1)
            }

            EConsecutiveState.CONSECUTIVE_RESET -> {
                userChallenge.updateNowConsecutiveParticipationDayCount(0)
            }
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

    suspend fun batchUpdateChallengeStatusFromRunningToPending(validateToday: LocalDate) {
        val runningUserChallengeList : List<UserChallenge> = userChallengeService.findAllByStatus(EUserChallengeStatus.RUNNING)

        runningUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsNotRunning(validateToday)) {
                makeReport(userChallenge)
            }
        }
    }

    suspend fun batchUpdateChallengeStatusFromPendingToCompleted(validateToday: LocalDate) {
        val pendingUserChallengeList : List<UserChallenge> = userChallengeService.findAllByStatus(EUserChallengeStatus.PENDING)
        pendingUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsCompleted(validateToday)) {
                userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
            }
        }
    }

    suspend fun batchUpdateChallengeStatusFromWaitingToCompleted(validateToday: LocalDate) {
        val waitingUserChallengeList : List<UserChallenge> = userChallengeService.findAllByStatus(EUserChallengeStatus.WAITING)
        waitingUserChallengeList.forEach { userChallenge ->
            if (userChallenge.checkIsCompleted(validateToday)) {
                userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
            }
        }
    }
}