package com.app.server.user_challenge.application.service

import com.app.server.challenge_certification.application.dto.CertificationDataDto
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.rank.application.service.RankQueryService
import com.app.server.user_challenge.application.service.command.UserChallengeCommandServiceImpl
import com.app.server.user_challenge.domain.event.ReportCreatedEvent
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.controller.ReportWaiter
import com.app.server.user_challenge.ui.dto.response.CertificationReportDataDto
import com.app.server.user_challenge.ui.dto.response.ReportDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserChallengeEventListener(
    private val userChallengeCommandService: UserChallengeCommandServiceImpl,
    private val userChallengeService: UserChallengeService,
    private val reportWaiter: ReportWaiter,
    private val rankQueryService: RankQueryService,
) {

    @EventListener
    fun handleCertificationSucceededEvent(certificationSucceededEvent: CertificationSucceededEvent) {
       processWhenReceive(certificationSucceededEvent)
    }

    fun processWhenReceive(event: CertificationSucceededEvent): SavedTodayUserChallengeCertificationEvent {
        return userChallengeCommandService.processAfterCertificateSuccess(
            userChallengeId = event.userChallengeId,
            certificationDto = CertificationDataDto(
                imageUrl = event.imageUrl,
                certificationDate = event.certificatedDate
            )
        )
    }


    @EventListener
    fun handleReportCreatedEvent(reportCreatedEvent: ReportCreatedEvent) {
        processWhenReceive(reportCreatedEvent)
    }

    fun processWhenReceive(event: ReportCreatedEvent) {
        val userChallenge = userChallengeService.findById(event.userChallengeId)

        val userRank = rankQueryService.execute(
            userId = userChallenge.userId,
            challengeId = userChallenge.challenge.id!!,
        )
        val reportDto = ReportDto(
            userChallengeId = userChallenge.id!!,
            totalDays = userChallenge.participantDays,
            userChallengeStatus = userChallenge.status,
            successDays = userChallenge.totalParticipationDayCount.toInt(),
            reportMessage = userChallenge.reportMessage!!,
            maxConsecutiveParticipationDays = userChallenge.maxConsecutiveParticipationDayCount,
            challengeRanking = userRank.userRankInfo.rank.toLong(),
            certificationDataList = mapCertificationDataList(userChallenge.getUserChallengeHistories())
        )

        // TODO : 리포트가 생성되었다는 알림도 보내야 함


        reportWaiter.notifyReportReady(event.userChallengeId, reportDto)
    }

    private fun mapCertificationDataList(histories: List<UserChallengeHistory>): List<CertificationReportDataDto> {
        return histories.map {
            CertificationReportDataDto(
                date = it.date,
                isCertificated = it.status.name
            )
        }
    }

}