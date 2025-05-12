package com.app.server.auth.ui.controller

import com.app.server.auth.application.service.AuthService
import com.app.server.auth.exception.AuthException
import com.app.server.auth.ui.dto.request.GoogleTokenForLoginRequestDto
import com.app.server.auth.ui.usecase.GoogleLoginUseCase
import com.app.server.common.constant.Constants
import com.app.server.common.exception.IllegalArgumentException
import com.app.server.common.response.ApiResponse
import com.app.server.core.security.dto.UserInfoResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "Authentication API")
class AuthController(
    private val authService: AuthService,
    private val googleLoginUseCase: GoogleLoginUseCase
) {

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
    ): ApiResponse<UserInfoResponseDto> {
        return ApiResponse.success(authService.refreshToken(refreshToken))
    }

    @Operation(
        summary = "OAuth2 로그인",
        description = "OAuth2 인증을 통해 사용자를 로그인합니다. Google 로그인만 지원되기 때문에 registrationId는 반드시 google로 설정해야 합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = UserInfoResponseDto::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청"
            )
        ]
    )

    @PostMapping("/oauth2/token")
    fun loginWithToken(
        @RequestBody dto: GoogleTokenForLoginRequestDto,
    ): ApiResponse<UserInfoResponseDto> {

        if (dto.registrationId.lowercase() != "google")
            throw IllegalArgumentException(AuthException.ILLEGAL_LOGIN_REGISTRATION_ID)

        return ApiResponse.success(googleLoginUseCase.execute(dto))
    }
}
