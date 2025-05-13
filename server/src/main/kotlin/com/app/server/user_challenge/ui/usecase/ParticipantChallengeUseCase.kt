package com.app.server.user_challenge.ui.usecase

import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.user_challenge.application.dto.response.UserChallengeResponseDto
import com.app.server.user_challenge.application.service.command.UserChallengeCommandServiceImpl
import org.springframework.stereotype.Component

interface ParticipantChallengeUseCase {
    /**
     * 사용자의 챌린지 참여를 위해 챌린지 정보와 참여 기간으로
     * 사용자 챌린지 정보를 생성한다.
     */
    fun execute(
        challengeParticipantDto: ChallengeParticipantDto,
    ) : UserChallengeResponseDto
}

@Component
class ParticipantChallengeUseCaseImpl(
    private val userChallengeCommandService: UserChallengeCommandServiceImpl
) : ParticipantChallengeUseCase {
    override fun execute(
        challengeParticipantDto : ChallengeParticipantDto,
    ): UserChallengeResponseDto {
        val result = userChallengeCommandService.execute(challengeParticipantDto)
        return result
    }
}