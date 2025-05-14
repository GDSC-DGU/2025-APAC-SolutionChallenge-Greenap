package com.app.server.user_challenge.ui.dto.response

import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ReportDto (
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("user_challenge_status")
    val userChallengeStatus: EUserChallengeStatus,
    @JsonProperty("success_days")
    val successDays: Int,
    @JsonProperty("report_message")
    val reportMessage: String,
    @JsonProperty("max_consecutive_participation_days")
    val maxConsecutiveParticipationDays: Long,
    @JsonProperty("challenge_ranking")
    val challengeRanking: Long = 0,
    @JsonProperty("certification_data_list")
    val certificationDataList: List<CertificationReportDataDto>
)

data class CertificationReportDataDto(
    val date: LocalDate,
    @JsonProperty("is_certificated")
    val isCertificated: String
)