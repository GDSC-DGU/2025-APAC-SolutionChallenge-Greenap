package com.app.server.core.security.handler

import com.app.server.common.response.ApiResponse
import com.app.server.core.security.dto.UserInfoResponseDto
import com.app.server.core.security.util.JwtUtil
import com.app.server.user.application.service.UserService
import com.app.server.user.domain.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2SuccessHandler(
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        val oauth2User = authentication.principal as OAuth2User
        val clientRegistrationId = (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId
        val principalName = authentication.name

        val client = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
            clientRegistrationId,
            principalName
        )

        val accessToken = client.accessToken.tokenValue

        // 사용자 정보 가져오기
        val userInfoResponseDto = getUserInfoFromIdToken(oauth2User)

        setResponse(response, userInfoResponseDto)
    }

    private fun setResponse(
        response: HttpServletResponse,
        userInfoResponseDto: UserInfoResponseDto
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK

        val responseBody = ApiResponse.success(userInfoResponseDto)

        response.writer.write(ObjectMapper().writeValueAsString(responseBody))
    }

    fun getUserInfoFromIdToken(oauth2User: OAuth2User): UserInfoResponseDto {
        val email: String = oauth2User.getAttribute<String>("email") ?: "unknown"
        val nickname = oauth2User.getAttribute<String>("name") ?: "unknown"
        val profileImage = oauth2User.getAttribute<String>("picture") ?: "default.png"

        val existUser = userService.findByEmail(email)

        val userInfoResponseDto = if (existUser == null) {
            val newUser = User(
                email = email,
                nickname = nickname,
                profileImageUrl = profileImage,
                nowMaxConsecutiveParticipationDayCount = 0
            )
            userService.save(newUser)
            handleExistingUserLogin(newUser)
        } else {
            handleExistingUserLogin(existUser)
        }
        return userInfoResponseDto
    }

    private fun handleExistingUserLogin(user: User): UserInfoResponseDto {
        val jwtTokenDto = jwtUtil.generateToken(user.id!!, user.email, "ROLE_USER")
        user.updateRefreshToken(jwtTokenDto.refreshToken)
        userService.save(user)

        return UserInfoResponseDto.fromUserEntity(user, jwtTokenDto)
    }
}