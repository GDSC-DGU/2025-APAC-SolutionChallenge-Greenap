package com.app.server.core.scheduler

import org.springframework.context.annotation.Configuration

@Configuration
class UserChallengeScheduler {
    /**
     *     TODO: 챌린지 종료 일자보다 오늘 일자가 더 크다면, 챌린지 상태를 Pending으로 변경한다.
     *     이건 즉 마지막 날짜에 인증하지 못한 챌린지들이 리포트를 발급받을 수 있게 한다.
     */

    // TODO : Pending 상태인 챌린지들도 시간 보고 COMPLETED 상태로 변경해줘야 함.
    // TODO 배치로 WAIT 상태인 챌린지들을 COMPLETED로 변경하는 작업이 필요하다.
    // TODO: WAIT 상태인 챌린지들은 endDate.plusDays(1)보다 .isAfter하면 COMPLETED로 상태 변경 배치 작업 필요

}