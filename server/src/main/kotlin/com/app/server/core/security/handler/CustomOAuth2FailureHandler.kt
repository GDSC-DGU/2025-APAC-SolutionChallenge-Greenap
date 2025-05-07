package com.app.server.core.security.handler

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper
import com.app.server.common.response.ApiResponse

@Component
class CustomOAuth2FailureHandler : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        response?.contentType = "application/json"
        response?.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorMessage = exception?.message ?: CommonResultCode.OAUTH2_EXCEPTION.message

        val responseBody = ApiResponse.failure<UnauthorizedException>(CommonResultCode.OAUTH2_EXCEPTION, errorMessage)

        response?.writer?.write(ObjectMapper().writeValueAsString(responseBody))
    }
}