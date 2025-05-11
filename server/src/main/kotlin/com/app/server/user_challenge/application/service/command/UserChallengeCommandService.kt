package com.app.server.user_challenge.application.service.command

import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.user_challenge.application.dto.response.UserChallengeResponseDto

interface UserChallengeCommandService {

    fun execute(challengeParticipantDto: ChallengeParticipantDto): UserChallengeResponseDto
}