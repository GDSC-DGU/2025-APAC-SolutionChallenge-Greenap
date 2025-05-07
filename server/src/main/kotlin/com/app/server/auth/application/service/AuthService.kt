package com.app.server.auth.application.service

import com.app.server.auth.exception.AuthException
import com.app.server.common.constant.Constants
import com.app.server.common.exception.BadRequestException
import com.app.server.core.security.dto.UserInfoResponseDto
import com.app.server.core.security.util.JwtUtil
import com.app.server.user.application.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthService(
    private val jwtUtil: JwtUtil,
    private val userService: UserService
) {
    fun refreshToken(token: String): UserInfoResponseDto {

        val refreshToken = refineToken(token)
        if (jwtUtil.validateToken(refreshToken)) {
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

    private fun refineToken(accessToken: String): String {
        return if (accessToken.startsWith(Constants.BEARER_PREFIX)) {
            accessToken.substring(Constants.BEARER_PREFIX.length)
        } else {
            accessToken
        }
    }
}