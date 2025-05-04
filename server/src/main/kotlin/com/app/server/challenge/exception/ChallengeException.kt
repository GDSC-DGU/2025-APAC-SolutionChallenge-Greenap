package com.app.server.challenge.exception

import com.app.server.common.enums.ResultCode

enum class ChallengeException(
    override val code: String,
    override val message: String
) : ResultCode {

    NOT_FOUND("CHA000", "챌린지를 찾을 수 없습니다."),
    NOT_FOUND_CHALLENGE_CATEGORY("CHA001", "해당하는 챌린지 카테고리를 찾을 수 없습니다."),
}