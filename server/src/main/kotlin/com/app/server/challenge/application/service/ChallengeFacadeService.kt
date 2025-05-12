package com.app.server.challenge.application.service

import com.app.server.challenge.ui.usecase.GetChallengeUseCase
import com.app.server.challenge.ui.usecase.dto.response.ChallengeDetailResponseDto
import com.app.server.user_challenge.application.service.query.UserChallengeQueryService
import org.springframework.stereotype.Service

@Service
class ChallengeFacadeService(
    private val challengeQueryService: ChallengeQueryService,
    private val userChallengeQueryService: UserChallengeQueryService
) : GetChallengeUseCase {

    override fun execute(challengeId: Long): ChallengeDetailResponseDto {
        return ChallengeDetailResponseDto.of(
            challengeDetailDto = challengeQueryService.getChallengeDetail(challengeId),
            percentOfCompletedUser = userChallengeQueryService.getChallengeCompletedUserPercent(challengeId)
        )
    }
}