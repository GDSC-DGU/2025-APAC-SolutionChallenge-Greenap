package com.app.server.auth.ui.controller

import com.app.server.auth.application.service.AuthService
import com.app.server.common.constant.Constants
import com.app.server.common.response.ApiResponse
import com.app.server.core.security.dto.UserInfoResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "Authentication API")
class AuthController (
    private val authService: AuthService
){

    @Operation(
        summary = "토큰 갱신",
        description = "Refresh 토큰을 사용하여 새로운 액세스 토큰을 발급합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200", 
                description = "토큰 갱신 성공",
                content = [Content(schema = Schema(implementation = UserInfoResponseDto::class))]
            ),
            SwaggerApiResponse(
                responseCode = "401", 
                description = "인증 실패"
            )
        ]
    )
    @GetMapping("/refresh")
    fun refreshToken(
        @Parameter(description = "Refresh 토큰", required = true)
        @Valid @RequestHeader(Constants.AUTHORIZATION_HEADER) refreshToken: String
    ) : ApiResponse<UserInfoResponseDto>{
        return ApiResponse.success(authService.refreshToken(refreshToken))
    }
}