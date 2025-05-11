package com.app.server.notification.ui.dto.request

import java.time.LocalDate

data class GetEncourageMessageRequestDto(
    val userId: Long,
    val todayDate: LocalDate
)
