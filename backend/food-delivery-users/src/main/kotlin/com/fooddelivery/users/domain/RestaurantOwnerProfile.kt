package com.fooddelivery.users.domain

import com.fooddelivery.common.domain.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "restaurant_owner_profiles",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_restaurant_owner_profiles_user_id", columnNames = ["user_id"]),
        UniqueConstraint(name = "uk_restaurant_owner_profiles_tax_id", columnNames = ["tax_id"])
    ]
)
class RestaurantOwnerProfile : AuditableEntity() {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null

    @Column(name = "business_name", nullable = false, length = 200)
    var businessName: String = ""

    @Column(name = "tax_id", nullable = false, length = 50)
    var taxId: String = ""

    @Column(name = "bank_account_iban", length = 34)
    var bankAccountIban: String? = null

    @Column(name = "bank_account_name", length = 200)
    var bankAccountName: String? = null

    @Column(name = "commission_rate", precision = 5, scale = 2, nullable = false)
    var commissionRate: BigDecimal = BigDecimal("15.00")
}