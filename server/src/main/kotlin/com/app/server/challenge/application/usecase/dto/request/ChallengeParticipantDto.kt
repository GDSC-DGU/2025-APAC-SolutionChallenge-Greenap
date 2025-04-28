package com.app.server.challenge.application.usecase.dto.request

import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import java.time.LocalDate

data class ChallengeParticipantDto(
    val userId: Long,
    val challengeId: Long,
    val participantsTotalDays: Int,
    val status: EUserChallengeStatus,
    val participantsStartDate: LocalDate
)
