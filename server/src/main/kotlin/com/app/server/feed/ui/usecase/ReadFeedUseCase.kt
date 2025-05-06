package com.app.server.feed.ui.usecase

import com.app.server.feed.ui.dto.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionCommand

interface ReadFeedUseCase {
    fun execute(
        readFeedProjectionCommand : ReadFeedProjectionCommand,
    ): FeedListResponseDto
}