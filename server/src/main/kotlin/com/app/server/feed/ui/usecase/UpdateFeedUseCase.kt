package com.app.server.feed.ui.usecase

import com.app.server.feed.domain.model.command.Feed

interface UpdateFeedUseCase {
    fun execute(feedId : Long, feedContent : String?) : Feed
}