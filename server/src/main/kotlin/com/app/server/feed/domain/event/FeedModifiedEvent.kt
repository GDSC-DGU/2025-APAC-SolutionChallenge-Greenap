package com.app.server.feed.domain.event

import com.app.server.feed.domain.model.command.Feed

data class FeedModifiedEvent(
    val feed : Feed
)