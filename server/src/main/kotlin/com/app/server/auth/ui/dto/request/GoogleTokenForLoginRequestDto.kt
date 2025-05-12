package com.app.server.auth.ui.dto.request

data class GoogleTokenForLoginRequestDto(
    val registrationId: String = "google",
    val accessToken: String,
    val idToken: String?
)