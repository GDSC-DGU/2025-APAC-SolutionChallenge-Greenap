package com.app.server.user_challenge.domain.enums

enum class EUserChallengeCertificationStatus(
    val content: String
) {
    FAILED("챌린지 인증 실패"),
    SUCCESS("챌린지 인증 성공"),
    ICE("챌린지 얼리기"),
}