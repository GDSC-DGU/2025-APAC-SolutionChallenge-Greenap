package com.app.server.challenge_certification.ui.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToCertificationServerRequestDto (

    val image_url: String,
    val challengeId: Long,
    @JsonProperty("challenge_title")
    val challenge_title: String,
    @JsonProperty("challenge_description")
    val challenge_description: String
)
