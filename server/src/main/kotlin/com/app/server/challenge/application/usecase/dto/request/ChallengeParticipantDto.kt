package com.app.server.challenge.application.usecase.dto.request

import com.app.server.user_challenge.domain.enums.EUserChallengeStatus

data class ChallengeParticipantDto(
    val userId: Long,
    val challengeId: Long,
    val participantsDate: Int,
    val status: EUserChallengeStatus
)
