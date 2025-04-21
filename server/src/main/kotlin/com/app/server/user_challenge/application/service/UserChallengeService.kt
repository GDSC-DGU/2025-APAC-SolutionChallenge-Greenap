package com.app.server.user_challenge.application.service

import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import org.springframework.stereotype.Service

@Service
class UserChallengeService (
    private val userChallengeRepository: UserChallengeRepository
) {

    fun countCompletedUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeIdAndStatusIsCompleted(
        challengeId, EUserChallengeStatus.COMPLETED
    )

    fun countAllParticipantUserBy(challengeId: Long): Long =
        userChallengeRepository.countByChallengeId(
        challengeId
    )
}