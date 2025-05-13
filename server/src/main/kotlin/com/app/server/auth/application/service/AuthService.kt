package com.app.server.auth.application.service

import com.app.server.auth.exception.AuthException
import com.app.server.auth.ui.dto.request.GoogleTokenForLoginRequestDto
import com.app.server.auth.ui.usecase.GoogleLoginUseCase
import com.app.server.common.constant.Constants
import com.app.server.common.exception.BadRequestException
import com.app.server.core.security.dto.UserInfoResponseDto
import com.app.server.core.security.handler.CustomOAuth2SuccessHandler
import com.app.server.core.security.util.JwtUtil
import com.app.server.user.application.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
@Transactional
class AuthService(
    private val jwtUtil: JwtUtil,
    private val userService: UserService,
    private val clientRegRepo: ClientRegistrationRepository,
    private val oauth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>,
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler,
) : GoogleLoginUseCase {

    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var googleClientId: String

    @Value("\${android-key.client-android-id}")
    private lateinit var androidClientId: String

    fun refreshToken(token: String): UserInfoResponseDto {

        val refreshToken = refineToken(token)
        if (!jwtUtil.validateToken(refreshToken)) {
            throw BadRequestException(AuthException.INVALID_REFRESH_TOKEN)
        }
        val userId = jwtUtil.getUserIdFromToken(refreshToken)

        val user = userService.findById(userId)

        if (!user.refreshToken.equals(refreshToken)) {
            throw BadRequestException(AuthException.INVALID_REFRESH_TOKEN)
        }

        val jwtTokenDto = jwtUtil.generateToken(user.id!!, user.email, "ROLE_USER")
        userService.updateRefreshToken(userId, jwtTokenDto.refreshToken)

        return UserInfoResponseDto.fromUserEntity(
            user = user,
            jwtTokenDto = jwtTokenDto
        )
    }

    override fun execute(dto: GoogleTokenForLoginRequestDto): UserInfoResponseDto {

        val registration = clientRegRepo.findByRegistrationId(dto.registrationId)
            ?: throw IllegalArgumentException("unknown registrationId")

        val accessToken = makeOAuth2AccessTokenFrom(dto)

        val oauth2Request = OAuth2UserRequest(registration, accessToken)

        val idToken: GoogleIdToken? = verityIdTokenFrom(dto)

        val oauth2User: OAuth2User = createOAuth2UserFromIdToken(idToken, dto, oauth2Request)

        return customOAuth2SuccessHandler.getUserInfoFromIdToken(oauth2User)
    }

    private fun verityIdTokenFrom(dto: GoogleTokenForLoginRequestDto): GoogleIdToken? {
        val verifier = GoogleIdTokenVerifier.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance()
        )
            .setAudience(
                listOf(
                    googleClientId,
                    androidClientId
                )
            )
            .build()

        val idToken: GoogleIdToken? = verifier.verify(dto.idToken)
        return idToken
    }

    private fun makeOAuth2AccessTokenFrom(dto: GoogleTokenForLoginRequestDto)
            : OAuth2AccessToken = OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        dto.accessToken,
        Instant.now(),
        Instant.now().plusSeconds(3600)
    )

    private fun createOAuth2UserFromIdToken(
        idToken: GoogleIdToken?,
        dto: GoogleTokenForLoginRequestDto,
        oauth2Request: OAuth2UserRequest
    ): OAuth2User = if (idToken != null) {

        val parts = dto.idToken!!.split('.')
        if (parts.size < 2) throw IllegalArgumentException("Invalid ID token format.")
        val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))

        val mapper = jacksonObjectMapper()
        val payloadMap: Map<String, Any> = mapper.readValue(payloadJson)

        val attributes = mapOf(
            "sub" to (payloadMap["sub"] as? String),
            "email" to (payloadMap["email"] as? String),
            "name" to (payloadMap["name"] as? String),
            "picture" to (payloadMap["picture"] as? String)
        )

        DefaultOAuth2User(
            AuthorityUtils.createAuthorityList("ROLE_USER"),
            attributes,
            "sub"
        )
    } else {
        oauth2UserService.loadUser(oauth2Request)
    }

    private fun refineToken(accessToken: String): String {
        return if (accessToken.startsWith(Constants.BEARER_PREFIX)) {
            accessToken.substring(Constants.BEARER_PREFIX.length)
        } else {
            accessToken
        }
    }

}

