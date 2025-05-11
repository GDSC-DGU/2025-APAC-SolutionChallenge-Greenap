package com.app.server.notification.port.outbound

import com.app.server.notification.dto.raw.request.SendToEncourageServerRequestDto

interface NotificationPort {

    fun getEncourageMessage(sendToEncourageServerRequestDto: SendToEncourageServerRequestDto): String
}