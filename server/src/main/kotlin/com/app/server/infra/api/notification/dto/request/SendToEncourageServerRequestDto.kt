package com.app.server.infra.api.notification.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToEncourageServerRequestDto(
    @JsonProperty("user_id")
    val user_id: String,
    @JsonProperty("challenge_title")
    val challenge_title: String,
    val progress: Int
)
