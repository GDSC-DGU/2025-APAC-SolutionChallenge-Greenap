package com.app.server.feed.application.service.query

import com.app.server.challenge.application.service.ChallengeCategoryService
import com.app.server.common.exception.BadRequestException
import com.app.server.feed.application.service.FeedProjectionService
import com.app.server.feed.application.service.FeedService
import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.enums.EFeedReadRequestState
import com.app.server.feed.enums.EFeedScope
import com.app.server.feed.exception.FeedException
import com.app.server.feed.ui.dto.response.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedProjectionQueryServiceImpl (
    private val feedProjectionService: FeedProjectionService,
    private val feedService: FeedService,
    private val challengeCategoryService: ChallengeCategoryService
) : FeedProjectionQueryService {

    override fun execute(readFeedProjectionQuery: ReadFeedProjectionQuery) : FeedListResponseDto {
        val categoryId = readFeedProjectionQuery.categoryId
        val scope = readFeedProjectionQuery.scope
        val userChallengeId = readFeedProjectionQuery.userChallengeId

        val requestType : EFeedReadRequestState = checkWhichRequestOfType(categoryId, scope, userChallengeId)

        val pageable = PageRequest.of(
            readFeedProjectionQuery.page!! - 1,
            readFeedProjectionQuery.size!!
        )

        val feedPage : Page<FeedProjection> =
            getFeedLists(requestType, readFeedProjectionQuery, categoryId, userChallengeId, pageable)


        return FeedListResponseDto.fromPage(
            feedProjectionPage = feedPage
        )
    }

    private fun getFeedLists(
        requestType: EFeedReadRequestState,
        readFeedProjectionQuery: ReadFeedProjectionQuery,
        categoryId: Long?,
        userChallengeId: Long?,
        pageable: PageRequest
    ): Page<FeedProjection> = when (requestType) {
        EFeedReadRequestState.ALL_FEED -> feedProjectionService.getAllFeed(
            pageable = pageable
        )

        EFeedReadRequestState.ALL_CATEGORY_FEED -> {
            val categoryName = challengeCategoryService.getNameByCategoryId(
                challengeCategoryId = categoryId!!
            )
            feedProjectionService.getAllCategoryFeed(
                categoryName = categoryName,
                pageable = pageable
            )
        }

        EFeedReadRequestState.USER_CATEGORY_FEED -> {
            val categoryName = challengeCategoryService.getNameByCategoryId(
                challengeCategoryId = categoryId!!
            )
            feedProjectionService.getUserCategoryFeed(
                userId = readFeedProjectionQuery.userId,
                categoryName = categoryName,
                pageable = pageable
            )
        }

        EFeedReadRequestState.SPECIFIC_USER_CHALLENGE_FEED -> {
            val feedIds : List<Long> = feedService.findAllIdsByUserChallengeId(
                userChallengeId = userChallengeId!!
            )

            feedProjectionService.getSpecificUserChallengeFeed(
                feedIds = feedIds,
                pageable = pageable
            )
        }
    }

    private fun checkWhichRequestOfType(
        categoryName: Long?,
        scope: EFeedScope,
        userChallengeId: Long?
    ): EFeedReadRequestState {
        return if (categoryName != null && scope == EFeedScope.ALL && userChallengeId == null) {
            EFeedReadRequestState.ALL_CATEGORY_FEED
        } else if (categoryName != null && scope == EFeedScope.USER && userChallengeId == null) {
            EFeedReadRequestState.USER_CATEGORY_FEED
        } else if (categoryName == null && scope == EFeedScope.USER && userChallengeId != null) {
            EFeedReadRequestState.SPECIFIC_USER_CHALLENGE_FEED
        } else if (categoryName == null && scope == EFeedScope.ALL && userChallengeId == null) {
            EFeedReadRequestState.ALL_FEED
        } else {
            throw BadRequestException(FeedException.INVALID_GET_FEED_REQUEST)
        }
    }
}