package com.app.server.core.security.handler

import com.app.server.common.response.ApiResponse.Companion.success
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomSignOutResultHandler : LogoutSuccessHandler {
    private val objectMapper = ObjectMapper()

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        setSuccessResponse(response)
        return
    }

    private fun setSuccessResponse(response: HttpServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_OK

        val apiResponse = success<Any>()
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(jsonResponse)
    }
}