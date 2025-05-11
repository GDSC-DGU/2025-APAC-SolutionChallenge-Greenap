package com.app.server.notification.port.outbound

import com.app.server.infra.api.notification.dto.request.SendToEncourageServerRequestDto

interface NotificationPort {

    fun getEncourageMessage(sendToEncourageServerRequestDto: SendToEncourageServerRequestDto): String
}