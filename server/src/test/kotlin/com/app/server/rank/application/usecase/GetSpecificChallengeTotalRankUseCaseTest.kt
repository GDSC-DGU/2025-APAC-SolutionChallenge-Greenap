package com.app.server.rank.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.exception.ChallengeException
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.common.exception.NotFoundException
import com.app.server.rank.application.service.RankEventListener
import com.app.server.rank.ui.usecase.GetSpecificChallengeTotalRankUseCase
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeEventListener
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
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
class GetSpecificChallengeTotalRankUseCaseTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var rankEventListener: RankEventListener

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var getSpecificChallengeTotalRankUseCase: GetSpecificChallengeTotalRankUseCase

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var eventPublisher: ApplicationEventPublisher

    var savedUserChallenge: UserChallenge? = null
    var savedSameUserToAnotherUserChallenge: UserChallenge? = null
    var savedAnotherUserToSameUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() = runTest {
        if(!redisContainer.isRunning) redisContainer.start()

        savedUserChallenge = makeUserChallengeAndHistory(userId, challengeId, participantsStartDate)
        savedSameUserToAnotherUserChallenge = makeUserChallengeAndHistory(userId, challengeId+1, participantsStartDate)
        savedAnotherUserToSameUserChallenge = makeUserChallengeAndHistory(secondUserId, challengeId, participantsStartDate)
    }

    @AfterEach
    fun tearDown() = runTest {
        userChallengeService.deleteAll()
        reset(eventPublisher)
    }

    @Test
    @DisplayName("특정 챌린지 내의 전체 랭킹을 1등부터 100등까지 조회할 수 있다.")
    fun getSpecificChallengeTotalRank() = runTest {
        // given
        makeSpecificRank(savedUserChallenge!!, participantsStartDate)
        makeSpecificRank(savedUserChallenge!!, participantsStartDate.plusDays(1))
        makeSpecificRank(savedAnotherUserToSameUserChallenge!!, participantsStartDate)

        // when
        val result = getSpecificChallengeTotalRankUseCase.execute(challengeId)

        // then
        assertThat(result.challengeTitle).isEqualTo(challengeTitle)
        assertThat(result.totalParticipantsCount).isEqualTo(2)
        assertThat(result.topParticipants).hasSize(2)
        assertThat(result.topParticipants[0].rank).isEqualTo(1)
        assertThat(result.topParticipants[0].user.nickname).isEqualTo(userName)
        assertThat(result.topParticipants[0].user.totalParticipationCount).isEqualTo(2)
        assertThat(result.topParticipants[1].rank).isEqualTo(2)
        assertThat(result.topParticipants[1].user.nickname).isEqualTo(secondUserName)
        assertThat(result.topParticipants[1].user.totalParticipationCount).isEqualTo(1)


        // given
        makeSpecificRank(savedSameUserToAnotherUserChallenge!!, participantsStartDate.plusDays(3))
        // when
        val anotherResult = getSpecificChallengeTotalRankUseCase.execute(challengeId + 1)

        delay(1000)

        // then
        assertThat(anotherResult.challengeTitle).isNotEqualTo(result.challengeTitle)
        assertThat(anotherResult.totalParticipantsCount).isEqualTo(1)
        assertThat(anotherResult.topParticipants).hasSize(1)
        assertThat(anotherResult.topParticipants[0].rank).isEqualTo(1)
        assertThat(anotherResult.topParticipants[0].user.nickname).isEqualTo(userName)
        assertThat(anotherResult.topParticipants[0].user.totalParticipationCount).isEqualTo(1)
    }

    @Test
    @DisplayName("챌린지 ID가 존재하지 않으면 예외가 발생한다.")
    fun getSpecificChallengeTotalRankWithNonExistentChallengeId() = runTest {
        // given
        val challengeId = 999L // 존재하지 않는 챌린지 ID

        // when
        val exception = assertThrows<NotFoundException> {
            getSpecificChallengeTotalRankUseCase.execute(challengeId)
        }

        // then
        assertThat(exception.message).isEqualTo(ChallengeException.NOT_FOUND.message)
    }

    @Test
    @DisplayName("해당 챌린지에 아무 사용자도 이용 중이 아니라면 빈 리스트를 반환한다.")
    fun getSpecificChallengeTotalRankWithNoUsers() = runTest {
        // given
        val noOneUseChallengeId = 3L
        // when
        val result = getSpecificChallengeTotalRankUseCase.execute(noOneUseChallengeId)

        // then
        assertThat(result.challengeTitle).isNotEmpty
        assertThat(result.totalParticipantsCount).isZero()
        assertThat(result.topParticipants).isEmpty()
    }

    private fun makeEvent(userChallengeId: Long, certificateDate: LocalDate)
            : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeSpecificRank(userChallenge: UserChallenge, certificateDate: LocalDate) {
        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                userChallenge.id!!,
                certificateDate
            )
        )
        rankEventListener.updateSpecificChallengeRank(
            userChallengeId = userChallenge.id!!,
            totalParticipationDayCount = userChallenge.totalParticipationDayCount
        )
    }

    private fun makeUserChallengeAndHistory(userId: Long, challengeId: Long, startDate: LocalDate): UserChallenge {
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