package com.app.server.feed.ui.usecase

interface DeleteFeedUseCase {
    fun execute(feedId: Long)
}