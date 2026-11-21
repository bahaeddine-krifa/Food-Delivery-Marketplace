package com.fooddelivery.common.exception;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\b\b\u0016\u0018\u00002\u00060\u0001j\u0002`\u0002B5\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\u0002\u0010\u000bR\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/fooddelivery/common/exception/BusinessException;", "Ljava/lang/RuntimeException;", "Lkotlin/RuntimeException;", "message", "", "errorCode", "httpStatus", "Lorg/springframework/http/HttpStatus;", "details", "", "", "(Ljava/lang/String;Ljava/lang/String;Lorg/springframework/http/HttpStatus;Ljava/util/Map;)V", "getDetails", "()Ljava/util/Map;", "getErrorCode", "()Ljava/lang/String;", "getHttpStatus", "()Lorg/springframework/http/HttpStatus;", "food-delivery-common"})
public class BusinessException extends java.lang.RuntimeException {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String errorCode = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.http.HttpStatus httpStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Object> details = null;
    
    public BusinessException(@org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    java.lang.String errorCode, @org.jetbrains.annotations.NotNull()
    org.springframework.http.HttpStatus httpStatus, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> details) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getErrorCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.springframework.http.HttpStatus getHttpStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> getDetails() {
        return null;
    }
}