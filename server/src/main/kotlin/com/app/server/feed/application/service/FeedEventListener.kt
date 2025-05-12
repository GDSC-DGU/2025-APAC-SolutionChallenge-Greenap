package com.app.server.feed.application.service

import com.app.server.feed.application.service.command.FeedProjectionCommandService
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.event.FeedDeletedEvent
import com.app.server.feed.domain.event.FeedModifiedEvent
import com.app.server.feed.domain.model.query.FeedProjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FeedEventListener(
    private val feedProjectionCommandService: FeedProjectionCommandService
) {

    @EventListener
    fun handleCreatedFeedEvent(createdEvent: FeedCreatedEvent) {
        try {
            CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
                createdFeedProjectionFrom(createdEvent)
            }
        }
        catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    suspend fun createdFeedProjectionFrom(createdEvent: FeedCreatedEvent): FeedProjection {
        return feedProjectionCommandService.createReadOnlyFeed(
            authorUserId = createdEvent.userId,
            feed = createdEvent.feed,
            relatedUserChallengeId = createdEvent.userChallengeId
        )
    }

    @EventListener
    fun handleDeletedFeedEvent(deletedEvent: FeedDeletedEvent) {
        try {
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                feedProjectionCommandService.deleteReadOnlyFeed(
                    feed = deletedEvent.feed,
                )
            }
        }
        catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    @EventListener
    fun handleModifiedFeedEvent(modifiedEvent: FeedModifiedEvent) {
        try {
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                feedProjectionCommandService.updateReadOnlyFeed(
                    feed = modifiedEvent.feed
                )
            }
        }
        catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }
}