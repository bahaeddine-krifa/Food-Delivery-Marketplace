package com.fooddelivery.orders

import com.fooddelivery.auth.dto.RegisterRequest
import com.fooddelivery.auth.service.JwtService
import com.fooddelivery.common.enums.DeliveryStatus
import com.fooddelivery.common.enums.OrderStatus
import com.fooddelivery.common.enums.UserRole
import com.fooddelivery.common.enums.UserStatus
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.deliveries.domain.Delivery
import com.fooddelivery.deliveries.repository.DeliveryRepository
import com.fooddelivery.deliveries.service.DeliveryService
import com.fooddelivery.deliveries.dto.AssignDeliveryRequest as DeliveryAssignRequest
import com.fooddelivery.deliveries.dto.UpdateDeliveryStatusRequest as DeliveryUpdateStatusRequest
import com.fooddelivery.orders.domain.Order
import com.fooddelivery.orders.domain.OrderItem
import com.fooddelivery.orders.dto.CreateOrderRequest
import com.fooddelivery.orders.dto.OrderItemRequest
import com.fooddelivery.orders.dto.UpdateOrderStatusRequest
import com.fooddelivery.orders.repository.OrderRepository
import com.fooddelivery.orders.repository.OrderItemRepository
import com.fooddelivery.orders.repository.OrderStatusHistoryRepository
import com.fooddelivery.orders.service.OrderService
import com.fooddelivery.orders.test.OrdersTestApplication
import com.fooddelivery.products.domain.Product
import com.fooddelivery.products.repository.ProductRepository
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.domain.RestaurantHours
import com.fooddelivery.common.enums.DayOfWeek
import com.fooddelivery.restaurants.repository.RestaurantRepository
import com.fooddelivery.restaurants.repository.RestaurantHoursRepository
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.repository.AddressRepository
import com.fooddelivery.users.repository.UserRepository
import com.fooddelivery.users.repository.CustomerProfileRepository
import com.fooddelivery.users.repository.RestaurantOwnerProfileRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest(classes = [
    OrdersTestApplication::class
])
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderLifecycleIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var customerProfileRepository: CustomerProfileRepository

    @Autowired
    private lateinit var restaurantOwnerProfileRepository: RestaurantOwnerProfileRepository

    @Autowired
    private lateinit var restaurantRepository: RestaurantRepository

    @Autowired
    private lateinit var restaurantHoursRepository: RestaurantHoursRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var orderStatusHistoryRepository: OrderStatusHistoryRepository

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var deliveryService: DeliveryService

    @Autowired
    private lateinit var jwtService: JwtService

    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var objectMapper: ObjectMapper

    private var customerToken: String = ""
    private var restaurantOwnerToken: String = ""
    private var deliveryPersonToken: String = ""
    private var adminToken: String = ""

    private var customerId: UUID = UUID.randomUUID()
    private var restaurantOwnerId: UUID = UUID.randomUUID()
    private var deliveryPersonId: UUID = UUID.randomUUID()
    private var adminId: UUID = UUID.randomUUID()

    private var restaurantId: UUID = UUID.randomUUID()
    private var productId: UUID = UUID.randomUUID()
    private var customerAddressId: UUID = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        passwordEncoder = BCryptPasswordEncoder(4)
        objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        // Clean up
        orderStatusHistoryRepository.deleteAll()
        orderItemRepository.deleteAll()
        orderRepository.deleteAll()
        deliveryRepository.deleteAll()
        productRepository.deleteAll()
        restaurantHoursRepository.deleteAll()
        restaurantRepository.deleteAll()
        customerProfileRepository.deleteAll()
        restaurantOwnerProfileRepository.deleteAll()
        addressRepository.deleteAll()
        userRepository.deleteAll()

        // Create test users
        val customer = createAndSaveUser("customer@test.com", UserRole.CUSTOMER, UserStatus.ACTIVE)
        customerId = customer.id
        customerToken = jwtService.generateAccessToken(customer.id, customer.email, customer.role)

        val restaurantOwner = createAndSaveUser("owner@test.com", UserRole.RESTAURANT_OWNER, UserStatus.ACTIVE)
        restaurantOwnerId = restaurantOwner.id
        restaurantOwnerToken = jwtService.generateAccessToken(restaurantOwner.id, restaurantOwner.email, restaurantOwner.role)

        val deliveryPerson = createAndSaveUser("delivery@test.com", UserRole.DELIVERY_PERSON, UserStatus.ACTIVE)
        deliveryPersonId = deliveryPerson.id
        deliveryPersonToken = jwtService.generateAccessToken(deliveryPerson.id, deliveryPerson.email, deliveryPerson.role)

        val admin = createAndSaveUser("admin@test.com", UserRole.ADMIN, UserStatus.ACTIVE)
        adminId = admin.id
        adminToken = jwtService.generateAccessToken(admin.id, admin.email, admin.role)

        // Create restaurant owner profile
        createAndSaveRestaurantOwnerProfile(restaurantOwnerId)

        // Create address for restaurant
        val restaurantAddress = createAndSaveAddress(restaurantOwnerId, "Restaurant", "123 Restaurant St", "Tunis", "1000", 36.8065, 10.1815)

        // Create restaurant
        val restaurant = createAndSaveRestaurant(restaurantOwnerId, restaurantAddress.id, "Test Restaurant", "Tunisian")
        restaurantId = restaurant.id

        // Create restaurant hours (open every day)
        createRestaurantHours(restaurant)

        // Create address for customer
        val customerAddress = createAndSaveAddress(customerId, "Home", "456 Customer Ave", "Tunis", "1000", 36.8100, 10.1850)
        customerAddressId = customerAddress.id

        // Create product
        val product = createAndSaveProduct(restaurantId, "Test Product", BigDecimal("25.00"))
        productId = product.id
    }

    private fun createAndSaveUser(email: String, role: UserRole, status: UserStatus): User {
        return userRepository.save(User().apply {
            this.email = email
            this.passwordHash = passwordEncoder.encode("Password123!")
            this.firstName = "Test"
            this.lastName = "User"
            this.role = role
            this.status = status
            this.emailVerified = true
        })
    }

    private fun createAndSaveRestaurantOwnerProfile(userId: UUID) {
        val user = userRepository.getReferenceById(userId)
        restaurantOwnerProfileRepository.save(com.fooddelivery.users.domain.RestaurantOwnerProfile().apply {
            this.user = user
            this.businessName = "Test Business"
            this.taxId = "TAX${System.currentTimeMillis()}"
            this.commissionRate = BigDecimal("15.00")
        })
    }

    private fun createAndSaveAddress(
        userId: UUID,
        label: String,
        street: String,
        city: String,
        postalCode: String,
        latitude: Double,
        longitude: Double
    ): Address {
        val user = userRepository.getReferenceById(userId)
        return addressRepository.save(Address().apply {
            this.user = user
            this.label = label
            this.street = street
            this.city = city
            this.state = "Tunis"
            this.postalCode = postalCode
            this.country = "TN"
            this.latitude = BigDecimal.valueOf(latitude)
            this.longitude = BigDecimal.valueOf(longitude)
            this.isDefault = true
        })
    }

    private fun createAndSaveRestaurant(
        ownerId: UUID,
        addressId: UUID,
        name: String,
        cuisine: String
    ): Restaurant {
        val owner = userRepository.getReferenceById(ownerId)
        val address = addressRepository.getReferenceById(addressId)
        return restaurantRepository.save(Restaurant().apply {
            this.owner = owner
            this.name = name
            this.slug = name.lowercase().replace(" ", "-") + "-${UUID.randomUUID()}"
            this.description = "Test restaurant description"
            this.phone = "+21612345678"
            this.email = "restaurant@test.com"
            this.cuisineType = cuisine
            this.status = com.fooddelivery.common.enums.RestaurantStatus.APPROVED
            this.address = address
            this.deliveryRadiusKm = BigDecimal("10.00")
            this.minimumOrderAmount = BigDecimal("10.00")
            this.deliveryFee = BigDecimal("2.50")
            this.preparationTimeMinutes = 30
        })
    }

    private fun createRestaurantHours(restaurant: Restaurant) {
        DayOfWeek.values().forEach { day ->
            restaurantHoursRepository.save(RestaurantHours().apply {
                this.restaurant = restaurant
                this.dayOfWeek = day
                this.openTime = java.time.LocalTime.of(9, 0)
                this.closeTime = java.time.LocalTime.of(22, 0)
                this.isClosed = false
            })
        }
    }

    private fun createAndSaveProduct(restaurantId: UUID, name: String, price: BigDecimal): Product {
        val restaurant = restaurantRepository.getReferenceById(restaurantId)
        return productRepository.save(Product().apply {
            this.restaurant = restaurant
            this.name = name
            this.description = "Test product description"
            this.price = price
            this.isAvailable = true
            this.isVegetarian = false
            this.isVegan = false
            this.isGlutenFree = false
            this.displayOrder = 0
        })
    }

    private fun getAuthHeader(token: String): String = "Bearer $token"

    @Test
    fun `complete order lifecycle - customer creates order`() {
        val request = CreateOrderRequest(
            restaurantId = restaurantId,
            addressId = customerAddressId,
            idempotencyKey = "idem-${UUID.randomUUID()}",
            customerNotes = "Please deliver quickly",
            items = listOf(OrderItemRequest(productId = productId, quantity = 2, notes = "Extra spicy"))
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
            .header("Authorization", getAuthHeader(customerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PENDING"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.items.size()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.items[0].quantity").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalAmount").value(62.00)) // 25*2 + tax(9.5) + delivery(2.5)
    }

    @Test
    fun `complete order lifecycle - idempotency prevents duplicate orders`() {
        val idempotencyKey = "idem-${UUID.randomUUID()}"
        val request = CreateOrderRequest(
            restaurantId = restaurantId,
            addressId = customerAddressId,
            idempotencyKey = idempotencyKey,
            items = listOf(OrderItemRequest(productId = productId, quantity = 1))
        )

        // First request
        val response1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
            .header("Authorization", getAuthHeader(customerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        // Second request with same idempotency key
        val response2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
            .header("Authorization", getAuthHeader(customerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        // Both should return the same order
        val order1 = objectMapper.readTree(response1.response.contentAsString).get("data").get("id").asText()
        val order2 = objectMapper.readTree(response2.response.contentAsString).get("data").get("id").asText()

        assert(order1 == order2) { "Idempotency should return same order" }
    }

    @Test
    fun `complete order lifecycle - restaurant owner accepts order`() {
        val orderId = createOrder(customerToken)

        // Restaurant owner accepts order
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(OrderStatus.ACCEPTED, "Order accepted"))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("ACCEPTED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.confirmedAt").exists())
    }

    @Test
    fun `complete order lifecycle - restaurant owner prepares order`() {
        val orderId = createOrder(customerToken)

        // Accept first
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(OrderStatus.ACCEPTED))))

        // Then prepare
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(OrderStatus.PREPARING))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PREPARING"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.preparingAt").exists())
    }

    @Test
    fun `complete order lifecycle - restaurant owner marks order ready`() {
        val orderId = createOrder(customerToken)

        // Accept -> Preparing -> Ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(OrderStatus.READY))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("READY"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.readyAt").exists())
    }

    @Test
    fun `complete order lifecycle - admin assigns delivery person`() {
        val orderId = createOrder(customerToken)

        // Restaurant owner marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin creates delivery
        val deliveryResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/$orderId")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PENDING_ASSIGNMENT"))
            .andReturn()

        val deliveryId = objectMapper.readTree(deliveryResponse.response.contentAsString).get("data").get("id").asText()

        // Admin assigns delivery person
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/$deliveryId/assign")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryAssignRequest(deliveryPersonId))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("ASSIGNED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveryPersonId").value(deliveryPersonId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.assignedAt").exists())
    }

    @Test
    fun `complete order lifecycle - delivery person accepts delivery`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin assigns delivery
        val deliveryId = assignDelivery(orderId, adminToken, deliveryPersonId)

        // Delivery person accepts
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryUpdateStatusRequest(DeliveryStatus.ACCEPTED))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("ACCEPTED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.acceptedAt").exists())
    }

    @Test
    fun `complete order lifecycle - delivery person picks up order`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin assigns delivery
        val deliveryId = assignDelivery(orderId, adminToken, deliveryPersonId)

        // Delivery person accepts
        updateDeliveryStatus(deliveryId, deliveryPersonToken, DeliveryStatus.ACCEPTED)

        // Delivery person picks up
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryUpdateStatusRequest(
                DeliveryStatus.PICKED_UP,
                pickupLatitude = BigDecimal("36.8065"),
                pickupLongitude = BigDecimal("10.1815"),
                pickupPhotoUrl = "https://example.com/pickup.jpg"
            ))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PICKED_UP"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.pickedUpAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.pickupPhotoUrl").value("https://example.com/pickup.jpg"))
    }

    @Test
    fun `complete order lifecycle - delivery person delivers order`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin assigns delivery
        val deliveryId = assignDelivery(orderId, adminToken, deliveryPersonId)

        // Delivery person accepts -> picks up -> delivering -> delivers
        updateDeliveryStatus(deliveryId, deliveryPersonToken, DeliveryStatus.ACCEPTED)
        updateDeliveryStatus(deliveryId, deliveryPersonToken, DeliveryStatus.PICKED_UP,
            pickupLatitude = BigDecimal("36.8065"), pickupLongitude = BigDecimal("10.1815"))

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryUpdateStatusRequest(
                DeliveryStatus.DELIVERING,
                deliveryLatitude = BigDecimal("36.8100"),
                deliveryLongitude = BigDecimal("10.1850")
            ))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("DELIVERING"))

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryUpdateStatusRequest(
                DeliveryStatus.DELIVERED,
                deliveryLatitude = BigDecimal("36.8100"),
                deliveryLongitude = BigDecimal("10.1850"),
                deliveryPhotoUrl = "https://example.com/delivered.jpg",
                customerSignatureUrl = "https://example.com/signature.jpg"
            ))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("DELIVERED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveredAt").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveryPhotoUrl").value("https://example.com/delivered.jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.customerSignatureUrl").value("https://example.com/signature.jpg"))
    }

    @Test
    fun `complete order lifecycle - customer cancels pending order`() {
        val orderId = createOrder(customerToken)

        // Customer cancels
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/$orderId/cancel")
            .header("Authorization", getAuthHeader(customerToken))
            .param("reason", "Changed my mind")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("CANCELLED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.cancellationReason").value("Changed my mind"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.cancelledAt").exists())
    }

    @Test
    fun `complete order lifecycle - restaurant owner cancels before acceptance`() {
        val orderId = createOrder(customerToken)

        // Restaurant owner cancels
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/$orderId/cancel")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .param("reason", "Out of ingredients")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("CANCELLED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.cancellationReason").value("Out of ingredients"))
    }

    @Test
    fun `complete order lifecycle - invalid transition rejected`() {
        val orderId = createOrder(customerToken)

        // Try to skip from PENDING to READY (should fail)
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(OrderStatus.READY))))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("INVALID_TRANSITION"))
    }

    @Test
    fun `complete order lifecycle - unauthorized user cannot update order`() {
        val orderId = createOrder(customerToken)

        // Another customer tries to cancel
        val otherCustomer = createAndSaveUser("other@test.com", UserRole.CUSTOMER, UserStatus.ACTIVE)
        val otherToken = jwtService.generateAccessToken(otherCustomer.id, otherCustomer.email, otherCustomer.role)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/$orderId/cancel")
            .header("Authorization", getAuthHeader(otherToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `complete order lifecycle - status history recorded`() {
        val orderId = createOrder(customerToken)

        // Restaurant accepts
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)

        // Check history
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/$orderId/history")
            .header("Authorization", getAuthHeader(customerToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(2)) // PENDING + ACCEPTED
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].toStatus").value("ACCEPTED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[1].toStatus").value("PENDING"))
    }

    @Test
    fun `complete order lifecycle - customer views own orders`() {
        createOrder(customerToken)
        createOrder(customerToken)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
            .header("Authorization", getAuthHeader(customerToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(2))
    }

    @Test
    fun `complete order lifecycle - restaurant owner views restaurant orders`() {
        createOrder(customerToken)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
            .header("Authorization", getAuthHeader(restaurantOwnerToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(1))
    }

    @Test
    fun `complete order lifecycle - admin views all orders`() {
        createOrder(customerToken)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(1))
    }

    @Test
    fun `complete order lifecycle - delivery person views my deliveries`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin assigns delivery
        val deliveryId = assignDelivery(orderId, adminToken, deliveryPersonId)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/my-deliveries")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].id").value(deliveryId.toString()))
    }

    @Test
    fun `complete order lifecycle - available deliveries list`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin creates delivery but doesn't assign
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/$orderId")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/available")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.size()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].status").value("PENDING_ASSIGNMENT"))
    }

    @Test
    fun `complete order lifecycle - delivery fails and order cancelled`() {
        val orderId = createOrder(customerToken)

        // Restaurant marks ready
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.ACCEPTED)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.PREPARING)
        updateOrderStatus(orderId, restaurantOwnerToken, OrderStatus.READY)

        // Admin assigns delivery
        val deliveryId = assignDelivery(orderId, adminToken, deliveryPersonId)

        // Delivery person accepts
        updateDeliveryStatus(deliveryId, deliveryPersonToken, DeliveryStatus.ACCEPTED)

        // Delivery fails
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(deliveryPersonToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryUpdateStatusRequest(DeliveryStatus.FAILED))))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("FAILED"))

        // Order should be cancelled
        val order = orderRepository.findById(orderId).orElseThrow()
        assert(order.status == OrderStatus.CANCELLED) { "Order should be cancelled when delivery fails" }
    }

    private fun createOrder(token: String): UUID {
        val request = CreateOrderRequest(
            restaurantId = restaurantId,
            addressId = customerAddressId,
            idempotencyKey = "idem-${UUID.randomUUID()}",
            items = listOf(OrderItemRequest(productId = productId, quantity = 1))
        )

        val response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
            .header("Authorization", getAuthHeader(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        return UUID.fromString(objectMapper.readTree(response.response.contentAsString).get("data").get("id").asText())
    }

    private fun updateOrderStatus(orderId: UUID, token: String, status: OrderStatus) {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/$orderId/status")
            .header("Authorization", getAuthHeader(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UpdateOrderStatusRequest(status))))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun assignDelivery(orderId: UUID, adminToken: String, deliveryPersonId: UUID): UUID {
        // Create delivery
        val deliveryResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/$orderId")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val deliveryId = UUID.fromString(
            objectMapper.readTree(deliveryResponse.response.contentAsString).get("data").get("id").asText()
        )

        // Assign delivery person
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/$deliveryId/assign")
            .header("Authorization", getAuthHeader(adminToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(DeliveryAssignRequest(deliveryPersonId))))
            .andExpect(MockMvcResultMatchers.status().isOk)

        return deliveryId
    }

    private fun updateDeliveryStatus(
        deliveryId: UUID,
        token: String,
        status: DeliveryStatus,
        pickupLatitude: BigDecimal? = null,
        pickupLongitude: BigDecimal? = null,
        deliveryLatitude: BigDecimal? = null,
        deliveryLongitude: BigDecimal? = null
    ) {
        val request = DeliveryUpdateStatusRequest(
            status = status,
            pickupLatitude = pickupLatitude,
            pickupLongitude = pickupLongitude,
            deliveryLatitude = deliveryLatitude,
            deliveryLongitude = deliveryLongitude
        )

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/$deliveryId/status")
            .header("Authorization", getAuthHeader(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}