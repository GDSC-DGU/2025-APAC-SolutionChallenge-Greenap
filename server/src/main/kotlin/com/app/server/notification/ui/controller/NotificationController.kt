package com.app.server.notification.ui.controller

import com.app.server.common.annotation.UserId
import com.app.server.common.response.ApiResponse
import com.app.server.notification.dto.request.GetEncourageMessageRequestDto
import com.app.server.notification.dto.response.GetEncourageMessageResponseDto
import com.app.server.notification.ui.usecase.GetEncourageMessageUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val getEncourageMessageUseCase: GetEncourageMessageUseCase,
) {

    @GetMapping("/encourage")
    fun getEncourageMessage(
        @UserId userId: Long,
    ): ApiResponse<GetEncourageMessageResponseDto> {
        return ApiResponse.success(
            getEncourageMessageUseCase.execute(
                GetEncourageMessageRequestDto(
                    userId,
                    LocalDate.now()
                )
            )
        )
    }
}