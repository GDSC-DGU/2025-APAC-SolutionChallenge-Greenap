package com.app.server.challenge_certification.dto.ui.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToCertificationServerRequestDto (

    val image_url: String,
    @JsonProperty("challenge_id")
    val challengeId: Long = 0,
    @JsonProperty("challenge_title")
    val challenge_title: String,
    @JsonProperty("challenge_description")
    val challenge_description: String
)
