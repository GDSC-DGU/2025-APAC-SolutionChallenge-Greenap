package com.app.server.user_challenge.domain.event

data class SavedTodayUserChallengeCertificationEvent(
    val userId: Long,
    val maxConsecutiveParticipationDayCount: Long
)
