package com.fooddelivery.users.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.VehicleType
import com.fooddelivery.common.enums.VerificationStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Index
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "delivery_profiles",
    uniqueConstraints = [UniqueConstraint(name = "uk_delivery_profiles_user_id", columnNames = ["user_id"])],
    indexes = [
        Index(name = "idx_delivery_profiles_available", columnList = "is_available"),
        Index(name = "idx_delivery_profiles_location", columnList = "current_latitude,current_longitude")
    ]
)
class DeliveryProfile : AuditableEntity() {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    var vehicleType: VehicleType = VehicleType.MOTORCYCLE

    @Column(name = "vehicle_plate", length = 20)
    var vehiclePlate: String? = null

    @Column(name = "vehicle_color", length = 30)
    var vehicleColor: String? = null

    @Column(name = "license_number", nullable = false, length = 50)
    var licenseNumber: String = ""

    @Column(name = "license_expiry", nullable = false)
    var licenseExpiry: LocalDate = LocalDate.now().plusYears(1)

    @Column(name = "id_document_url", length = 500)
    var idDocumentUrl: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "background_check_status", nullable = false, length = 20)
    var backgroundCheckStatus: VerificationStatus = VerificationStatus.PENDING

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = false

    @Column(name = "current_latitude", precision = 10, scale = 8)
    var currentLatitude: BigDecimal? = null

    @Column(name = "current_longitude", precision = 11, scale = 8)
    var currentLongitude: BigDecimal? = null

    @Column(name = "last_location_update")
    var lastLocationUpdate: OffsetDateTime? = null

    @Column(name = "average_rating", precision = 3, scale = 2, nullable = false)
    var averageRating: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_deliveries", nullable = false)
    var totalDeliveries: Int = 0
}