package com.app.server.user_challenge.domain.event

data class ReportCreatedEvent (
    val userChallengeId: Long,
    val content: String
)