package com.app.server.rank.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.domain.event.CertificationSucceededEvent
import com.app.server.rank.application.service.RankEventListener
import com.app.server.rank.enums.RankState
import com.app.server.rank.ui.usecase.GetUserSpecificChallengeTotalRankUseCase
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
class GetUserSpecificChallengeTotalRankUseCaseTest : IntegrationTestContainer(){

    @Autowired
    private lateinit var rankEventListener: RankEventListener

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var getUserSpecificChallengeTotalRankUseCase: GetUserSpecificChallengeTotalRankUseCase

    @Autowired
    private lateinit var userChallengeEventListener: UserChallengeEventListener

    @MockitoBean
    private lateinit var eventPublisher: ApplicationEventPublisher

    var savedUserChallenge: UserChallenge? = null
    var savedSameUserToAnotherChallenge: UserChallenge? = null
    var savedAnotherUserToSameChallenge: UserChallenge? = null
    var savedAnotherUserToAnotherChallenge: UserChallenge? = null

    @BeforeEach
    fun setUp() = runTest {
        if(!redisContainer.isRunning) redisContainer.start()

        savedUserChallenge = makeUserChallengeAndHistory(userId, challengeId, participantsStartDate)
        savedSameUserToAnotherChallenge = makeUserChallengeAndHistory(userId, challengeId+1, participantsStartDate)
        savedAnotherUserToSameChallenge = makeUserChallengeAndHistory(secondUserId, challengeId, participantsStartDate)
        savedAnotherUserToAnotherChallenge = makeUserChallengeAndHistory(secondUserId, challengeId+1, participantsStartDate)
    }

    @AfterEach
    fun tearDown() = runTest {
        userChallengeService.deleteAll()
        reset(eventPublisher)
    }

    @Test
    @DisplayName("특정 챌린지의 전체 랭킹 중 사용자의 랭킹만 따로 조회할 수 있다.")
    fun getUserSpecificChallengeTotalRank() = runTest {
        // given
        makeSpecificRank(savedUserChallenge!!, participantsStartDate)
        makeSpecificRank(savedUserChallenge!!, participantsStartDate.plusDays(1))
        makeSpecificRank(savedUserChallenge!!, participantsStartDate.plusDays(2))

        makeSpecificRank(savedAnotherUserToSameChallenge!!, participantsStartDate)
        makeSpecificRank(savedAnotherUserToSameChallenge!!, participantsStartDate.plusDays(2))

        makeSpecificRank(savedAnotherUserToAnotherChallenge!!, participantsStartDate.plusDays(3))
        // when
        val result = getUserSpecificChallengeTotalRankUseCase.execute(
            challengeId = challengeId,
            userId = userId
        )
        // then
        assertThat(result.challengeTitle).isEqualTo(challengeTitle)
        assertThat(result.userRankInfo.user.nickname).isEqualTo(userName)
        assertThat(result.userRankInfo.user.totalParticipationCount).isEqualTo(3)
        assertThat(result.userRankInfo.rank).isEqualTo(1)

        // when
        val anotherResult = getUserSpecificChallengeTotalRankUseCase.execute(
            challengeId = challengeId,
            userId = secondUserId
        )
        // then
        assertThat(anotherResult.challengeTitle).isEqualTo(challengeTitle)
        assertThat(anotherResult.userRankInfo.rank).isEqualTo(2)
        assertThat(anotherResult.userRankInfo.user.nickname).isEqualTo(secondUserName)
        assertThat(anotherResult.userRankInfo.user.totalParticipationCount).isEqualTo(2)

        // when
        val thirdResult = getUserSpecificChallengeTotalRankUseCase.execute(
                challengeId = challengeId + 1,
                userId = secondUserId
        )
        // then
        assertThat(thirdResult.userRankInfo.rank).isEqualTo(1)
        assertThat(thirdResult.userRankInfo.user.nickname).isEqualTo(secondUserName)
        assertThat(thirdResult.userRankInfo.user.totalParticipationCount).isEqualTo(1)
    }

    private fun makeEvent(userChallengeId: Long, certificateDate: LocalDate)
            : CertificationSucceededEvent {
        return CertificationSucceededEvent(
            userChallengeId = userChallengeId,
            imageUrl = imageUrl,
            certificatedDate = certificateDate
        )
    }

    private fun makeSpecificRank(userChallenge: UserChallenge, certificateDate: LocalDate): RankState {
        userChallengeEventListener.processWhenReceive(
            event = makeEvent(
                userChallenge.id!!,
                certificateDate
            )
        )
        return rankEventListener.updateSpecificChallengeRank(
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