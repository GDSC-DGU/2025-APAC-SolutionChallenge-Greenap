package com.app.server.user_challenge.domain.model

import com.app.server.challenge.domain.model.Challenge
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.BusinessException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.common.model.BaseEntity
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
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

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @OneToMany(mappedBy = "userChallenge", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    private val userChallengeHistories: MutableList<UserChallengeHistory> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    val challenge: Challenge,

    @Enumerated(EnumType.STRING)
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

    private constructor(userId: Long, challenge: Challenge, participantDays: Int, status: EUserChallengeStatus) : this(
        id = null,
        userId = userId,
        challenge = challenge,
        participantDays = participantDays,
        iceCount = 0,
        status = status,
        nowConsecutiveParticipationDayCount = 0,
        maxConsecutiveParticipationDayCount = 0,
        reportMessage = null,
        totalParticipationDayCount = 0,
        userChallengeHistories = mutableListOf()
    )

    companion object {
        fun createEntity(createUserChallengeDto: CreateUserChallengeDto): UserChallenge {
            return UserChallenge(
                userId = createUserChallengeDto.userId,
                challenge = createUserChallengeDto.challenge,
                participantDays = createUserChallengeDto.participantsDate,
                status = createUserChallengeDto.status
            )
        }
    }

    fun validateCanParticipants() {

        when(this.status){
            EUserChallengeStatus.COMPLETED -> {}
            EUserChallengeStatus.RUNNING -> throw BadRequestException(UserChallengeException.ALREADY_PARTICIPATED_AND_STATUS_IS_RUNNING)
            EUserChallengeStatus.PENDING -> throw BadRequestException(UserChallengeException.CHALLENGE_WAITED_AND_STATUS_IS_PENDING)
            EUserChallengeStatus.WAITING -> throw BusinessException(UserChallengeException.CONTINUE_CHALLENGE_AND_STATUS_IS_WAITING)
            EUserChallengeStatus.DEAD -> throw InternalServerErrorException(UserChallengeException.REPORT_NOT_FOUND_AND_STATUS_IS_DEAD)
            else -> throw InternalServerErrorException(UserChallengeException.CANNOT_PARTICIPATE)
        }
    }
    
    fun getUserChallengeHistories() : List<UserChallengeHistory> {
        return this.userChallengeHistories
    }
    
    fun addUserChallengeHistories(userChallengeHistories: List<UserChallengeHistory>) {
        this.userChallengeHistories.addAll(userChallengeHistories)
    }

}