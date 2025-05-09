package com.app.server.challenge.application.service

import com.app.server.challenge.ui.usecase.GetListChallengesUseCase
import com.app.server.challenge.ui.usecase.dto.response.CategoryDto
import com.app.server.challenge.ui.usecase.dto.response.ChallengeDetailDto
import com.app.server.challenge.ui.usecase.dto.response.ChallengesSimpleDto
import com.app.server.challenge.domain.model.Challenge
import com.app.server.challenge.domain.model.ChallengeCategory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeQueryService(
    private val challengeService: ChallengeService,
    private val challengeCategoryService: ChallengeCategoryService
) : GetListChallengesUseCase {

    override fun execute(): List<CategoryDto> {
        val challengeCategories : List<ChallengeCategory> = challengeCategoryService.findAll()
        return categoryDtoList(challengeCategories)
    }

    private fun categoryDtoList(challengeCategories: List<ChallengeCategory>) =
        challengeCategories.map { category ->
            CategoryDto(
                id = category.id,
                title = category.title,
                description = category.description,
                imageUrl = category.categoryImageUrl!!,
                challenges = category.challenges.map { challenge ->
                    ChallengesSimpleDto(
                        id = challenge.id,
                        title = challenge.title,
                        preDescription = challenge.preDescription,
                        mainImageUrl = challenge.mainImageUrl
                    )
                }
            )
        }

    fun getChallengeDetail(challengeId: Long): ChallengeDetailDto {

        val challenge : Challenge = challengeService.findById(challengeId)
        return ChallengeDetailDto(challenge)
    }
}
