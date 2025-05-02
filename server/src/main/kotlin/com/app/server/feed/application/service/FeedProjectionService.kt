package com.app.server.feed.application.service

import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.repository.FeedProjectionRepository
import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.exception.FeedException
import org.springframework.stereotype.Service

@Service
class FeedProjectionService (
    private val feedProjectionRepository: FeedProjectionRepository
){

    fun findById(feedId: Long) : FeedProjection = feedProjectionRepository.findById(feedId).orElseThrow{
        throw NotFoundException(FeedException.NOT_FOUND_FEED_PROJECTION)
    }
}