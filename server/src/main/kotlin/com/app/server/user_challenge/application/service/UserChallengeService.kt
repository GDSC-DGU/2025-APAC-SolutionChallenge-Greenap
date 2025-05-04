package com.app.server.user_challenge.application.service

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.NotFoundException
import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserChallengeService (
    private val userChallengeRepository: UserChallengeRepository
) {

    fun save(userChallenge: UserChallenge) : UserChallenge {
        return userChallengeRepository.save(userChallenge)
    }

    fun saveAndFlush(userChallenge: UserChallenge) : UserChallenge {
        return userChallengeRepository.saveAndFlush(userChallenge)
    }

    fun saveAll(userChallenges: List<UserChallenge>) : List<UserChallenge> {
        return userChallengeRepository.saveAll(userChallenges)
    }

    fun findById(userChallengeId: Long) : UserChallenge =
        userChallengeRepository.findById(userChallengeId)
            .orElseThrow { NotFoundException(CommonResultCode.NOT_FOUND, "해당 챌린지를 참여하고 있지 않습니다.")
            }
    fun findAllByUserId(userId: Long): List<UserChallenge> =
        userChallengeRepository.findAllByUserId(userId)

    fun countCompletedUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeIdAndStatusIsCompleted(
        challengeId, EUserChallengeStatus.COMPLETED
    )

    fun countAllParticipantUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeId(
        challengeId
    )

    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): UserChallenge? =
        userChallengeRepository.findByUserIdAndChallengeId(
            userId = userId,
            challengeId = challengeId
        )

    fun deleteAll() {
        userChallengeRepository.deleteAll()
    }

    fun isCertificatedWhen(userChallengeId : Long, todayDate : LocalDate) : EUserChallengeCertificationStatus {
        return findById(userChallengeId).isCertificatedToday(todayDate)
    }
    fun getUserChallengeImageUrl(userChallengeId : Long, imageDate : LocalDate): String {
        return findById(userChallengeId).getUserChallengeImageUrl(imageDate)
    }

    fun findAllByStatus(status: EUserChallengeStatus): List<UserChallenge> {
        return userChallengeRepository.findAllByStatus(status)
            .orElseThrow{
                NotFoundException(UserChallengeException.NOT_FOUND_THIS_STATUS)
            }
    }
}