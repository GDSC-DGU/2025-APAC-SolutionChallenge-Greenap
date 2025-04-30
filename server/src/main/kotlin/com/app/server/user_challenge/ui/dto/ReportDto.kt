package com.app.server.user_challenge.ui.dto

import java.time.LocalDate

data class ReportDto (
    val userChallengeId: Long,
    val totalDays: Int,
    val successDays: Int,
    val reportMessage: String,
    val maxConsecutiveParticipationDays: Long,
    val challengeRanking: Long = 0,
    val certificationDataList: List<CertificationReportDataDto>
){
}

data class CertificationReportDataDto(
    val date: LocalDate,
    val isCertificated: String
)