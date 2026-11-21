package com.fooddelivery.users.service

import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ConflictException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.CustomerProfile
import com.fooddelivery.users.domain.DeliveryProfile
import com.fooddelivery.users.domain.RestaurantOwnerProfile
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.AddressRepository
import com.fooddelivery.users.repository.CustomerProfileRepository
import com.fooddelivery.users.repository.DeliveryProfileRepository
import com.fooddelivery.users.repository.RestaurantOwnerProfileRepository
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val customerProfileRepository: CustomerProfileRepository,
    private val deliveryProfileRepository: DeliveryProfileRepository,
    private val restaurantOwnerProfileRepository: RestaurantOwnerProfileRepository,
    private val addressRepository: AddressRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        role: UserRole
    ): User {
        if (userRepository.existsByEmail(email)) {
            throw ConflictException("Email already registered")
        }

        val user = User().apply {
            this.email = email.toLowerCase()
            this.passwordHash = passwordEncoder.encode(password)
            this.firstName = firstName
            this.lastName = lastName
            this.phone = phone
            this.role = role
            this.status = UserStatus.PENDING_VERIFICATION
            this.emailVerificationToken = UUID.randomUUID().toString()
        }

        return userRepository.save(user)
    }

    fun createProfile(userId: UUID, role: UserRole): Any {
        val user = findById(userId)
        return when (role) {
            UserRole.CUSTOMER -> {
                val profile = CustomerProfile().apply { this.user = user }
                customerProfileRepository.save(profile)
            }
            UserRole.DELIVERY_PERSON -> {
                val profile = DeliveryProfile().apply { this.user = user }
                deliveryProfileRepository.save(profile)
            }
            UserRole.RESTAURANT_OWNER -> {
                val profile = RestaurantOwnerProfile().apply { this.user = user }
                restaurantOwnerProfileRepository.save(profile)
            }
            else -> throw BusinessException("Cannot create profile for role: $role", "INVALID_ROLE")
        }
    }

    @Transactional
    fun verifyEmail(token: String): User {
        val user = userRepository.findByEmailVerificationToken(token)
            ?: throw ResourceNotFoundException("Verification token", token)

        if (user.emailVerified) {
            throw BusinessException("Email already verified", "ALREADY_VERIFIED")
        }

        user.emailVerified = true
        user.emailVerificationToken = null
        user.status = UserStatus.ACTIVE
        return userRepository.save(user)
    }

    fun requestPasswordReset(email: String): User? {
        return userRepository.findByEmail(email)?.apply {
            this.passwordResetToken = UUID.randomUUID().toString()
            this.passwordResetExpires = OffsetDateTime.now().plusHours(1)
            userRepository.save(this)
        }
    }

    fun resetPassword(token: String, newPassword: String): User {
        val user = userRepository.findByPasswordResetToken(token)
            ?: throw ResourceNotFoundException("Password reset token", token)

        if (user.passwordResetExpires == null || user.passwordResetExpires!! < OffsetDateTime.now()) {
            throw BusinessException("Password reset token expired", "TOKEN_EXPIRED")
        }

        user.passwordHash = passwordEncoder.encode(newPassword)
        user.passwordResetToken = null
        user.passwordResetExpires = null
        user.resetFailedAttempts()
        return userRepository.save(user)
    }

    fun changePassword(userId: UUID, currentPassword: String, newPassword: String): User {
        val user = findById(userId)
        if (!passwordEncoder.matches(currentPassword, user.passwordHash)) {
            throw BusinessException("Current password is incorrect", "INVALID_PASSWORD")
        }
        user.passwordHash = passwordEncoder.encode(newPassword)
        return userRepository.save(user)
    }

    fun recordLogin(userId: UUID): User {
        val user = findById(userId)
        user.lastLoginAt = OffsetDateTime.now()
        user.resetFailedAttempts()
        return userRepository.save(user)
    }

    fun recordFailedLogin(email: String): User? {
        return userRepository.findByEmail(email)?.apply {
            incrementFailedAttempts()
            userRepository.save(this)
        }
    }

    fun findById(id: UUID): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User", id) }
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findAll(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<User> {
        return userRepository.findAll(pageable)
    }

    fun updateProfile(userId: UUID, firstName: String?, lastName: String?, phone: String?, avatarUrl: String?): User {
        val user = findById(userId)
        firstName?.let { user.firstName = it }
        lastName?.let { user.lastName = it }
        phone?.let { user.phone = it }
        avatarUrl?.let { user.avatarUrl = it }
        return userRepository.save(user)
    }

    fun updateStatus(userId: UUID, status: UserStatus): User {
        val user = findById(userId)
        user.status = status
        return userRepository.save(user)
    }

    fun updateRole(userId: UUID, role: UserRole): User {
        val user = findById(userId)
        user.role = role
        return userRepository.save(user)
    }

    // Address methods
    fun addAddress(userId: UUID, address: Address): Address {
        val user = findById(userId)
        address.user = user

        // If this is the first address or marked as default, make it default
        val existingCount = addressRepository.countByUserId(userId)
        if (existingCount == 0L || address.isDefault) {
            addressRepository.findByUserId(userId).forEach { it.isDefault = false }
            address.isDefault = true
        }

        return addressRepository.save(address)
    }

    fun updateAddress(userId: UUID, addressId: UUID, updatedAddress: Address): Address {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw BusinessException("Address does not belong to user", "ADDRESS_MISMATCH")
        }

        address.label = updatedAddress.label
        address.street = updatedAddress.street
        address.building = updatedAddress.building
        address.floor = updatedAddress.floor
        address.apartment = updatedAddress.apartment
        address.city = updatedAddress.city
        address.state = updatedAddress.state
        address.postalCode = updatedAddress.postalCode
        address.country = updatedAddress.country
        address.latitude = updatedAddress.latitude
        address.longitude = updatedAddress.longitude
        address.instructions = updatedAddress.instructions
        address.addressType = updatedAddress.addressType

        return addressRepository.save(address)
    }

    fun deleteAddress(userId: UUID, addressId: UUID) {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw BusinessException("Address does not belong to user", "ADDRESS_MISMATCH")
        }

        val wasDefault = address.isDefault
        addressRepository.delete(address)

        // If deleted address was default, set another as default
        if (wasDefault) {
            addressRepository.findByUserId(userId).firstOrNull()?.apply {
                isDefault = true
                addressRepository.save(this)
            }
        }
    }

    fun setDefaultAddress(userId: UUID, addressId: UUID): Address {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw BusinessException("Address does not belong to user", "ADDRESS_MISMATCH")
        }

        addressRepository.findByUserId(userId).forEach { it.isDefault = false }
        address.isDefault = true
        return addressRepository.save(address)
    }

    fun getAddresses(userId: UUID): List<Address> {
        return addressRepository.findByUserId(userId)
    }

    fun getDefaultAddress(userId: UUID): Address? {
        return addressRepository.findDefaultByUserId(userId)
    }
}