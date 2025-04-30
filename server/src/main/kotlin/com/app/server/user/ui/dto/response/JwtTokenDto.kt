package com.app.server.user.ui.dto.response

data class JwtTokenDto(
    val accessToken: String,
    val refreshToken: String
)
