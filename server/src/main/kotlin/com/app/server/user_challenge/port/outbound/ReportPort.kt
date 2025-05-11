package com.app.server.user_challenge.port.outbound

import com.app.server.user_challenge.application.dto.raw.request.SendToReportServerRequestDto
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto

interface ReportPort {

    fun getReportMessage(sendToReportServerRequestDto: SendToReportServerRequestDto)
            : GetReportResponseDto
}