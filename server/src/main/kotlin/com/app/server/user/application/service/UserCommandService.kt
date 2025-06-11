package com.app.server.user.application.service

import com.app.server.common.exception.BadRequestException
import com.app.server.infra.cloud_storage.CloudStorageUtil
import com.app.server.user.domain.model.User
import com.app.server.user.exception.UserExceptionCode
import com.app.server.user_challenge.application.service.UserChallengeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class UserCommandService(
    private val userService: UserService,
    private val userChallengeService: UserChallengeService,
    private val cloudStorageUtil: CloudStorageUtil
) {

    fun verifyUserCanUpdateNowMaxConsecutiveParticipantsDayCount(
        userChallengeId: Long,
        updateMaxConsecutiveParticipationDayCount: Long
    ) {

        val userChallenge = userChallengeService.findById(userChallengeId)
        val user = userService.findById(userChallenge.userId)

        val userNowMaxConsecutiveParticipationDayCount: Long = user.nowMaxConsecutiveParticipationDayCount

        if (userNowMaxConsecutiveParticipationDayCount < updateMaxConsecutiveParticipationDayCount) {
            user.updateNowMaxConsecutiveParticipationDayCount(
                maxConsecutiveParticipationDayCount = updateMaxConsecutiveParticipationDayCount
            )
        }
    }

    fun changeNickname(userId: Long, newNickname: String): User {

        verifyNewNickname(newNickname)

        checkAlreadyExists(newNickname)

        val user = userService.findById(userId)
        user.updateNickname(newNickname)

        return userService.save(user)
    }

    private fun checkAlreadyExists(newNickname: String) {
        if (userService.findByNickname(newNickname)) {
            throw BadRequestException(UserExceptionCode.DUPLICATED_NICKNAME)
        }
    }

    private fun verifyNewNickname(newNickname: String) {
        if (newNickname.length > 20) {
            throw BadRequestException(UserExceptionCode.NICKNAME_EXCEED_LENGTH_LIMIT)
        }

        if (newNickname.isBlank()) {
            throw BadRequestException(UserExceptionCode.NICKNAME_BLANK)
        }
    }

    fun changeProfileImage(userId: Long, profileImage: MultipartFile): User {
        val user = userService.findById(userId)

        verifyProfileImage(profileImage)

        checkImageSizeLessThan(10 * 1024 * 1024, profileImage.size)

        val profileImageUrl = saveImageAndConvertUrl(profileImage, userId)
        user.updateProfileImageUrl(profileImageUrl)

        return userService.save(user)
    }

    private fun verifyProfileImage(profileImage: MultipartFile) {
        if (profileImage.isEmpty) {
            throw BadRequestException(UserExceptionCode.INVALID_PROFILE_IMAGE)
        }

        if (!profileImage.contentType?.startsWith("image/")!! ||
            !profileImage.contentType!!.contains("jpeg") &&
            !profileImage.contentType!!.contains("png") &&
            !profileImage.contentType!!.contains("jpg")
        ) {
            throw BadRequestException(UserExceptionCode.INVALID_PROFILE_IMAGE_TYPE)
        }
    }

    fun saveImageAndConvertUrl(profileImage: MultipartFile, userId: Long): String {
        val profileImageUrl = cloudStorageUtil.uploadProfileImageToCloudStorage(profileImage, userId)
        return profileImageUrl
    }

    private fun checkImageSizeLessThan(maxSize: Long, imageSize: Long) {
        if (imageSize > maxSize) { // 10MB
            throw BadRequestException(UserExceptionCode.PROFILE_IMAGE_SIZE_LIMIT_EXCEEDED)
        }
    }
}