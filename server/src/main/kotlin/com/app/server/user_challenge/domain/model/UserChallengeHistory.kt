package com.app.server.user_challenge.domain.model

import com.app.server.common.exception.BadRequestException
import com.app.server.common.model.BaseEntity
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "user_challenge_histories",
    indexes = [Index(
        name = "idx_user_challenge_history_id_user_challenge_id",
        columnList = "user_challenge_history_id, user_challenge_id",
        unique = true
    )]
)
class UserChallengeHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_challenge_history_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_challenge_id", nullable = false)
    val userChallenge: UserChallenge,

    val date: LocalDate,

    @Enumerated(EnumType.STRING)
    var status: EUserChallengeCertificationStatus = EUserChallengeCertificationStatus.FAILED,

    @Column(name = "certificated_image_url")
    var certificatedImageUrl: String?

) : BaseEntity() {

    fun updateStatus(status: EUserChallengeCertificationStatus) {
        if (this.status == status) {
            throw BadRequestException(UserChallengeException.ALREADY_CERTIFICATED)
        }
        this.status = status
    }

    fun updateCertificatedImageUrl(certificatedImageUrl: String) {
        this.certificatedImageUrl = certificatedImageUrl
    }
}