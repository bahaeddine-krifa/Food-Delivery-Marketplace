package com.fooddelivery.common.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import java.time.OffsetDateTime
import java.util.UUID

@MappedSuperclass
open class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Version
    var version: Long = 0
)

@MappedSuperclass
open class AuditableEntity : BaseEntity() {
    var createdAt: OffsetDateTime = OffsetDateTime.now()
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
}