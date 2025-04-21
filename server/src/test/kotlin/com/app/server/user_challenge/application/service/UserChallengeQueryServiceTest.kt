package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.domain.model.Challenge
import com.app.server.user.application.repository.UserRepository
import com.app.server.user.domain.model.User
import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Transactional
@Rollback
@SpringBootTest
class UserChallengeQueryServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var userChallengeQueryService : UserChallengeQueryService
    @Autowired
    private lateinit var challengeService: ChallengeService
    @Autowired
    private lateinit var userChallengeRepository: UserChallengeRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @DisplayName("특정 챌린지를 완료한 참가자의 퍼센트를 확인할 수 있다.")
    fun getChallenge() {
        // given
        val challenge: Challenge = challengeService.findById(challengeId)
        val completedUser =
            User(
                nickname = "testUser1",
                email = "testEmail1@email.com",
                profileImageUrl = "testImageUrl1",
                nowMaxConsecutiveParticipationDayCount = 0L,
                refreshToken = null
            )
        val notCompletedUser =
            User(
                nickname = "testUser2",
                email = "testEmail2@email.com",
                profileImageUrl = "testImageUrl2",
                nowMaxConsecutiveParticipationDayCount = 0L,
                refreshToken = null
            )
        userRepository.save(completedUser)
        userRepository.save(notCompletedUser)
        userChallengeRepository.saveAll(
            listOf(
                UserChallenge(
                    user = completedUser,
                    challenge = challenge,
                    status = EUserChallengeStatus.COMPLETED,
                    participantDays = 7,
                    iceCount = 0,
                    nowConsecutiveParticipationDayCount = 0,
                    maxConsecutiveParticipationDayCount = 0,
                    totalParticipationDayCount = 0,
                    reportMessage = null
                ),
                UserChallenge(
                    user = notCompletedUser,
                    challenge = challenge,
                    status = EUserChallengeStatus.RUNNING,
                    participantDays = 7,
                    iceCount = 0,
                    nowConsecutiveParticipationDayCount = 0,
                    maxConsecutiveParticipationDayCount = 0,
                    totalParticipationDayCount = 0,
                    reportMessage = null
                )
            )
        )
        // when
        val completedUserPercent : Double = userChallengeQueryService.getChallengeCompletedUserPercent(challengeId)
        // then
        assertThat(completedUserPercent).isEqualTo(0.50)

    }
}