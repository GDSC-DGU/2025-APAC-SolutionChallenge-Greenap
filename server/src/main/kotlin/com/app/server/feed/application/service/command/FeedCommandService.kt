package com.app.server.feed.application.service.command

import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.ui.dto.CreateFeedCommand

interface FeedCommandService {

    fun execute(createFeedCommand: CreateFeedCommand): Feed
}