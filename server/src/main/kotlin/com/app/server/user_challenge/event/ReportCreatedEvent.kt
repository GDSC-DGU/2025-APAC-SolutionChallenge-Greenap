package com.app.server.user_challenge.event

data class ReportCreatedEvent (
    val userChallengeId: Long,
    val content: String
)