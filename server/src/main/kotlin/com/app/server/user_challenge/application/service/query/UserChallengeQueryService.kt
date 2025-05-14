package com.app.server.user_challenge.application.service.query

import com.app.server.common.exception.BadRequestException
import com.app.server.rank.application.service.RankService
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.application.service.command.UserChallengeCommandService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.dto.response.*
import com.app.server.user_challenge.ui.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.ui.usecase.GetUserChallengeReportUseCase
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserChallengeQueryService(
    private val userChallengeService: UserChallengeService,
    private val userChallengeCommandService: UserChallengeCommandService,
    private val rankService: RankService
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

    override fun getReport(userChallengeId: Long, todayDate: LocalDate): ReportDto {

        val userChallenge : UserChallenge = userChallengeService.findById(userChallengeId)

        if (isReportReceivedFrom(userChallenge))
            throw BadRequestException(UserChallengeException.REPORT_NOT_FOUND_AND_STATUS_IS_DEAD)

        val successDays = userChallenge.getUserChallengeHistories()
            .count { it.status != EUserChallengeCertificationStatus.FAILED }

        val key = "rank:challenge:${userChallenge.challenge.id}"
        val value = userChallenge.userId

        val userRank = rankService.getRank(key, value)

        userChallengeCommandService.updateUserChallengeStatus(userChallenge, todayDate)

        return ReportDto(
            userChallengeId = userChallenge.id!!,
            totalDays = userChallenge.participantDays,
            userChallengeStatus = userChallenge.status,
            successDays = successDays,
            reportMessage = userChallenge.reportMessage!!,
            maxConsecutiveParticipationDays = userChallenge.maxConsecutiveParticipationDayCount,
            challengeRanking = userRank + 1,
            certificationDataList = mapCertificationReportDataList(userChallenge.getUserChallengeHistories())
        )

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
        val progress = userChallenge.calculateProgressFromElapsedDays(todayDate)
        val certificationToday = userChallenge.isCertificatedToday(todayDate)
        val certificationDataList = mapCertificationDataList(histories)

        return UserChallengeQuery(
            id = userChallenge.id!!,
            challengeId = userChallenge.challenge.id!!,
            title = userChallenge.challenge.title,
            category = userChallenge.challenge.challengeCategory.title, // TODO: 캐싱 필요
            status = userChallenge.status.content,
            totalDays = userChallenge.participantDays,
            iceCount = userChallenge.iceCount,
            elapsedDays = elapsedDays,
            progress = progress,
            isCertificatedInToday = certificationToday,
            mainImageUrl = userChallenge.challenge.mainImageUrl,
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