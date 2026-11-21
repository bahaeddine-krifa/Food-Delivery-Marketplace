package com.fooddelivery.addresses.service

import com.fooddelivery.addresses.dto.AddressResponse
import com.fooddelivery.addresses.dto.CreateAddressRequest
import com.fooddelivery.addresses.dto.SetDefaultAddressRequest
import com.fooddelivery.addresses.dto.UpdateAddressRequest
import com.fooddelivery.common.enums.AddressType
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.AddressRepository
import com.fooddelivery.users.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class AddressService(
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository
) {

    fun createAddress(userId: UUID, request: CreateAddressRequest): AddressResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User", userId) }

        // If this is the first address or explicitly set as default, unset other defaults
        if (request.isDefault || addressRepository.countByUserId(userId) == 0L) {
            addressRepository.findByUserIdAndIsDefault(userId, true)?.let { existingDefault ->
                existingDefault.isDefault = false
                addressRepository.save(existingDefault)
            }
        }

        val address = Address().apply {
            this.user = user
            this.label = request.label
            this.street = request.street
            this.building = request.building
            this.floor = request.floor
            this.apartment = request.apartment
            this.city = request.city
            this.state = request.state
            this.postalCode = request.postalCode
            this.country = request.country
            this.latitude = request.latitude
            this.longitude = request.longitude
            this.instructions = request.instructions
            this.isDefault = request.isDefault || addressRepository.countByUserId(userId) == 0L
            this.addressType = request.addressType
        }

        return AddressResponse.fromEntity(addressRepository.save(address))
    }

    fun getAddresses(userId: UUID, pageable: Pageable): Page<AddressResponse> {
        return addressRepository.findByUserId(userId, pageable)
            .map { AddressResponse.fromEntity(it) }
    }

    fun getAddressById(userId: UUID, addressId: UUID): AddressResponse {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw AccessDeniedException("You can only access your own addresses")
        }

        return AddressResponse.fromEntity(address)
    }

    fun updateAddress(userId: UUID, addressId: UUID, request: UpdateAddressRequest): AddressResponse {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw AccessDeniedException("You can only update your own addresses")
        }

        request.label?.let { address.label = it }
        request.street?.let { address.street = it }
        request.building?.let { address.building = it }
        request.floor?.let { address.floor = it }
        request.apartment?.let { address.apartment = it }
        request.city?.let { address.city = it }
        request.state?.let { address.state = it }
        request.postalCode?.let { address.postalCode = it }
        request.country?.let { address.country = it }
        request.latitude?.let { address.latitude = it }
        request.longitude?.let { address.longitude = it }
        request.instructions?.let { address.instructions = it }
        request.addressType?.let { address.addressType = it }

        if (request.isDefault == true) {
            val existingDefault = addressRepository.findByUserIdAndIsDefault(userId, true)
            if (existingDefault != null && existingDefault.id != addressId) {
                existingDefault.isDefault = false
                addressRepository.save(existingDefault)
            }
            address.isDefault = true
        }

        return AddressResponse.fromEntity(addressRepository.save(address))
    }

    fun setDefaultAddress(userId: UUID, request: SetDefaultAddressRequest): AddressResponse {
        val newDefault = addressRepository.findById(request.addressId)
            .orElseThrow { ResourceNotFoundException("Address", request.addressId) }

        if (newDefault.user?.id != userId) {
            throw AccessDeniedException("You can only set your own addresses as default")
        }

        val existingDefault = addressRepository.findByUserIdAndIsDefault(userId, true)
        if (existingDefault != null && existingDefault.id != request.addressId) {
            existingDefault.isDefault = false
            addressRepository.save(existingDefault)
        }

        newDefault.isDefault = true
        return AddressResponse.fromEntity(addressRepository.save(newDefault))
    }

    fun deleteAddress(userId: UUID, addressId: UUID) {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address", addressId) }

        if (address.user?.id != userId) {
            throw AccessDeniedException("You can only delete your own addresses")
        }

        val wasDefault = address.isDefault
        addressRepository.delete(address)

        // If we deleted the default address, set another one as default
        if (wasDefault) {
            addressRepository.findByUserId(userId).firstOrNull()?.let { newDefault ->
                newDefault.isDefault = true
                addressRepository.save(newDefault)
            }
        }
    }

    fun getDefaultAddress(userId: UUID): AddressResponse? {
        return addressRepository.findDefaultByUserId(userId)
            ?.let { AddressResponse.fromEntity(it) }
    }
}