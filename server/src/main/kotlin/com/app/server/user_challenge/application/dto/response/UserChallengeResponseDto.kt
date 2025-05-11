package com.app.server.user_challenge.application.dto.response

import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import java.time.LocalDate

data class UserChallengeResponseDto(
    val userChallengeId: Long,
    val challengeTitle: String,
    val userId: Long,
    val nowConsecutiveDays: Long,
    val totalConsecutiveDays: Long,
    val maxConsecutiveDays: Long,
    val challengeStartDate: String,
    val participantsDate: Int,
    val status: EUserChallengeStatus,
    val iceCount: Int,
    val progressFromTotal: Int,
    val progressFromElapsed: Int,
    val reportMessage: String?,
    val challengeHistories: List<ChallengeHistoryResponseDto>,
){
    data class ChallengeHistoryResponseDto(
        val date: LocalDate,
        val isCertificated: EUserChallengeCertificationStatus,
        val imageUrl: String? = null
    )
}

