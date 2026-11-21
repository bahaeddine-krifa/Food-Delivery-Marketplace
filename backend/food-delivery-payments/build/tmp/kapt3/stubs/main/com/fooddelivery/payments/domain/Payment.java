package com.fooddelivery.payments.domain;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "payments", uniqueConstraints = {@jakarta.persistence.UniqueConstraint(name = "uk_payments_order_id", columnNames = {"order_id"})}, indexes = {@jakarta.persistence.Index(name = "idx_payments_status", columnList = "status"), @jakarta.persistence.Index(name = "idx_payments_transaction_ref", columnList = "transaction_reference")})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR \u0010\t\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001e\u0010\u0015\u001a\u00020\u00168\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR \u0010\u001b\u001a\u0004\u0018\u00010\u001c8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R \u0010!\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\f\"\u0004\b#\u0010\u000eR \u0010$\u001a\u0004\u0018\u00010\u001c8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u001e\"\u0004\b&\u0010 R\u001e\u0010\'\u001a\u00020(8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,R \u0010-\u001a\u0004\u0018\u00010\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b.\u0010\f\"\u0004\b/\u0010\u000e\u00a8\u00060"}, d2 = {"Lcom/fooddelivery/payments/domain/Payment;", "Lcom/fooddelivery/common/domain/AuditableEntity;", "()V", "amount", "Ljava/math/BigDecimal;", "getAmount", "()Ljava/math/BigDecimal;", "setAmount", "(Ljava/math/BigDecimal;)V", "gatewayResponse", "", "getGatewayResponse", "()Ljava/lang/String;", "setGatewayResponse", "(Ljava/lang/String;)V", "method", "Lcom/fooddelivery/common/enums/PaymentMethod;", "getMethod", "()Lcom/fooddelivery/common/enums/PaymentMethod;", "setMethod", "(Lcom/fooddelivery/common/enums/PaymentMethod;)V", "orderId", "Ljava/util/UUID;", "getOrderId", "()Ljava/util/UUID;", "setOrderId", "(Ljava/util/UUID;)V", "paidAt", "Ljava/time/OffsetDateTime;", "getPaidAt", "()Ljava/time/OffsetDateTime;", "setPaidAt", "(Ljava/time/OffsetDateTime;)V", "refundReason", "getRefundReason", "setRefundReason", "refundedAt", "getRefundedAt", "setRefundedAt", "status", "Lcom/fooddelivery/common/enums/PaymentStatus;", "getStatus", "()Lcom/fooddelivery/common/enums/PaymentStatus;", "setStatus", "(Lcom/fooddelivery/common/enums/PaymentStatus;)V", "transactionReference", "getTransactionReference", "setTransactionReference", "food-delivery-payments"})
public final class Payment extends com.fooddelivery.common.domain.AuditableEntity {
    @jakarta.persistence.Column(name = "order_id", nullable = false, unique = true, columnDefinition = "UUID")
    @org.jetbrains.annotations.NotNull()
    private java.util.UUID orderId;
    @jakarta.persistence.Column(nullable = false, precision = 10, scale = 2)
    @org.jetbrains.annotations.NotNull()
    private java.math.BigDecimal amount;
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(nullable = false, length = 30)
    @org.jetbrains.annotations.NotNull()
    private com.fooddelivery.common.enums.PaymentMethod method = com.fooddelivery.common.enums.PaymentMethod.CASH_ON_DELIVERY;
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(nullable = false, length = 30)
    @org.jetbrains.annotations.NotNull()
    private com.fooddelivery.common.enums.PaymentStatus status = com.fooddelivery.common.enums.PaymentStatus.PENDING;
    @jakarta.persistence.Column(name = "transaction_reference", length = 100)
    @org.jetbrains.annotations.Nullable()
    private java.lang.String transactionReference;
    @org.hibernate.annotations.JdbcTypeCode(value = org.hibernate.type.SqlTypes.JSON)
    @jakarta.persistence.Column(name = "gateway_response", columnDefinition = "jsonb")
    @org.jetbrains.annotations.Nullable()
    private java.lang.String gatewayResponse;
    @jakarta.persistence.Column(name = "paid_at")
    @org.jetbrains.annotations.Nullable()
    private java.time.OffsetDateTime paidAt;
    @jakarta.persistence.Column(name = "refunded_at")
    @org.jetbrains.annotations.Nullable()
    private java.time.OffsetDateTime refundedAt;
    @jakarta.persistence.Column(name = "refund_reason", columnDefinition = "TEXT")
    @org.jetbrains.annotations.Nullable()
    private java.lang.String refundReason;
    
    public Payment() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.UUID getOrderId() {
        return null;
    }
    
    public final void setOrderId(@org.jetbrains.annotations.NotNull()
    java.util.UUID p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.math.BigDecimal getAmount() {
        return null;
    }
    
    public final void setAmount(@org.jetbrains.annotations.NotNull()
    java.math.BigDecimal p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.enums.PaymentMethod getMethod() {
        return null;
    }
    
    public final void setMethod(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.enums.PaymentMethod p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.enums.PaymentStatus getStatus() {
        return null;
    }
    
    public final void setStatus(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.enums.PaymentStatus p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTransactionReference() {
        return null;
    }
    
    public final void setTransactionReference(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getGatewayResponse() {
        return null;
    }
    
    public final void setGatewayResponse(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.time.OffsetDateTime getPaidAt() {
        return null;
    }
    
    public final void setPaidAt(@org.jetbrains.annotations.Nullable()
    java.time.OffsetDateTime p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.time.OffsetDateTime getRefundedAt() {
        return null;
    }
    
    public final void setRefundedAt(@org.jetbrains.annotations.Nullable()
    java.time.OffsetDateTime p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRefundReason() {
        return null;
    }
    
    public final void setRefundReason(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
}