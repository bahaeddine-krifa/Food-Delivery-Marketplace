package com.fooddelivery.payments.repository;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\bg\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\t2\u0006\u0010\n\u001a\u00020\u0003H&J\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000eH&J\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00020\t2\u0006\u0010\u0010\u001a\u00020\u0011H&\u00a8\u0006\u0012"}, d2 = {"Lcom/fooddelivery/payments/repository/PaymentRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/fooddelivery/payments/domain/Payment;", "Ljava/util/UUID;", "countByStatus", "", "status", "Lcom/fooddelivery/common/enums/PaymentStatus;", "findByOrderId", "Ljava/util/Optional;", "orderId", "findByStatus", "Lorg/springframework/data/domain/Page;", "pageable", "Lorg/springframework/data/domain/Pageable;", "findByTransactionReference", "reference", "", "food-delivery-payments"})
public abstract interface PaymentRepository extends org.springframework.data.jpa.repository.JpaRepository<com.fooddelivery.payments.domain.Payment, java.util.UUID> {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.Optional<com.fooddelivery.payments.domain.Payment> findByOrderId(@org.jetbrains.annotations.NotNull()
    java.util.UUID orderId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract org.springframework.data.domain.Page<com.fooddelivery.payments.domain.Payment> findByStatus(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.enums.PaymentStatus status, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.Optional<com.fooddelivery.payments.domain.Payment> findByTransactionReference(@org.jetbrains.annotations.NotNull()
    java.lang.String reference);
    
    public abstract long countByStatus(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.enums.PaymentStatus status);
}