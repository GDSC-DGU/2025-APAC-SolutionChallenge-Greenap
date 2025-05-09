package com.app.server.notification.dto.request

import java.time.LocalDate

data class GetEncourageMessageRequestDto(
    val userId: Long,
    val todayDate: LocalDate
)
