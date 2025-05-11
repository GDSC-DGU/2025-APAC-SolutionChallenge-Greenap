package com.app.server.challenge_certification.dto.ui.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GetCertificatedImageUrlResponseDto(
    @JsonProperty("image_url")
    val imageUrl: String
)
