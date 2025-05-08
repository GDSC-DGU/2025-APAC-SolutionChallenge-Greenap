package com.app.server.feed.application

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.service.FeedEventListener
import com.app.server.feed.application.service.FeedProjectionService
import com.app.server.feed.application.service.FeedService
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.exception.FeedException
import com.app.server.feed.ui.dto.CreateFeedCommand
import com.app.server.feed.ui.dto.CreateFeedRequestDto
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.DeleteFeedUseCase
import com.app.server.feed.ui.usecase.UpdateFeedUseCase
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class FeedCommandServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var feedService: FeedService

    @Autowired
    private lateinit var deleteFeedUseCase: DeleteFeedUseCase

    @Autowired
    private lateinit var updateFeedUseCase: UpdateFeedUseCase

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var createFeedUseCase: CreateFeedUseCase

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @Autowired
    private lateinit var feedProjectionService: FeedProjectionService

    @Autowired
    private lateinit var feedEventListener: FeedEventListener

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        image = mock(MultipartFile::class.java)
    )

    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl,
        challengeId,
        challengeTitle,
        challengeDescription
    )

    var savedUserChallenge: UserChallenge? = null

    var createFeedRequestDto = CreateFeedRequestDto(
        userChallengeId = userChallengeId,
        content = "testContent",
        imageUrl = imageUrl,
        publishDate = participantsStartDate
    )

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            image = mock(MultipartFile::class.java)
        )

        val challenge = challengeService.findById(challengeId)

        sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
            imageUrl,
            challenge.id!!,
             challenge.title,
            challenge.description
        )

        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
        feedService.deleteAll()
        feedProjectionService.deleteAll()
    }

    @Test
    @DisplayName("인증에 성공한 챌린지는 피드로 작성할 수 있다.")
    fun createFeed() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate
            )
        )
        // when
        val feed: Feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = "testContent", publishDate = participantsStartDate)
        )
        // then
        assertThat(feed).isNotNull
        assertThat(feed.userChallengeId).isEqualTo(savedUserChallenge!!.id!!)
        assertThat(feed.content).isEqualTo(createFeedRequestDto.content)
        assertThat(feed.imageUrl).isEqualTo(createFeedRequestDto.imageUrl)
    }

    @Test
    @DisplayName("피드로 작성하는 챌린지는 사용자의 인증 사진을 피드에 올리게 된다.")
    fun createFeedWithImage() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        // when
        val feed: Feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = "testContent", publishDate = participantsStartDate)
        )
        // then
        assertThat(feed).isNotNull
        assertThat(feed.imageUrl)
            .isEqualTo(createFeedRequestDto.imageUrl)
            .isEqualTo(savedUserChallenge!!.getUserChallengeHistories().first().certificatedImageUrl)
    }

    @Test
    @DisplayName("피드의 내용은 1000자 이하로 작성할 수 있다.")
    fun createFeedWithContent() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        // when
        val feed: Feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = "testContent", publishDate = participantsStartDate)
        )
        // then
        assertThat(feed).isNotNull
        assertThat(feed.content!!.length).isLessThanOrEqualTo(1000)
    }

    @Test
    @DisplayName("피드의 내용은 1000자 이상으로 작성할 수 없다.")
    fun createFeedWithContentOverLimit() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        // when
        val exception = assertThrows<BadRequestException> {
            val feedContent = "test".repeat(251)
            assertThat(feedContent.length).isGreaterThan(1000)
            createFeedUseCase.execute(
                makeFeedRequestDto(content = feedContent, publishDate = participantsStartDate)
            )
        }
        // then
        assertThat(exception.message).isEqualTo(FeedException.CONTENT_OVER_LIMIT.message)
    }

    @Test
    @DisplayName("피드의 내용은 없어도 게시할 수 있다.")
    fun createFeedWithEmptyContent() =runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        // when
        val feed: Feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = null, publishDate = participantsStartDate)
        )
        // then
        assertThat(feed).isNotNull
        assertThat(feed.content).isNull()

        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate.plusDays(1),
            )
        )
        // when
        val nextFeed: Feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = "", publishDate = participantsStartDate.plusDays(1))
        )
        assertThat(nextFeed).isNotNull
        assertThat(nextFeed.content).isBlank
    }

    @Test
    @DisplayName("피드가 작성되어 피드 생성 이벤트가 게시되면, 피드 조회 전용 테이블에 피드가 복사되어 저장된다.")
    fun createFeedWithCopy() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = null, publishDate = participantsStartDate)
        )
        verify(applicationEventPublisher).publishEvent(eq(FeedCreatedEvent.fromEntity(feed)))
        // when
        val feedProjection = feedEventListener.createdFeedProjectionFrom(
            FeedCreatedEvent.fromEntity(feed)
        )
        // then
        assertThat(feedProjection).isNotNull
        assertThat(feedProjection.id).isEqualTo(feed.id!!)
        assertThat(feedProjection.feedContent).isEqualTo(feed.content)
        assertThat(feedProjection.challengeTitle).isEqualTo(savedUserChallenge!!.challenge.title)
        assertThat(feedProjection.challengeCategoryTitle).isEqualTo(savedUserChallenge!!.challenge.challengeCategory.title)
        assertThat(feedProjection.userName).isEqualTo(userName)
    }

    @Test
    @DisplayName("피드 수정 시, 피드의 내용을 1000자 이하로 수정할 수 있다.")
    fun updateFeedWithContent() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        val feedContent = "test"
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = feedContent, publishDate = participantsStartDate)
        )
        assertThat(feed.content).isEqualTo(feedContent)
        // when
        val newFeedContent = "renew".repeat(200)
        assertThat(newFeedContent.length).isEqualTo(1000)
        updateFeedUseCase.execute(
            feedId = feed.id!!,
            feedContent = newFeedContent
        )

        // then
        assertThat(feed.content).isEqualTo(newFeedContent)
        assertThat(feed.content!!.length).isEqualTo(1000)
    }

    @Test
    @DisplayName("피드 수정 시, 피드의 내용을 1000자 이상으로 수정할 수 없다.")
    fun updateFeedWithContentOverLimit() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        val feedContent = "test".repeat(250)
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = feedContent, publishDate = participantsStartDate)
        )
        // when
        val exception = assertThrows<BadRequestException> {
            val newFeedContent = "test".repeat(251)
            assertThat(newFeedContent.length).isGreaterThan(1000)
            updateFeedUseCase.execute(
                feedId = feed.id!!,
                feedContent = newFeedContent
            )
        }
        // then
        assertThat(exception.message).isEqualTo(FeedException.CONTENT_OVER_LIMIT.message)
    }

    @Test
    @DisplayName("피드를 삭제할 수 있다.")
    fun deleteFeed() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        val feedContent = "test".repeat(250)
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = feedContent, publishDate = participantsStartDate)
        )
        // when
        deleteFeedUseCase.execute(feedId = feed.id!!)
        // then
        val exception = assertThrows<NotFoundException> {
            feedService.findById(feed.id!!)
        }
        assertThat(exception.message).isEqualTo(FeedException.NOT_FOUND_FEED.message)
    }

    @Test
    @DisplayName("피드를 삭제하면 피드 조회 전용 테이블에서도 삭제된다.")
    fun deleteFeedWithCopy() = runTest {
        // given
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
            )
        )
        val feedContent = "test".repeat(250)
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = feedContent, publishDate = participantsStartDate)
        )
        // when
        deleteFeedUseCase.execute(feedId = feed.id!!)
        // then
        val exception = assertThrows<NotFoundException> {
            feedProjectionService.findById(feed.id!!)
        }
        assertThat(exception.message).isEqualTo(FeedException.NOT_FOUND_FEED_PROJECTION.message)
    }

    private fun makeFeedRequestDto(content: String?, publishDate: LocalDate): CreateFeedCommand {
        return CreateFeedCommand(
            userChallengeId = savedUserChallenge!!.id!!,
            content = content,
            imageUrl = imageUrl,
            publishDate = publishDate,
            userId = userId
        )
    }

    private fun makeCertificationSucceededEvent(
        certificateDate: LocalDate
    ): CertificationSucceededEvent {
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

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher {
            return mock(ApplicationEventPublisher::class.java)
        }
    }

}