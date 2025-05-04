package com.app.server.rank.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.rank.ui.usecase.GetSpecificChallengeTotalRankUseCase
import com.app.server.rank.ui.usecase.GetTotalRankUseCase
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
class RankEventListenerTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var rankEventListener: RankEventListener

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var getTotalRankUseCase: GetTotalRankUseCase

    @Autowired
    private lateinit var getSpecificChallengeTotalRankUseCase: GetSpecificChallengeTotalRankUseCase

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var eventPublisher: ApplicationEventPublisher

    var savedUserChallenge: UserChallenge? = null
    var savedSameUserToAnotherUserChallenge: UserChallenge? = null
    var savedAnotherUserToSameUserChallenge: UserChallenge? = null
    var savedAnotherUserToAnotherUserChallenge: UserChallenge? = null


    @BeforeEach
    fun setUp() = runTest {
        if(!redisContainer.isRunning) redisContainer.start()

        savedUserChallenge =
            makeUserChallengeAndHistory(userId, challengeId, participantsStartDate)
        savedSameUserToAnotherUserChallenge =
            makeUserChallengeAndHistory(userId, challengeId+1, participantsStartDate)
        savedAnotherUserToSameUserChallenge =
            makeUserChallengeAndHistory(secondUserId, challengeId, participantsStartDate)
        savedAnotherUserToAnotherUserChallenge =
            makeUserChallengeAndHistory(secondUserId, challengeId+2, participantsStartDate)
    }

    @AfterEach
    fun tearDown() = runTest {
        userChallengeService.deleteAll()
        reset(eventPublisher)
    }

    private fun makeEvent(userChallengeId: Long, certificateDate: LocalDate)
            : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private suspend fun makeSpecificRank(userChallenge: UserChallenge, certificateDate: LocalDate) {
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

    private suspend fun makeTotalRank(userChallenge: UserChallenge, certificateDate: LocalDate) {
        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                userChallenge.id!!,
                certificateDate
            )
        )
        rankEventListener.updateTotalRank(
            userChallengeId = userChallenge.id!!,
             maxConsecutiveParticipationDayCount = userChallenge.maxConsecutiveParticipationDayCount
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

    @Test
    @DisplayName("챌린지에 인증하면 전체 랭크가 최대 연속 참여일 기준으로 업데이트 된다.")
    fun updateTotalRank() = runTest {
        // given
        makeTotalRank(savedUserChallenge!!, participantsStartDate)
        makeTotalRank(savedUserChallenge!!, participantsStartDate.plusDays(1))
        makeTotalRank(savedUserChallenge!!, participantsStartDate.plusDays(2))

        makeTotalRank(savedAnotherUserToSameUserChallenge!!, participantsStartDate)
        makeTotalRank(savedAnotherUserToSameUserChallenge!!, participantsStartDate.plusDays(2))

        makeTotalRank(savedAnotherUserToAnotherUserChallenge!!, participantsStartDate.plusDays(3))
        // when
        val result = getTotalRankUseCase.execute()
        // then
        assertThat(result.totalParticipantsCount).isEqualTo(2)
        assertThat(result.topParticipants.size).isEqualTo(2)
        assertThat(result.topParticipants[0].user.nickname).isEqualTo(userName)
        assertThat(result.topParticipants[0].user.longestConsecutiveParticipationCount).isEqualTo(3)
        assertThat(result.topParticipants[1].user.nickname).isEqualTo(secondUserName)
        assertThat(result.topParticipants[1].user.longestConsecutiveParticipationCount).isEqualTo(1)
    }

    @Test
    @DisplayName("챌린지에 인증하면 해당 챌린지 랭크가 총 참여일을 기준으로 업데이트 된다.")
    fun updateSpecificChallengeRank() = runTest{
        // given
        makeSpecificRank(savedUserChallenge!!, participantsStartDate)
        makeSpecificRank(savedUserChallenge!!, participantsStartDate.plusDays(1))
        makeSpecificRank(savedUserChallenge!!, participantsStartDate.plusDays(2))

        makeSpecificRank(savedAnotherUserToSameUserChallenge!!, participantsStartDate)
        makeSpecificRank(savedAnotherUserToSameUserChallenge!!, participantsStartDate.plusDays(2))

        makeSpecificRank(savedAnotherUserToAnotherUserChallenge!!, participantsStartDate.plusDays(3))

        // when
        val result = getSpecificChallengeTotalRankUseCase.execute(challengeId)

        // then
        assertThat(result.topParticipants.size).isEqualTo(2)
        assertThat(result.totalParticipantsCount).isEqualTo(2)
        assertThat(result.challengeTitle).isEqualTo(challengeTitle)
        assertThat(result.topParticipants[0].user.nickname).isEqualTo(userName)
        assertThat(result.topParticipants[0].user.totalParticipationCount).isEqualTo(3)
        assertThat(result.topParticipants[1].user.nickname).isEqualTo(secondUserName)
        assertThat(result.topParticipants[1].user.totalParticipationCount).isEqualTo(2)

    }

}