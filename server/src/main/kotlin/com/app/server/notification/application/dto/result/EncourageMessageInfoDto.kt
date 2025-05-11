package com.app.server.notification.application.dto.result

data class EncourageMessageInfoDto(
    val message: String,
    val challengeTitle: String,
    val progress: Int,
    val userChallengeId: Long?
)
