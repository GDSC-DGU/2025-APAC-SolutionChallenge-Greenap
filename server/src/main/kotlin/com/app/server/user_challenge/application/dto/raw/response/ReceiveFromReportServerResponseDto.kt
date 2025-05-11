package com.app.server.user_challenge.application.dto.raw.response

data class ReceiveFromReportServerResponseDto(
    val success: Boolean,
    val status_code: Int,
    val result_text: String,
    val message: String
)
