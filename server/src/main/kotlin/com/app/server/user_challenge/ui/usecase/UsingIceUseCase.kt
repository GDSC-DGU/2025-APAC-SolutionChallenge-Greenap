package com.app.server.user_challenge.ui.usecase

import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.user_challenge.domain.model.UserChallenge
import java.time.LocalDate

interface UsingIceUseCase {

    fun processAfterCertificateIce(iceRequestDto: UserChallengeIceRequestDto, certificationDate: LocalDate) : UserChallenge
}