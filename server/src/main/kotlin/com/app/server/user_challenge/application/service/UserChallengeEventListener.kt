package com.app.server.user_challenge.application.service

import com.app.server.challenge_certification.application.dto.CertificationDataDto
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.user_challenge.domain.event.ReportCreatedEvent
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.controller.ReportWaiter
import com.app.server.user_challenge.ui.dto.CertificationReportDataDto
import com.app.server.user_challenge.ui.dto.ReportDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserChallengeEventListener(
    private val userChallengeCommandService: UserChallengeCommandService,
    private val userChallengeService: UserChallengeService,
    private val reportWaiter: ReportWaiter,
) {

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @EventListener
    fun handleCertificationSucceededEvent(certificationSucceededEvent: CertificationSucceededEvent) {
        try {
            scope.launch {
                processWhenReceive(certificationSucceededEvent)
            }
        } catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    suspend fun processWhenReceive(event: CertificationSucceededEvent) {
        userChallengeCommandService.processAfterCertificateSuccess(
            userChallengeId = event.userChallengeId,
            certificationDto = CertificationDataDto(
                imageUrl = event.imageUrl,
                certificationDate = event.certificatedDate
            )
        )
    }


    @EventListener
    fun handleReportCreatedEvent(reportCreatedEvent: ReportCreatedEvent) {
        try {
            scope.launch {
                processWhenReceive(reportCreatedEvent)
            }
        } catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    suspend fun processWhenReceive(event: ReportCreatedEvent) {
        val userChallenge = userChallengeService.findById(event.userChallengeId)

        val userRank = 1L // TODO: UserChallenge에서 랭킹을 가져오는 로직 추가 필요
        val reportDto = ReportDto(
            userChallengeId = userChallenge.id!!,
            totalDays = userChallenge.participantDays,
            userChallengeStatus = userChallenge.status,
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

}