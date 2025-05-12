package com.app.server.notification.ui.usecase

import com.app.server.notification.application.dto.command.GetEncourageMessageCommand
import com.app.server.notification.application.service.NotificationServiceImpl
import com.app.server.notification.ui.dto.request.GetEncourageMessageRequestDto
import com.app.server.notification.ui.dto.response.GetEncourageMessageResponseDto
import org.springframework.stereotype.Component

interface GetEncourageMessageUseCase {
    fun execute(dto: GetEncourageMessageRequestDto): GetEncourageMessageResponseDto
}

@Component
class GetEncourageMessageUseCaseImpl(
    private val notificationService: NotificationServiceImpl
) : GetEncourageMessageUseCase {
    override fun execute(dto: GetEncourageMessageRequestDto): GetEncourageMessageResponseDto {
        val command = GetEncourageMessageCommand(
            userId = dto.userId,
            todayDate = dto.todayDate
        )
        val result = notificationService.getEncourageMessageForMain(command)
        return GetEncourageMessageResponseDto(
            challengeTitle = result.challengeTitle,
            progress = result.progress,
            userChallengeId = result.userChallengeId,
            message = result.message
        )
    }
}