package com.app.server.user_challenge.application.usecase

import com.app.server.IntegrationTestContainer
import com.app.server.challenge.application.service.ChallengeService
import com.app.server.challenge_certification.application.service.CertificationService
import com.app.server.challenge_certification.enums.EUserCertificatedResultCode
import com.app.server.challenge_certification.infra.CertificationInfraService
import com.app.server.challenge_certification.ui.dto.request.CertificationRequestDto
import com.app.server.challenge_certification.ui.dto.request.SendToCertificationServerRequestDto
import com.app.server.challenge_certification.ui.usecase.CertificationUseCase
import com.app.server.core.infra.cloud_storage.CloudStorageUtil
import com.app.server.user_challenge.application.dto.CreateUserChallengeDto
import com.app.server.user_challenge.application.service.UserChallengeService
import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.enums.EUserChallengeStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import com.app.server.user_challenge.ui.dto.GetTotalUserChallengeResponseDto
import com.app.server.user_challenge.ui.usecase.GetTotalUserChallengeUseCase
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import kotlin.text.Charsets.UTF_8

@SpringBootTest
@Transactional
@Rollback
class GetTotalUserChallengeUseCaseTest : IntegrationTestContainer() {

    @MockitoBean
    private lateinit var cloudStorageUtil: CloudStorageUtil

    @MockitoSpyBean
    private lateinit var certificationService: CertificationService

    @MockitoBean
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userChallengeService: UserChallengeService

    @MockitoBean
    private lateinit var certificationInfraService: CertificationInfraService

    @Autowired
    private lateinit var getTotalUserChallengeUseCase: GetTotalUserChallengeUseCase

    @Autowired
    private lateinit var certificationUseCase: CertificationUseCase

    val newChallengeId: Long = 2L
    var completedUserChallenge: UserChallenge? = null

    @BeforeEach
    fun setup() {
        completedUserChallenge = makeUserChallengeAndHistory(challengeId, participantsStartDate)
        val certificationRequestDto = CertificationRequestDto(
            userChallengeId = completedUserChallenge!!.id!!,
            image = MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test".toByteArray(UTF_8)
            )
        )
        given(certificationInfraService.certificate(any())).willReturn(
            mapOf(EUserCertificatedResultCode.SUCCESS_CERTIFICATED to "Test")
        )
        given(cloudStorageUtil.uploadImageToCloudStorage(any(), any()))
            .willReturn(imageUrl)

        doReturn(imageUrl).`when`(certificationService).encodeImageToBase64(
            MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test".toByteArray(UTF_8)
            )
        )

        for (i in 0 until completedUserChallenge!!.participantDays) {
            certificationUseCase.certificateChallengeWithDate(
                certificationRequestDto,
                participantsStartDate.plusDays(i.toLong())
            )
        }
    }

    @AfterEach
    fun tearDown() {
        userChallengeService.deleteAll()
        reset(applicationEventPublisher)
    }

    private fun makeUserChallengeAndHistory(challengeSelectId: Long, startDate: LocalDate): UserChallenge {
        val mainTestChallenge = challengeService.findById(challengeSelectId)

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
    @DisplayName("사용자가 참여했던, 참여 중인 모든 챌린지 목록을 조회할 수 있다.")
    fun getUserChallengeList() {
        // given
        val newUserChallenge = makeUserChallengeAndHistory(newChallengeId, participantsStartDate)
        // when
        val getTotalUserChallengeResponseDto: GetTotalUserChallengeResponseDto =
            getTotalUserChallengeUseCase.execute(userId, participantsStartDate)
        // then
        assertThat(getTotalUserChallengeResponseDto.userChallenges.first().certificationDataList.first().isCertificated)
            .isEqualTo(
                completedUserChallenge!!.getUserChallengeHistoriesBeforeToday(LocalDate.now())
                    .first().status.name
            )
//        assertThat(getTotalUserChallengeResponseDto.userChallenges.first().status)
//            .isEqualTo(completedUserChallenge!!.status.content)
//            .isEqualTo(EUserChallengeStatus.COMPLETED.content)
        assertThat(getTotalUserChallengeResponseDto.userChallenges.last().totalDays)
            .isEqualTo(newUserChallenge.participantDays)
        assertThat(getTotalUserChallengeResponseDto.userChallenges.last().status)
            .isEqualTo(newUserChallenge.status.content)

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
