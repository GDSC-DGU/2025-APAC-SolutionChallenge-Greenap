package com.app.server.feed.ui.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraints.Min
import kotlin.reflect.KClass

data class ReadFeedRequestDto(
    @JsonProperty("category_id")
    val categoryId : Long?,
    @field:ValidFeedScope
    val scope: String?,
    @JsonProperty("user_challenge_id")
    val userChallengeId: Long,
    @Min(1)
    val page: Int?,
    @Min(1)
    val size: Int?
)

class FeedScopeValidator : ConstraintValidator<ValidFeedScope, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value == null || (value.equals("all", true) || value.equals("user", true))
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FeedScopeValidator::class])
annotation class ValidFeedScope(
    val message: String = "scope 값이 올바르지 않습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)