package com.app.server.notification.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.notification.application.dto.command.GetEncourageMessageCommand
import com.app.server.notification.application.dto.result.EncourageMessageInfoDto
import com.app.server.notification.dto.raw.request.SendToEncourageServerRequestDto
import com.app.server.notification.dto.response.GetEncourageMessageResponseDto
import com.app.server.notification.port.outbound.NotificationPort
import com.app.server.notification.ui.usecase.GetEncourageMessageUseCase
import com.app.server.user.application.service.UserService
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NotificationServiceImpl(
    private val userService: UserService,
    private val challengeService: ChallengeService,
    private val userChallengeService: UserChallengeService,
    private val notificationPort: NotificationPort
) : NotificationService {

    override fun getEncourageMessageForMain(command: GetEncourageMessageCommand): EncourageMessageInfoDto {
        val userId = command.userId
        val todayDateIs = command.todayDate

        val userName = userService.findById(userId).nickname

        val maxChallengeScope = challengeService.findAll().size

        val challengeId: Long = selectChallengeIdForTargetOfEncourage(
            maxChallengeScope = maxChallengeScope,
            userId = userId,
            todayDate = todayDateIs
        )

        val selectChallengeTitle = challengeService.findById(challengeId).title

        val userChallenge: UserChallenge? = userChallengeService.findByUserIdAndChallengeId(
            userId = userId,
            challengeId = challengeId
        )
        val progress: Int = getProgressOf(userChallenge, todayDateIs)

        val rawRequestDto = SendToEncourageServerRequestDto(
            userName,
            selectChallengeTitle,
            progress
        )

        val response: String = notificationPort.getEncourageMessage(rawRequestDto)

        return EncourageMessageInfoDto(
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
