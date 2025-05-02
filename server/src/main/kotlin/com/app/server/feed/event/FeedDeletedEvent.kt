package com.app.server.feed.event

import com.app.server.feed.domain.model.command.Feed

data class FeedDeletedEvent(
    val feed : Feed
)
