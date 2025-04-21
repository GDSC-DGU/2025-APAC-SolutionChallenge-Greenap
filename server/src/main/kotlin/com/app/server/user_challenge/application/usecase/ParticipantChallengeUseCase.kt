package com.app.server.user_challenge.application.usecase

import com.app.server.challenge.application.usecase.dto.request.ChallengeParticipantDto

interface ParticipantChallengeUseCase {
    /**
     * 사용자의 챌린지 참여를 위해 챌린지 정보와 참여 기간으로
     * 사용자 챌린지 정보를 생성한다.
     */
    fun execute(challengeParticipantDto: ChallengeParticipantDto)
}