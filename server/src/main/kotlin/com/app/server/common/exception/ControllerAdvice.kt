package com.app.server.common.exception

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ApiResponse<*> {
        return ApiResponse.failure<Any>(CommonResultCode.ILLEGAL_ARGUMENT, e.message?:CommonResultCode.ILLEGAL_ARGUMENT.message)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: BadRequestException): ApiResponse<*> {
        return ApiResponse.failure<Any>(e.resultCode, e.message?:CommonResultCode.BAD_REQUEST.message)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: NotFoundException): ApiResponse<*> {
        return ApiResponse.failure<Any>(e.resultCode, e.message?:CommonResultCode.NOT_FOUND.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedException(e: UnauthorizedException): ApiResponse<*> {
        return ApiResponse.failure<Any>(e.resultCode, e.message?:CommonResultCode.UNAUTHORIZED.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(e: ForbiddenException): ApiResponse<*> {
        return ApiResponse.failure<Any>(e.resultCode, e.message?:CommonResultCode.FORBIDDEN.message)
    }

    @ExceptionHandler(InternalServerErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerErrorException(e: InternalServerErrorException): ApiResponse<*> {
        return ApiResponse.failure<Any>(
            e.resultCode,
            e.message?:CommonResultCode.INTERNAL_SERVER_ERROR.message
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ApiResponse<*> {
        val errorMessage = e.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ApiResponse.failure<Any>(CommonResultCode.BAD_REQUEST, errorMessage)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ApiResponse<*> {
        return ApiResponse.failure<Any>(
            CommonResultCode.INTERNAL_SERVER_ERROR,
            e.message?:CommonResultCode.INTERNAL_SERVER_ERROR.message
        )
    }
}
