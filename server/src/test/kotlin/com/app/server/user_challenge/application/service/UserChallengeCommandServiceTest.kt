package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.usecase.dto.request.ChallengeParticipantDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.BusinessException
import com.app.server.common.exception.InternalServerErrorException
import com.app.server.user_challenge.application.usecase.ParticipantChallengeUseCase
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class UserChallengeCommandServiceTest : IntegrationTestContainer() {

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공하면 해당 날짜의 인증 상태를 변경할 수 있다.")
    fun completeChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증 대신 얼리기를 사용하여 인증을 건너뛸 수 있다.")
    fun skipChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("얼리기를 사용하면 전체 참여일은 올라가지 않는다.")
    fun skipChallengeWithoutIncreasingParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("얼리기를 사용하면 연속 참여 일수는 똑같이 올라간다.")
    fun skipChallengeWithIncreasingConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("얼리기는 전체 참여기간의 50% 이상 성공했을 때 챌린지에서 딱 1번 사용할 수 있다.")
    fun skipChallengeWithLimit() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공하면 전체 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족한다면 연속 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족하지 않는다면 연속 참여 일수가 초기화된다.")
    fun completeChallengeWithResettingConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("해당 챌린지에서 가장 오래 연속으로 참여한 일수를 확인할 수 있다.")
    fun getMaxConsecutiveParticipationDays() {
        // given
        // when
        // then
    }

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
    @DisplayName("찰린지 종료 일자보다 오늘 일자가 더 크다면, 챌린지 상태를 Pending으로 변경한다.")
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
    @DisplayName("챌린지 인증에 실패했다면, 인증에 실패하였음을 클라이언트에게 전달한다.")
    fun failChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공했다면, 인증 사진을 저장한다.")
    fun saveChallengeImage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공하여 사진까지 저장했다면, 인증 성공 이벤트를 게시한다.")
    fun publishChallengeImage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 인증에 성공한 날짜가 챌린지 종료일자와 같다면, 리포트 메시지를 AI 서버로부터 받아온다.")
    fun getReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("챌린지 리포트 메시지를 받아왔다면 저장하고, 챌린지의 상태를 Pending으로 변경한다.")
    fun saveReportMessage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("마지막 날짜의 챌린지 인증에는 성공하였으나, 리포트 메시지를 받아오지 못했다면, 챌린지의 상태를 Dead으로 변경한다.")
    fun getReportMessageWithFail() {
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

    @Test
    @Disabled
    @DisplayName("인증에 성공한 챌린지는 피드로 작성할 수 있다.")
    fun createFeed() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드로 작성하는 챌린지는 사용자의 인증 사진을 피드에 올리게 된다.")
    fun createFeedWithImage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드의 내용은 1000자 이하로 작성할 수 있다.")
    fun createFeedWithContent() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드의 내용은 1000자 이상으로 작성할 수 없다.")
    fun createFeedWithContentOverLimit() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드가 작성되어 피드 저장 완료 이벤트가 게시되면, 피드 조회 전용 테이블에 피드가 복사되어 저장된다.")
    fun createFeedWithCopy() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드 조회는 피드 조회 전용 테이블에서 가져온다.")
    fun getFeed() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("카테고리별로 피드를 필터링하여 조회할 수 있다.")
    fun getFeedWithCategory() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드를 출력할 시에는 가장 최신순으로 정렬되어 제공된다.")
    fun getFeedWithSort() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드 조회 시, 사용자가 원하는 page와 size로 조회할 수 있다.")
    fun getFeedWithPage() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("특정 사용자가 작성한 피드들만 조회할 수 있다.")
    fun getFeedWithUser() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("특정 사용자의 특정 챌린지들에서 작성한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndChallenge() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("특정 사용자의 특정 카테고리 내에서 참여했던 모든 챌린지들에 대한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndCategory() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드 수정 시, 피드의 내용을 1000자 이하로 수정할 수 있다.")
    fun updateFeedWithContent() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드 수정 시, 피드의 내용을 1000자 이상으로 수정할 수 없다.")
    fun updateFeedWithContentOverLimit() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드를 삭제할 수 있다.")
    fun deleteFeed() {
        // given
        // when
        // then
    }

    @Test
    @Disabled
    @DisplayName("피드를 삭제하면 피드 조회 전용 테이블에서도 삭제된다.")
    fun deleteFeedWithCopy() {
        // given
        // when
        // then
    }

    // TODO: 랭킹 작업 관련 테스트 작성
    // TODO: 알림 작업 관련 테스트 작성

}