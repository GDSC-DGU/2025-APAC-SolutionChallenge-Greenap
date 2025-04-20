package com.app.server.challenge.domain.model

import com.app.server.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "challenge_categories")
class ChallengeCategory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_category_id")
    val id: Long,
    val title: String,
    val description: String,

    @Column(name = "category_image_url")
    val categoryImageUrl: String?,

    @OneToMany(mappedBy = "challengeCategory", fetch = FetchType.LAZY)
    val challenges: List<Challenge> = listOf()

) : BaseEntity() {
}