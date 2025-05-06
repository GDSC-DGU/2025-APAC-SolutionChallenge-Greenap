package com.app.server.rank.application.service

import com.app.server.challenge.application.service.ChallengeService
import com.app.server.rank.ui.dto.SpecificChallengeTotalRankResponseDto
import com.app.server.rank.ui.dto.TotalRankResponseDto
import com.app.server.rank.ui.dto.UserRankForSpecificChallengeDto
import com.app.server.rank.ui.dto.UserRankForTotalDto
import com.app.server.rank.ui.dto.UserRankInSpecificChallengeTotalRankResponseDto
import com.app.server.rank.ui.dto.UserRankInTotalRankResponseDto
import com.app.server.rank.ui.usecase.GetSpecificChallengeTotalRankUseCase
import com.app.server.rank.ui.usecase.GetTotalRankUseCase
import com.app.server.rank.ui.usecase.GetUserSpecificChallengeTotalRankUseCase
import com.app.server.rank.ui.usecase.GetUserTotalRankUseCase
import com.app.server.user.application.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RankQueryService(
    private val rankService: RankService,
    private val userService: UserService,
    private val challengeService: ChallengeService,
) : GetTotalRankUseCase,
    GetSpecificChallengeTotalRankUseCase,
    GetUserTotalRankUseCase,
    GetUserSpecificChallengeTotalRankUseCase {

    override fun execute(): TotalRankResponseDto {

        val key = "rank:total"

        val totalParticipants = rankService.count(key) ?: 0

        val pairListOfUserIdAndLongConsecutiveDayCount = rankService.getTop100UserInfo(key)

        val topParticipantsRankInfoList = pairListOfUserIdAndLongConsecutiveDayCount.mapIndexed { index, pair ->
            val userId : Long = pair.first.toString().toLong()
            val consecutiveDayCount = pair.second

            val user = userService.findById(userId)

            UserRankForTotalDto.from(
                rank = index + 1,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                longestConsecutiveParticipationCount = consecutiveDayCount.toLong()
            )
        }

        return TotalRankResponseDto.from(
            totalParticipants,
            topParticipantsRankInfoList
        )
    }

    override fun execute(challengeId: Long): SpecificChallengeTotalRankResponseDto {
        val key = "rank:challenge:$challengeId"
        val challengeTitle = challengeService.findById(challengeId).title
        val totalParticipants = rankService.count(key) ?: 0

        val pairListOfUserIdAndLongConsecutiveDayCount = rankService.getTop100UserInfo(key)

        val topParticipantsRankInfoList = pairListOfUserIdAndLongConsecutiveDayCount.mapIndexed { index, pair ->
            val userId = pair.first.toString().toLong()
            val totalParticipantDayCount = pair.second

            val user = userService.findById(userId)

            UserRankForSpecificChallengeDto.from(
                rank = index + 1,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                totalParticipationCount = totalParticipantDayCount.toLong()
            )
        }

        return SpecificChallengeTotalRankResponseDto.from(
            challengeTitle,
            totalParticipants,
            topParticipantsRankInfoList
        )
    }

    override fun executeOfUserIs(userId: Long): UserRankInTotalRankResponseDto {
        val key = "rank:total"
        val value = userId

        val rank = rankService.getRank(key, value)
        val score = rankService.getScore(key, value)

        val user = userService.findById(userId)

        return UserRankInTotalRankResponseDto.from(
            rank = rank + 1,
            score = score,
            userNickname = user.nickname,
            userProfileImageUrl = user.profileImageUrl
        )
    }

    override fun execute(challengeId: Long, userId: Long): UserRankInSpecificChallengeTotalRankResponseDto {
        val key = "rank:challenge:$challengeId"
        val value = userId

        val rank = rankService.getRank(key, value)
        val score = rankService.getScore(key, value)

        val user = userService.findById(userId)
        val challengeTitle = challengeService.findById(challengeId).title

        return UserRankInSpecificChallengeTotalRankResponseDto.from(
            challengeTitle,
            rank + 1,
            score,
            user.nickname,
            user.profileImageUrl
        )
    }
}