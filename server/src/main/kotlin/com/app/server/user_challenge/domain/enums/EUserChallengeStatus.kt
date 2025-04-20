package com.app.server.user_challenge.domain.enums

enum class EUserChallengeStatus(
    private val content: String
) {
    RUNNING("챌린지 진행 중"),
    PENDING("리포트 확인 대기 중"),
    REUSE("챌린지 이어하기 작업 중"),
    WAIT("챌린지 이어하기 / 종료 결정 기다리는 중"),
    COMPLETED("챌린지 완료"),
}
