package com.app.solutionchallenge.user.model

import com.app.solutionchallenge.common.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.Getter

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val email: String,
    val profileImageUrl: String,
    val nickname: String,
    val deviceToken: String,
     val refreshToken: String
) : BaseEntity(){

}
