package com.app.server.auth.exception

import com.app.server.common.enums.ResultCode

enum class AuthException(
    override val code: String,
    override val message: String
) : ResultCode{

    INVALID_REFRESH_TOKEN("AUT000", "유효하지 않은 리프레시 토큰입니다."),
}