package com.app.server.user_challenge.application.service.command

import com.app.server.challenge.ui.usecase.dto.request.ChallengeParticipantDto
import com.app.server.user_challenge.application.dto.response.UserChallengeResponseDto
import com.app.server.user_challenge.domain.model.UserChallenge
import java.time.LocalDate

interface UserChallengeCommandService {

    fun execute(challengeParticipantDto: ChallengeParticipantDto): UserChallengeResponseDto

    fun updateUserChallengeStatus(userChallenge: UserChallenge, todayDate: LocalDate)
}