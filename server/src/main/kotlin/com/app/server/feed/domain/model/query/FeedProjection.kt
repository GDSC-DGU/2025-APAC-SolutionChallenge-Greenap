package com.app.server.feed.domain.model.query

import com.app.server.common.model.BaseEntity
import com.app.server.feed.domain.model.command.Feed
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "feed_projections", indexes = [
    Index(
        name = "idx_feed_projections_feed_id",
        columnList = "feed_id",
        unique = true
    )
])
class FeedProjection (

    @Id
    @Column(name = "feed_id")
    val id: Long? = null,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "feed_id", nullable = false)
    val feed: Feed,

    @Column(name = "challenge_category_title")
    val challengeCategoryTitle: String,
    @Column(name = "challenge_title")
    val challengeTitle: String,
    @Column(name = "user_id")
    val userId: Long,
    @Column(name = "user_name")
    val userName: String,
    @Column(name = "user_profile_image_url")
    val userProfileImageUrl: String?,
    @Column(name = "user_now_max_consecutive_participation_day_count")
    val userNowMaxConsecutiveParticipationDayCount: Long,
    @Column(name = "feed_image_url")
    val feedImageUrl: String,
    @Column(name = "feed_content")
    var feedContent: String?,
    @Column(name = "publish_date")
    val publishDate: LocalDateTime

    ) : BaseEntity(){

    companion object {
        fun createEntity(
            feed : Feed,
            challengeCategoryTitle: String,
            challengeTitle: String,
            userName: String,
            userProfileImageUrl: String?,
            userNowMaxConsecutiveParticipationDayCount: Long,
        ): FeedProjection {
            return FeedProjection(
                feed = feed,
                challengeCategoryTitle = challengeCategoryTitle,
                challengeTitle = challengeTitle,
                userId = feed.userId,
                userName = userName,
                userProfileImageUrl = userProfileImageUrl,
                userNowMaxConsecutiveParticipationDayCount = userNowMaxConsecutiveParticipationDayCount,
                feedImageUrl = feed.imageUrl,
                feedContent = feed.content,
                publishDate = feed.publishDate
            )
        }
    }

    fun updateContent(content: String?) {
        this.feedContent = content
    }
}