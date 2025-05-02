package com.app.server.feed.application

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.mockito.BDDMockito.given
import org.mockito.kotlin.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class FeedCommandServiceTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService


    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
    )

    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl = imageUrl,
        challengeId = challengeId,
        challengeName = challengeTitle,
        challengeDescription = challengeDescription
    )

    var savedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
        )

        val challenge = challengeService.findById(challengeId)

        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl = imageUrl,
            challengeId = challenge.id!!,
            challengeName = challenge.title,
            challengeDescription = challenge.description
        )

        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    @Test
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
    private fun makeCertificationSucceededEvent(certificateDate : LocalDate) : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeUserChallengeAndHistory(startDate: LocalDate): UserChallenge {
        val mainTestChallenge = challengeService.findById(challengeId)

        val userChallenge: UserChallenge = UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = userId,
                challenge = mainTestChallenge,
                participantsDate = 7,
                status = EUserChallengeStatus.RUNNING
            )
        )
        userChallenge.addUserChallengeHistories(
            listOf(
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate,
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(1),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(2),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(3),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(4),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(5),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                ),
                UserChallengeHistory(
                    userChallenge = userChallenge,
                    date = startDate.plusDays(6),
                    status = EUserChallengeCertificationStatus.FAILED,
                    certificatedImageUrl = null
                )
            )
        )
        return userChallengeService.save(userChallenge)
    }

}