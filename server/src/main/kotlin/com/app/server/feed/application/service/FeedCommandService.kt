package com.app.server.feed.application.service

import com.app.server.common.exception.BadRequestException
import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.event.FeedDeletedEvent
import com.app.server.feed.domain.event.FeedModifiedEvent
import com.app.server.feed.exception.FeedException
import com.app.server.feed.ui.dto.CreateFeedCommand
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.DeleteFeedUseCase
import com.app.server.feed.ui.usecase.UpdateFeedUseCase
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class FeedCommandService(
    private val feedService: FeedService,
    private val userChallengeService: UserChallengeService,
    private val eventPublisher: ApplicationEventPublisher,
) : CreateFeedUseCase, UpdateFeedUseCase, DeleteFeedUseCase {
    override fun execute(createFeedCommand: CreateFeedCommand): Feed {

        val userChallengeId : Long = createFeedCommand.userChallengeId
        val createFeedDate : LocalDate = createFeedCommand.publishDate
        val feedImageUrl : String = createFeedCommand.imageUrl
        val feedContent : String? = createFeedCommand.content

        // 인증 여부 확인
        if (
            userChallengeService.isCertificatedWhen(userChallengeId, createFeedDate)
             != EUserChallengeCertificationStatus.SUCCESS
        ) {
            throw BadRequestException(FeedException.NOT_CERTIFICATED_THIS_USER_CHALLENGE)
        }
        // 사진 일치 여부 확인
        if (userChallengeService.getUserChallengeImageUrl(userChallengeId, createFeedDate)
            != feedImageUrl){
            throw BadRequestException(FeedException.NOT_MATCHED_IMAGE_URL)
        }
        // content 확인
        if (feedContent != null && feedContent.length > 1000) {
            throw BadRequestException(FeedException.CONTENT_OVER_LIMIT)
        }
        // 피드 생성
        val feed : Feed = createFeed(createFeedCommand)

        feedService.saveAndFlush(feed)

        // 피드 생성 이벤트 발행
        eventPublisher.publishEvent(FeedCreatedEvent.fromEntity(feed))
        return feed
    }

    private fun createFeed(
        createFeedCommand: CreateFeedCommand
    ) : Feed {
        return Feed.createEntity(
            userId = createFeedCommand.userId,
            userChallengeId = createFeedCommand.userChallengeId,
            imageUrl = createFeedCommand.imageUrl,
            content = createFeedCommand.content
        )
    }

    // DELETE
    override fun execute(feedId: Long)  {
        val feed = feedService.findById(feedId)
        feed.delete()
        feedService.saveAndFlush(feed)
        eventPublisher.publishEvent(
            FeedDeletedEvent(
                feed = feed
            )
        )
    }

    // UPDATE
    override fun execute(feedId: Long, feedContent: String?): Feed {
        val feed = feedService.findById(feedId)
        if (feed.content == feedContent) {
            throw BadRequestException(FeedException.NOT_MODIFIED_CONTENT)
        }
        else if (feedContent != null && feedContent.length > 1000) {
            throw BadRequestException(FeedException.CONTENT_OVER_LIMIT)
        }

        feed.updateContent(feedContent)

        feedService.saveAndFlush(feed)

        eventPublisher.publishEvent(
            FeedModifiedEvent(
                feed = feed
        ))
        return feed
    }
}