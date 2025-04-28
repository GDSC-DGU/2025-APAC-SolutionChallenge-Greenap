package com.app.server.user_challenge.ui.dto

import com.app.server.challenge.application.usecase.dto.request.ChallengeParticipantDto
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ChallengeParticipantRequestDto(
    @JsonProperty("challenge_id")
    val challengeId: Long,
    @JsonProperty("participants_date")
    val participantsDate: Int,
)
{
    fun toChallengeParticipantDto(userId: Long) = ChallengeParticipantDto(
        userId = userId,
        challengeId = challengeId,
        participantsTotalDays = participantsDate,
        status = EUserChallengeStatus.RUNNING,
        participantsStartDate = LocalDate.now(),
    )
}