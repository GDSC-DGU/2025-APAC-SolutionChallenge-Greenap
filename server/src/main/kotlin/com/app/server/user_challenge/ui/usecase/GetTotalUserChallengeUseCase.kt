package com.app.server.user_challenge.ui.usecase

import com.app.server.user_challenge.ui.dto.response.GetTotalUserChallengeResponseDto
import java.time.LocalDate

interface GetTotalUserChallengeUseCase {

    fun execute(userId : Long, todayDate: LocalDate) : GetTotalUserChallengeResponseDto
}
