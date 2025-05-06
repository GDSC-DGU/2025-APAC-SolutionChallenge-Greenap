package com.app.server.feed.enums

enum class EFeedReadRequestState {
    /**
     * 모든 피드를 조회한다.
     */
    ALL_FEED,
    /**
     * 특정 카테고리의 모든 피드를 조회한다.
     */
    ALL_CATEGORY_FEED,
    /**
     * 특정 사용자가 참여했던, 참여 중인 챌린지들에서 작성한 피드들 중 카테고리 단위로 모든 피드를 조회한다.
     */
    USER_CATEGORY_FEED,
    /**
     * 특정 사용자가 참여했던, 참여 중인 특정 챌린지에서 작성한 모든 피드들을 조회한다.
     */
    SPECIFIC_USER_CHALLENGE_FEED,
}