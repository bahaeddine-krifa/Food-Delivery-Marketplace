package com.fooddelivery.orders.domain

import com.fooddelivery.common.domain.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "order_items",
    indexes = [
        Index(name = "idx_order_items_order", columnList = "order_id"),
        Index(name = "idx_order_items_product", columnList = "product_id")
    ]
)
class OrderItem : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order? = null

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    var product: com.fooddelivery.products.domain.Product? = null

    @Column(name = "product_name_snapshot", nullable = false, length = 200)
    var productNameSnapshot: String = ""

    @Column(name = "unit_price_snapshot", nullable = false, precision = 10, scale = 2)
    var unitPriceSnapshot: BigDecimal = BigDecimal.ZERO

    @Column(nullable = false)
    var quantity: Int = 1

    @Column(columnDefinition = "TEXT")
    var notes: String? = null

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    var lineTotal: BigDecimal = BigDecimal.ZERO
}