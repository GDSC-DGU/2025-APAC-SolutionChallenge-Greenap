package com.app.server.auth.ui.usecase

import com.app.server.auth.ui.dto.request.GoogleTokenForLoginRequestDto
import com.app.server.core.security.dto.UserInfoResponseDto

interface GoogleLoginUseCase {

    fun execute(
        dto: GoogleTokenForLoginRequestDto
    ): UserInfoResponseDto
}