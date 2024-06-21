package com.myhousestair.myhousestair.domain.common

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class PrimaryKeyEntity: Persistable<UUID> {
    @Id
    private val id: UUID = UUID.randomUUID()

    @Transient
    private var _isNew = true

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null

    @CreationTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null

    override fun getId(): UUID = id

    override fun isNew(): Boolean = _isNew

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrimaryKeyEntity

        return id == other.id
    }

    override fun hashCode() = Objects.hashCode(id)
}