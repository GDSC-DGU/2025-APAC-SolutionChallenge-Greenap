package com.app.server.challenge_certification.enums

enum class EUserCertificatedResultCode (
    val message: String,
){

    SUCCESS_CERTIFICATED("인증 성공"),
    CERTIFICATED_FAILED("인증 실패"),
    ERROR_IN_CERTIFICATED_SERVER("인증 서버 오류"),

}