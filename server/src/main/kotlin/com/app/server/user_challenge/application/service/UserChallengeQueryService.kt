package com.app.server.user_challenge.application.service

import org.springframework.stereotype.Service

@Service
class UserChallengeQueryService(
    private val userChallengeService: UserChallengeService
) {
    fun getChallengeCompletedUserPercent(challengeId: Long): Double {

        val countCompletedUser : Long = userChallengeService.countCompletedUserBy(challengeId)

        val countAllUser : Long = userChallengeService.countAllParticipantUserBy(challengeId)

        return calculatePercentOfChallengeParticipants(countAllUser, countCompletedUser)
    }

    private fun calculatePercentOfChallengeParticipants(countAllUser: Long, countCompletedUser: Long): Double {
        if (countAllUser == 0L) return 0.0

        return String.format("%.2f", countCompletedUser.toDouble() / countAllUser.toDouble()).toDouble()
    }
}