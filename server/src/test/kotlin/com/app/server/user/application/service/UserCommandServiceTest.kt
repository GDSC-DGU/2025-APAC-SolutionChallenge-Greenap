package com.app.server.user.application.service

import com.app.server.IntegrationTestContainer
import com.app.server.common.exception.BadRequestException
import com.app.server.user.domain.model.User
import com.app.server.user.exception.UserExceptionCode
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Rollback
import kotlin.test.Test

@SpringBootTest
@Transactional
@Rollback
class UserCommandServiceTest : IntegrationTestContainer() {

    @Autowired
    private lateinit var userCommandService: UserCommandService

    @Test
    @DisplayName("사용자가 원하는 사용자명으로 변경할 수 있다.")
    fun updateNickname() {
        // given
        val newNickname = "newNickname"

        // when
        val refreshUser: User = userCommandService.changeNickname(userId, newNickname)

        // then
        assertThat(refreshUser.nickname).isEqualTo(newNickname)
    }

    @Test
    @DisplayName("사용자가 프로필 사진을 변경할 수 있다. 프로필 사진은 파일로 입력을 받아 url로 저장된다.")
    fun updateProfileImage() {
        // given
        val imageContent = ByteArray(1024) { 0x1 } // 1KB dummy 이미지
        val mockImage = MockMultipartFile(
            "new_profile_image_file",
            "test-image.jpg",
            "image/jpeg",
            imageContent
        )

        // when
        val updatedUser: User = userCommandService.changeProfileImage(userId, mockImage)

        // then
        assertThat(updatedUser.profileImageUrl).isNotEmpty()
        assertThat(updatedUser.profileImageUrl).matches("https://.*\\.${mockImage.contentType?.substringAfterLast('/')}")
    }

    @Test
    @DisplayName("사용자의 프로필 사진은 10MB 이하의 크기로 제한된다.")
    fun updateProfileImageSizeLimit() {
        // given
        val oversizedContent = ByteArray(10 * 1024 * 1024 + 1) { 0x1 } // 10MB + 1byte
        val oversizedImage = MockMultipartFile(
            "oversized_profile_image_file",
            "oversized.jpg",
            "image/jpeg",
            oversizedContent
        )
        // when
        val exception = assertThrows(BadRequestException::class.java) {
            userCommandService.changeProfileImage(userId, oversizedImage)
        }
        // then
        assertThat(exception.message).contains(UserExceptionCode.PROFILE_IMAGE_SIZE_LIMIT_EXCEEDED.message)
    }

    @Test
    @DisplayName("사용자 명을 변경할 때는 다른 사용자가 이미 사용 중인 이름으로 변경할 수 없다.")
    fun updateNicknameUniqueConstraint() {
        // given
        val newNickname = secondUserName

        // when
        val exception = assertThrows(BadRequestException::class.java) {
            userCommandService.changeNickname(userId, newNickname)
        }

        // then
        assertThat(exception.message).contains(UserExceptionCode.DUPLICATED_NICKNAME.message)
    }

    @Test
    @DisplayName("사용자 명은 최대 20자까지 입력할 수 있다.")
    fun updateNicknameLengthLimit() {
        // given
        val notLongNicknameByKorean = "가".repeat(20) // 20 characters
        val notLongNicknameByEnglish = "a".repeat(20) // 20 characters
        val longNicknameByKorean = "가".repeat(21) // 21 characters
        val longNicknameByEnglish = "a".repeat(21) // 21 characters

        // when & then
        val refreshUserKorean : User = userCommandService.changeNickname(userId, notLongNicknameByKorean)
        assertThat(refreshUserKorean.nickname).isEqualTo(notLongNicknameByKorean)

        val refreshUserEnglish : User = userCommandService.changeNickname(userId, notLongNicknameByEnglish)
        assertThat(refreshUserEnglish.nickname).isEqualTo(notLongNicknameByEnglish)

        val exceptionKorean = assertThrows(BadRequestException::class.java) {
            userCommandService.changeNickname(userId, longNicknameByKorean)
        }
        assertThat(exceptionKorean.message).contains(UserExceptionCode.NICKNAME_EXCEED_LENGTH_LIMIT.message)
        val exceptionEnglish = assertThrows(BadRequestException::class.java) {
            userCommandService.changeNickname(userId, longNicknameByEnglish)
        }
        assertThat(exceptionEnglish.message).contains(UserExceptionCode.NICKNAME_EXCEED_LENGTH_LIMIT.message)

    }

    @Test
    @DisplayName("프로필 사진은 JPEG, PNG, JPG 형식만 허용된다.")
    fun updateProfileImageFormatLimit() {
        // given
        val invalidImageFormats = listOf("gif", "bmp", "tiff", "svg")
        val validImageFormats = listOf("jpg", "jpeg", "png")

        // when & then
        for (format in invalidImageFormats) {
            val invalidImage = MockMultipartFile(
                "invalid_image_file",
                "test-image.$format",
                "image/$format",
                ByteArray(1024) // 1KB dummy image
            )
            val exception = assertThrows(BadRequestException::class.java) {
                userCommandService.changeProfileImage(userId, invalidImage)
            }
            assertThat(exception.message).contains("Unsupported image format: $format")
        }

        for (format in validImageFormats) {
            val validImage = MockMultipartFile(
                "valid_image_file",
                "test-image.$format",
                "image/$format",
                ByteArray(1024) // 1KB dummy image
            )
            val updatedUser: User = userCommandService.changeProfileImage(userId, validImage)
            assertThat(updatedUser.profileImageUrl).isNotEmpty()
            assertThat(updatedUser.profileImageUrl).matches("https://.*\\.$format")
        }
    }

    @Test
    @DisplayName("사용자 명에는 특수문자와 숫자의 사용이 가능하다.")
    fun updateNicknameSpecialCharactersAndNumbers() {
        // given
        val nicknameWithSpecialChars = "user_name123!@#"
        val longNicknameBySpecificChar = "\'!@#$%^&*()_+[]{}"
        val longNicknameBySpecificChar2 = "|;':,.<>?/~`\""

        // when
        val refreshUser: User = userCommandService.changeNickname(userId, nicknameWithSpecialChars)
        // then
        assertThat(refreshUser.nickname).isEqualTo(nicknameWithSpecialChars)
        // when
        val refreshUser2: User = userCommandService.changeNickname(userId, longNicknameBySpecificChar)
        // then
        assertThat(refreshUser2.nickname).isEqualTo(longNicknameBySpecificChar)
        // when
        val refreshUser3: User = userCommandService.changeNickname(userId, longNicknameBySpecificChar2)
        // then
        assertThat(refreshUser3.nickname).isEqualTo(longNicknameBySpecificChar2)
    }

}