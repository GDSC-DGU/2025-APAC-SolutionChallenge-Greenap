package com.app.server.core.security.handler

import com.app.server.core.security.info.CustomUserDetails
import com.app.server.user.application.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component

@Component
class CustomSignOutProcessHandler(
    private val userService: UserService
) : LogoutHandler {

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val userDetails = authentication.principal as CustomUserDetails
        processSignOut(userDetails.getId())
    }

    protected fun processSignOut(userId: Long?) {
        userService.updateRefreshToken(userId!!, null) // RefreshToken 삭제
    }
}