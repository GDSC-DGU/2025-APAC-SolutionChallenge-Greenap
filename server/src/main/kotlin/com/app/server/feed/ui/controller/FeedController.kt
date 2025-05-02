package com.app.server.feed.ui.controller

import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.feed.ui.dto.CreateFeedRequestDto
import com.app.server.feed.ui.dto.FeedListResponseDto
import com.app.server.feed.ui.dto.ReadFeedProjectionCommand
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.DeleteFeedUseCase
import com.app.server.feed.ui.usecase.ReadFeedUseCase
import com.app.server.feed.ui.usecase.UpdateFeedUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/feeds")
class FeedController(
    private val createFeedUseCase: CreateFeedUseCase,
    private val deleteFeedUseCase: DeleteFeedUseCase,
    private val updateFeedUseCase: UpdateFeedUseCase,
    private val readFeedUseCase: ReadFeedUseCase
) {

    @GetMapping
    fun readFeed(
        @RequestParam("category") category: String,
        @RequestParam("scope") scope: String,
        @RequestParam("challenge") userChallengeId: Long,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ) : ApiResponse<FeedListResponseDto> {
        return ApiResponse.success(
            readFeedUseCase.execute(
                ReadFeedProjectionCommand.toCommand(
                    categoryName = category,
                    scope = scope,
                    userChallengeId = userChallengeId,
                    page = page,
                    size = size
                )
            )
        )
    }

    @PostMapping
    fun createFeed(
        @UserId userId: Long,
        @RequestBody createFeedRequestDto: CreateFeedRequestDto
    ) : ApiResponse<ResultCode> {
        createFeedUseCase.execute(
            createFeedCommand = createFeedRequestDto.toCommand(userId)
        )
        return ApiResponse.success(CommonResultCode.SUCCESS)
    }

    @PutMapping("/{feedId}")
    fun updateFeed(
        @PathVariable feedId: Long,
        @RequestParam("content") newContent: String
    ) : ApiResponse<ResultCode> {
        updateFeedUseCase.execute(feedId, newContent)
        return ApiResponse.success(CommonResultCode.SUCCESS)
    }

    @DeleteMapping("/{feedId}")
    fun deleteFeed(
        @PathVariable feedId: Long
    ) : ApiResponse<ResultCode> {
        deleteFeedUseCase.execute(feedId)
        return ApiResponse.success(CommonResultCode.SUCCESS)
    }
}