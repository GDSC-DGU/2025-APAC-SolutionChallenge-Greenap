package com.app.server.feed.ui.dto

import com.app.server.feed.enums.EFeedScope

data class ReadFeedProjectionCommand(
    val userId : Long,
    val categoryId : Long?,
    val scope : EFeedScope = EFeedScope.ALL,
    val userChallengeId : Long?,
    val page : Int? = 1,
    val size : Int? = 7
) {
    companion object{
        fun toCommand(
            userId : Long,
            categoryId : Long?,
            scope : String?,
            userChallengeId : Long?,
            page : Int?,
            size : Int?
        ): ReadFeedProjectionCommand {
            if (scope == null)
                return ReadFeedProjectionCommand(
                    userId = userId,
                    categoryId = categoryId,
                    scope = EFeedScope.ALL,
                    userChallengeId = userChallengeId,
                    page = page,
                    size = size
                )

            return ReadFeedProjectionCommand(
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