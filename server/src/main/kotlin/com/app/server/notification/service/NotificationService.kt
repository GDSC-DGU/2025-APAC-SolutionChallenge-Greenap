package com.app.server.notification.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.notification.dto.raw.request.SendToEncourageServerRequestDto
import com.app.server.notification.dto.request.GetEncourageMessageRequestDto
import com.app.server.notification.dto.response.GetEncourageMessageResponseDto
import com.app.server.notification.ui.usecase.GetEncourageMessageUseCase
import com.app.server.user.application.service.UserService
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NotificationService(
    private val userService: UserService,
    private val challengeService: ChallengeService,
    private val userChallengeService: UserChallengeService,
    private val notificationClient: NotificationClient
) : GetEncourageMessageUseCase {

    override fun execute(dto: GetEncourageMessageRequestDto): GetEncourageMessageResponseDto {
        val userId = dto.userId
        val userName = userService.findById(userId).nickname

        val maxChallengeScope = challengeService.findAll().size

        val challengeId: Long = selectChallengeIdForTargetOfEncourage(
            maxChallengeScope = maxChallengeScope,
            userId = userId,
            todayDate = dto.todayDate
        )

        val selectChallengeTitle = challengeService.findById(challengeId).title

        val userChallenge: UserChallenge? = userChallengeService.findByUserIdAndChallengeId(
            userId = userId,
            challengeId = challengeId
        )
        val progress: Int = getProgressOf(userChallenge, dto.todayDate)

        val rawRequestDto = SendToEncourageServerRequestDto(
            userName,
            selectChallengeTitle,
            progress
        )

        val response: String = notificationClient.send(rawRequestDto)

        return GetEncourageMessageResponseDto(
            challengeTitle = selectChallengeTitle,
            progress = progress,
            userChallengeId = userChallenge?.id,
            message = response
        )
    }

    private fun getProgressOf(userChallenge: UserChallenge?, nowDay: LocalDate): Int =
        userChallenge
            ?.takeIf { it.status == EUserChallengeStatus.RUNNING }
            ?.calculateProgressFromTotalParticipationDays(nowDay)
            ?: 0


    private fun selectChallengeIdForTargetOfEncourage(
        maxChallengeScope: Int,
        userId: Long,
        todayDate: LocalDate
    ): Long {
        val running = userChallengeService.findAllByUserId(userId)
            .filter { it.status == EUserChallengeStatus.RUNNING }

        if (running.isEmpty()) {
            return (1..maxChallengeScope).random().toLong()
        }

        val progressMap = running.associateWith { it.calculateProgressFromTotalParticipationDays(todayDate) }
        val minProgress = progressMap.values.minOrNull()!!

        val lowestList = progressMap.filter { it.value == minProgress }.keys.toList()
        val selected = lowestList.random()
        return selected.challenge.id!!
    }
}
