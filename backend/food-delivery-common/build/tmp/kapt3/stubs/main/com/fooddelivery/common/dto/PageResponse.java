package com.fooddelivery.common.dto;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001e\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002BK\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\n\u0012\u0006\u0010\u000f\u001a\u00020\f\u00a2\u0006\u0002\u0010\u0010J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0006H\u00c6\u0003J\t\u0010 \u001a\u00020\bH\u00c6\u0003J\t\u0010!\u001a\u00020\nH\u00c6\u0003J\t\u0010\"\u001a\u00020\fH\u00c6\u0003J\t\u0010#\u001a\u00020\fH\u00c6\u0003J\t\u0010$\u001a\u00020\nH\u00c6\u0003J\t\u0010%\u001a\u00020\fH\u00c6\u0003Je\u0010&\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\u000e\b\u0002\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\'\u001a\u00020\f2\b\u0010(\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010)\u001a\u00020\nH\u00d6\u0001J\t\u0010*\u001a\u00020+H\u00d6\u0001R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018\u00a8\u0006,"}, d2 = {"Lcom/fooddelivery/common/dto/PageResponse;", "T", "", "content", "", "pageable", "Lcom/fooddelivery/common/dto/PageableInfo;", "totalElements", "", "totalPages", "", "first", "", "last", "numberOfElements", "empty", "(Ljava/util/List;Lcom/fooddelivery/common/dto/PageableInfo;JIZZIZ)V", "getContent", "()Ljava/util/List;", "getEmpty", "()Z", "getFirst", "getLast", "getNumberOfElements", "()I", "getPageable", "()Lcom/fooddelivery/common/dto/PageableInfo;", "getTotalElements", "()J", "getTotalPages", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "toString", "", "food-delivery-common"})
public final class PageResponse<T extends java.lang.Object> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<T> content = null;
    @org.jetbrains.annotations.NotNull()
    private final com.fooddelivery.common.dto.PageableInfo pageable = null;
    private final long totalElements = 0L;
    private final int totalPages = 0;
    private final boolean first = false;
    private final boolean last = false;
    private final int numberOfElements = 0;
    private final boolean empty = false;
    
    public PageResponse(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends T> content, @org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.dto.PageableInfo pageable, long totalElements, int totalPages, boolean first, boolean last, int numberOfElements, boolean empty) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<T> getContent() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.dto.PageableInfo getPageable() {
        return null;
    }
    
    public final long getTotalElements() {
        return 0L;
    }
    
    public final int getTotalPages() {
        return 0;
    }
    
    public final boolean getFirst() {
        return false;
    }
    
    public final boolean getLast() {
        return false;
    }
    
    public final int getNumberOfElements() {
        return 0;
    }
    
    public final boolean getEmpty() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<T> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.dto.PageableInfo component2() {
        return null;
    }
    
    public final long component3() {
        return 0L;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fooddelivery.common.dto.PageResponse<T> copy(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends T> content, @org.jetbrains.annotations.NotNull()
    com.fooddelivery.common.dto.PageableInfo pageable, long totalElements, int totalPages, boolean first, boolean last, int numberOfElements, boolean empty) {
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