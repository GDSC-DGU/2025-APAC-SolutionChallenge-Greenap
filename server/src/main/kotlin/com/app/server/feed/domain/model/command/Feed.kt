package com.app.server.feed.domain.model.command

import com.app.server.common.model.BaseEntity
import com.app.server.user.domain.model.User
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.persistence.*

@Entity
@Table(name = "feeds")
class Feed (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_challenge_id", nullable = false)
    val userChallenge: UserChallenge,

    val image_url: String,
    val content: String?

) : BaseEntity() {
}