package com.app.server.feed.exception

import com.app.server.common.enums.ResultCode

enum class FeedException (
    override val code: String,
    override val message: String
) : ResultCode {

    CONTENT_OVER_LIMIT("FED001", "피드 내용은 1000자 이내로 작성해주세요."),
    NOT_CERTIFICATED_THIS_USER_CHALLENGE("FED002", "인증되지 않은 챌린지는 피드로 작성할 수 없습니다."),
    NOT_MATCHED_IMAGE_URL("FED003", "인증된 챌린지의 이미지와 일치하지 않습니다."),
    NOT_FOUND_FEED("FED004", "피드를 찾을 수 없습니다."),
    NOT_MODIFIED_CONTENT("FED005", "피드 내용이 수정되지 않았습니다."),
    NOT_FOUND_FEED_PROJECTION("FED006", "조회용 피드를 찾을 수 없습니다."),
    PAGE_OVER_LIMIT("FED007", "페이지는 1 이상이어야 합니다."),
    SIZE_OVER_LIMIT("FED008", "사이즈는 1 이상이어야 합니다."),
    ;

}
