package com.app.server.notification.dto.raw.response

data class ReceiveFromEncourageServerResponseDto(
    val success: String,
    val status_code: Int,
    val result_text: String,
    val message: String
)
