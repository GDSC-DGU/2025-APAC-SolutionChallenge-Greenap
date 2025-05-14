package com.app.server.feed.domain.model.command

import com.app.server.common.model.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

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

    var content: String?,

    @Column(name = "publish_date", nullable = false)
    val publishDate: LocalDateTime

) : BaseEntity() {

    private constructor(
        userId: Long,
        userChallengeId: Long,
        imageUrl: String,
        content: String?,
        publishDate: LocalDateTime?
    ) : this(
        id = null,
        userId = userId,
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
        content = content,
        publishDate = publishDate ?: LocalDateTime.now()
    )

    companion object{
        fun createEntity(
            userId: Long,
            userChallengeId: Long,
            imageUrl: String,
            content: String?,
            publishDate: LocalDateTime
        ): Feed {
            return Feed(
                userId = userId,
                userChallengeId = userChallengeId,
                imageUrl = imageUrl,
                content = content,
                publishDate = publishDate
            )
        }
    }

    fun updateContent(newContent: String?) {
        this.content = newContent
    }
}