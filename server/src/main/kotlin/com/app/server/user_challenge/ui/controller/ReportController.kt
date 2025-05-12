package com.app.server.user_challenge.ui.controller

import com.app.server.user_challenge.ui.dto.response.ReportDto
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.user_challenge.ui.usecase.GetUserChallengeReportUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import java.time.LocalDate

@RestController
@Tag(name = "Report API", description = "챌린지 레포트 발급 관련 API")
@RequestMapping("/api/v1/challenges/user")
class ReportController (
    private val reportWaiter: ReportWaiter,
    private val getUserChallengeReportUseCase: GetUserChallengeReportUseCase,
) {

    @GetMapping("/{userChallengeId}/report")
    @Operation(
        summary = "챌린지 레포트 발급",
        description = "챌린지 레포트를 발급합니다. 레포트가 아직 발급되지 않았더라면 발급을 요청하고 돌아올 때까지 10초 기다립니다." +
                "레포트 발급 조건을 충족하지 못했다면 발급되지 않습니다."
    )
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