package com.app.server.infra.api.report

import com.app.server.infra.AbstractRestApiClient
import com.app.server.infra.api.report.dto.request.SendToReportServerRequestDto
import com.app.server.infra.api.report.dto.response.ReceiveFromReportServerResponseDto
import com.app.server.user_challenge.application.dto.response.GetReportResponseDto
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.port.outbound.ReportPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class ReportAdaptor(
    @Value("\${report-server.url}")
    private val baseUrl: String,
) : AbstractRestApiClient<
        SendToReportServerRequestDto,
        ReceiveFromReportServerResponseDto,
        GetReportResponseDto
        >(
    baseUrl
), ReportPort {

    override fun rawResponseType() = ReceiveFromReportServerResponseDto::class.java

    override fun parseResponse(response: ResponseEntity<ReceiveFromReportServerResponseDto>): GetReportResponseDto {
        val responseBody = response.body ?: return GetReportResponseDto(
            status = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER,
            message = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER.message
        )

        if (responseBody.success.toString().lowercase() == "true") {
            return GetReportResponseDto(
                status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS,
                message = responseBody.result_text
            )
        } else if (responseBody.success.toString().lowercase() == "false") {
            return GetReportResponseDto(
                status = EUserReportResultCode.RECEIVE_REPORT_FAILED,
                message = EUserReportResultCode.RECEIVE_REPORT_FAILED.message
            )
        }
        else {
            return GetReportResponseDto(
                status = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER,
                message = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER.message
            )
        }
    }

    override fun getReportMessage(sendToReportServerRequestDto: SendToReportServerRequestDto): GetReportResponseDto {
        return this.send(sendToReportServerRequestDto)
    }
}