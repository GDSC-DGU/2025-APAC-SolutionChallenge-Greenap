package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
@ExtendWith(SpringExtension::class)
class UserChallengeCommandServiceTest : IntegrationTestContainer() {

    @Test
    @Disabled
    @DisplayName("챌린지 종료 일자와 오늘 일자가 같고 오늘 인증에 성공했다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 종료 일자보다 오늘 일자가 더 크다면, 챌린지 상태를 Pending으로 변경한다.")
    fun finishChallengeWithOverdue() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("Pending 상태의 챌린지는 리포트를 발급받은 상태이다.")
    fun issueReport() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("Pending 상태의 챌린지는 사용자가 챌린지 종료 다음날 안으로 리포트를 확인한다면 WAIT 상태로 변경된다.")
    fun checkReport() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("Pending 상태의 챌린지는 사용자가 챌린지 종료 다음날까지도 리포트를 확인하지 못했다면 COMPLETED 상태로 변경된다.")
    fun completeReport() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("COMPLETED 상태의 챌린지는 챌린지 이어하기 기능을 사용할 수 없다. 즉, 다시 Running 상태로 변경할 수 없다.")
    fun continueChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지는 챌린지 종료 이틀 내로 이어할 수 있다.")
    fun continueChallengeWithWait() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 총 참가 일수가 증가한다.")
    fun continueChallengeWithWaitAndIncreaseTotalParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 참여 중인 챌린지의 참여 히스토리가 늘어난다.")
    fun continueChallengeWithWaitAndIncreaseParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 새로 추가된 히스토리들의 상태는 모두 fail이다.")
    fun continueChallengeWithWaitAndChangeStatusToFail() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하여 참여 히스토리가 늘어났을 때, 기존 히스토리들은 그대로 유지된다.")
    fun continueChallengeWithWaitAndKeepStatus() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어하고 싶다면, 기존 리포트 메시지는 삭제된다.")
    fun continueChallengeWithWaitAndDeleteReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태의 챌린지를 사용자가 이어했을 때, 연속 참여 횟수도 이어서 증가할 수 있다.")
    fun continueChallengeWithWaitAndIncreaseConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 이어하기를 성공했다면, 해당 챌린지의 상태는 다시 Running으로 변경된다.")
    fun continueChallengeWithSuccess() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("WAIT 상태인 챌린지가 챌린지 종료일자로부터 이틀이 지났다면, 챌린지 상태를 COMPLETED로 변경한다.")
    fun completeChallengeWithWait() {
         // given
         // when
         // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지의 상태가 Dead인 챌린지들을 모아 다시 리포트 메시지를 요청할 수 있다. 이 요청은 주기적으로 이루어지거나, Dead 상태의 챌린지들의 개수가 10개 이상 일 때 이루어진다.")
    fun getReportMessageWithDead() {
        // given
        // when
        // then
    }

    // TODO: 랭킹 작업 관련 테스트 작성
    // TODO: 알림 작업 관련 테스트 작성

}