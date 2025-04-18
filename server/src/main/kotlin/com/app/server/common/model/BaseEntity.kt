package com.app.server.common.model

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import lombok.*
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity (
    @Column(updatable = false)
    @CreationTimestamp
    protected val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    protected var updatedAt: LocalDateTime? = null,

    @Column(nullable = true)
    protected var deletedAt: LocalDateTime? = null
){

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}