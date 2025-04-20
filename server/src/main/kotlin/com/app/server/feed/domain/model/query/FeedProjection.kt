package com.app.server.feed.domain.model.query

import com.app.server.common.model.BaseEntity
import com.app.server.feed.domain.model.command.Feed
import jakarta.persistence.*

@Entity
@Table(name = "feed_projections")
class FeedProjection (

    @Id
    @Column(name = "feed_id")
    val id: Long,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    val feed: Feed,

    @Column(name = "challenge_category_title")
    val challengeCategoryTitle: String,
    @Column(name = "challenge_title")
    val challengeTitle: String,
    @Column(name = "user_name")
    val userName: String,
    @Column(name = "user_profile_image_url")
    val userProfileImageUrl: String?,
    @Column(name = "user_now_max_consecutive_participation_day_count")
    val userNowMaxConsecutiveParticipationDayCount: Long,
    @Column(name = "feed_image_url")
    val feedImageUrl: String,
    @Column(name = "feed_content")
    val feedContent: String?,

    ) : BaseEntity(){
}