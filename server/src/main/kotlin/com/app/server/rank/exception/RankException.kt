package com.app.server.rank.exception

import com.app.server.common.enums.ResultCode

enum class RankException (
    override val code: String,
    override val message: String,
) : ResultCode {
    CANNOT_UPDATE_RANK("RANK_001", "랭크 업데이트에 실패했습니다."),
    NOT_FOUND_RANK("RANK_002", "랭크를 찾을 수 없습니다."),
    NOT_FOUND_USER_RANK("RANK_003", "유저 랭크를 찾을 수 없습니다."),
    NOT_FOUND_SCORE("RANK_004", "점수를 찾을 수 없습니다."),
}