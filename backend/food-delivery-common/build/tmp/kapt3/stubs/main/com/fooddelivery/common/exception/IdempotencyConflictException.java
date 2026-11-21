package com.fooddelivery.common.exception;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/fooddelivery/common/exception/IdempotencyConflictException;", "Lcom/fooddelivery/common/exception/BusinessException;", "idempotencyKey", "", "(Ljava/lang/String;)V", "food-delivery-common"})
public final class IdempotencyConflictException extends com.fooddelivery.common.exception.BusinessException {
    
    public IdempotencyConflictException(@org.jetbrains.annotations.NotNull()
    java.lang.String idempotencyKey) {
        super(null, null, null, null);
    }
}