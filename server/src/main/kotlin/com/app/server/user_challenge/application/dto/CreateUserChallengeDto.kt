package com.app.server.user_challenge.application.dto

import com.app.server.challenge.domain.model.Challenge
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus

data class CreateUserChallengeDto(
    val userId: Long,
    val challenge: Challenge,
    val participantsDate: Int,
    val status: EUserChallengeStatus
)
