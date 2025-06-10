package com.app.server.user.exception

import com.app.server.common.enums.ResultCode
import lombok.Getter
import lombok.RequiredArgsConstructor

enum class UserExceptionCode(
    override val code: String,
    override val message: String
) : ResultCode {
    //failure
    NO_SUCH_ROLE("USR001", "No such user role"),
    NOT_FOUND_USER("USR002", "User not found"),
    DUPLICATED_NICKNAME("USR003", "Duplicated nickname"),
    DUPLICATED_EMAIL("USR004", "Duplicated email"),
    NO_SUCH_EMAIL("USR005", "No such email"),
    DELETED_USER("USR006", "Deleted user"),
    DELETE_FAILED("USR007", "Failed to delete user"),
    PROFILE_IMAGE_SIZE_LIMIT_EXCEEDED("USR008", "프로필 사진은 10MB 이하로 업로드해야 합니다."),
    NICKNAME_EXCEED_LENGTH_LIMIT("USR009", "사용자 명은 최대 20자까지 입력할 수 있습니다.")
    ;
}
