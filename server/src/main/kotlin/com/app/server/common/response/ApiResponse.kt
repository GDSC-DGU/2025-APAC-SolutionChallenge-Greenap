package com.app.server.common.response

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.domain.Page


data class ApiResponse<T>(
    val code: String?,
    val message: String?,
    @field:JsonInclude(JsonInclude.Include.NON_NULL) @param:JsonInclude(JsonInclude.Include.NON_NULL) val data: T
) {
    
    data class PageResponse<T>(
        val content: List<T>,
        val page: Int,
        val size: Int,
        val hasNext: Boolean
    ) {
        constructor(page: Page<T>) : this(
            page.content,
            page.number,
            page.size,
            page.hasNext()
        )
    }

    companion object {
        @JvmStatic
        fun <T> success(): ApiResponse<T?> {
            return ApiResponse(
                CommonResultCode.SUCCESS.code,
                CommonResultCode.SUCCESS.message,
                null
            )
        }

        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                CommonResultCode.SUCCESS.code,
                CommonResultCode.SUCCESS.message,
                data
            )
        }

        fun <T> success(data: List<T>): ApiResponse<List<T>> {
            return ApiResponse(
                CommonResultCode.SUCCESS.code,
                CommonResultCode.SUCCESS.message,
                data
            )
        }

        fun <T> success(data: Page<T>): ApiResponse<PageResponse<T>> {
            return ApiResponse(
                CommonResultCode.SUCCESS.code,
                CommonResultCode.SUCCESS.message,
                PageResponse(data)
            )
        }

        @JvmStatic
        fun <T> failure(resultCode: ResultCode): ApiResponse<T?> {
            return ApiResponse(
                resultCode.code,
                resultCode.message,
                null
            )
        }

        @JvmStatic
        fun <T> failure(resultCode: ResultCode, message: String): ApiResponse<T?> {
            return ApiResponse(resultCode.code, message, null)
        }
    }
}