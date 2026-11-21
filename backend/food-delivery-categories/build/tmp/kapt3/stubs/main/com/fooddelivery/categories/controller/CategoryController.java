package com.fooddelivery.categories.controller;

@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/v1/restaurants/{restaurantId}/categories"})
@error.NonExistentClass()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001:\u0002&\'B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J0\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0017J0\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0011\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0017J0\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0011\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0017J\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0012J,\u0010\u0014\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00150\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0017JJ\u0010\u0016\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00170\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0018\u001a\u00020\u00192\b\b\u0001\u0010\u001a\u001a\u00020\u00192\b\b\u0001\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\r\u001a\u00020\u000eH\u0017J6\u0010\u001d\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00150\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\u001e2\u0006\u0010\r\u001a\u00020\u000eH\u0017J\"\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H 0\u0017\"\u0004\b\u0000\u0010 2\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H 0!H\u0012J:\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0011\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020#2\u0006\u0010\r\u001a\u00020\u000eH\u0017J:\u0010$\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0011\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020%2\u0006\u0010\r\u001a\u00020\u000eH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/fooddelivery/categories/controller/CategoryController;", "", "categoryService", "Lcom/fooddelivery/categories/service/CategoryService;", "(Lcom/fooddelivery/categories/service/CategoryService;)V", "createCategory", "Lorg/springframework/http/ResponseEntity;", "Lcom/fooddelivery/common/dto/ApiResponse;", "Lcom/fooddelivery/categories/domain/Category;", "restaurantId", "Ljava/util/UUID;", "request", "Lcom/fooddelivery/categories/service/CategoryService$CreateCategoryRequest;", "httpRequest", "Ljakarta/servlet/http/HttpServletRequest;", "deleteCategory", "", "id", "getCategory", "getCurrentUserId", "listAllCategories", "", "listCategories", "Lcom/fooddelivery/common/dto/PageResponse;", "page", "", "size", "sort", "", "reorderCategories", "Lcom/fooddelivery/categories/controller/CategoryController$ReorderRequest;", "toPageResponse", "T", "Lorg/springframework/data/domain/Page;", "updateCategory", "Lcom/fooddelivery/categories/service/CategoryService$UpdateCategoryRequest;", "uploadImage", "Lcom/fooddelivery/categories/controller/CategoryController$ImageUploadRequest;", "ImageUploadRequest", "ReorderRequest", "food-delivery-categories"})
public class CategoryController {
    @org.jetbrains.annotations.NotNull()
    private final com.fooddelivery.categories.service.CategoryService categoryService = null;
    
    public CategoryController(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.service.CategoryService categoryService) {
        super();
    }
    
    @org.springframework.web.bind.annotation.PostMapping()
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<com.fooddelivery.categories.domain.Category>> createCategory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.service.CategoryService.CreateCategoryRequest request, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping()
    @error.NonExistentClass()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<com.fooddelivery.common.dto.PageResponse<com.fooddelivery.categories.domain.Category>>> listCategories(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0")
    int page, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20")
    int size, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "displayOrder,asc")
    @org.jetbrains.annotations.NotNull()
    java.lang.String sort, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/all"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\') or hasRole(\'ADMIN\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<java.util.List<com.fooddelivery.categories.domain.Category>>> listAllCategories(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/{id}"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\') or hasRole(\'ADMIN\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<com.fooddelivery.categories.domain.Category>> getCategory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID id, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.PatchMapping(value = {"/{id}"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<com.fooddelivery.categories.domain.Category>> updateCategory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID id, @jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.service.CategoryService.UpdateCategoryRequest request, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/{id}"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<kotlin.Unit>> deleteCategory(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID id, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.PatchMapping(value = {"/reorder"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<java.util.List<com.fooddelivery.categories.domain.Category>>> reorderCategories(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.controller.CategoryController.ReorderRequest request, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/{id}/image"})
    @error.NonExistentClass()
    @org.springframework.security.access.prepost.PreAuthorize(value = "hasRole(\'RESTAURANT_OWNER\')")
    @org.jetbrains.annotations.NotNull()
    public org.springframework.http.ResponseEntity<com.fooddelivery.common.dto.ApiResponse<com.fooddelivery.categories.domain.Category>> uploadImage(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.util.UUID id, @jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.controller.CategoryController.ImageUploadRequest request, @org.jetbrains.annotations.NotNull()
    jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    private java.util.UUID getCurrentUserId(jakarta.servlet.http.HttpServletRequest httpRequest) {
        return null;
    }
    
    private <T extends java.lang.Object>com.fooddelivery.common.dto.PageResponse<T> toPageResponse(org.springframework.data.domain.Page<T> page) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\f\u001a\u00020\rH\u00d6\u0001J\t\u0010\u000e\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/fooddelivery/categories/controller/CategoryController$ImageUploadRequest;", "", "imageUrl", "", "(Ljava/lang/String;)V", "getImageUrl", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "food-delivery-categories"})
    public static final class ImageUploadRequest {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String imageUrl = null;
        
        public ImageUploadRequest(@jakarta.validation.constraints.NotBlank()
        @org.jetbrains.annotations.NotNull()
        java.lang.String imageUrl) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getImageUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.fooddelivery.categories.controller.CategoryController.ImageUploadRequest copy(@jakarta.validation.constraints.NotBlank()
        @org.jetbrains.annotations.NotNull()
        java.lang.String imageUrl) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u000e\b\u0001\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0003\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/fooddelivery/categories/controller/CategoryController$ReorderRequest;", "", "categoryIds", "", "Ljava/util/UUID;", "(Ljava/util/List;)V", "getCategoryIds", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "food-delivery-categories"})
    public static final class ReorderRequest {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.util.UUID> categoryIds = null;
        
        public ReorderRequest(@jakarta.validation.constraints.NotEmpty()
        @org.jetbrains.annotations.NotNull()
        java.util.List<java.util.UUID> categoryIds) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.util.UUID> getCategoryIds() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.util.UUID> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.fooddelivery.categories.controller.CategoryController.ReorderRequest copy(@jakarta.validation.constraints.NotEmpty()
        @org.jetbrains.annotations.NotNull()
        java.util.List<java.util.UUID> categoryIds) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}