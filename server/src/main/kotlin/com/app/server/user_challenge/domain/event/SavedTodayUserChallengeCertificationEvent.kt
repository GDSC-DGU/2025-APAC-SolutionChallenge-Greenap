package com.app.server.user_challenge.domain.event

data class SavedTodayUserChallengeCertificationEvent(
    val userChallengeId: Long,
    val totalParticipationDayCount: Long,
    val maxConsecutiveParticipationDayCount: Long
)
