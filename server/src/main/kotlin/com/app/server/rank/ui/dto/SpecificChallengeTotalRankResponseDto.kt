package com.app.server.rank.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpecificChallengeTotalRankResponseDto (
    @JsonProperty("challenge_title")
    val challengeTitle: String,
    @JsonProperty("total_participants_count")
    val totalParticipantsCount: Long,
    @JsonProperty("top_100_participants")
    val topParticipants: List<UserRankForSpecificChallengeDto>,
) {
    companion object {
        fun from(
            challengeTitle: String,
            totalParticipantsCount: Long,
            topParticipants: List<UserRankForSpecificChallengeDto>
        ): SpecificChallengeTotalRankResponseDto {
            return SpecificChallengeTotalRankResponseDto(
                challengeTitle = challengeTitle,
                totalParticipantsCount = totalParticipantsCount,
                topParticipants = topParticipants
            )
        }
    }
}
