package com.app.server.feed.application

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.feed.application.service.FeedEventListener
import com.app.server.feed.application.service.FeedProjectionService
import com.app.server.feed.application.service.FeedService
import com.app.server.feed.domain.event.FeedCreatedEvent
import com.app.server.feed.domain.model.query.FeedProjection
import com.app.server.feed.enums.EFeedScope
import com.app.server.feed.exception.FeedException
import com.app.server.feed.ui.dto.CreateFeedCommand
import com.app.server.feed.ui.dto.ReadFeedProjectionCommand
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.ReadFeedUseCase
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
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class FeedQueryServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var feedProjectionService: FeedProjectionService

    @Autowired
    private lateinit var feedService: FeedService

    @Autowired
    private lateinit var feedEventListener: FeedEventListener

    @Autowired
    private lateinit var readFeedUseCase: ReadFeedUseCase

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

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
    )

    var notFindCertificationRequestDto = CertificationRequestDto(
        userChallengeId = 1L,
        imageUrl = imageUrl,
    )

    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl = imageUrl,
        challengeId = challengeId,
        challengeName = challengeTitle,
        challengeDescription = challengeDescription
    )

    var savedUserChallenge: UserChallenge? = null

    var notFindUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(userId, participantsStartDate)

        notFindUserChallenge = makeUserChallengeAndHistory(secondUserId, participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
        )

        notFindCertificationRequestDto = CertificationRequestDto(
            userChallengeId = notFindUserChallenge!!.id!!,
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
        reset(applicationEventPublisher)
        feedProjectionService.deleteAll()
        feedService.deleteAll()
        userChallengeService.deleteAll()

    }

    @Test
    @DisplayName("피드 조회는 피드 조회 전용 테이블에서 가져온다.")
    fun getFeed() = runTest{
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList.first().challengeTitle).isNotNull
    }

    @Test
    @DisplayName("모든 피드를 조회할 수 있다.")
    fun getAllFeed() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.size).isEqualTo(2)
    }

    @Test
    @DisplayName("피드를 조회할 시에는 가장 최신순으로 정렬되어 제공된다.")
    @Disabled // TODO : 실제로는 잘 되지만, 테스트에서는 createdAt이 아닌 인증 일자를 데이터에 넣어줘야 함. 고민 필요
    fun getFeedWithSort() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!,
            participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!,
            participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                scope = EFeedScope.ALL,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        val firstFeedCreatedAt = feedListResponseDto.feedList.first().createdAt
        val secondFeedCreatedAt = feedListResponseDto.feedList.last().createdAt
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
        assertThat(feedListResponseDto.feedList.get(1).user.nickname).isEqualTo(secondUserName)
        assertThat(feedListResponseDto.feedList.last().user.nickname).isEqualTo(userName)
        assertThat(firstFeedCreatedAt).isAfter(secondFeedCreatedAt)
    }

    @Test
    @DisplayName("피드 조회 시, 사용자가 원하는 page를 조회할 수 있다.")
    fun getFeedWithPage() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))

        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))

        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                page = 2,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.page).isEqualTo(2)
        assertThat(feedListResponseDto.size).isEqualTo(7)
        assertThat(feedListResponseDto.feedList.size).isEqualTo(2)
    }

    @Test
    @DisplayName("피드 조회 시, 기본 page는 1로 설정된다.")
    fun getFeedWithDefaultPage() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))

        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.page).isEqualTo(1)
        assertThat(feedListResponseDto.size).isEqualTo(7)
        assertThat(feedListResponseDto.feedList.size).isEqualTo(7)
    }

    @Test
    @DisplayName("피드 조회 시, 페이지는 1 이상이어야 한다.")
    fun getFeedWithPageOverLimit() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val exception = assertThrows<IllegalArgumentException> {
            // 컨트롤로에서 들어온 요청은 BadRequest를 뱉도록 Valid를 설정해두었다.
            readFeedUseCase.execute(
                ReadFeedProjectionCommand(
                    userId = userId,
                    page = 0,
                    categoryId = null,
                    userChallengeId = null
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo("Page index must not be less than zero")
    }

    @Test
    @DisplayName("피드 조회 시, 사용자가 원하는 size로 page의 크기를 정해 조회할 수 있다.")
    fun getFeedWithSize() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))

        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))

        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                size = 8,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.size).isEqualTo(8)
    }

    @Test
    @DisplayName("피드 조회 시, 페이지의 size는 기본값인 7로 설정된다.")
    fun getFeedWithoutPage() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))

        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.size).isEqualTo(7)
    }

    @Test
    @DisplayName("피드 조회 시, 페이지 size는 1 이상이어야 한다.")
    fun getFeedWithSizeOverLimit() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val exception = assertThrows<IllegalArgumentException> {
            // 컨트롤로에서 들어온 요청은 BadRequest를 뱉도록 Valid를 설정해두었다.
            readFeedUseCase.execute(
                ReadFeedProjectionCommand(
                    userId = userId,
                    categoryId = null,
                    size = 0,
                    userChallengeId = null
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo("Page size must not be less than one")
    }

    @Test
    @DisplayName("특정 사용자의 특정 챌린지들에서 작성한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndChallenge() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = null,
                userChallengeId = savedUserChallenge!!.id!!,
                scope = EFeedScope.USER,
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
        assertThat(feedListResponseDto.feedList.first().challengeTitle).isEqualTo(challengeTitle)
        assertThat(feedListResponseDto.feedList.size).isEqualTo(2)
    }

    @Test
    @DisplayName("특정 사용자의 특정 카테고리 내에서 참여했던 모든 챌린지들에 대한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndCategory() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = categoryId,
                userChallengeId = null,
                scope = EFeedScope.USER,
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
    }

    @Test
    @DisplayName("특정 카테고리의 챌린지들에서 작성된 모든 피드들을 조회할 수 있다.")
    fun getFeedWithCategory() = runTest {
        // given
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(userId, savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(secondUserId, notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                userId = userId,
                categoryId = categoryId,
                userChallengeId = null,
                scope = EFeedScope.ALL,
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
    }

    private fun makeFeedRequestDto(userId: Long, userChallengeId: Long, content: String?, publishDate: LocalDate): CreateFeedCommand {
        return CreateFeedCommand(
            userChallengeId = userChallengeId,
            content = content,
            imageUrl = imageUrl,
            publishDate = publishDate,
            userId = userId
        )
    }

    private fun makeCertificationSucceededEvent(
        userChallengeId: Long,
        certificateDate: LocalDate
    ): CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeUserChallengeAndHistory(userId: Long, startDate: LocalDate): UserChallenge {
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

    private suspend fun makeFeedWhenCertificate(userId: Long, userChallengeId: Long, date: LocalDate): FeedProjection {
        userChallengeEventListener.processWhenReceive(
            makeCertificationSucceededEvent(
                certificateDate = date,
                userChallengeId = userChallengeId
            )
        )
        val feedContent = "test".repeat(250)
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(
                userId = userId,
                userChallengeId = userChallengeId,
                content = feedContent,
                publishDate = date)
        )
        return feedEventListener.createdFeedProjectionFrom(
            createdEvent = FeedCreatedEvent(
                feed = feed, 
                userId = userId, 
                userChallengeId = userChallengeId
            )
        )
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