package com.app.server.feed.ui.dto

import com.app.server.feed.enums.EFeedScope

data class ReadFeedProjectionQuery(
    val userId : Long,
    val categoryId : Long?,
    val scope : EFeedScope = EFeedScope.ALL,
    val userChallengeId : Long?,
    val page : Int? = 1,
    val size : Int? = 7
) {
    companion object{
        fun toQuery(
            userId : Long,
            categoryId : Long?,
            scope : String?,
            userChallengeId : Long?,
            page : Int?,
            size : Int?
        ): ReadFeedProjectionQuery {
            if (scope == null)
                return ReadFeedProjectionQuery(
                    userId = userId,
                    categoryId = categoryId,
                    scope = EFeedScope.ALL,
                    userChallengeId = userChallengeId,
                    page = page,
                    size = size
                )

            return ReadFeedProjectionQuery(
                userId = userId,
                categoryId = categoryId,
                scope = EFeedScope.valueOf(scope.uppercase()),
                userChallengeId = userChallengeId,
                page = page,
                size = size
            )
        }
    }
}