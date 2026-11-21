package com.fooddelivery.common.domain;

@jakarta.persistence.MappedSuperclass()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0006\"\u0004\b\u000b\u0010\b\u00a8\u0006\f"}, d2 = {"Lcom/fooddelivery/common/domain/AuditableEntity;", "Lcom/fooddelivery/common/domain/BaseEntity;", "()V", "createdAt", "Ljava/time/OffsetDateTime;", "getCreatedAt", "()Ljava/time/OffsetDateTime;", "setCreatedAt", "(Ljava/time/OffsetDateTime;)V", "updatedAt", "getUpdatedAt", "setUpdatedAt", "food-delivery-common"})
public class AuditableEntity extends com.fooddelivery.common.domain.BaseEntity {
    @org.jetbrains.annotations.NotNull()
    private java.time.OffsetDateTime createdAt;
    @org.jetbrains.annotations.NotNull()
    private java.time.OffsetDateTime updatedAt;
    
    public AuditableEntity() {
        super(null, 0L);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.OffsetDateTime getCreatedAt() {
        return null;
    }
    
    public final void setCreatedAt(@org.jetbrains.annotations.NotNull()
    java.time.OffsetDateTime p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.OffsetDateTime getUpdatedAt() {
        return null;
    }
    
    public final void setUpdatedAt(@org.jetbrains.annotations.NotNull()
    java.time.OffsetDateTime p0) {
    }
}