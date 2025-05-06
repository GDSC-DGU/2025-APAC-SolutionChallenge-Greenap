package com.app.server.user_challenge.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToReportServerRequestDto (

    @JsonProperty("challenge_title")
    val challengeTitle: String,
    @JsonProperty("progress")
    val progress: Int,
    @JsonProperty("total_day")
    val totalDay: Int
){
    companion object {
        fun from(challengeTitle: String, progress: Int, totalDay: Int) = SendToReportServerRequestDto(
            challengeTitle = challengeTitle,
            progress = progress,
            totalDay = totalDay
        )
    }
}