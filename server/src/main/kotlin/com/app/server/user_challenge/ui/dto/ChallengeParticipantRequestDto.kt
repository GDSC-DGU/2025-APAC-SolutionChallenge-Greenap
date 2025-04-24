package com.app.server.user_challenge.ui.dto

import com.app.server.challenge.application.usecase.dto.request.ChallengeParticipantDto
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus

data class ChallengeParticipantRequestDto(
    val challengeId: Long,
    val participantsDate: Int,
)
{
    fun toChallengeParticipantDto(userId: Long) = ChallengeParticipantDto(
        userId = userId,
        challengeId = challengeId,
        participantsDate = participantsDate,
        status = EUserChallengeStatus.RUNNING
    )
}