package com.fooddelivery.common.type

import com.fooddelivery.common.enums.AddressType
import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.DayOfWeek
import com.fooddelivery.common.enums.DeliveryStatus
import com.fooddelivery.common.enums.ModerationStatus
import com.fooddelivery.common.enums.NotificationPriority
import com.fooddelivery.common.enums.NotificationType
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.PaymentMethod
import com.fooddelivery.common.enums.PaymentStatus
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.enums.ReviewTargetType
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.enums.VehicleType
import com.fooddelivery.common.enums.VerificationStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class UserRoleConverter : AttributeConverter<UserRole, String> {
    override fun convertToDatabaseColumn(attribute: UserRole?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): UserRole? = dbData?.let { UserRole.valueOf(it) }
}

@Converter(autoApply = false)
class UserStatusConverter : AttributeConverter<UserStatus, String> {
    override fun convertToDatabaseColumn(attribute: UserStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): UserStatus? = dbData?.let { UserStatus.valueOf(it) }
}

@Converter(autoApply = false)
class RestaurantStatusConverter : AttributeConverter<RestaurantStatus, String> {
    override fun convertToDatabaseColumn(attribute: RestaurantStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): RestaurantStatus? = dbData?.let { RestaurantStatus.valueOf(it) }
}

@Converter(autoApply = false)
class ApplicationStatusConverter : AttributeConverter<ApplicationStatus, String> {
    override fun convertToDatabaseColumn(attribute: ApplicationStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): ApplicationStatus? = dbData?.let { ApplicationStatus.valueOf(it) }
}

@Converter(autoApply = false)
class AddressTypeConverter : AttributeConverter<AddressType, String> {
    override fun convertToDatabaseColumn(attribute: AddressType?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): AddressType? = dbData?.let { AddressType.valueOf(it) }
}

@Converter(autoApply = false)
class OrderStatusConverter : AttributeConverter<OrderStatus, String> {
    override fun convertToDatabaseColumn(attribute: OrderStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): OrderStatus? = dbData?.let { OrderStatus.valueOf(it) }
}

@Converter(autoApply = false)
class DeliveryStatusConverter : AttributeConverter<DeliveryStatus, String> {
    override fun convertToDatabaseColumn(attribute: DeliveryStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): DeliveryStatus? = dbData?.let { DeliveryStatus.valueOf(it) }
}

@Converter(autoApply = false)
class PaymentMethodConverter : AttributeConverter<PaymentMethod, String> {
    override fun convertToDatabaseColumn(attribute: PaymentMethod?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): PaymentMethod? = dbData?.let { PaymentMethod.valueOf(it) }
}

@Converter(autoApply = false)
class PaymentStatusConverter : AttributeConverter<PaymentStatus, String> {
    override fun convertToDatabaseColumn(attribute: PaymentStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): PaymentStatus? = dbData?.let { PaymentStatus.valueOf(it) }
}

@Converter(autoApply = false)
class NotificationTypeConverter : AttributeConverter<NotificationType, String> {
    override fun convertToDatabaseColumn(attribute: NotificationType?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): NotificationType? = dbData?.let { NotificationType.valueOf(it) }
}

@Converter(autoApply = false)
class NotificationPriorityConverter : AttributeConverter<NotificationPriority, String> {
    override fun convertToDatabaseColumn(attribute: NotificationPriority?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): NotificationPriority? = dbData?.let { NotificationPriority.valueOf(it) }
}

@Converter(autoApply = false)
class ReviewTargetTypeConverter : AttributeConverter<ReviewTargetType, String> {
    override fun convertToDatabaseColumn(attribute: ReviewTargetType?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): ReviewTargetType? = dbData?.let { ReviewTargetType.valueOf(it) }
}

@Converter(autoApply = false)
class ModerationStatusConverter : AttributeConverter<ModerationStatus, String> {
    override fun convertToDatabaseColumn(attribute: ModerationStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): ModerationStatus? = dbData?.let { ModerationStatus.valueOf(it) }
}

@Converter(autoApply = false)
class VerificationStatusConverter : AttributeConverter<VerificationStatus, String> {
    override fun convertToDatabaseColumn(attribute: VerificationStatus?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): VerificationStatus? = dbData?.let { VerificationStatus.valueOf(it) }
}

@Converter(autoApply = false)
class VehicleTypeConverter : AttributeConverter<VehicleType, String> {
    override fun convertToDatabaseColumn(attribute: VehicleType?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): VehicleType? = dbData?.let { VehicleType.valueOf(it) }
}

@Converter(autoApply = false)
class DayOfWeekConverter : AttributeConverter<DayOfWeek, String> {
    override fun convertToDatabaseColumn(attribute: DayOfWeek?): String? = attribute?.name
    override fun convertToEntityAttribute(dbData: String?): DayOfWeek? = dbData?.let { DayOfWeek.valueOf(it) }
}