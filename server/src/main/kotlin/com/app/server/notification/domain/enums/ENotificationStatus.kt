package com.app.server.notification.domain.enums

enum class ENotificationStatus(
    private val content: String
) {
    FAILED("전송 실패"),
    SUCCESS("전송 성공"),
}
