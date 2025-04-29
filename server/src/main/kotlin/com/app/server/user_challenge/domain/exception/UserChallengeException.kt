package com.app.server.user_challenge.domain.exception

import com.app.server.common.enums.ResultCode

enum class UserChallengeException(
    override val code: String,
    override val message: String
) : ResultCode{

    ALREADY_PARTICIPATED_AND_STATUS_IS_RUNNING("UCH001", "이미 참여 중인 챌린지가 있습니다."),
    CHALLENGE_WAITED_AND_STATUS_IS_PENDING("UCH002", "아직 리포트를 확인하지 않은 챌린지가 있습니다. 리포트를 확인하세요."),
    CONTINUE_CHALLENGE_AND_STATUS_IS_WAITING("UCH003", "챌린지 이어하기를 통해 챌린지를 계속할 수 있습니다."),
    CANNOT_PARTICIPATE("UCH004", "챌린지 참여가 불가능합니다."),
    REPORT_NOT_FOUND_AND_STATUS_IS_DEAD("UCH005", "리포트가 존재하지 않습니다."),
    NOT_FOUND_CHALLENGE_HISTORY("UCH006", "챌린지 히스토리가 존재하지 않습니다."),
    ALREADY_CERTIFICATED("UCH007", "이미 인증된 챌린지입니다."),
    FAILED_CERTIFICATION("UCH008", "인증에 실패했습니다."),
    CANNOT_USE_ICE("UCH009", "얼리기 기회가 없어 사용이 불가합니다."),
    CANNOT_UPDATE_CONSECUTIVE_PARTICIPATION_DAY_COUNT("UCH010", "연속 참여일 수를 업데이트할 수 없습니다."),
    ERROR_IN_CERTIFICATED_SERVER("UCH011", "챌린지 인증 서버 오류입니다."),
    ;

}