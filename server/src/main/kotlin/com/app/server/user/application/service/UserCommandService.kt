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

        val userNowMaxConsecutiveParticipationDayCount: Int = userService.getNowMaxConsecutiveParticipationDayCount(userId)

        if (userNowMaxConsecutiveParticipationDayCount < updateMaxConsecutiveParticipationDayCount) {
            userService.updateNowMaxConsecutiveParticipationDayCount(
                userId = userId,
                maxConsecutiveParticipationDayCount = updateMaxConsecutiveParticipationDayCount
            )
        }
    }
}