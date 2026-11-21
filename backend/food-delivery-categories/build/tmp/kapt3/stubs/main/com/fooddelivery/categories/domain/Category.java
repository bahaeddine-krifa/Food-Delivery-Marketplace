package com.fooddelivery.categories.domain;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "categories", indexes = {@jakarta.persistence.Index(name = "idx_categories_restaurant", columnList = "restaurant_id,display_order")})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u000e\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R \u0010\u0003\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR \u0010\u000f\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0006\"\u0004\b\u0011\u0010\bR\u001e\u0010\u0012\u001a\u00020\u00138\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0017\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0006\"\u0004\b\u0019\u0010\bR \u0010\u001a\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010 \u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001f\u00a8\u0006!"}, d2 = {"Lcom/fooddelivery/categories/domain/Category;", "Lcom/fooddelivery/common/domain/AuditableEntity;", "()V", "description", "", "getDescription", "()Ljava/lang/String;", "setDescription", "(Ljava/lang/String;)V", "displayOrder", "", "getDisplayOrder", "()I", "setDisplayOrder", "(I)V", "imageUrl", "getImageUrl", "setImageUrl", "isActive", "", "()Z", "setActive", "(Z)V", "name", "getName", "setName", "restaurant", "error/NonExistentClass", "getRestaurant", "()Lerror/NonExistentClass;", "setRestaurant", "(Lerror/NonExistentClass;)V", "Lerror/NonExistentClass;", "food-delivery-categories"})
public final class Category extends com.fooddelivery.common.domain.AuditableEntity {
    @jakarta.persistence.ManyToOne()
    @jakarta.persistence.JoinColumn(name = "restaurant_id", nullable = false)
    @org.jetbrains.annotations.Nullable()
    private error.NonExistentClass restaurant;
    @jakarta.persistence.Column(nullable = false, length = 100)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name = "";
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    @org.jetbrains.annotations.Nullable()
    private java.lang.String description;
    @jakarta.persistence.Column(name = "image_url", length = 500)
    @org.jetbrains.annotations.Nullable()
    private java.lang.String imageUrl;
    @jakarta.persistence.Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
    @jakarta.persistence.Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    public Category() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final error.NonExistentClass getRestaurant() {
        return null;
    }
    
    public final void setRestaurant(@org.jetbrains.annotations.Nullable()
    error.NonExistentClass p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    public final void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    public final void setDescription(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getImageUrl() {
        return null;
    }
    
    public final void setImageUrl(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getDisplayOrder() {
        return 0;
    }
    
    public final void setDisplayOrder(int p0) {
    }
    
    public final boolean isActive() {
        return false;
    }
    
    public final void setActive(boolean p0) {
    }
}