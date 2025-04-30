package com.app.server.challenge_certification.ui.usecase

import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.user_challenge.domain.model.UserChallenge
import java.time.LocalDate

interface CertificationUseCase {

    fun certificateChallengeWithDate(
        userId: Long,
        certificationRequestDto: CertificationRequestDto,
        certificationDate: LocalDate
    ): UserChallenge

}
