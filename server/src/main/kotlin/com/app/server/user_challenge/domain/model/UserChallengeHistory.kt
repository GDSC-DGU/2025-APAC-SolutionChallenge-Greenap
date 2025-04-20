package com.app.server.user_challenge.domain.model

import com.app.server.common.model.BaseEntity
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user_challenge_histories")
class UserChallengeHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_challenge_history_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_challenge_id", nullable = false)
    val userChallenge: UserChallenge,

    val date: LocalDate,

    val status: EUserChallengeCertificationStatus = EUserChallengeCertificationStatus.FAILED,

    @Column(name = "certificated_image_url")
    val certificatedImageUrl: String?

) : BaseEntity() {
}