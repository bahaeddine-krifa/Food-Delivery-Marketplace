package com.fooddelivery.categories.service;

@org.springframework.stereotype.Service()
@jakarta.transaction.Transactional()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001:\u0002\"#B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J \u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0016J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0016J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\u00132\u0006\u0010\f\u001a\u00020\u000bH\u0016J\u001e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\t0\u00152\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J \u0010\u0018\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0016J\u001d\u0010\u0019\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0012\u00a2\u0006\u0002\u0010\u001aJ,\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\u00132\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0013H\u0016J(\u0010\u001d\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u001eH\u0016J(\u0010\u001f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020!H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\u00a8\u0006$"}, d2 = {"Lcom/fooddelivery/categories/service/CategoryService;", "", "categoryRepository", "Lcom/fooddelivery/categories/repository/CategoryRepository;", "restaurantRepository", "error/NonExistentClass", "(Lcom/fooddelivery/categories/repository/CategoryRepository;Lerror/NonExistentClass;)V", "Lerror/NonExistentClass;", "createCategory", "Lcom/fooddelivery/categories/domain/Category;", "ownerId", "Ljava/util/UUID;", "restaurantId", "request", "Lcom/fooddelivery/categories/service/CategoryService$CreateCategoryRequest;", "deleteCategory", "", "categoryId", "getAllCategories", "", "getCategories", "Lorg/springframework/data/domain/Page;", "pageable", "Lorg/springframework/data/domain/Pageable;", "getCategory", "getOwnedRestaurant", "(Ljava/util/UUID;Ljava/util/UUID;)Lerror/NonExistentClass;", "reorderCategories", "categoryIds", "updateCategory", "Lcom/fooddelivery/categories/service/CategoryService$UpdateCategoryRequest;", "uploadImage", "imageUrl", "", "CreateCategoryRequest", "UpdateCategoryRequest", "food-delivery-categories"})
public class CategoryService {
    @org.jetbrains.annotations.NotNull()
    private final com.fooddelivery.categories.repository.CategoryRepository categoryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final error.NonExistentClass restaurantRepository = null;
    
    public CategoryService(@org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.repository.CategoryRepository categoryRepository, @org.jetbrains.annotations.NotNull()
    error.NonExistentClass restaurantRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.categories.domain.Category createCategory(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.service.CategoryService.CreateCategoryRequest request) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.domain.Page<com.fooddelivery.categories.domain.Category> getCategories(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.fooddelivery.categories.domain.Category> getAllCategories(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.categories.domain.Category getCategory(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.UUID categoryId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.categories.domain.Category updateCategory(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.UUID categoryId, @org.jetbrains.annotations.NotNull()
    com.fooddelivery.categories.service.CategoryService.UpdateCategoryRequest request) {
        return null;
    }
    
    public void deleteCategory(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.UUID categoryId) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.fooddelivery.categories.domain.Category> reorderCategories(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.util.UUID> categoryIds) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.fooddelivery.categories.domain.Category uploadImage(@org.jetbrains.annotations.NotNull()
    java.util.UUID ownerId, @org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.UUID categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String imageUrl) {
        return null;
    }
    
    private error.NonExistentClass getOwnedRestaurant(java.util.UUID ownerId, java.util.UUID restaurantId) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\'\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J+\u0010\u000e\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0015"}, d2 = {"Lcom/fooddelivery/categories/service/CategoryService$CreateCategoryRequest;", "", "name", "", "description", "imageUrl", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDescription", "()Ljava/lang/String;", "getImageUrl", "getName", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "food-delivery-categories"})
    public static final class CreateCategoryRequest {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String name = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String description = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String imageUrl = null;
        
        public CreateCategoryRequest(@jakarta.validation.constraints.NotBlank()
        @jakarta.validation.constraints.Size(max = 100)
        @org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
        java.lang.String imageUrl) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getName() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getDescription() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getImageUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.fooddelivery.categories.service.CategoryService.CreateCategoryRequest copy(@jakarta.validation.constraints.NotBlank()
        @jakarta.validation.constraints.Size(max = 100)
        @org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\bJ\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\fJ>\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0014J\u0013\u0010\u0015\u001a\u00020\u00072\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u0006\u0010\fR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001a"}, d2 = {"Lcom/fooddelivery/categories/service/CategoryService$UpdateCategoryRequest;", "", "name", "", "description", "imageUrl", "isActive", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;)V", "getDescription", "()Ljava/lang/String;", "getImageUrl", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getName", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;)Lcom/fooddelivery/categories/service/CategoryService$UpdateCategoryRequest;", "equals", "other", "hashCode", "", "toString", "food-delivery-categories"})
    public static final class UpdateCategoryRequest {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String name = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String description = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String imageUrl = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Boolean isActive = null;
        
        public UpdateCategoryRequest(@org.jetbrains.annotations.Nullable()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
        java.lang.String imageUrl, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isActive) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getName() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getDescription() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getImageUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean isActive() {
            return null;
        }
        
        public UpdateCategoryRequest() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.fooddelivery.categories.service.CategoryService.UpdateCategoryRequest copy(@org.jetbrains.annotations.Nullable()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
        java.lang.String imageUrl, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isActive) {
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