package com.app.server.infra.api.notification.dto.response

data class ReceiveFromEncourageServerResponseDto(
    val success: String,
    val status_code: Int,
    val result_text: String,
    val message: String
)
