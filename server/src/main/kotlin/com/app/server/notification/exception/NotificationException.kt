package com.app.server.notification.exception

import com.app.server.common.enums.ResultCode

enum class NotificationException(
    override val code: String,
    override val message: String,
) : ResultCode {

    RECEIVE_FAILED("NOT001", "독려 메시지 서버 문제로 메시지를 받아오지 못했습니다.")
}