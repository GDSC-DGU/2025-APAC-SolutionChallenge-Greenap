package com.app.server.infra.api.certification.dto

data class ReceiveFromCertificationServerResponseDto(
    val success: Boolean,
    val status_code: Int,
    val result_text: String,
    val message: String
)
