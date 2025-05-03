package com.app.server.feed.application.service

import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.event.FeedDeletedEvent
import com.app.server.feed.domain.event.FeedModifiedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FeedEventListener (
    private val feedProjectionCommandService: FeedProjectionCommandService
){

    @Async
    @EventListener
    fun handleCreatedFeedEvent(createdEvent: FeedCreatedEvent) : FeedProjection {
        return feedProjectionCommandService.createReadOnlyFeed(
            authorUserId = createdEvent.userId,
            feed = createdEvent.feed,
            relatedUserChallengeId = createdEvent.userChallengeId
        )
    }

    @Async
    @EventListener
    fun handleDeletedFeedEvent(deletedEvent: FeedDeletedEvent) : FeedProjection {
        return feedProjectionCommandService.deleteReadOnlyFeed(
            feed = deletedEvent.feed,
        )
    }

    @Async
    @EventListener
    fun handleModifiedFeedEvent(modifiedEvent: FeedModifiedEvent) : FeedProjection {
        return feedProjectionCommandService.updateReadOnlyFeed(
            feed = modifiedEvent.feed
        )
    }
}