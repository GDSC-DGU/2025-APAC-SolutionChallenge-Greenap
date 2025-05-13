package com.app.server.rank.application.service

import com.app.server.common.exception.InternalServerErrorException
import com.app.server.rank.enums.RankState
import com.app.server.rank.exception.RankException
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.event.SavedTodayUserChallengeCertificationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RankEventListener(
    private val rankService: RankService,
    private val userChallengeService: UserChallengeService
) {


    @EventListener
    fun handleSavedTodayUserChallengeCertificationEventForUpdateSpecificChallengeRank(
        event: SavedTodayUserChallengeCertificationEvent
    ) {
        updateSpecificChallengeRank(
            event.userChallengeId,
            event.totalParticipationDayCount
        )
    }

    @EventListener
    fun handleSavedTodayUserChallengeCertificationEventForUpdateTotalUserRank(
        event: SavedTodayUserChallengeCertificationEvent
    ) {
        updateTotalRank(
            event.userChallengeId,
            event.maxConsecutiveParticipationDayCount
        )
    }

    fun updateSpecificChallengeRank(userChallengeId: Long, totalParticipationDayCount: Long): RankState {

        val userChallenge = userChallengeService.findById(userChallengeId)
        val value = userChallenge.userId
        val challenge = userChallenge.challenge

        val key = "rank:challenge:${challenge.id}"

        val existScore = rankService.getScore(key, value)

        return updateRankScoreIfNeeded(existScore, totalParticipationDayCount, key, value)
    }

    fun updateTotalRank(userChallengeId: Long, maxConsecutiveParticipationDayCount: Long): RankState {
        val userChallenge = userChallengeService.findById(userChallengeId)
        val key = userChallenge.userId

        val slot = "rank:total"

        val existScore = rankService.getScore(slot, key)

        return updateRankScoreIfNeeded(existScore, maxConsecutiveParticipationDayCount, slot, key)
    }

    private fun updateRankScoreIfNeeded(
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

    private fun isUpdatedRankScore(isProcess: Boolean?): RankState {
        if (isProcess != null && isProcess)
            return RankState.RANK_UPDATE_SUCCESS
        else if (isProcess != null)
            return RankState.RANK_UPDATE_FAIL
        throw InternalServerErrorException(RankException.CANNOT_UPDATE_RANK)
    }
}