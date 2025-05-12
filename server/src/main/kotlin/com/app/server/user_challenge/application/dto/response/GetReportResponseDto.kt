package com.app.server.user_challenge.application.dto.response

import com.app.server.user_challenge.enums.EUserReportResultCode

data class GetReportResponseDto(
    val status: EUserReportResultCode,
    val message: String
)