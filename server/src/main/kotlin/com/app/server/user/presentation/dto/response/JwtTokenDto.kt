package com.app.server.user.presentation.dto.response

data class JwtTokenDto(
    val accessToken: String,
    val refreshToken: String
)
