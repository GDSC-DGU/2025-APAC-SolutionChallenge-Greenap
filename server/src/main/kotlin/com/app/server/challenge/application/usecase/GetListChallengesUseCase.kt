package com.app.server.challenge.application.usecase

import com.app.server.challenge.application.usecase.dto.response.CategoryDto

interface GetListChallengesUseCase {
    /**
     * 모든 챌린지를 조회하거나,
     * categoryId등 필터 조건이 있으면 해당 카테고리 챌린지만 반환한다.
     */
    fun execute(): List<CategoryDto>
}