package com.app.server.user_challenge.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge.domain.model.Challenge
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.event.CertificationSucceededEvent
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.user.application.repository.UserRepository
import com.app.server.user.domain.model.User
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.dto.ReceiveReportResponseDto
import com.app.server.user_challenge.application.repository.UserChallengeRepository
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.enums.EUserReportResultCode
import com.app.server.user_challenge.event.ReportCreatedEvent
import com.app.server.user_challenge.infra.ReportInfraService
import com.app.server.user_challenge.ui.dto.SendToReportServerRequestDto
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito.mock
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

@Transactional
@Rollback
@SpringBootTest
class UserChallengeQueryServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var userChallengeQueryService: UserChallengeQueryService

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeRepository: UserChallengeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @MockitoBean
    private lateinit var reportInfraService: ReportInfraService

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
    var sendToReportServerRequestDto = SendToReportServerRequestDto(
        userId = userId.toString(),
        challengeTitle = challengeTitle,
        progress = 80,
        totalDay = participationDays
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
        sendToReportServerRequestDto = SendToReportServerRequestDto(
            userId = userId.toString(),
            challengeTitle = challenge.title,
            progress = savedUserChallenge!!.totalParticipationDayCount.floorDiv(
                savedUserChallenge!!.participantDays.toLong()
            ).toInt(),
            totalDay = participationDays
        )
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
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
        val saveCompletedUser: User = userRepository.save(completedUser)
        val saveUnCompletedUser: User = userRepository.save(notCompletedUser)

        val completedUserChallenge: UserChallenge = UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = saveCompletedUser.id!!,
                challenge = challenge,
                participantsDate = 7,
                status = EUserChallengeStatus.COMPLETED
            )
        )
        val unCompletedUserChallenge: UserChallenge = UserChallenge.createEntity(
            CreateUserChallengeDto(
                userId = saveUnCompletedUser.id!!,
                challenge = challenge,
                participantsDate = 7,
                status = EUserChallengeStatus.RUNNING
            )
        )
        userChallengeRepository.saveAll(listOf(completedUserChallenge, unCompletedUserChallenge))
        // when
        val completedUserPercent: Double = userChallengeQueryService.getChallengeCompletedUserPercent(challengeId)
        // then
        assertThat(completedUserPercent).isEqualTo(0.50)

    }
}