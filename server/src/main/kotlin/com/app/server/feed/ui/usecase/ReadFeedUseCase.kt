package com.app.server.feed.ui.usecase

import com.app.server.feed.application.service.query.FeedProjectionQueryService
import com.app.server.feed.ui.dto.response.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionQuery
import com.app.server.feed.ui.dto.request.ReadFeedRequestDto
import org.springframework.stereotype.Component

interface ReadFeedUseCase {
    fun execute(
        readFeedRequestDto: ReadFeedRequestDto
    ): FeedListResponseDto
}

@Component
class ReadFeedUseCaseImpl(
    private val feedProjectionQueryService: FeedProjectionQueryService
) : ReadFeedUseCase {
    override fun execute(
        request: ReadFeedRequestDto
    ): FeedListResponseDto {
        val command = ReadFeedProjectionQuery.toQuery(
            userId = request.userId!!,
            categoryId = request.categoryId,
            scope = request.scope,
            userChallengeId = request.userChallengeId,
            page = request.page,
            size = request.size
        )
        val result = feedProjectionQueryService.execute(command)

        return result
    }
}