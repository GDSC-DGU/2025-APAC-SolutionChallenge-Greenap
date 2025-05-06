package com.app.server.user_challenge.application.dto

import com.app.server.user_challenge.enums.EUserReportResultCode

data class ReceiveReportResponseDto(
    val status: EUserReportResultCode,
    val message: String
)
