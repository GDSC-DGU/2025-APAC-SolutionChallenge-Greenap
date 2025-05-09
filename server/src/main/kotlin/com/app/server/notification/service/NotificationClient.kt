package com.app.server.notification.service

import com.app.server.common.exception.InternalServerErrorException
import com.app.server.infra.AbstractRestApiClient
import com.app.server.notification.dto.raw.request.SendToEncourageServerRequestDto
import com.app.server.notification.dto.raw.response.ReceiveFromEncourageServerResponseDto
import com.app.server.notification.exception.NotificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class NotificationClient(
    @Value("\${encourage-message-server.url}")
    private val baseUrl: String,
) : AbstractRestApiClient<
        SendToEncourageServerRequestDto,
        ReceiveFromEncourageServerResponseDto,
        String>
    (baseUrl) {

    override fun rawResponseType() = ReceiveFromEncourageServerResponseDto::class.java

    override fun parseResponse(response: ResponseEntity<ReceiveFromEncourageServerResponseDto>)
            : String {
        val responseBody = response.body ?: throw InternalServerErrorException(
            NotificationException.RECEIVE_FAILED
        )

        return responseBody.message

    }

}