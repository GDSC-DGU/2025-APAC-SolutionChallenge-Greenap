package com.app.server.user_challenge.application.service

import com.app.server.user_challenge.application.usecase.GetTotalUserChallengeUseCase
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.dto.CertificationData
import com.app.server.user_challenge.ui.dto.GetTotalUserChallengeResponseDto
import com.app.server.user_challenge.ui.dto.UserChallengeQuery
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserChallengeQueryService(
    private val userChallengeService: UserChallengeService
) : GetTotalUserChallengeUseCase {
    fun getChallengeCompletedUserPercent(challengeId: Long): Double {

        val countCompletedUser : Long = userChallengeService.countCompletedUserBy(challengeId)

        val countAllUser : Long = userChallengeService.countAllParticipantUserBy(challengeId)

        return calculatePercentOfChallengeParticipants(countAllUser, countCompletedUser)
    }

    private fun calculatePercentOfChallengeParticipants(countAllUser: Long, countCompletedUser: Long): Double {
        if (countAllUser == 0L) return 0.0

        return String.format("%.2f", countCompletedUser.toDouble() / countAllUser.toDouble()).toDouble()
    }

    override fun execute(userId: Long): GetTotalUserChallengeResponseDto {
        val todayDate = LocalDate.now()
        val userTotalChallengeList: List<UserChallenge> = userChallengeService.findAllByUserId(userId)

        val mappedList = userTotalChallengeList.map { mapToUserChallengeQuery(it, todayDate) }

        return GetTotalUserChallengeResponseDto(userChallenges = mappedList)
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
}