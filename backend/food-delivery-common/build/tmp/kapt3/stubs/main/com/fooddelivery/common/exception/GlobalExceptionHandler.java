package com.fooddelivery.common.exception;

@org.springframework.web.bind.annotation.RestControllerAdvice()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002JB\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0012J\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u00182\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u001a2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u001c2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\u001e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\"\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\n\u0010\u0013\u001a\u00060 j\u0002`!2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010\"\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020#2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010$\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020%2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010&\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\'2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017J\u001e\u0010(\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020)2\u0006\u0010\u0010\u001a\u00020\u0011H\u0017R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/fooddelivery/common/exception/GlobalExceptionHandler;", "", "()V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "buildResponse", "Lorg/springframework/http/ResponseEntity;", "Lcom/fooddelivery/common/exception/ErrorResponse;", "status", "Lorg/springframework/http/HttpStatus;", "code", "", "message", "details", "", "request", "Ljakarta/servlet/http/HttpServletRequest;", "handleAccessDenied", "ex", "Lorg/springframework/security/access/AccessDeniedException;", "handleBadCredentials", "Lorg/springframework/security/authentication/BadCredentialsException;", "handleBindException", "Lorg/springframework/validation/BindException;", "handleBusinessException", "Lcom/fooddelivery/common/exception/BusinessException;", "handleConstraintViolation", "Ljakarta/validation/ConstraintViolationException;", "handleDataIntegrityViolation", "Lorg/springframework/dao/DataIntegrityViolationException;", "handleGenericException", "Ljava/lang/Exception;", "Lkotlin/Exception;", "handleHttpMessageNotReadable", "Lorg/springframework/http/converter/HttpMessageNotReadableException;", "handleMissingParam", "Lorg/springframework/web/bind/MissingServletRequestParameterException;", "handleTypeMismatch", "Lorg/springframework/web/method/annotation/MethodArgumentTypeMismatchException;", "handleValidationException", "Lorg/springframework/web/bind/MethodArgumentNotValidException;", "food-delivery-common"})
public class GlobalExceptionHandler {
    private final org.slf4j.Logger logger = null;
    
    public GlobalExceptionHandler() {
        super();
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {com.fooddelivery.common.exception.BusinessException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleBusinessException(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.exception.BusinessException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.web.bind.MethodArgumentNotValidException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleValidationException(@org.jetbrains.annotations.NotNull()
    org.springframework.web.bind.MethodArgumentNotValidException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.validation.BindException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleBindException(@org.jetbrains.annotations.NotNull()
    org.springframework.validation.BindException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {jakarta.validation.ConstraintViolationException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleConstraintViolation(@org.jetbrains.annotations.NotNull()
    jakarta.validation.ConstraintViolationException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.http.converter.HttpMessageNotReadableException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleHttpMessageNotReadable(@org.jetbrains.annotations.NotNull()
    org.springframework.http.converter.HttpMessageNotReadableException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.web.bind.MissingServletRequestParameterException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleMissingParam(@org.jetbrains.annotations.NotNull()
    org.springframework.web.bind.MissingServletRequestParameterException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleTypeMismatch(@org.jetbrains.annotations.NotNull()
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.security.authentication.BadCredentialsException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleBadCredentials(@org.jetbrains.annotations.NotNull()
    org.springframework.security.authentication.BadCredentialsException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.security.access.AccessDeniedException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleAccessDenied(@org.jetbrains.annotations.NotNull()
    org.springframework.security.access.AccessDeniedException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {org.springframework.dao.DataIntegrityViolationException.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleDataIntegrityViolation(@org.jetbrains.annotations.NotNull()
    org.springframework.dao.DataIntegrityViolationException ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {java.lang.Exception.class})
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> handleGenericException(@org.jetbrains.annotations.NotNull()
    java.lang.Exception ex, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
    
    private org.springframework.http.ResponseEntity<com.fooddelivery.common.exception.ErrorResponse> buildResponse(org.springframework.http.HttpStatus status, java.lang.String code, java.lang.String message, java.util.Map<java.lang.String, ? extends java.lang.Object> details, jakarta.servlet.http.HttpServletRequest request) {
        return null;
    }
}