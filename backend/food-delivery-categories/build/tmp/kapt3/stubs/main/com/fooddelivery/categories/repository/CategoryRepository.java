package com.fooddelivery.categories.repository;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\bg\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0003H&J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH&J\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\f2\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000eH\'J\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00020\u00102\u0006\u0010\u0006\u001a\u00020\u0003H&J\u001a\u0010\u0011\u001a\u0004\u0018\u00010\u00022\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0003H&\u00a8\u0006\u0013"}, d2 = {"Lcom/fooddelivery/categories/repository/CategoryRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/fooddelivery/categories/domain/Category;", "Ljava/util/UUID;", "countByRestaurantId", "", "restaurantId", "existsByRestaurantIdAndName", "", "name", "", "findActiveByRestaurantId", "Lorg/springframework/data/domain/Page;", "pageable", "Lorg/springframework/data/domain/Pageable;", "findByRestaurantId", "", "findByRestaurantIdAndId", "id", "food-delivery-categories"})
public abstract interface CategoryRepository extends org.springframework.data.jpa.repository.JpaRepository<com.fooddelivery.categories.domain.Category, java.util.UUID> {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.fooddelivery.categories.domain.Category> findByRestaurantId(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId);
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT c FROM Category c WHERE c.restaurant.id = :restaurantId AND c.isActive = true ORDER BY c.displayOrder")
    @org.jetbrains.annotations.NotNull()
    public abstract org.springframework.data.domain.Page<com.fooddelivery.categories.domain.Category> findActiveByRestaurantId(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.fooddelivery.categories.domain.Category findByRestaurantIdAndId(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.util.UUID id);
    
    public abstract long countByRestaurantId(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId);
    
    public abstract boolean existsByRestaurantIdAndName(@org.jetbrains.annotations.NotNull()
    java.util.UUID restaurantId, @org.jetbrains.annotations.NotNull()
    java.lang.String name);
}