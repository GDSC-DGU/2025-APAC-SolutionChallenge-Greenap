package com.app.server.challenge_certification.infra

import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.HttpStatusCodeException

@Service
class CertificationInfraService {

    private val restTemplate: RestTemplate = RestTemplate().apply {
        errorHandler = object : DefaultResponseErrorHandler() {
            override fun hasError(response: ClientHttpResponse): Boolean {
                return false
            }
        }
    }
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Value("\${certification-server.url}")
    private lateinit var certificationServerUrl: String

    fun certificate(
        sendToCertificationServerRequestDto: SendToCertificationServerRequestDto
    ): Map<EUserCertificatedResultCode, String> {
        val httpEntity = setRequest(sendToCertificationServerRequestDto)
        val response: ResponseEntity<String> = try {
            restTemplate.postForEntity(
                certificationServerUrl,
                httpEntity,
                String::class.java
            )
        } catch (ex: HttpStatusCodeException) {
            ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
        }
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

    private fun fromResponseToResultCode(response: ResponseEntity<String>): Map<EUserCertificatedResultCode, String> {
        val body = response.body ?: return mapOf(EUserCertificatedResultCode.ERROR_IN_CERTIFICATED_SERVER
                to HttpStatus.INTERNAL_SERVER_ERROR.name)

        val jsonResponseObject = objectMapper.readTree(body)

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
}