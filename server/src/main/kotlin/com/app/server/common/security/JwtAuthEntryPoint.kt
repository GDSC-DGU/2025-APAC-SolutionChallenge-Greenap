package com.app.server.common.security

import com.app.server.common.response.ApiResponse.Companion.failure
import com.app.server.common.security.enums.SecurityExceptionCode
import com.app.server.user.exception.UserExceptionCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

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
