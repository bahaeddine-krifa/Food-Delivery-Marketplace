import { api } from './api';
import type {
  Restaurant,
  CreateRestaurantRequest,
  UpdateRestaurantRequest,
  RestaurantHours,
  RestaurantHoursRequest,
  Category,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  ReorderCategoriesRequest,
  ImageUploadRequest,
  Product,
  CreateProductRequest,
  UpdateProductRequest,
  ReorderProductsRequest,
  ProductImageUploadRequest,
  OrderSummaryResponse,
  OrderResponse,
  OrderStatus,
  UpdateOrderStatusRequest,
  PageResponse,
} from '@/types/api';

export const restaurantApi = {
  // Restaurant
  getMyRestaurants: (): Promise<Restaurant[]> =>
    api.get('/restaurants/mine'),

  getRestaurant: (id: string): Promise<Restaurant> =>
    api.get(`/restaurants/${id}`),

  createDraft: (data: CreateRestaurantRequest): Promise<Restaurant> =>
    api.post('/restaurants', data),

  updateRestaurant: (id: string, data: UpdateRestaurantRequest): Promise<Restaurant> =>
    api.patch(`/restaurants/${id}`, data),

  submitForApproval: (id: string): Promise<Restaurant> =>
    api.post(`/restaurants/${id}/submit`),

  deleteRestaurant: (id: string): Promise<void> =>
    api.delete(`/restaurants/${id}`),

  // Restaurant hours
  getHours: (restaurantId: string): Promise<RestaurantHours[]> =>
    api.get(`/restaurants/${restaurantId}/hours`),

  updateHours: (restaurantId: string, hours: RestaurantHoursRequest[]): Promise<RestaurantHours[]> =>
    api.put(`/restaurants/${restaurantId}/hours`, hours),

  // Categories
  listCategories: (restaurantId: string, params?: { page?: number; size?: number }): Promise<PageResponse<Category>> =>
    api.get(`/restaurants/${restaurantId}/categories`, { params }),

  listAllCategories: (restaurantId: string): Promise<Category[]> =>
    api.get(`/restaurants/${restaurantId}/categories/all`),

  getCategory: (restaurantId: string, id: string): Promise<Category> =>
    api.get(`/restaurants/${restaurantId}/categories/${id}`),

  createCategory: (restaurantId: string, data: CreateCategoryRequest): Promise<Category> =>
    api.post(`/restaurants/${restaurantId}/categories`, data),

  updateCategory: (restaurantId: string, id: string, data: UpdateCategoryRequest): Promise<Category> =>
    api.patch(`/restaurants/${restaurantId}/categories/${id}`, data),

  deleteCategory: (restaurantId: string, id: string): Promise<void> =>
    api.delete(`/restaurants/${restaurantId}/categories/${id}`),

  reorderCategories: (restaurantId: string, data: ReorderCategoriesRequest): Promise<Category[]> =>
    api.patch(`/restaurants/${restaurantId}/categories/reorder`, data),

  uploadCategoryImage: (restaurantId: string, id: string, data: ImageUploadRequest): Promise<Category> =>
    api.post(`/restaurants/${restaurantId}/categories/${id}/image`, data),

  // Products
  listProducts: (restaurantId: string, params?: {
    page?: number;
    size?: number;
    categoryId?: string;
    availableOnly?: boolean;
    vegetarian?: boolean;
    vegan?: boolean;
    glutenFree?: boolean;
    search?: string;
  }): Promise<PageResponse<Product>> =>
    api.get(`/restaurants/${restaurantId}/products`, { params }),

  getProduct: (restaurantId: string, id: string): Promise<Product> =>
    api.get(`/restaurants/${restaurantId}/products/${id}`),

  createProduct: (restaurantId: string, data: CreateProductRequest): Promise<Product> =>
    api.post(`/restaurants/${restaurantId}/products`, data),

  updateProduct: (restaurantId: string, id: string, data: UpdateProductRequest): Promise<Product> =>
    api.patch(`/restaurants/${restaurantId}/products/${id}`, data),

  deleteProduct: (restaurantId: string, id: string): Promise<void> =>
    api.delete(`/restaurants/${restaurantId}/products/${id}`),

  toggleAvailability: (restaurantId: string, id: string): Promise<Product> =>
    api.patch(`/restaurants/${restaurantId}/products/${id}/availability`),

  reorderProducts: (restaurantId: string, data: ReorderProductsRequest): Promise<Product[]> =>
    api.patch(`/restaurants/${restaurantId}/products/reorder`, data),

  uploadProductImages: (restaurantId: string, id: string, data: ProductImageUploadRequest): Promise<Product> =>
    api.post(`/restaurants/${restaurantId}/products/${id}/images`, data),

  // Orders
  listOrders: (restaurantId: string, params?: {
    page?: number;
    size?: number;
    status?: OrderStatus | OrderStatus[];
  }): Promise<PageResponse<OrderSummaryResponse>> =>
    api.get(`/restaurants/${restaurantId}/orders`, { params }),

  getOrder: (restaurantId: string, id: string): Promise<OrderResponse> =>
    api.get(`/restaurants/${restaurantId}/orders/${id}`),

  acceptOrder: (restaurantId: string, id: string): Promise<OrderResponse> =>
    api.post(`/restaurants/${restaurantId}/orders/${id}/accept`),

  rejectOrder: (restaurantId: string, id: string): Promise<OrderResponse> =>
    api.post(`/restaurants/${restaurantId}/orders/${id}/reject`),

  updateOrderStatus: (restaurantId: string, id: string, data: UpdateOrderStatusRequest): Promise<OrderResponse> =>
    api.patch(`/restaurants/${restaurantId}/orders/${id}/status`, data),
};