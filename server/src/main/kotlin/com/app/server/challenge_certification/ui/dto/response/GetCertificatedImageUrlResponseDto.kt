package com.app.server.challenge_certification.ui.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GetCertificatedImageUrlResponseDto(
    @JsonProperty("image_url")
    val imageUrl: String,
    @JsonProperty("is_finished")
    val isFinished: Boolean,
)
