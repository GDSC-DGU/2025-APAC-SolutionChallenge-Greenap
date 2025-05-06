package com.app.server.rank.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRankInTotalRankResponseDto(
    @JsonProperty("user_rank_info")
    val userRankInfo: UserRankForTotalDto
) {
    companion object {
        fun from(
            rank: Long,
            score: Double,
            userNickname: String,
            userProfileImageUrl: String?,
        ): UserRankInTotalRankResponseDto {
            return UserRankInTotalRankResponseDto(
                userRankInfo = UserRankForTotalDto(
                    rank = rank.toInt(),
                    user = UserRankInfoForTotalDto(
                        nickname = userNickname,
                        profileImageUrl = userProfileImageUrl,
                        longestConsecutiveParticipationCount = score.toLong()
                    )
                )
            )
        }
    }
}