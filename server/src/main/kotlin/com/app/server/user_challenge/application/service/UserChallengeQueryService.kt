package com.app.server.user_challenge.application.service

import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.dto.*
import com.app.server.user_challenge.ui.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.ui.usecase.GetUserChallengeReportUseCase
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserChallengeQueryService(
    private val userChallengeService: UserChallengeService
) : GetTotalUserChallengeUseCase, GetUserChallengeReportUseCase {
    fun getChallengeCompletedUserPercent(challengeId: Long): Double {

        val countCompletedUser : Long = userChallengeService.countCompletedUserBy(challengeId)

        val countAllUser : Long = userChallengeService.countAllParticipantUserBy(challengeId)

        return calculatePercentOfChallengeParticipants(countAllUser, countCompletedUser)
    }

    private fun calculatePercentOfChallengeParticipants(countAllUser: Long, countCompletedUser: Long): Double {
        if (countAllUser == 0L) return 0.0

        return String.format("%.2f", countCompletedUser.toDouble() / countAllUser.toDouble()).toDouble()
    }

    override fun execute(userId: Long, todayDate: LocalDate) : GetTotalUserChallengeResponseDto {
        val userTotalChallengeList: List<UserChallenge> = userChallengeService.findAllByUserId(userId)

        val mappedList = userTotalChallengeList.map { mapToUserChallengeQuery(it, todayDate) }

        return GetTotalUserChallengeResponseDto(userChallenges = mappedList)
    }

    override fun getReport(userChallengeId: Long, todayDate: LocalDate): ReportDto? {

        val userChallenge : UserChallenge = userChallengeService.findById(userChallengeId)

        if (isReportReceivedFrom(userChallenge)) return null

        val successDays = userChallenge.getUserChallengeHistories()
            .count { it.status != EUserChallengeCertificationStatus.FAILED }

        val userRank = 1L // TODO: UserChallenge에서 랭킹을 가져오는 로직 추가 필요

        updateUserChallengeStatus(userChallenge, todayDate)

        return ReportDto(
            userChallengeId = userChallenge.id!!,
            totalDays = userChallenge.participantDays,
            userChallengeStatus = userChallenge.status,
            successDays = successDays,
            reportMessage = userChallenge.reportMessage!!,
            maxConsecutiveParticipationDays = userChallenge.maxConsecutiveParticipationDayCount,
            challengeRanking = userRank,
            certificationDataList = mapCertificationReportDataList(userChallenge.getUserChallengeHistories())
        )

    }

    private fun updateUserChallengeStatus(
        userChallenge: UserChallenge,
        todayDate: LocalDate
    ) {
        val endDate : LocalDate = userChallenge.createdAt!!.toLocalDate()
            .plusDays(userChallenge.participantDays-1L)

        if (userChallenge.status == EUserChallengeStatus.PENDING &&
            todayDate.isBefore(endDate.plusDays(2))
            ){
            userChallenge.updateStatus(EUserChallengeStatus.WAITING)
        }
        else if (userChallenge.status == EUserChallengeStatus.PENDING &&
            todayDate.isAfter(endDate.plusDays(1))
            ){
            userChallenge.updateStatus(EUserChallengeStatus.COMPLETED)
        }
    }

    private fun isReportReceivedFrom(userChallenge: UserChallenge): Boolean {
        if (userChallenge.reportMessage == null) {
            return true
        }
        return false
    }

    private fun mapToUserChallengeQuery(userChallenge: UserChallenge, todayDate: LocalDate): UserChallengeQuery {
        val histories = userChallenge.getUserChallengeHistoriesBeforeToday(todayDate)
        val elapsedDays = userChallenge.calculateElapsedDays(todayDate)
        val progress = userChallenge.calculateProgress(todayDate)
        val certificationToday = userChallenge.isCertificatedToday(todayDate)
        val certificationDataList = mapCertificationDataList(histories)

        return UserChallengeQuery(
            id = userChallenge.id!!,
            title = userChallenge.challenge.title,
            category = userChallenge.challenge.challengeCategory.title, // TODO: 캐싱 필요
            status = userChallenge.status.content,
            totalDays = userChallenge.participantDays,
            iceCount = userChallenge.iceCount,
            elapsedDays = elapsedDays,
            progress = progress,
            isCertificatedInToday = certificationToday,
            certificationDataList = certificationDataList
        )
    }

    private fun mapCertificationDataList(histories: List<UserChallengeHistory>): List<CertificationData> {
        return histories.map {
            CertificationData(
                date = it.date,
                isCertificated = it.status.name
            )
        }
    }

    private fun mapCertificationReportDataList(histories: List<UserChallengeHistory>): List<CertificationReportDataDto> {
        return histories.map {
            CertificationReportDataDto(
                date = it.date,
                isCertificated = it.status.name
            )
        }
    }
}