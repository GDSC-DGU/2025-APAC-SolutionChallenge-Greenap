package com.app.server.feed.application.service

import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.repository.FeedRepository
import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.exception.FeedException
import org.springframework.stereotype.Service

@Service
class FeedService (
    private val feedRepository : FeedRepository
){

    fun saveAndFlush(feed : Feed){
        feedRepository.saveAndFlush(feed)
    }

    fun findById(feedId: Long) : Feed = feedRepository.findById(feedId).orElseThrow {
        throw NotFoundException(FeedException.NOT_FOUND_FEED)
    }

}
