package com.app.server.challenge.application.usecase.dto.request

data class ChallengeParticipantDto(
    val userId: Long,
    val challengeId: Long,
    val participantsDate: Long,
)
