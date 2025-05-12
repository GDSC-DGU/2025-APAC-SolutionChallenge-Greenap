package com.app.server.notification.application.dto.command

import java.time.LocalDate

data class GetEncourageMessageCommand(
    val userId: Long,
    val todayDate: LocalDate
)
