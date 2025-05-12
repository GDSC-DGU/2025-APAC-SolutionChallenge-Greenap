package com.app.server.infra

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.InternalServerErrorException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate

/**
 * 외부 API 요청/응답을 담당하는 기본 인터페이스
 */
interface ExternalApiClient<Req, Res> {
    fun send(request: Req): Res
}

/**
 * RestTemplate + Jackson 으로 외부 JSON API를 호출하는
 * 공통 템플릿 메서드를 제공하는 추상 클래스
 */
abstract class AbstractRestApiClient<Req, Raw, Res>(
    private val baseUrl: String,
    private val restTemplate: RestTemplate = RestTemplate().apply {
        this.messageConverters.add(0, MappingJackson2HttpMessageConverter().apply {
            this.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)
        })
    },
    private val objectMapper: ObjectMapper = ObjectMapper()
) : ExternalApiClient<Req, Res> {

    /** 요청 DTO → JSON 문자열 변환 및 HttpEntity 생성 */
    protected open fun buildEntity(request: Req): HttpEntity<String> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-Type", "application/json")
        val json = objectMapper.writeValueAsString(request)
        return HttpEntity(json, headers)
    }

    /** RestTemplate 호출 후, 받은 원시 응답(Raw 타입) */
    protected open fun callApi(entity: HttpEntity<String>): ResponseEntity<Raw> =
        try {
            restTemplate.postForEntity(baseUrl, entity, rawResponseType())
        } catch (ex: HttpStatusCodeException) {
            throw InternalServerErrorException(
                CommonResultCode.EXTERNAL_SERVER_ERROR, ex.message!!
            )
        }

    /** from ResponseEntity<Raw> → 최종 변환 Res 타입 */
    protected abstract fun parseResponse(response: ResponseEntity<Raw>): Res

    /** RestTemplate 에서 사용할 리스폰스 타입 토큰을 제공 */
    protected abstract fun rawResponseType(): Class<Raw>

    /** ExternalApiClient 인터페이스 구현 */
    final override fun send(request: Req): Res {
        val entity = buildEntity(request)
        val response = callApi(entity)
        return parseResponse(response)
    }
}