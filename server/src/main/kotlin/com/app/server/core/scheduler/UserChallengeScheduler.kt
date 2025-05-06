package com.app.server.core.scheduler

import com.app.server.user_challenge.application.service.UserChallengeCommandService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

@Configuration
class UserChallengeScheduler (
    private val userChallengeCommandService: UserChallengeCommandService
){

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusToPending() {
        scope.launch {
            userChallengeCommandService.batchUpdateChallengeStatusFromRunningToPending(LocalDate.now())
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusFromPendingToCompleted() {
        scope.launch {
            userChallengeCommandService.batchUpdateChallengeStatusFromPendingToCompleted(LocalDate.now())
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusFromWaitingToCompleted() {
        scope.launch {
            userChallengeCommandService.batchUpdateChallengeStatusFromWaitingToCompleted(LocalDate.now())
        }
    }

}