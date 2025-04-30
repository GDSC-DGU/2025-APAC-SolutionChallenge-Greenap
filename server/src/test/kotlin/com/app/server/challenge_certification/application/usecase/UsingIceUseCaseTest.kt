package com.app.server.challenge_certification.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.dto.UserChallengeIceRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.user_challenge.ui.usecase.UsingIceUseCase
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
class UsingIceUseCaseTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var usingIceUseCase: UsingIceUseCase

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeService : UserChallengeService

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    var userChallengeIceRequestDto = UserChallengeIceRequestDto(
        userChallengeId = 1L
    )

    val requiredSuccessfulDaysForIce = (participationDays).floorDiv(2).plus(1)
    val notSufficientSuccessfulDaysForIce = (participationDays).floorDiv(2).minus(1)

    var certificationRequestDto = CertificationRequestDto(
        userChallengeId = userChallengeId,
        imageUrl = imageUrl,
    )

    var savedUserChallenge: UserChallenge? = null
    var sendToCertificationServerRequestDto = SendToCertificationServerRequestDto(
        imageUrl = imageUrl,
        challengeId = challengeId,
        challengeName = challengeTitle,
        challengeDescription = challengeDescription
    )

    @BeforeEach
    fun setUpChallengeFixture() {
        savedUserChallenge = makeUserChallengeAndHistory(participantsStartDate)

        certificationRequestDto = CertificationRequestDto(
            userChallengeId = savedUserChallenge!!.id!!,
            imageUrl = imageUrl,
        )

        userChallengeIceRequestDto = UserChallengeIceRequestDto(
            userChallengeId = savedUserChallenge!!.id!!
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

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
    }

    @Test
    @DisplayName("얼리기는 전체 참여 기간의 50% 이상 성공했을 때, 인증 대신 얼리기를 사용하여 인증을 건너뛸 수 있다.")
    fun skipChallengeWithLimit() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        for ( i in 0 until requiredSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))

        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
    }

    @Test
    @DisplayName("전체 참여 기간의 50% 이상을 성공하지 못했다면 얼리기를 사용할 수 없다.")
    fun skipChallengeWithLimitFailed() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        for ( i in 0 until notSufficientSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))
        // when
        val exception = assertThrows<BadRequestException> {
            usingIceUseCase.processAfterCertificateIce(
                iceRequestDto = userChallengeIceRequestDto,
                certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_USE_ICE.message)

    }


    @Test
    @DisplayName("얼리기를 사용하면 전체 참여일은 올라가지 않는다.")
    fun skipChallengeWithoutIncreasingParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        for ( i in 0 until requiredSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))

        val existTotalParticipantDayCount = savedUserChallenge!!.totalParticipationDayCount
        // when
        val userChallenge: UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.totalParticipationDayCount).isEqualTo(existTotalParticipantDayCount)
    }

    @Test
    @DisplayName("얼리기를 사용했을 때 연속 참여 조건을 만족한다면 연속 참여 일수가 증가한다.")
    fun skipChallengeWithIncreasingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        for ( i in 0 until requiredSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))
        val existNowConsecutiveParticipantDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        val userChallenge : UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isGreaterThan(existNowConsecutiveParticipantDayCount)
        assertThat(userChallenge.nowConsecutiveParticipationDayCount - existNowConsecutiveParticipantDayCount).isOne
    }

    @Test
    @DisplayName("얼리기를 사용했을 때 연속 참여 조건을 만족하지 않는다면 연속 참여 일수는 증가하지 않는다.")
    fun skipChallengeWithoutIncreasingConsecutiveParticipationDays() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )
        for ( i in 0 until requiredSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))
        val existNowConsecutiveParticipantDayCount = savedUserChallenge!!.nowConsecutiveParticipationDayCount
        // when
        val userChallenge : UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.plus(1).toLong())
        )
        // then
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce-1).status).isEqualTo(
            EUserChallengeCertificationStatus.SUCCESS
        )
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.FAILED
        )
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce+1).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isLessThan(existNowConsecutiveParticipantDayCount)
        assertThat(userChallenge.nowConsecutiveParticipationDayCount).isZero
    }


    @Test
    @DisplayName("얼리기를 한번 사용하면 해당 챌린지에서는 다시 얼리기를 사용할 수 없다.")
    fun skipChallengeWithIceCount() {
        // given
        given(certificationInfraService.certificate(sendToCertificationServerRequestDto)).willReturn(
            EUserCertificatedResultCode.SUCCESS_CERTIFICATED
        )

        for ( i in 0 until requiredSuccessfulDaysForIce)
            certificationUseCase.certificateChallengeWithDate(userId, certificationRequestDto, participantsStartDate.plusDays(i.toLong()))

        val userChallenge : UserChallenge = usingIceUseCase.processAfterCertificateIce(
            iceRequestDto = userChallengeIceRequestDto,
            certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.toLong())
        )

        // when
        assertThat(userChallenge.getUserChallengeHistories().get(requiredSuccessfulDaysForIce).status).isEqualTo(
            EUserChallengeCertificationStatus.ICE
        )

        val exception = assertThrows<BadRequestException>{
            usingIceUseCase.processAfterCertificateIce(
                iceRequestDto = userChallengeIceRequestDto,
                certificationDate = participantsStartDate.plusDays(requiredSuccessfulDaysForIce.plus(1).toLong())
            )
        }
        assertThat(exception.message).isEqualTo(UserChallengeException.CANNOT_USE_ICE.message)

    }
}