package com.app.server.common.security.handler

import com.app.server.common.response.ApiResponse.Companion.failure
import com.app.server.common.security.enums.SecurityExceptionCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    private val objectMapper = ObjectMapper()

    override fun handle(
        request: HttpServletRequest, response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val apiResponse = failure<Any>(SecurityExceptionCode.ACCESS_DENIED_ERROR)
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(jsonResponse)
    }
}