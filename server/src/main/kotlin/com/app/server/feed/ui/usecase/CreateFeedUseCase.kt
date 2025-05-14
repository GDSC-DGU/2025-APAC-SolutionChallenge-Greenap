package com.app.server.feed.ui.usecase

import com.app.server.feed.application.service.command.FeedCommandServiceImpl
import com.app.server.feed.ui.dto.CreateFeedCommand
import com.app.server.feed.ui.dto.response.CreateFeedResponseDto
import org.springframework.stereotype.Component

interface CreateFeedUseCase {
    fun execute(createFeedCommand : CreateFeedCommand) : CreateFeedResponseDto
}

@Component
class CreateFeedUseCaseImpl(
    private val feedCommandService: FeedCommandServiceImpl
) : CreateFeedUseCase {
    override fun execute(createFeedCommand: CreateFeedCommand): CreateFeedResponseDto {
        val response =  feedCommandService.execute(createFeedCommand)
        return CreateFeedResponseDto(
            feedId = response.id!!,
            userChallengeId = response.userChallengeId,
            imageUrl = response.imageUrl,
            content = response.content,
        )
    }
}
