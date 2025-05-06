package com.app.server.user.application.service

import com.app.server.user_challenge.application.service.UserChallengeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userService: UserService,
    private val userChallengeService: UserChallengeService,
) {

    fun verifyUserCanUpdateNowMaxConsecutiveParticipantsDayCount(
        userChallengeId: Long,
        updateMaxConsecutiveParticipationDayCount: Long
    ){

        val userChallenge = userChallengeService.findById(userChallengeId)
        val user = userService.findById(userChallenge.userId)

        val userNowMaxConsecutiveParticipationDayCount: Long = user.nowMaxConsecutiveParticipationDayCount

        if (userNowMaxConsecutiveParticipationDayCount < updateMaxConsecutiveParticipationDayCount) {
            user.updateNowMaxConsecutiveParticipationDayCount(
                maxConsecutiveParticipationDayCount = updateMaxConsecutiveParticipationDayCount
            )
        }
    }
}