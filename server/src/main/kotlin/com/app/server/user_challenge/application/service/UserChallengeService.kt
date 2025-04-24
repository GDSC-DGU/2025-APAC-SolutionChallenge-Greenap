package com.app.server.user_challenge.application.service

import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import org.springframework.stereotype.Service

@Service
class UserChallengeService (
    private val userChallengeRepository: UserChallengeRepository
) {

    fun save(userChallenge: UserChallenge) {
        userChallengeRepository.save(userChallenge)
    }

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

}