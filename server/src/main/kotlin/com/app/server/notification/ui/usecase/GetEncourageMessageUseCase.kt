package com.app.server.notification.ui.usecase

import com.app.server.notification.dto.request.GetEncourageMessageRequestDto
import com.app.server.notification.dto.response.GetEncourageMessageResponseDto

interface GetEncourageMessageUseCase {
    fun execute(dto: GetEncourageMessageRequestDto): GetEncourageMessageResponseDto
}