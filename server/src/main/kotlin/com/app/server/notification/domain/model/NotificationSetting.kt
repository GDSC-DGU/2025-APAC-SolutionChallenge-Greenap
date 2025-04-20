package com.app.server.notification.domain.model

import com.app.server.user.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "notification_settings")
class NotificationSetting(

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    val preDailyEnabled: Boolean = false,
    val endChallengeEnabled: Boolean = false,
    val fcmToken: String
){
}