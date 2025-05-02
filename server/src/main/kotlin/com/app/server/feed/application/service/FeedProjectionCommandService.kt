package com.app.server.feed.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.repository.FeedProjectionRepository
import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.exception.FeedException
import com.app.server.user.application.service.UserService
import com.app.server.user_challenge.application.service.UserChallengeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedProjectionCommandService (
    private val userService: UserService,
    private val userChallengeService: UserChallengeService,
    private val challengeService: ChallengeService,
    private val feedProjectionRepository: FeedProjectionRepository,
    private val feedProjectionService: FeedProjectionService,
){

    fun createReadOnlyFeed(
        feed : Feed,
        relatedUserChallengeId: Long,
        authorUserId: Long
    ) : FeedProjection {
        val user = userService.findById(authorUserId)

        val participantChallengeTitle = userChallengeService.findById(relatedUserChallengeId).challenge.title
        val challengeCategoryName = challengeService.findByTitle(participantChallengeTitle).challengeCategory.title

        val feedProjection : FeedProjection = createFeedProjectionEntity(
            feed = feed,
            challengeTitle = participantChallengeTitle,
            challengeCategoryName = challengeCategoryName,
            userName = user.nickname,
            userProfileImageUrl = user.profileImageUrl,
            userNowConsecutiveDays = user.nowMaxConsecutiveParticipationDayCount
        )

        return feedProjectionService.save(feedProjection)
    }

    private fun createFeedProjectionEntity(
        feed : Feed,
        challengeTitle : String,
        challengeCategoryName : String,
        userName : String,
        userProfileImageUrl : String?,
        userNowConsecutiveDays : Long,
    ) : FeedProjection {
        return FeedProjection.createEntity(
            feed = feed,
            challengeTitle = challengeTitle,
            challengeCategoryTitle = challengeCategoryName,
            userName = userName,
            userProfileImageUrl = userProfileImageUrl,
            userNowMaxConsecutiveParticipationDayCount = userNowConsecutiveDays
        )
    }

    fun updateReadOnlyFeed(feed: Feed) : FeedProjection {
        val feedProjection = feedProjectionService.findById(feed.id!!)

        feedProjection.updateContent(
            content = feed.content
        )

        return feedProjection
    }

    fun deleteReadOnlyFeed(feed: Feed): FeedProjection {
        val feedProjection = feedProjectionRepository.findById(feed.id!!).orElseThrow {
            throw NotFoundException(FeedException.NOT_FOUND_FEED_PROJECTION)
        }

        feedProjection.delete()

        return feedProjection
    }

}