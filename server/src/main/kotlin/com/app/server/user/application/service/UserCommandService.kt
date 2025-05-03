package com.app.server.user.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userService: UserService
) {

    fun verifyUserCanUpdateNowMaxConsecutiveParticipantsDayCount(
        userId: Long,
        updateMaxConsecutiveParticipationDayCount: Long
    ){

        val user = userService.findById(userId)

        val userNowMaxConsecutiveParticipationDayCount: Long = user.nowMaxConsecutiveParticipationDayCount

        if (userNowMaxConsecutiveParticipationDayCount < updateMaxConsecutiveParticipationDayCount) {
            user.updateNowMaxConsecutiveParticipationDayCount(
                maxConsecutiveParticipationDayCount = updateMaxConsecutiveParticipationDayCount
            )
        }
    }
}