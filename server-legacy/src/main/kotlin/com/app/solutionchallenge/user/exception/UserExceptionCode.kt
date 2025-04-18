package com.app.solutionchallenge.user.exception

import com.app.solutionchallenge.common.enums.ResultCode
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
    ;
}
