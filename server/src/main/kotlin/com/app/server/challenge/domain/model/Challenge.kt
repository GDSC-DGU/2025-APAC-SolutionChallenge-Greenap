package com.app.server.challenge.domain.model

import com.app.server.challenge.domain.enums.EChallengeStatus
import com.app.server.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "challenges")
class Challenge(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    val id: Long,
    val title: String,
    @Column(name = "pre_description")
    val preDescription: String,
    val description: String,

    @Column(name = "certification_method_description")
    val certificationMethodDescription: String,

    @Enumerated(EnumType.STRING)
    val status: EChallengeStatus = EChallengeStatus.RUNNING,

    @Column(name = "main_image_url")
    val mainImageUrl: String?,
    @Column(name = "certification_example_image_url")
    val certificationExampleImageUrl: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_category_id", nullable = false)
    var challengeCategory: ChallengeCategory

) : BaseEntity() {


}