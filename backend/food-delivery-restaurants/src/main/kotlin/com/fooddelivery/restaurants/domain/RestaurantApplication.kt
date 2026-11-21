package com.fooddelivery.restaurants.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.ApplicationStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "restaurant_applications",
    indexes = [
        Index(name = "idx_restaurant_applications_owner", columnList = "owner_id"),
        Index(name = "idx_restaurant_applications_status", columnList = "status")
    ]
)
class RestaurantApplication : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: com.fooddelivery.users.domain.User? = null

    @Column(name = "proposed_name", nullable = false, length = 200)
    var proposedName: String = ""

    @Column(name = "proposed_cuisine", length = 100)
    var proposedCuisine: String? = null

    @ManyToOne
    @JoinColumn(name = "proposed_address_id", nullable = false)
    var proposedAddress: com.fooddelivery.users.domain.Address? = null

    @Column(name = "business_license_url", nullable = false, length = 500)
    var businessLicenseUrl: String = ""

    @Column(name = "tax_id_document_url", nullable = false, length = 500)
    var taxIdDocumentUrl: String = ""

    @Column(name = "owner_id_document_url", nullable = false, length = 500)
    var ownerIdDocumentUrl: String = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: ApplicationStatus = ApplicationStatus.SUBMITTED

    @Column(name = "review_notes", columnDefinition = "TEXT")
    var reviewNotes: String? = null

    @Column(name = "reviewed_by")
    var reviewedBy: UUID? = null

    @Column(name = "reviewed_at")
    var reviewedAt: OffsetDateTime? = null

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: OffsetDateTime = OffsetDateTime.now()
}