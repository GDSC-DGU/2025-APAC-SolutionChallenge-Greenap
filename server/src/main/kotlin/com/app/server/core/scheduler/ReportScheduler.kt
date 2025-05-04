package com.app.server.core.scheduler

import org.springframework.context.annotation.Configuration

@Configuration
class ReportScheduler {

    // TODO: 레포트 발급에 실패한 챌린지들은 따로 모아 배치작업으로 다시 재발급 필요
    // TODO: Dead 상태인 챌린지들은 한 곳에 모여 주기적으로, 혹은 개수가 10개 이상 쌓였을 때 트리거되는 리포트 메시지 GET 요청에 의해 다시 리포트 메시지를 받아올 수 있다.
}