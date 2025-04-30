package com.app.server.user_challenge.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToReportServerRequestDto (
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("challenge_title")
    val challengeTitle: String,
    @JsonProperty("progress")
    val progress: Int,
    @JsonProperty("total_day")
    val totalDay: Int
){
    companion object {
        fun from(userId: Long, challengeTitle: String, progress: Int, totalDay: Int) = SendToReportServerRequestDto(
            userId = userId.toString(),
            challengeTitle = challengeTitle,
            progress = progress,
            totalDay = totalDay
        )
    }
}