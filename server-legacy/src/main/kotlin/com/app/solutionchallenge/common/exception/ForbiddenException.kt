package com.app.solutionchallenge.common.exception

import com.app.solutionchallenge.common.enums.CommonResultCode
import com.app.solutionchallenge.common.enums.ResultCode

class ForbiddenException : BusinessException {
    constructor() : super(CommonResultCode.FORBIDDEN)

    constructor(resultCode: ResultCode) : super(resultCode)

    constructor(resultCode: ResultCode, message: String) : super(resultCode, message)

    constructor(resultCode: ResultCode, cause: Throwable) : super(resultCode, cause)

    constructor(resultCode: ResultCode, message: String, cause: Throwable) : super(resultCode, message, cause)
}
