package com.fooddelivery.common.enums;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\b\u0086\u0081\u0002\u0018\u0000 \u00122\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0012B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u0003H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0013"}, d2 = {"Lcom/fooddelivery/common/enums/OrderStatus;", "", "value", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getValue", "()Ljava/lang/String;", "fromValue", "PENDING", "ACCEPTED", "REJECTED", "PREPARING", "READY", "ASSIGNED", "PICKED_UP", "DELIVERING", "DELIVERED", "CANCELLED", "Companion", "food-delivery-common"})
public enum OrderStatus {
    /*public static final*/ PENDING /* = new PENDING(null) */,
    /*public static final*/ ACCEPTED /* = new ACCEPTED(null) */,
    /*public static final*/ REJECTED /* = new REJECTED(null) */,
    /*public static final*/ PREPARING /* = new PREPARING(null) */,
    /*public static final*/ READY /* = new READY(null) */,
    /*public static final*/ ASSIGNED /* = new ASSIGNED(null) */,
    /*public static final*/ PICKED_UP /* = new PICKED_UP(null) */,
    /*public static final*/ DELIVERING /* = new DELIVERING(null) */,
    /*public static final*/ DELIVERED /* = new DELIVERED(null) */,
    /*public static final*/ CANCELLED /* = new CANCELLED(null) */;
    @com.fasterxml.jackson.annotation.JsonValue()
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String value = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.fooddelivery.common.enums.OrderStatus.Companion Companion = null;
    
    OrderStatus(java.lang.String value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getValue() {
        return null;
    }
    
    @com.fasterxml.jackson.annotation.JsonCreator()
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.enums.OrderStatus fromValue(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.fooddelivery.common.enums.OrderStatus> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\"\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006J\u0018\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\n0\t\u00a8\u0006\u000b"}, d2 = {"Lcom/fooddelivery/common/enums/OrderStatus$Companion;", "", "()V", "canTransition", "", "from", "Lcom/fooddelivery/common/enums/OrderStatus;", "to", "validTransitions", "", "", "food-delivery-common"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<com.fooddelivery.common.enums.OrderStatus, java.util.Set<com.fooddelivery.common.enums.OrderStatus>> validTransitions() {
            return null;
        }
        
        public final boolean canTransition(@org.jetbrains.annotations.NotNull()
        com.fooddelivery.common.enums.OrderStatus from, @org.jetbrains.annotations.NotNull()
        com.fooddelivery.common.enums.OrderStatus to) {
            return false;
        }
    }
}