package com.app.server.rank.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRankForTotalDto(
    val rank: Int,
    val user: UserRankInfoForTotalDto
){
    companion object{
        fun from(
            rank: Int,
            nickname: String,
            profileImageUrl: String?,
            longestConsecutiveParticipationCount: Long
        ): UserRankForTotalDto {
            return UserRankForTotalDto(
                rank = rank,
                user = UserRankInfoForTotalDto(
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    longestConsecutiveParticipationCount = longestConsecutiveParticipationCount
                )
            )
        }
    }
}

data class UserRankForSpecificChallengeDto(
    val rank: Int,
    val user: UserRankInfoForSpecificChallengeDto
){
    companion object{
        fun from(
            rank: Int,
            nickname: String,
            profileImageUrl: String?,
            totalParticipationCount: Long
        ): UserRankForSpecificChallengeDto {
            return UserRankForSpecificChallengeDto(
                rank = rank,
                user = UserRankInfoForSpecificChallengeDto(
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    totalParticipationCount = totalParticipationCount
                )
            )
        }
    }
}

data class UserRankInfoForSpecificChallengeDto(
    val nickname: String,
    @JsonProperty("profile_image_url")
    val profileImageUrl: String?,
    @JsonProperty("total_participation_count")
    val totalParticipationCount: Long
)

data class UserRankInfoForTotalDto(
    val nickname: String,
    @JsonProperty("profile_image_url")
    val profileImageUrl: String?,
    @JsonProperty("longest_consecutive_participation_count")
    val longestConsecutiveParticipationCount: Long
)