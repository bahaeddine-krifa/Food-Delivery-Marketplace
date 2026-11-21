package com.fooddelivery.common.exception;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/fooddelivery/common/exception/InsufficientInventoryException;", "Lcom/fooddelivery/common/exception/BusinessException;", "productName", "", "requested", "", "available", "(Ljava/lang/String;II)V", "food-delivery-common"})
public final class InsufficientInventoryException extends com.fooddelivery.common.exception.BusinessException {
    
    public InsufficientInventoryException(@org.jetbrains.annotations.NotNull()
    java.lang.String productName, int requested, int available) {
        super(null, null, null, null);
    }
}