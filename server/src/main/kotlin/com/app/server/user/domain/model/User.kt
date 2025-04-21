package com.app.server.user.domain.model

import com.app.server.common.model.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@Table(name = "users")
@DynamicUpdate
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,
    val email: String,
    @Column(name = "profile_image_url")
    val profileImageUrl: String?,
    val nickname: String,
    @Column(name = "now_max_consecutive_participation_day_count")
    val nowMaxConsecutiveParticipationDayCount: Long,
    @Column(name = "refresh_token")
    val refreshToken: String?
) : BaseEntity() {


// TODO: Auth 도메인에서 refreshToken 업데이트 로직 추가
// TODO: UserService에서 바로 접근할 수 있는 nickname, profileImageUrl update 메서드 추가
// TODO: User 도메인에서 nowMaxConsecutiveParticipantionDayCount update 메서드 추가
}
