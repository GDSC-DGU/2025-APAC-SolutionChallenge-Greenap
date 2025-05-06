package com.app.server.core.security

import com.app.server.common.response.ApiResponse.Companion.failure
import com.app.server.core.security.enums.SecurityExceptionCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthEntryPoint : AuthenticationEntryPoint {
    private val objectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        authException.printStackTrace()
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val apiResponse = failure<Any>(SecurityExceptionCode.ACCESS_DENIED_ERROR, SecurityExceptionCode.ACCESS_DENIED_ERROR.message)
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(jsonResponse)
    }
}
