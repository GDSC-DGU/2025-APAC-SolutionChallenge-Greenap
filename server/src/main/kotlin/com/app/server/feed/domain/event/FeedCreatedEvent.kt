package com.app.server.feed.domain.event

import com.app.server.feed.domain.model.command.Feed

data class FeedCreatedEvent(
    val feed : Feed,
    val userId: Long,
    val userChallengeId: Long
) {

    companion object {
        fun fromEntity(
            feed: Feed
        ): FeedCreatedEvent {
            return FeedCreatedEvent(
                feed = feed,
                userId = feed.userId,
                userChallengeId = feed.userChallengeId
            )
        }
    }
}
