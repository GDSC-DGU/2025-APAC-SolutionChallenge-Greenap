package com.app.server.user_challenge.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class GetTotalUserChallengeResponseDto (
    @JsonProperty("user_challenges")
    val userChallenges: List<UserChallengeQuery>
)

data class UserChallengeQuery(
    var id: Long,
    var title: String,
    val category: String,
    val status: String,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("elapsed_days")
    val elapsedDays: Long,
    val progress: Int,
    @JsonProperty("ice_count")
    val iceCount: Int,
    @JsonProperty("is_certificated_in_today")
    val isCertificatedInToday: Boolean,
    @JsonProperty("certification_data_list")
    val certificationDataList: List<CertificationData>
)

data class CertificationData(
    val date: LocalDate,
    @JsonProperty("is_certificated")
    val isCertificated: String
)