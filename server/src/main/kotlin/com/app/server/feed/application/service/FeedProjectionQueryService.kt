package com.app.server.feed.application.service

import com.app.server.feed.ui.dto.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionCommand
import com.app.server.feed.ui.usecase.ReadFeedUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedProjectionQueryService (
    private val feedProjectionService: FeedProjectionService
) : ReadFeedUseCase {

    override fun execute(readFeedProjectionCommand: ReadFeedProjectionCommand) : FeedListResponseDto {
        TODO("Not yet implemented")
    }
}