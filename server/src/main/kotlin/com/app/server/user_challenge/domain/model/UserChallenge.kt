package com.app.server.user_challenge.domain.model

import com.app.server.challenge.domain.model.Challenge
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.BusinessException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.common.model.BaseEntity
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate

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
    var status: EUserChallengeStatus = EUserChallengeStatus.RUNNING,
    @Column(name = "participant_days", nullable = false)
    val participantDays: Int,
    @Column(name = "ice_count")
    var iceCount: Int = 0,
    @Column(name = "now_consecutive_participation_day_count", nullable = false)
    var nowConsecutiveParticipationDayCount: Long = 0L,
    @Column(name = "max_consecutive_participation_day_count", nullable = false)
    var maxConsecutiveParticipationDayCount: Long = 0L,
    @Column(name = "total_participation_day_count", nullable = false)
    var totalParticipationDayCount: Long = 0L,
    @Column(name = "report_message")
    var reportMessage: String?

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

        when (this.status) {
            EUserChallengeStatus.COMPLETED -> {}
            EUserChallengeStatus.RUNNING -> throw BadRequestException(UserChallengeException.ALREADY_PARTICIPATED_AND_STATUS_IS_RUNNING)
            EUserChallengeStatus.PENDING -> throw BadRequestException(UserChallengeException.CHALLENGE_WAITED_AND_STATUS_IS_PENDING)
            EUserChallengeStatus.WAITING -> throw BusinessException(UserChallengeException.CONTINUE_CHALLENGE_AND_STATUS_IS_WAITING)
            EUserChallengeStatus.DEAD -> throw InternalServerErrorException(UserChallengeException.REPORT_NOT_FOUND_AND_STATUS_IS_DEAD)
        }
    }

    fun getUserChallengeHistories(): List<UserChallengeHistory> {
        return this.userChallengeHistories
    }

    fun addUserChallengeHistories(userChallengeHistories: List<UserChallengeHistory>) {
        this.userChallengeHistories.addAll(userChallengeHistories)
    }

    fun updateCertificationStateIsSuccess(certificationDate: LocalDate, certificatedImageUrl: String) {
        val userChallengeHistory: UserChallengeHistory? = getUserChallengeHistoryWhen(certificationDate)

        userChallengeHistory!!.updateStatus(status = EUserChallengeCertificationStatus.SUCCESS)
        userChallengeHistory.updateCertificatedImageUrl(certificatedImageUrl = certificatedImageUrl)
    }

    fun increaseTotalParticipatedDays(certificationDate: LocalDate) {
        val userChallengeHistory: UserChallengeHistory? = getUserChallengeHistoryWhen(certificationDate)

        validateUpdateTotalParticipatedDaysAndUpdate(userChallengeHistory!!)
    }

    private fun validateUpdateTotalParticipatedDaysAndUpdate(userChallengeHistory: UserChallengeHistory) {
        // 얼리기는 전체 참여 일수에 포함되지 않음
        if (userChallengeHistory.status == EUserChallengeCertificationStatus.SUCCESS) {
            this.totalParticipationDayCount += 1
        } else {
            throw BadRequestException(UserChallengeException.ALREADY_CERTIFICATED)
        }
    }

    fun getUserChallengeHistoryWhen(certificationDate: LocalDate): UserChallengeHistory? {
        val userChallengeHistory: UserChallengeHistory =
            this.userChallengeHistories.find { it.date.isEqual(certificationDate) }
                ?: return null
        return userChallengeHistory
    }

    fun validateCanIceAndUse(): Boolean {
        if (this.iceCount > 0) {
            this.iceCount -= 1
            return true
        }
        throw BadRequestException(UserChallengeException.CANNOT_USE_ICE)
    }

    fun validateIncreaseIceCount(): Int {
        // 얼리기 조건 확인 후 얼리기 가능 여부 판단
        if (this.totalParticipationDayCount > (this.participantDays.floorDiv(2))) {
            this.iceCount += 1
        }
        return this.iceCount
    }

    fun updateCertificationStateIsIce(certificationDate: LocalDate) {
        val userChallengeHistory: UserChallengeHistory? = getUserChallengeHistoryWhen(certificationDate)

        userChallengeHistory!!.updateStatus(status = EUserChallengeCertificationStatus.ICE)
    }

    fun updateReportMessage(reportMessage: String) {
        this.reportMessage = reportMessage
    }

    fun updateStatus(status: EUserChallengeStatus) {
        this.status = status
    }

    fun updateNowConsecutiveParticipationDayCount(day: Long) {
        this.nowConsecutiveParticipationDayCount = day
    }

    fun updateMaxConsecutiveParticipationDayCount(day: Long) {
        this.maxConsecutiveParticipationDayCount = day
    }

    fun calculateElapsedDays(todayDate: LocalDate): Long {
        return todayDate.toEpochDay() - userChallengeHistories.first().date.toEpochDay()
    }

    fun calculateProgress(todayDate: LocalDate): Int {
        return ((calculateElapsedDays(todayDate).toDouble() / totalParticipationDayCount) * 100).toInt()
            .coerceIn(0, 100)
    }

    fun isCertificatedToday(todayDate: LocalDate): Boolean {
        return userChallengeHistories.find { it.date.isEqual(todayDate) }
            ?.status!! != EUserChallengeCertificationStatus.FAILED
    }

    fun getUserChallengeHistoriesBeforeToday(todayDate: LocalDate): List<UserChallengeHistory> {
        return userChallengeHistories.filter { it.date.isBefore(todayDate.plusDays(1)) }
    }

    fun checkIsDone(todayDate: LocalDate): Boolean {
        // status가 running인데, 오늘 날짜와 챌린지 종료 날짜가 같은 상황에서 인증이 완료되었거나, 챌린지 종료 날짜가 지난 경우
        val endDate = this.createdAt!!.toLocalDate().plusDays(participantDays.toLong())

        return this.status == EUserChallengeStatus.RUNNING &&
                (
                        todayDate.isAfter(endDate) ||
                                (todayDate.isEqual(endDate) &&
                                        userChallengeHistories.last().status != EUserChallengeCertificationStatus.FAILED)
                        )
    }

    fun getSuccessDayCount() : Int {
        return this.userChallengeHistories
            .count { it.status != EUserChallengeCertificationStatus.FAILED }
    }

}