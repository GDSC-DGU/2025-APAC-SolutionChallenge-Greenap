package com.app.server.rank.application.service

import com.app.server.common.exception.InternalServerErrorException
import com.app.server.rank.enums.RankState
import com.app.server.rank.exception.RankException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RankEventListener(
    private val rankService: RankService,
    private val userChallengeService: UserChallengeService
) {

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @EventListener
    fun handleSavedTodayUserChallengeCertificationEventForUpdateSpecificChallengeRank(
        event: SavedTodayUserChallengeCertificationEvent
    ) {
        try {
            scope.launch {
                updateSpecificChallengeRank(
                    event.userChallengeId,
                    event.totalParticipationDayCount
                )
            }
        } catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    @EventListener
    fun handleSavedTodayUserChallengeCertificationEventForUpdateTotalUserRank(
        event: SavedTodayUserChallengeCertificationEvent
    ) {
        try {
            scope.launch {
                updateTotalRank(
                    event.userChallengeId,
                    event.maxConsecutiveParticipationDayCount
                )
            }
        } catch (e: Exception) {
            // TODO : 실패 시 보상 트랜잭션 이벤트 제공 필요
            throw e
        }
    }

    suspend fun updateSpecificChallengeRank(userChallengeId: Long, totalParticipationDayCount: Long): RankState {

        val userChallenge = userChallengeService.findById(userChallengeId)
        val value = userChallenge.userId
        val challenge = userChallenge.challenge

        val key = "rank:challenge:${challenge.id}"

        val existScore = rankService.getScore(key, value)

        return updateRankScoreIfNeeded(existScore, totalParticipationDayCount, key, value)
    }

    suspend fun updateTotalRank(userChallengeId: Long, maxConsecutiveParticipationDayCount: Long): RankState {
        val userChallenge = userChallengeService.findById(userChallengeId)
        val key = userChallenge.userId

        val slot = "rank:total"

        val existScore = rankService.getScore(slot, key)

        return updateRankScoreIfNeeded(existScore, maxConsecutiveParticipationDayCount, slot, key)
    }

    private suspend fun updateRankScoreIfNeeded(
        existScore: Double,
        score: Long,
        slot: String,
        key: Long
    ): RankState {
        if (existScore.toLong() < score) {
            val isProcess: Boolean? = rankService.updateValue(slot, key, score)

            return isUpdatedRankScore(isProcess)
        }

        return RankState.RANK_NOT_UPDATE
    }

    private suspend fun isUpdatedRankScore(isProcess: Boolean?): RankState {
        if (isProcess != null && isProcess)
            return RankState.RANK_UPDATE_SUCCESS
        else if (isProcess != null)
            return RankState.RANK_UPDATE_FAIL
        throw InternalServerErrorException(RankException.CANNOT_UPDATE_RANK)
    }
}