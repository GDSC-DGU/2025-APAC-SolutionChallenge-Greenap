package com.app.server.infra.api.report.dto.response

data class ReceiveFromReportServerResponseDto(
    val success: Boolean,
    val status_code: Int,
    val result_text: String,
    val message: String
)
