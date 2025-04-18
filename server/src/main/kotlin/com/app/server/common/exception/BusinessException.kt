package com.app.server.common.exception

import com.app.server.common.enums.ResultCode
import lombok.Getter

open class BusinessException : RuntimeException {
    private val resultCode: ResultCode

    constructor(resultCode: ResultCode) : super(resultCode.message) {
        this.resultCode = resultCode
    }

    constructor(resultCode: ResultCode, message: String) : super(message) {
        this.resultCode = resultCode
    }

    constructor(resultCode: ResultCode, cause: Throwable) : super(resultCode.message, cause) {
        this.resultCode = resultCode
    }

    constructor(resultCode: ResultCode, message: String, cause: Throwable) : super(message, cause) {
        this.resultCode = resultCode
    }
}

