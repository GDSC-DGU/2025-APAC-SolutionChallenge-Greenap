package com.app.server.feed.ui.controller

import com.app.server.common.annotation.UserId
import com.app.server.common.enums.CommonResultCode
import com.app.server.common.enums.ResultCode
import com.app.server.common.response.ApiResponse
import com.app.server.feed.ui.dto.FeedListResponseDto
import com.app.server.feed.ui.dto.request.CreateFeedRequestDto
import com.app.server.feed.ui.dto.request.ReadFeedRequestDto
import com.app.server.feed.ui.usecase.CreateFeedUseCase
import com.app.server.feed.ui.usecase.DeleteFeedUseCase
import com.app.server.feed.ui.usecase.ReadFeedUseCase
import com.app.server.feed.ui.usecase.UpdateFeedUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Feed API", description = "Feed API")
@RequestMapping("/api/v1/feeds")
class FeedController(
    private val createFeedUseCase: CreateFeedUseCase,
    private val deleteFeedUseCase: DeleteFeedUseCase,
    private val updateFeedUseCase: UpdateFeedUseCase,
    private val readFeedUseCase: ReadFeedUseCase
) {

    @GetMapping
    @Operation(
        summary = "피드 조회",
        description = "피드를 조회합니다. 카테고리 ID, 스코프, 유저 챌린지 ID, 페이지, 사이즈를 입력하세요." +
                "// page, size는 요청하기 나름 \n" +
                "모든 사용자가 작성한 전체 피드를 조회하고 싶다 -> /api/v1/feeds\n" +
                "특정 카테고리의 모든 피드를 조회하고 싶다 -> /api/v1/feeds?category_id=1\n" +
                "특정 사용자가 참여했던 챌린지들 중 특정 카테고리 내 챌린지들에서의 모든 피드를 조회하고 싶다. -> /api/v1/feeds?category_id=1&scope=user\n" +
                "특정 사용자가 참여 중인 혹은 참여했던 특정 챌린지에서 작성한 모든 피드들을 조회하고 싶다. -> /api/v1/feeds?scope=user&user_challenge_id=1"
    )
    fun readFeed(
        @UserId userId: Long,
        @RequestParam(required = false, name = "category_id") categoryId: Long?,
        @RequestParam(required = false) scope: String?,
        @RequestParam(required = false, name = "user_challenge_id") userChallengeId: Long?,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "7") size: Int
    ) : ApiResponse<FeedListResponseDto> {
        return ApiResponse.success(
            readFeedUseCase.execute(
                ReadFeedRequestDto(
                    categoryId = categoryId,
                    scope = scope,
                    userChallengeId = userChallengeId,
                    page = page,
                    size = size,
                    userId = userId
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