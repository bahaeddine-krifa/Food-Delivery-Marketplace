package com.fooddelivery.users.domain

import com.fooddelivery.common.domain.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(
    name = "customer_profiles",
    uniqueConstraints = [UniqueConstraint(name = "uk_customer_profiles_user_id", columnNames = ["user_id"])]
)
class CustomerProfile : AuditableEntity() {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null

    @Column(name = "default_address_id")
    var defaultAddressId: UUID? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences")
    var preferences: String = "{}"

    @Column(name = "loyalty_points", nullable = false)
    var loyaltyPoints: Int = 0
}