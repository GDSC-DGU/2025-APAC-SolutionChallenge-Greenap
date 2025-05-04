package com.app.server.user.application.service

import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserEventListener (
    private val userCommandService : UserCommandService
) {

    @EventListener
    fun handleUserCreatedEvent(event: SavedTodayUserChallengeCertificationEvent) {
        userCommandService.verifyUserCanUpdateNowMaxConsecutiveParticipantsDayCount(
            event.userChallengeId,
            event.maxConsecutiveParticipationDayCount
        )
    }

}