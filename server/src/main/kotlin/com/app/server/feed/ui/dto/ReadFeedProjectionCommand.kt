package com.app.server.feed.ui.dto

import com.app.server.feed.enums.EFeedScope

data class ReadFeedProjectionCommand(
    val categoryName : String?,
    val scope : String = EFeedScope.ALL.name,
    val userChallengeId : Long?,
    val page : Int? = 1,
    val size : Int? = 7
) {
    companion object{
        fun toCommand(
            categoryName : String?,
            scope : String,
            userChallengeId : Long?,
            page : Int,
            size : Int
        ): ReadFeedProjectionCommand {
            return ReadFeedProjectionCommand(
                categoryName = categoryName,
                scope = scope,
                userChallengeId = userChallengeId,
                page = page,
                size = size
            )
        }
    }
}