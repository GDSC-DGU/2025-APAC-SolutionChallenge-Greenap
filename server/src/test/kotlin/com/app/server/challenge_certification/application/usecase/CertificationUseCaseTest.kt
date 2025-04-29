package com.app.server.challenge_certification.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.usecase.CertificationUseCase
import com.app.server.common.exception.BadRequestException
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.exception.UserChallengeException
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
@ExtendWith(SpringExtension::class)
class CertificationUseCaseTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @Autowired
    private lateinit var challengeService: ChallengeService

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
    }

    private fun makeUserChallengeAndHistory(startDate : LocalDate) : UserChallenge{
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
    @DisplayName("챌린지 인증에 성공하면 해당 날짜의 인증 상태를 변경할 수 있다.")
    fun completeChallenge() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        // when
        val todayUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, participantsStartDate)
        // then
        assertThat(todayUserChallenge.getUserChallengeHistories().first().status).isEqualTo(EUserChallengeCertificationStatus.SUCCESS)
    }

    @Test
    @DisplayName("챌린지 인증에 성공하면 전체 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        val pastUserChallenge : UserChallenge = makeUserChallengeAndHistory(participantsStartDate.minusDays(1))

        // when
        val todayUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, participantsStartDate)

        // then
        assertThat(todayUserChallenge.totalParticipationDayCount).isGreaterThan(pastUserChallenge.totalParticipationDayCount)
        assertThat(todayUserChallenge.totalParticipationDayCount - pastUserChallenge.totalParticipationDayCount).isOne
    }

    @Test
    @DisplayName("챌린지에 처음 인증에 성공했다면, 연속 참여 일수가 증가한다.")
    fun completeChallengeFirstTryWithIncreasingConsecutiveParticipationDays()  {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        val pastUserChallengeNowConsecutiveParticipationDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount
        val pastUserChallengeMaxConsecutiveParticipationDayCount = savedUserChallenge!!.maxConsecutiveParticipationDayCount

        // when
        val todayUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, participantsStartDate)
        // then
        assertThat(todayUserChallenge.nowConsecutiveParticipationDayCount).isGreaterThan(pastUserChallengeNowConsecutiveParticipationDayCount)
        assertThat(todayUserChallenge.nowConsecutiveParticipationDayCount - pastUserChallengeNowConsecutiveParticipationDayCount).isOne
        assertThat(todayUserChallenge.maxConsecutiveParticipationDayCount).isEqualTo(pastUserChallengeMaxConsecutiveParticipationDayCount)
    }

    @Test
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족한다면 연속 참여 일수가 증가한다.")
    fun completeChallengeWithIncreasingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        val certificationDate: LocalDate = LocalDate.now()
        val afterCertificationDate: LocalDate = LocalDate.now().plusDays(1)
        val todayUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, certificationDate)

        val todayUserChallengeNowConsecutiveParticipationDayCount = todayUserChallenge.nowConsecutiveParticipationDayCount
        // when
        val afterUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, afterCertificationDate)

        // then
        assertThat(afterUserChallenge.nowConsecutiveParticipationDayCount).isGreaterThan(todayUserChallengeNowConsecutiveParticipationDayCount)
        assertThat(afterUserChallenge.nowConsecutiveParticipationDayCount - todayUserChallengeNowConsecutiveParticipationDayCount).isOne
    }

    @Test
    @DisplayName("챌린지 인증에 성공했을 때 연속 참여 조건을 만족하지 않는다면 연속 참여 일수가 초기화된다.")
    fun completeChallengeWithResettingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        val certificationDate: LocalDate = LocalDate.now()
        val afterCertificationDate: LocalDate = LocalDate.now().plusDays(2)
        val todayUserChallenge : UserChallenge =
            certificationUseCase.certificateChallengeWithDate(certificationRequestDto, certificationDate)

        // when
        certificationUseCase.certificateChallengeWithDate(certificationRequestDto, afterCertificationDate)

        // then
        assertThat(todayUserChallenge.nowConsecutiveParticipationDayCount).isLessThan(todayUserChallenge.maxConsecutiveParticipationDayCount).isZero()
    }

    @Test
    @DisplayName("챌린지 인증에 실패하면 인증 실패임을 알린다.")
    fun failCertificated() {
        //given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.CERTIFICATED_FAILED
        )
        // when & then
        val exception = assertThrows<BadRequestException>{
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.FAILED_CERTIFICATION.message)
    }

    @Test
    @DisplayName("오늘 인증한 챌린지에 대해 인증을 시도하면 예외가 발생한다.")
    fun alreadyCertificatedToday() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        certificationUseCase.certificateChallengeWithDate(
            certificationRequestDto = certificationRequestDto,
            certificationDate = participantsStartDate
        )

        // when & then
        val exception = assertThrows<BadRequestException> {
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto = certificationRequestDto,
                certificationDate = participantsStartDate
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.ALREADY_CERTIFICATED.message)
    }
}