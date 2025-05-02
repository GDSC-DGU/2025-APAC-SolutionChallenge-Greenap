package com.app.server.feed.ui.usecase

import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.ui.dto.CreateFeedCommand

interface CreateFeedUseCase {
    fun execute(createFeedCommand : CreateFeedCommand) : Feed
}
