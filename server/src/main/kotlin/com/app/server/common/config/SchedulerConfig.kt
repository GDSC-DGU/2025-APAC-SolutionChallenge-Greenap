package com.app.server.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class SchedulerConfig {
    // TODO : Pending 상태인 챌린지들도 시간 보고 COMPLETED 상태로 변경해줘야 함
}