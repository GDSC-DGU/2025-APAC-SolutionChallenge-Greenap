package com.app.server.feed.application

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.common.exception.BadRequestException
import com.app.server.common.exception.NotFoundException
import com.app.server.feed.application.service.FeedEventListener
import com.app.server.feed.application.service.FeedProjectionService
import com.app.server.feed.application.service.FeedService
import com.app.server.feed.domain.model.command.Feed
import com.app.server.feed.enums.EFeedScope
import com.app.server.feed.event.FeedCreatedEvent
import com.app.server.feed.exception.FeedException
import com.app.server.feed.ui.dto.CreateFeedCommand
import com.app.server.feed.ui.dto.CreateFeedRequestDto
import com.app.server.feed.ui.dto.ReadFeedProjectionCommand
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.DeleteFeedUseCase
import com.app.server.feed.ui.usecase.ReadFeedUseCase
import com.app.server.feed.ui.usecase.UpdateFeedUseCase
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.argThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.hamcrest.MockitoHamcrest.argThat
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
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class FeedCommandServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var readFeedUseCase: ReadFeedUseCase

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

    var createFeedRequestDto = CreateFeedRequestDto(
        userChallengeId = userChallengeId,
        content = "testContent",
        imageUrl = imageUrl,
        publishDate = participantsStartDate
    )

    @BeforeEach
    fun setUp() {

        savedUserChallenge = makeUserChallengeAndHistory(userId, participantsStartDate)

        notFindUserChallenge = makeUserChallengeAndHistory(notFindUserId, participantsStartDate.plusDays(1))

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
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    @Test
    @DisplayName("인증에 성공한 챌린지는 피드로 작성할 수 있다.")
    fun createFeed() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun createFeedWithImage() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun createFeedWithContent() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun createFeedWithContentOverLimit() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun createFeedWithEmptyContent() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate.plusDays(1),
                userChallengeId = savedUserChallenge!!.id!!
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
    fun createFeedWithCopy() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
            )
        )
        val feed = createFeedUseCase.execute(
            makeFeedRequestDto(content = null, publishDate = participantsStartDate)
        )
        verify(applicationEventPublisher).publishEvent(eq(FeedCreatedEvent.fromEntity(feed)))
        // when
        val feedProjection = feedEventListener.handleCreatedFeedEvent(
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
    fun updateFeedWithContent() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun updateFeedWithContentOverLimit() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun deleteFeed() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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
    fun deleteFeedWithCopy() {
        // given
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = participantsStartDate,
                userChallengeId = savedUserChallenge!!.id!!
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

    @Test
    @DisplayName("피드 조회는 피드 조회 전용 테이블에서 가져온다.")
    fun getFeed() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList.first().challengeTitle).isNotNull
    }

    @Test
    @DisplayName("모든 피드를 조회할 수 있다.")
    fun getAllFeed() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.last().user.nickname).isEqualTo(userName)
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(notFindUserName)
    }

    @Test
    @DisplayName("카테고리별로 사용자가 작성한 피드를 필터링하여 조회할 수 있다.")
    fun getFeedWithCategory() {
        // given
        makeFeedWhenCertificate(
            savedUserChallenge!!.id!!,
            participantsStartDate
        )
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = categoryName,
                scope = EFeedScope.USER.name,
                userChallengeId = null
            )
        )
        val failureFindFeedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = "No Category",
                scope = EFeedScope.USER.name,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().category).isEqualTo(categoryName)
        assertThat(failureFindFeedListResponseDto.feedList).isEmpty()
        assertThat(failureFindFeedListResponseDto.feedList.firstOrNull()).isNull()
    }

    @Test
    @DisplayName("피드를 조회할 시에는 가장 최신순으로 정렬되어 제공된다.")
    fun getFeedWithSort() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!,
            participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!,
            participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                scope = EFeedScope.ALL.name,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        val firstFeedCreatedAt = LocalDate.parse(feedListResponseDto.feedList.first().createdAt)
        val secondFeedCreatedAt = LocalDate.parse(feedListResponseDto.feedList.last().createdAt)
        assertThat(firstFeedCreatedAt).isAfter(secondFeedCreatedAt)
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
        assertThat(feedListResponseDto.feedList.get(1).user.nickname).isEqualTo(notFindUserName)
        assertThat(feedListResponseDto.feedList.last().user.nickname).isEqualTo(userName)
    }

    @Test
    @DisplayName("피드 조회 시, 사용자가 원하는 page를 조회할 수 있다.")
    fun getFeedWithPage() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(5))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(6))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                page = 2,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isEmpty()
        assertThat(feedListResponseDto.page).isEqualTo(2)
        assertThat(feedListResponseDto.size).isEqualTo(4)
    }

    @Test
    @DisplayName("피드 조회 시, 기본 page는 1로 설정된다.")
    fun getFeedWithDefaultPage() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(5))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(6))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isEmpty()
        assertThat(feedListResponseDto.page).isEqualTo(1)
        assertThat(feedListResponseDto.size).isEqualTo(7)
    }

    @Test
    @DisplayName("피드 조회 시, 페이지는 1부터 시작한다.")
    fun getFeedWithPageOverLimit() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val exception = assertThrows<BadRequestException> {
            readFeedUseCase.execute(
                ReadFeedProjectionCommand(
                    categoryName = null,
                    page = 0,
                    userChallengeId = null
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(FeedException.PAGE_OVER_LIMIT.message)
    }

    @Test
    @DisplayName("피드 조회 시, 사용자가 원하는 size로 page의 크기를 정해 조회할 수 있다.")
    fun getFeedWithSize() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(5))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(6))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))

        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
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
    fun getFeedWithoutPage() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(4))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(5))
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(6))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(2))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate.plusDays(3))
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                userChallengeId = null
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.size).isEqualTo(7)
    }

    @Test
    @DisplayName("피드 조회 시, 페이지 size는 1 이상이어야 한다.")
    fun getFeedWithSizeOverLimit() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        // when
        val exception = assertThrows<BadRequestException> {
            readFeedUseCase.execute(
                ReadFeedProjectionCommand(
                    categoryName = null,
                    size = 0,
                    userChallengeId = null
                )
            )
        }
        // then
        assertThat(exception.message).isEqualTo(FeedException.SIZE_OVER_LIMIT.message)
    }

    @Test
    @DisplayName("특정 사용자의 특정 챌린지들에서 작성한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndChallenge() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = null,
                userChallengeId = savedUserChallenge!!.id!!,
                scope = EFeedScope.USER.name,
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
        assertThat(feedListResponseDto.feedList.first().challengeTitle).isEqualTo(challengeTitle)
        assertThat(feedListResponseDto.feedList.size).isEqualTo(1)
    }

    @Test
    @DisplayName("특정 사용자의 특정 카테고리 내에서 참여했던 모든 챌린지들에 대한 피드들만 조회할 수 있다.")
    fun getFeedWithUserAndCategory() {
        // given
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate)
        makeFeedWhenCertificate(savedUserChallenge!!.id!!, participantsStartDate.plusDays(1))
        makeFeedWhenCertificate(notFindUserChallenge!!.id!!, participantsStartDate)
        // when
        val feedListResponseDto = readFeedUseCase.execute(
            ReadFeedProjectionCommand(
                categoryName = categoryName,
                userChallengeId = null,
                scope = EFeedScope.USER.name,
            )
        )
        // then
        assertThat(feedListResponseDto.feedList).isNotEmpty
        assertThat(feedListResponseDto.feedList.first().user.nickname).isEqualTo(userName)
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

    private fun makeFeedRequestDto(content: String?, publishDate: LocalDate): CreateFeedCommand {
        return CreateFeedCommand(
            userChallengeId = savedUserChallenge!!.id!!,
            content = content,
            imageUrl = imageUrl,
            publishDate = publishDate,
            userId = userId
        )
    }

    private fun makeFeedWhenCertificate(userChallengeId: Long, date: LocalDate): Feed {
        userChallengeEventListener.handleCertificationSucceededEventForTest(
            makeCertificationSucceededEvent(
                certificateDate = date,
                userChallengeId = userChallengeId
            )
        )
        val feedContent = "test".repeat(250)
        return createFeedUseCase.execute(
            makeFeedRequestDto(content = feedContent, publishDate = date)
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