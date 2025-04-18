package com.app.server.common.exception

import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode


class UnauthorizedException : BusinessException {
    constructor() : super(CommonResultCode.UNAUTHORIZED)

    constructor(resultCode: ResultCode) : super(resultCode)

    constructor(resultCode: ResultCode, message: String) : super(resultCode, message)

    constructor(resultCode: ResultCode, cause: Throwable) : super(resultCode, cause)

    constructor(resultCode: ResultCode, message: String, cause: Throwable) : super(resultCode, message, cause)
}
