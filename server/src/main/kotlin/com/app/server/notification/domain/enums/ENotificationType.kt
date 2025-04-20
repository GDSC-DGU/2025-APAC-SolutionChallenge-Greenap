package com.app.server.notification.domain.enums

enum class ENotificationType(
    private val content: String
) {
    PRE_DAILY("일일 참여 독려 알림"),
    CHALLENGE_END("챌린지 종료 알림"),
}