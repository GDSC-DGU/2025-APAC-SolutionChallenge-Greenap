package com.app.server.notification.application.dto.result

import com.app.server.user_challenge.domain.enums.EUserChallengeStatus

data class EncourageMessageInfoDto(
    val message: String,
    val challengeTitle: String,
    val progress: Int,
    val userChallengeId: Long?,
    val userChallengeStatus: EUserChallengeStatus
)
