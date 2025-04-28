package com.app.server.challenge_certification.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SendToCertificationServerRequestDto (

    @JsonProperty("image_url")
    val imageUrl: String,
    @JsonProperty("challenge_id")
    val challengeId: Long?,
    @JsonProperty("challenge_name")
    val challengeName: String,
    @JsonProperty("challenge_description")
    val challengeDescription: String
)
