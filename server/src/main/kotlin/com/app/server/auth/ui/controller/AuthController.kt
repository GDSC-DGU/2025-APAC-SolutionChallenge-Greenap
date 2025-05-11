package com.app.server.auth.ui.controller

import com.app.server.auth.application.service.AuthService
import com.app.server.common.constant.Constants
import com.app.server.common.response.ApiResponse
import com.app.server.core.security.dto.UserInfoResponseDto
import com.app.server.core.security.handler.CustomOAuth2SuccessHandler
import com.app.server.core.security.util.JwtUtil
import com.app.server.user.application.service.UserService
import com.app.server.user.domain.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "Authentication API")
class AuthController(
    private val authService: AuthService,
    private val clientRegRepo: ClientRegistrationRepository,
    private val oauth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>,
    private val customSuccessHandler: CustomOAuth2SuccessHandler,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientId: String,
    @Value("\${android-key.client-android-id}")
    private val androidClientId: String,
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
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
        description = "OAuth2 인증을 통해 사용자를 로그인합니다."
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
        @RequestBody dto: TokenRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        // 1) 클라이언트 등록 정보 조회
        val registration = clientRegRepo.findByRegistrationId(dto.registrationId)
            ?: throw IllegalArgumentException("unknown registrationId")

        // 2) AccessToken 생성 및 OAuth2UserRequest
        val accessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            dto.accessToken,
            Instant.now(),
            Instant.now().plusSeconds(3600)
        )
        val oauth2Request = OAuth2UserRequest(registration, accessToken)

        // 3) 사용자 프로필 로드 (ID 토큰 검증 우선)
        val oauth2User: OAuth2User = if (dto.idToken != null) {
            // 1) 토큰을 ‘.’으로 나눠서 페이로드 부분만 Base64로 디코딩
            val parts = dto.idToken.split('.')
            if (parts.size < 2) throw IllegalArgumentException("Invalid ID token format.")
            val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))

            // 2) Jackson 객체로 JSON → Map 변환
            val mapper = jacksonObjectMapper()
            val payloadMap: Map<String, Any> = mapper.readValue(payloadJson)

            // 3) Map에서 sub, email, name, picture 꺼내서 attributes 구성
            val attributes = mapOf(
                "sub" to (payloadMap["sub"] as? String),
                "email" to (payloadMap["email"] as? String),
                "name" to (payloadMap["name"] as? String),
                "picture" to (payloadMap["picture"] as? String)
            )

            // 4) 권한 ROLE_USER 로 DefaultOAuth2User 생성
            DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                attributes,
                "sub"
            )
        } else {
            // 기존 OAuth2UserService 활용
            oauth2UserService.loadUser(oauth2Request)
        }

        // 4) Authentication 토큰 생성 및 SecurityContext 설정
        val auth = OAuth2AuthenticationToken(
            oauth2User,
            oauth2User.authorities,
            registration.registrationId
        )
        SecurityContextHolder.getContext().authentication = auth


        // principal에서 OAuth2User 정보 꺼내기
        val oauth2UserPrincipal = auth.principal as OAuth2User
        val attributes = oauth2UserPrincipal.attributes

        // 필요한 클레임 추출
        val email = attributes["email"] as String
        val name = attributes["name"] as? String ?: ""
        val profileImage = attributes["picture"] as? String ?: ""

        val existUser = userService.findByEmail(email)

        val userInfoResponseDto = if (existUser == null) {
            val newUser = User(
                email = email,
                nickname = name,
                profileImageUrl = profileImage,
                nowMaxConsecutiveParticipationDayCount = 0
            )
            userService.save(newUser)
            handleExistingUserLogin(newUser)
        } else {
            handleExistingUserLogin(existUser)
        }

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK

        val responseBody = ApiResponse.success(userInfoResponseDto)

        response.writer.write(ObjectMapper().writeValueAsString(responseBody))
    }

    private fun handleExistingUserLogin(user: User): UserInfoResponseDto {
        val jwtTokenDto = jwtUtil.generateToken(user.id!!, user.email, "ROLE_USER")
        user.updateRefreshToken(jwtTokenDto.refreshToken)
        userService.save(user)

        return UserInfoResponseDto.fromUserEntity(user, jwtTokenDto)
    }
}

data class TokenRequest(
    val registrationId: String = "google",
    val accessToken: String,
    val idToken: String?
)