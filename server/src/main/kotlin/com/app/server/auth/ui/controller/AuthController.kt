package com.app.server.auth.ui.controller

import com.app.server.auth.application.service.AuthService
import com.app.server.common.constant.Constants
import com.app.server.common.response.ApiResponse
import com.app.server.core.security.dto.UserInfoResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController (
    private val authService: AuthService
){

    @GetMapping("/refresh")
    fun refreshToken(
        @Valid @RequestHeader(Constants.AUTHORIZATION_HEADER) refreshToken: String
    ) : ApiResponse<UserInfoResponseDto>{
        return ApiResponse.success(authService.refreshToken(refreshToken))
    }
}