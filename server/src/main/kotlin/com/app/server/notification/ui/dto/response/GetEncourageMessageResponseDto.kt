package com.app.server.notification.ui.dto.response

data class GetEncourageMessageResponseDto(
    val message: String,
    val challengeTitle: String,
    val progress: Int,
    val userChallengeId: Long?
)