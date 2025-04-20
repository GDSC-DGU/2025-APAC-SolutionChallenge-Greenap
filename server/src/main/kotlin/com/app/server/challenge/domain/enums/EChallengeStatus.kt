package com.app.server.challenge.domain.enums

enum class EChallengeStatus(
    private val content: String
){
    RUNNING("서비스 중"),
    COMPLETED("서비스 종료"),
}