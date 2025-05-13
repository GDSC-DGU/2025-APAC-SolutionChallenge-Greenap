package com.app.server.user_challenge.ui.usecase

import com.app.server.user_challenge.ui.dto.response.ReportDto
import java.time.LocalDate

interface GetUserChallengeReportUseCase {
    fun getReport(userChallengeId: Long, todayDate: LocalDate): ReportDto

}