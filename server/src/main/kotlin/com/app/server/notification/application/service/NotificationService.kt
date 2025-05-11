package com.app.server.notification.application.service

import com.app.server.notification.application.dto.command.GetEncourageMessageCommand
import com.app.server.notification.application.dto.result.EncourageMessageInfoDto

interface NotificationService {

    fun getEncourageMessageForMain(command: GetEncourageMessageCommand): EncourageMessageInfoDto
}