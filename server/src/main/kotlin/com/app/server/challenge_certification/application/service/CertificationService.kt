package com.app.server.challenge_certification.application.service

import com.app.server.challenge_certification.application.dto.CertificationFacadeToServiceDto
import com.app.server.challenge_certification.application.service.constant.EConsecutiveState
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CertificationService(
    private val userChallengeService: UserChallengeService
) {

    fun processAfterCertificateSuccess(
       userChallenge: UserChallenge, certificationDto: CertificationFacadeToServiceDto
    ): UserChallenge {
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

    fun processAfterUserChallengeIce(
        userChallengeIceRequestDto: UserChallengeIceRequestDto, certificationDate: LocalDate
    ): UserChallenge {
        val userChallenge = userChallengeService.findById(userChallengeId = userChallengeIceRequestDto.userChallengeId)

        // 얼리기 가능 여부 판단
        userChallenge.validateCanIceAndUse()
        // 날짜 인증 상태 Ice로 변경
        userChallenge.updateCertificationStateIsIce(certificationDate)
        // 연속 참여 일수 증가
        readyForValidateCanIncreaseConsecutiveParticipantDays(userChallenge, certificationDate)

        return userChallenge
    }
}