package com.app.server.infra.api.notification

import com.app.server.common.exception.InternalServerErrorException
import com.app.server.infra.AbstractRestApiClient
import com.app.server.notification.dto.raw.request.SendToEncourageServerRequestDto
import com.app.server.notification.dto.raw.response.ReceiveFromEncourageServerResponseDto
import com.app.server.notification.exception.NotificationException
import com.app.server.notification.port.outbound.NotificationPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class NotificationAdaptor(
    @Value("\${encourage-message-server.url}")
    private val baseUrl: String,
) : AbstractRestApiClient<
        SendToEncourageServerRequestDto,
        ReceiveFromEncourageServerResponseDto,
        String>
    (baseUrl), NotificationPort {

    override fun rawResponseType() = ReceiveFromEncourageServerResponseDto::class.java

    override fun parseResponse(response: ResponseEntity<ReceiveFromEncourageServerResponseDto>)
            : String {
        val responseBody = response.body ?: throw InternalServerErrorException(
            NotificationException.RECEIVE_FAILED
        )

        return responseBody.message

    }

    override fun getEncourageMessage(sendToEncourageServerRequestDto: SendToEncourageServerRequestDto): String {
        return this.send(sendToEncourageServerRequestDto)
    }
}