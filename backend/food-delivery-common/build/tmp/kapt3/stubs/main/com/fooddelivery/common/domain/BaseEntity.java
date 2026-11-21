package com.fooddelivery.common.domain;

@jakarta.persistence.MappedSuperclass()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\n\b\u0017\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/fooddelivery/common/domain/BaseEntity;", "", "id", "Ljava/util/UUID;", "version", "", "(Ljava/util/UUID;J)V", "getId", "()Ljava/util/UUID;", "setId", "(Ljava/util/UUID;)V", "getVersion", "()J", "setVersion", "(J)V", "food-delivery-common"})
public class BaseEntity {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @org.jetbrains.annotations.NotNull()
    private java.util.UUID id;
    @jakarta.persistence.Version()
    private long version;
    
    public BaseEntity(@org.jetbrains.annotations.NotNull()
    java.util.UUID id, long version) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.UUID getId() {
        return null;
    }
    
    public final void setId(@org.jetbrains.annotations.NotNull()
    java.util.UUID p0) {
    }
    
    public final long getVersion() {
        return 0L;
    }
    
    public final void setVersion(long p0) {
    }
    
    public BaseEntity() {
        super();
    }
}