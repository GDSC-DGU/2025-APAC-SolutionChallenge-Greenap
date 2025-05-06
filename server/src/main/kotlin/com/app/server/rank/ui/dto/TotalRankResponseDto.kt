package com.app.server.rank.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TotalRankResponseDto(
    @JsonProperty("total_participants_count")
    val totalParticipantsCount: Long,
    @JsonProperty("top_100_participants")
    val topParticipants: List<UserRankForTotalDto>,
){
    companion object {
        fun from(
            totalParticipantsCount: Long,
            topParticipants: List<UserRankForTotalDto>
        ): TotalRankResponseDto {
            return TotalRankResponseDto(
                totalParticipantsCount = totalParticipantsCount,
                topParticipants = topParticipants
            )
        }
    }
}
