package com.app.server.feed.application.service.query

import com.app.server.feed.ui.dto.response.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionQuery

interface FeedProjectionQueryService {

    fun execute(readFeedProjectionQuery: ReadFeedProjectionQuery): FeedListResponseDto
}