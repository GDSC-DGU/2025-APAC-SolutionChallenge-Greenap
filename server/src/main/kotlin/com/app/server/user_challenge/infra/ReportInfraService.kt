package com.app.server.user_challenge.infra

import com.app.server.user_challenge.application.dto.ReceiveReportResponseDto
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.ui.dto.SendToReportServerRequestDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class ReportInfraService {

    private val restTemplate: RestTemplate = RestTemplate()
    private val objectMapper: ObjectMapper = ObjectMapper()

    private val reportServerUrl = "https://ai_server/api/v1/report"

    fun receiveReportMessage(
        sendToReportServerRequestDto: SendToReportServerRequestDto
    ): ReceiveReportResponseDto {
        val httpEntity = setRequest(sendToReportServerRequestDto)

        val response: ResponseEntity<ReportServerResponseDto> =
            restTemplate.postForEntity(
                reportServerUrl,
                httpEntity,
                ReportServerResponseDto::class.java
            )

        return fromResponseToResultCode(response)
    }

    private fun setRequest(sendToReportServerRequestDto: SendToReportServerRequestDto): HttpEntity<String> {
        val httpHeaders: MultiValueMap<String, String> = LinkedMultiValueMap()
        httpHeaders.add("Content-Type", "application/json")

        val requestJsonAsString: String = objectMapper.writeValueAsString(
            sendToReportServerRequestDto
        )

        val httpEntity = HttpEntity(
            requestJsonAsString,
            httpHeaders
        )
        return httpEntity
    }

    private fun fromResponseToResultCode(response: ResponseEntity<ReportServerResponseDto>): ReceiveReportResponseDto {
        if (response.statusCode.isError || response.body == null) {
            return ReceiveReportResponseDto(
                status = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER,
                message = EUserReportResultCode.ERROR_IN_RECEIVE_REPORT_SERVER.message
            )
        } else if (response.body!!.status == EUserReportResultCode.RECEIVE_REPORT_FAILED.message) {
            return ReceiveReportResponseDto(
                status = EUserReportResultCode.RECEIVE_REPORT_FAILED,
                message = EUserReportResultCode.RECEIVE_REPORT_FAILED.message
            )
        }
        return ReceiveReportResponseDto(
            status = EUserReportResultCode.RECEIVE_REPORT_SUCCESS,
            message = response.body!!.message
        )
    }

}

private data class ReportServerResponseDto(
    val message: String,
    val status: String
)