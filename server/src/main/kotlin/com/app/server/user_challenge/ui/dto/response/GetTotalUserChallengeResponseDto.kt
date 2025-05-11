package com.app.server.user_challenge.ui.dto.response

import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class GetTotalUserChallengeResponseDto (
    @JsonProperty("user_challenges")
    val userChallenges: List<UserChallengeQuery>
)

data class UserChallengeQuery(
    var id: Long,
    @JsonProperty("challenge_id")
    val challengeId: Long,
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
    val isCertificatedInToday: EUserChallengeCertificationStatus,
    @JsonProperty("main_image_url")
    val mainImageUrl: String,
    @JsonProperty("certification_data_list")
    val certificationDataList: List<CertificationData>
)

data class CertificationData(
    val date: LocalDate,
    @JsonProperty("is_certificated")
    val isCertificated: String
)