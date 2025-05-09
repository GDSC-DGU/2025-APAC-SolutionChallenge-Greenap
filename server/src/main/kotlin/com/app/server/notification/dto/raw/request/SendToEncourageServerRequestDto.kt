package com.app.server.notification.dto.raw.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToEncourageServerRequestDto(
    @JsonProperty("user_id")
    val user_id: String,
    @JsonProperty("challenge_title")
    val challenge_title: String,
    val progress: Int
)
