package com.app.server.notification.domain.model

import com.app.server.common.model.BaseEntity
import com.app.server.notification.domain.enums.ENotificationStatus
import com.app.server.notification.domain.enums.ENotificationType
import com.app.server.user.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "notifications")
class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    val type: ENotificationType,
    val payload: String,
    val status: ENotificationStatus,

    ) : BaseEntity() {
}