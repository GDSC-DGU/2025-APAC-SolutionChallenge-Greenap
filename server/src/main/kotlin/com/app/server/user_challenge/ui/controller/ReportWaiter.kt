package com.app.server.user_challenge.ui.controller

import com.app.server.user_challenge.ui.dto.ReportDto
import com.app.server.common.response.ApiResponse
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap

@Component
class ReportWaiter {
    private val waiters = ConcurrentHashMap<Long, DeferredResult<ApiResponse<ReportDto>>>()

    fun register(id: Long, result: DeferredResult<ApiResponse<ReportDto>>) {
        waiters[id] = result
    }

    fun notifyReportReady(id: Long, report: ReportDto) {
        waiters.remove(id)?.setResult(ApiResponse.Companion.success(report))
    }

    fun hasReport(id: Long): Boolean {
        return waiters.containsKey(id)
    }

    fun remove(id: Long) {
        waiters.remove(id)
    }

}