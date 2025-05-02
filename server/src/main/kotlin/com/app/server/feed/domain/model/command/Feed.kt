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
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId : Long,

    @Column(name = "user_challenge_id", nullable = false)
    val userChallengeId : Long,

    @Column(name = "image_url")
    val imageUrl: String,

    var content: String?

) : BaseEntity() {

    private constructor(
        userId: Long,
        userChallengeId: Long,
        imageUrl: String,
        content: String?
    ) : this(
        id = null,
        userId = userId,
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
        content = content
    )

    companion object{
        fun createEntity(
            userId: Long,
            userChallengeId: Long,
            imageUrl: String,
            content: String?
        ): Feed {
            return Feed(
                userId = userId,
                userChallengeId = userChallengeId,
                imageUrl = imageUrl,
                content = content
            )
        }
    }

    fun updateContent(newContent: String?) {
        this.content = newContent
    }
}