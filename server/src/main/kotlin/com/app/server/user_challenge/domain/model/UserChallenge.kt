package com.app.server.user_challenge.domain.model

import com.app.server.challenge.domain.model.Challenge
import com.app.server.common.model.BaseEntity
import com.app.server.user.domain.model.User
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@Table(name = "user_challenges")
@DynamicUpdate
class UserChallenge(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_challenge_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    val challenge: Challenge,

    val status: EUserChallengeStatus = EUserChallengeStatus.RUNNING,
    @Column(name = "participant_days", nullable = false)
    val participantDays: Int,
    @Column(name = "ice_count")
    val iceCount: Int = 0,
    @Column(name = "now_consecutive_participation_day_count", nullable = false)
    val nowConsecutiveParticipationDayCount: Long = 0L,
    @Column(name = "max_consecutive_participation_day_count", nullable = false)
    val maxConsecutiveParticipationDayCount: Long = 0L,
    @Column(name = "total_participation_day_count", nullable = false)
    val totalParticipationDayCount: Long,
    @Column(name = "report_message")
    val reportMessage: String?

) : BaseEntity() {
}