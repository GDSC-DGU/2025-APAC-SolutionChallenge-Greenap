package com.app.server.user_challenge.application.dto.raw.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToReportServerRequestDto (

    @JsonProperty("challenge_title")
    val challenge_title: String,
    @JsonProperty("progress")
    val progress: Int,
    @JsonProperty("total_day")
    val total_day: Int
){
    companion object {
        fun from(challengeTitle: String, progress: Int, totalDay: Int) = SendToReportServerRequestDto(
            challengeTitle,
            progress,
            totalDay
        )
    }
}