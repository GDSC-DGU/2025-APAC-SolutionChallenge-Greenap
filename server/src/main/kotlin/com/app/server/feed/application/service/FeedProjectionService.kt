package com.app.server.feed.application.service

import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.repository.FeedProjectionRepository
import com.app.server.feed.application.repository.FeedRepository
import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.exception.FeedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class FeedProjectionService (
    private val feedProjectionRepository: FeedProjectionRepository,
){

    fun save(feedProjection: FeedProjection) : FeedProjection {
        return feedProjectionRepository.save(feedProjection)
    }

    fun findById(feedId: Long) : FeedProjection = feedProjectionRepository.findById(feedId).orElseThrow{
        throw NotFoundException(FeedException.NOT_FOUND_FEED_PROJECTION)
    }

    fun getAllFeed(pageable: PageRequest): Page<FeedProjection> {
        return feedProjectionRepository.findAll(pageable)
    }
    fun getAllCategoryFeed(categoryName: String, pageable: PageRequest): Page<FeedProjection> {
        return feedProjectionRepository.findAllByCategoryName(categoryName, pageable)
    }
    fun getUserCategoryFeed(userId: Long, categoryName: String, pageable: PageRequest): Page<FeedProjection> {
        return feedProjectionRepository.findByUserIdAndCategoryName(userId, categoryName, pageable)
    }
    fun getSpecificUserChallengeFeed(
        feedIds : List<Long>,
        pageable: PageRequest
    ): Page<FeedProjection> {
        return feedProjectionRepository.findByIdIn(
            feedIds = feedIds,
            pageable = pageable
        )
    }

    fun deleteAll(){
        feedProjectionRepository.deleteAll()
    }
}