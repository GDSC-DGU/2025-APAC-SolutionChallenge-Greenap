package com.app.server.user.application.service

import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class UserEventListener (
    private val userCommandService : UserCommandService
) {

    @Async
    @EventListener
    fun handleUserCreatedEvent(event: SavedTodayUserChallengeCertificationEvent) {
        userCommandService.verifyUserCanUpdateNowMaxConsecutiveParticipantsDayCount(
            event.userId,
            event.maxConsecutiveParticipationDayCount
        )
    }

}