package com.app.server.user_challenge.application.service

import com.app.server.challenge_certification.application.dto.CertificationDataDto
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.event.ReportCreatedEvent
import com.app.server.user_challenge.ui.controller.ReportWaiter
import com.app.server.user_challenge.ui.dto.CertificationReportDataDto
import com.app.server.user_challenge.ui.dto.ReportDto
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class UserChallengeEventListener(
    private val userChallengeCommandService: UserChallengeCommandService,
    private val userChallengeService: UserChallengeService,
    private val reportWaiter: ReportWaiter,
) {

    @Profile("!test")
    @Async
    @EventListener
    fun handleCertificationSucceededEvent(certificationSucceededEvent: CertificationSucceededEvent) {
        processWhenReceive(certificationSucceededEvent)
    }

    @Profile("test")
    @EventListener
    fun handleCertificationSucceededEventForTest(certificationSucceededEvent: CertificationSucceededEvent) {
        processWhenReceive(certificationSucceededEvent)
    }

    private fun processWhenReceive(event: CertificationSucceededEvent) {
        userChallengeCommandService.processAfterCertificateSuccess(
            userId = event.userId,
            userChallengeId = event.userChallengeId,
            certificationDto = CertificationDataDto(
                imageUrl = event.imageUrl,
                certificationDate = event.certificatedDate
            )
        )
    }

    @Profile("!test")
    @Async
    @EventListener
    fun handleReportCreatedEvent(reportCreatedEvent: ReportCreatedEvent) {
        processWhenReceive(reportCreatedEvent)
    }

    @Profile("test")
    @EventListener
    fun handleReportCreatedEventForTest(reportCreatedEvent: ReportCreatedEvent) {
        processWhenReceive(reportCreatedEvent)
    }

    private fun processWhenReceive(event: ReportCreatedEvent) {
        val userChallenge = userChallengeService.findById(event.userChallengeId)

        val userRank = 1L // TODO: UserChallenge에서 랭킹을 가져오는 로직 추가 필요
        val reportDto = ReportDto(
            userChallengeId = userChallenge.id!!,
            totalDays = userChallenge.participantDays,
            successDays = userChallenge.totalParticipationDayCount.toInt(),
            reportMessage = userChallenge.reportMessage!!,
            maxConsecutiveParticipationDays = userChallenge.maxConsecutiveParticipationDayCount,
            challengeRanking = userRank,
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

    fun isReadyForReport(userChallengeId: Long): Boolean {
        return reportWaiter.hasReport(userChallengeId)
    }

}