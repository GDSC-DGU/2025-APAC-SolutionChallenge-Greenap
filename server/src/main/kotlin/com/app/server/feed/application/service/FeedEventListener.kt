package com.app.server.feed.application.service

import com.app.server.feed.application.service.command.FeedProjectionCommandService
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.event.FeedDeletedEvent
import com.app.server.feed.domain.event.FeedModifiedEvent
import com.app.server.feed.domain.model.query.FeedProjection
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FeedEventListener(
    private val feedProjectionCommandService: FeedProjectionCommandService
) {

    @EventListener
    fun handleCreatedFeedEvent(createdEvent: FeedCreatedEvent) {
        createdFeedProjectionFrom(createdEvent)
    }

    fun createdFeedProjectionFrom(createdEvent: FeedCreatedEvent): FeedProjection {
        return feedProjectionCommandService.createReadOnlyFeed(
            authorUserId = createdEvent.userId,
            feed = createdEvent.feed,
            relatedUserChallengeId = createdEvent.userChallengeId
        )
    }

    @EventListener
    fun handleDeletedFeedEvent(deletedEvent: FeedDeletedEvent) {
        feedProjectionCommandService.deleteReadOnlyFeed(deletedEvent.feed)
    }

    @EventListener
    fun handleModifiedFeedEvent(modifiedEvent: FeedModifiedEvent) {
       feedProjectionCommandService.updateReadOnlyFeed(modifiedEvent.feed)
    }
}