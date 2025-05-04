package com.app.server.user.domain.model

import com.app.server.common.model.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@Table(name = "users")
@DynamicUpdate
// TODO : Index 설정 고려
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,
    val email: String,
    @Column(name = "profile_image_url")
    var profileImageUrl: String?,
    var nickname: String,
    @Column(name = "now_max_consecutive_participation_day_count")
    var nowMaxConsecutiveParticipationDayCount: Long,
    @Column(name = "refresh_token")
    var refreshToken: String?
) : BaseEntity() {

// TODO: Auth 도메인에서 refreshToken 업데이트 로직 추가
    fun updateRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }

    fun updateProfileImageUrl(profileImageUrl: String?) {
        this.profileImageUrl = profileImageUrl
    }

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updateNowMaxConsecutiveParticipationDayCount(
        maxConsecutiveParticipationDayCount: Long
    ) {
        this.nowMaxConsecutiveParticipationDayCount = maxConsecutiveParticipationDayCount
    }
}
