package com.app.server.user_challenge.application.usecase

import com.app.server.user_challenge.ui.dto.GetTotalUserChallengeResponseDto

interface GetTotalUserChallengeUseCase {

    fun execute(userId : Long) : GetTotalUserChallengeResponseDto
}
