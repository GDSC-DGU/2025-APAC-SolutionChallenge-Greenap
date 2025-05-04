package com.app.server.rank.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRankInSpecificChallengeTotalRankResponseDto(
    @JsonProperty("challenge_title")
    val challengeTitle: String,
    @JsonProperty("user_rank_info")
    val userRankInfo: UserRankForSpecificChallengeDto
){
    companion object {
        fun from(
            challengeTitle: String,
            rank: Long,
            totalParticipationCount: Double,
            userNickname: String,
            userProfileImageUrl: String?,
        ): UserRankInSpecificChallengeTotalRankResponseDto {
            return UserRankInSpecificChallengeTotalRankResponseDto(
                challengeTitle = challengeTitle,
                userRankInfo = UserRankForSpecificChallengeDto(
                    rank = rank.toInt(),
                    user = UserRankInfoForSpecificChallengeDto(
                        nickname = userNickname,
                        profileImageUrl = userProfileImageUrl,
                        totalParticipationCount = totalParticipationCount.toLong()
                    )
                )
            )
        }

    }
}
