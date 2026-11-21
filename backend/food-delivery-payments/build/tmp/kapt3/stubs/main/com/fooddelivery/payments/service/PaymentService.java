package com.fooddelivery.payments.service;

@org.springframework.stereotype.Service()
@jakarta.transaction.Transactional()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0010\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\bH\u0016J\u001c\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u0010\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/fooddelivery/payments/service/PaymentService;", "", "paymentRepository", "Lcom/fooddelivery/payments/repository/PaymentRepository;", "(Lcom/fooddelivery/payments/repository/PaymentRepository;)V", "createPaymentForOrder", "Lcom/fooddelivery/payments/domain/Payment;", "orderId", "Ljava/util/UUID;", "amount", "Ljava/math/BigDecimal;", "method", "Lcom/fooddelivery/common/enums/PaymentMethod;", "getPaymentById", "id", "getPaymentByOrderId", "getPayments", "Lorg/springframework/data/domain/Page;", "status", "Lcom/fooddelivery/common/enums/PaymentStatus;", "pageable", "Lorg/springframework/data/domain/Pageable;", "initiateRefund", "paymentId", "reason", "", "adminId", "markAsFailed", "markAsPaid", "food-delivery-payments"})
public class PaymentService {
    @org.jetbrains.annotations.NotNull()
    private final com.fooddelivery.payments.repository.PaymentRepository paymentRepository = null;
    
    public PaymentService(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.payments.repository.PaymentRepository paymentRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment createPaymentForOrder(@org.jetbrains.annotations.NotNull()
    java.util.UUID orderId, @org.jetbrains.annotations.NotNull()
    java.math.BigDecimal amount, @org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.enums.PaymentMethod method) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment getPaymentByOrderId(@org.jetbrains.annotations.NotNull()
    java.util.UUID orderId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment markAsPaid(@org.jetbrains.annotations.NotNull()
    java.util.UUID paymentId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment markAsFailed(@org.jetbrains.annotations.NotNull()
    java.util.UUID paymentId, @org.jetbrains.annotations.Nullable()
    java.lang.String reason) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment initiateRefund(@org.jetbrains.annotations.NotNull()
    java.util.UUID paymentId, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    java.util.UUID adminId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.domain.Page<com.fooddelivery.payments.domain.Payment> getPayments(@org.jetbrains.annotations.Nullable()
    com.fooddelivery.common.enums.PaymentStatus status, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.payments.domain.Payment getPaymentById(@org.jetbrains.annotations.NotNull()
    java.util.UUID id) {
        return null;
    }
}