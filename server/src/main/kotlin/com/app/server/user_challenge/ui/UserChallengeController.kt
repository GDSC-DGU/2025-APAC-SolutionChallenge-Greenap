package com.app.server.user_challenge.ui

import com.app.server.common.annotation.UserId
import com.app.server.user_challenge.application.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.ui.dto.ChallengeParticipantRequestDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserChallengeController (
    private val participantChallengeUseCase: ParticipantChallengeUseCase
) {

    @PostMapping("/challenge")
    fun participateChallenge(@UserId userId: Long, @RequestBody requestBody: ChallengeParticipantRequestDto) {
        val challengeParticipantDto = requestBody.toChallengeParticipantDto(userId)
        participantChallengeUseCase.execute(challengeParticipantDto)
    }
}