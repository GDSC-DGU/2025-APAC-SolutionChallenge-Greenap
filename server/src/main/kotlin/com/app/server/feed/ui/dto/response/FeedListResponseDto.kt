package com.app.server.feed.ui.dto.response

import com.app.server.feed.domain.model.query.FeedProjection
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class FeedListResponseDto(
    val feedList: List<FeedDto>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
    val totalElements: Long,
    val totalPages: Int
){
    companion object {
        fun fromPage(feedProjectionPage: Page<FeedProjection>): FeedListResponseDto {
            val feedList = feedProjectionPage.content.map { projection ->
                FeedDto(
                    category = projection.challengeCategoryTitle,
                    challengeTitle = projection.challengeTitle,
                    imageUrl = projection.feedImageUrl,
                    content = projection.feedContent,
                    createdAt = projection.createdAt!!,
                    user = FeedUserDto(
                        nickname = projection.userName,
                        profileImageUrl = projection.userProfileImageUrl,
                        burningLevel = projection.userNowMaxConsecutiveParticipationDayCount.toInt()
                    )
                )
            }

            return FeedListResponseDto(
                feedList = feedList,
                page = feedProjectionPage.number + 1,
                size = feedProjectionPage.size,
                hasNext = feedProjectionPage.hasNext(),
                totalElements = feedProjectionPage.totalElements,
                totalPages = feedProjectionPage.totalPages
            )
        }
    }
}

data class FeedDto(
    val category: String,
    val challengeTitle: String,
    val imageUrl: String,
    val content: String?,
    val createdAt: LocalDateTime,
    val user: FeedUserDto
)

data class FeedUserDto(
    val nickname: String,
    val profileImageUrl: String?,
    val burningLevel: Int
)