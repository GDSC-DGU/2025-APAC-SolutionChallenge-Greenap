package com.app.server.challenge_certification.dto.application.raw

data class ReceiveFromCertificationServerResponseDto(
    val success: Boolean,
    val status_code: Int,
    val result_text: String,
    val message: String
)
