package com.fooddelivery.orders.domain

import com.fooddelivery.common.domain.BaseEntity
import com.fooddelivery.common.enums.OrderStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "order_status_history",
    indexes = [Index(name = "idx_order_status_history_order", columnList = "order_id,created_at")]
)
class OrderStatusHistory : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    var fromStatus: OrderStatus? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    var toStatus: OrderStatus = OrderStatus.PENDING

    @Column(name = "changed_by", nullable = false)
    var changedBy: UUID = UUID.randomUUID()

    @Column(name = "change_reason", columnDefinition = "TEXT")
    var changeReason: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    var metadata: String? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
}