package com.app.server.core.scheduler

import com.app.server.user_challenge.application.service.command.UserChallengeCommandServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

@Configuration
class UserChallengeScheduler (
    private val userChallengeCommandServiceImpl: UserChallengeCommandServiceImpl
){

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusToPending() {
        scope.launch {
            userChallengeCommandServiceImpl.batchUpdateChallengeStatusFromRunningToPending(LocalDate.now())
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusFromPendingToCompleted() {
        scope.launch {
            userChallengeCommandServiceImpl.batchUpdateChallengeStatusFromPendingToCompleted(LocalDate.now())
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateChallengeStatusFromWaitingToCompleted() {
        scope.launch {
            userChallengeCommandServiceImpl.batchUpdateChallengeStatusFromWaitingToCompleted(LocalDate.now())
        }
    }

}