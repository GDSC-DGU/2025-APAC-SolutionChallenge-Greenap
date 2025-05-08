package com.app.server.core.security.dto

import com.app.server.user.domain.model.User
import com.app.server.user.ui.dto.response.JwtTokenDto


data class UserInfoResponseDto(
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val jwtTokenDto: JwtTokenDto?
) {

    companion object {
        fun fromUserEntity(user: User, jwtTokenDto: JwtTokenDto?): UserInfoResponseDto {
            return UserInfoResponseDto(
                nickname = user.nickname,
                email = user.email,
                profileImageUrl = user.profileImageUrl,
                jwtTokenDto = jwtTokenDto
            )
        }
    }
}