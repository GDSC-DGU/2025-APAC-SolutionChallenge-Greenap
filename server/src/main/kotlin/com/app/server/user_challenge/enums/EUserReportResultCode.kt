package com.app.server.user_challenge.enums

enum class EUserReportResultCode (
    val message: String,
) {
    RECEIVE_REPORT_SUCCESS("레포트 발급 성공"),
    RECEIVE_REPORT_FAILED("레포트 발급 실패"),
    ERROR_IN_RECEIVE_REPORT_SERVER("레포트 발급 서버 오류"),
}