package com.app.server.infra.api.certification

import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.port.outbound.CertificationPort
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.infra.AbstractRestApiClient
import com.app.server.infra.api.certification.dto.ReceiveFromCertificationServerResponseDto
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class CertificationAdaptor(
    @Value("\${certification-server.url}")
    private val certificationServerUrl: String,
    private val objectMapper: ObjectMapper,
) : AbstractRestApiClient<
        SendToCertificationServerRequestDto,
        String,
        Map<EUserCertificatedResultCode, String>>(certificationServerUrl), CertificationPort {

    override fun rawResponseType() = String::class.java

    override fun parseResponse(response: ResponseEntity<String>)
            : Map<EUserCertificatedResultCode, String> {

        val responseBody = response.body ?: throw InternalServerErrorException(
            UserChallengeException.ERROR_IN_CERTIFICATED_SERVER
        )

        val jsonResponseObject = objectMapper.readTree(responseBody)

        if (jsonResponseObject.get("success").toString().lowercase() == "true"
            || jsonResponseObject.get("result_text").toString().contains("success")) {
            return mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to jsonResponseObject.get("result_text").toString())
        }
        else if ((jsonResponseObject.get("success").toString().lowercase() == "false")
            || jsonResponseObject.get("result_text").toString().contains("failure")) {
            return mapOf(EUserCertificatedResultCode.CERTIFICATED_FAILED to jsonResponseObject.get("result_text").toString())
        }
        else {
            return mapOf(EUserCertificatedResultCode.ERROR_IN_CERTIFICATED_SERVER
                    to HttpStatus.INTERNAL_SERVER_ERROR.name)
        }
    }

    override fun verifyCertificate(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto)
    : Map<EUserCertificatedResultCode, String> {
        return this.send(sendToCertificationServerRequestDto)
    }
}