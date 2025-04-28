package com.app.server.challenge_certification.usecase

import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.user_challenge.domain.model.UserChallenge
import java.time.LocalDate

interface UsingIceUseCase {

    fun execute(iceRequestDto: UserChallengeIceRequestDto, certificationDate: LocalDate) : UserChallenge
}