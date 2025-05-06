package com.app.server.challenge_certification.infra

import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class CertificationInfraService {

    private val restTemplate: RestTemplate = RestTemplate()
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Value("\${certification-server.url}")
    private lateinit var certificationServerUrl: String

    fun certificate(
        sendToCertificationServerRequestDto: SendToCertificationServerRequestDto
    ): EUserCertificatedResultCode {
        val httpEntity = setRequest(sendToCertificationServerRequestDto)

        val response: ResponseEntity<String> =
            restTemplate.postForEntity(
                certificationServerUrl,
                httpEntity,
                String::class.java
            )

        return fromResponseToResultCode(response)
    }

    private fun setRequest(sendToCertificationServerRequestDto: SendToCertificationServerRequestDto): HttpEntity<String> {
        val httpHeaders: MultiValueMap<String, String> = LinkedMultiValueMap()
        httpHeaders.add("Content-Type", "application/json")

        val requestJsonAsString: String = objectMapper.writeValueAsString(
            sendToCertificationServerRequestDto
        )

        val httpEntity = HttpEntity(
            requestJsonAsString,
            httpHeaders
        )
        return httpEntity
    }

    private fun fromResponseToResultCode(response: ResponseEntity<String>): EUserCertificatedResultCode {
        if (response.statusCode.isError || response.body == null) {
            return EUserCertificatedResultCode.ERROR_IN_CERTIFICATED_SERVER
        } else if (response.body == EUserCertificatedResultCode.CERTIFICATED_FAILED.message) {
            return EUserCertificatedResultCode.CERTIFICATED_FAILED
        }
        return EUserCertificatedResultCode.SUCCESS_CERTIFICATED
    }
}