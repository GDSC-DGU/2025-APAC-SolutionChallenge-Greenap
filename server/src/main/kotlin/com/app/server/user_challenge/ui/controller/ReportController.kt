package com.app.server.user_challenge.ui.controller

import com.app.server.user_challenge.ui.dto.ReportDto
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.ui.usecase.GetUserChallengeReportUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/challenges/user")
class ReportController (
    private val reportWaiter: ReportWaiter,
    private val getUserChallengeReportUseCase: GetUserChallengeReportUseCase,
) {

    @GetMapping("/{userChallengeId}/report")
    fun getReport(@PathVariable userChallengeId: Long): DeferredResult<ApiResponse<ReportDto>> {

        val deferredResult = DeferredResult<ApiResponse<ReportDto>>(10_000)

        deferredResult.onTimeout {
            deferredResult.setErrorResult(ApiResponse.failure<ReportDto>(CommonResultCode.TIMEOUT))
        }

        deferredResult.onCompletion {
            reportWaiter.remove(userChallengeId)
        }

        val report : ReportDto? = getUserChallengeReportUseCase.getReport(userChallengeId, LocalDate.now())

        if (report != null) {
            deferredResult.setResult(ApiResponse.success(report))
            return deferredResult
        }

        // 없다면 기다릴 준비
        reportWaiter.register(userChallengeId, deferredResult)

        return deferredResult
    }

}