// API Response types matching backend ApiResponse<T>
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ErrorDetail | null;
  meta: Meta;
}

export interface ErrorDetail {
  code: string;
  message: string;
  details: Record<string, unknown> | null;
}

export interface Meta {
  timestamp: string;
  requestId: string;
  path?: string | null;
}

export interface PageResponse<T> {
  content: T[];
  pageable: PageableInfo;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface PageableInfo {
  pageNumber: number;
  pageSize: number;
  sort: SortInfo;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface SortInfo {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}

// Auth types
export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: UserRole;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserProfileResponse;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserProfileResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string | null;
  avatarUrl?: string | null;
  role: UserRole;
  status: UserStatus;
  emailVerified: boolean;
  createdAt: string;
}

export type UserRole = 'CUSTOMER' | 'RESTAURANT_OWNER' | 'DELIVERY_PERSON' | 'ADMIN';
export type UserStatus = 'PENDING_VERIFICATION' | 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

// Restaurant types
export interface Restaurant {
  id: string;
  name: string;
  slug: string;
  description?: string | null;
  phone: string;
  email?: string | null;
  logoUrl?: string | null;
  coverImageUrl?: string | null;
  cuisineType?: string | null;
  status: RestaurantStatus;
  deliveryRadiusKm: string;
  minimumOrderAmount: string;
  deliveryFee: string;
  preparationTimeMinutes: number;
  averageRating: string;
  totalReviews: number;
  isOpenNow: boolean;
  address?: Address | null;
  hours?: RestaurantHours[] | null;
  createdAt: string;
  updatedAt: string;
}

export type RestaurantStatus = 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' | 'CLOSED';

export interface CreateRestaurantRequest {
  name: string;
  phone: string;
  addressId: string;
}

export interface UpdateRestaurantRequest {
  name?: string;
  description?: string | null;
  phone?: string;
  email?: string | null;
  cuisineType?: string | null;
  deliveryRadiusKm?: string;
  minimumOrderAmount?: string;
  deliveryFee?: string;
  preparationTimeMinutes?: number;
  logoUrl?: string;
  coverImageUrl?: string;
}

export interface RestaurantHours {
  id: string;
  restaurantId: string;
  dayOfWeek: DayOfWeek;
  openTime?: string | null;
  closeTime?: string | null;
  isClosed: boolean;
  createdAt: string;
  updatedAt: string;
}

export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface RestaurantHoursRequest {
  dayOfWeek: DayOfWeek;
  openTime?: string | null;
  closeTime?: string | null;
  isClosed?: boolean;
}

export interface Address {
  id: string;
  userId: string;
  label: string;
  street: string;
  city: string;
  state?: string | null;
  postalCode: string;
  country: string;
  latitude?: number | null;
  longitude?: number | null;
  isDefault: boolean;
  type: AddressType;
  createdAt: string;
  updatedAt: string;
}

export type AddressType = 'HOME' | 'WORK' | 'OTHER';

// Category types
export interface Category {
  id: string;
  restaurantId: string;
  name: string;
  description?: string | null;
  imageUrl?: string | null;
  displayOrder: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
  imageUrl?: string;
  displayOrder?: number;
}

export interface UpdateCategoryRequest {
  name?: string;
  description?: string;
  imageUrl?: string;
  displayOrder?: number;
  isActive?: boolean;
}

export interface ReorderCategoriesRequest {
  categoryIds: string[];
}

export interface ImageUploadRequest {
  imageUrl: string;
}

// Product types
export interface Product {
  id: string;
  restaurantId: string;
  categoryId?: string | null;
  name: string;
  description?: string | null;
  price: string;
  imageUrl?: string | null;
  additionalImages?: string[] | null;
  isAvailable: boolean;
  isVegetarian: boolean;
  isVegan: boolean;
  isGlutenFree: boolean;
  allergens?: string | null;
  tags?: string[] | null;
  preparationTimeMinutes?: number | null;
  displayOrder: number;
  averageRating: string;
  totalReviews: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  categoryId?: string;
  name: string;
  description?: string;
  price: string;
  imageUrl?: string;
  isAvailable?: boolean;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
  allergens?: string;
  tags?: string[];
  preparationTimeMinutes?: number;
  displayOrder?: number;
}

export interface UpdateProductRequest {
  categoryId?: string;
  name?: string;
  description?: string;
  price?: string;
  imageUrl?: string;
  isAvailable?: boolean;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
  allergens?: string;
  tags?: string[];
  preparationTimeMinutes?: number;
  displayOrder?: number;
}

export interface ReorderProductsRequest {
  productIds: string[];
}

export interface ProductImageUploadRequest {
  imageUrls: string[];
}

// Order types
export type OrderStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'PREPARING'
  | 'READY'
  | 'ASSIGNED'
  | 'PICKED_UP'
  | 'DELIVERING'
  | 'DELIVERED'
  | 'CANCELLED';

export interface OrderSummaryResponse {
  id: string;
  orderNumber: string;
  restaurantName: string;
  status: OrderStatus;
  totalAmount: string;
  itemCount: number;
  createdAt: string;
}

export interface OrderItemResponse {
  id: string;
  productId: string;
  productName: string;
  unitPrice: string;
  quantity: number;
  notes?: string | null;
  lineTotal: string;
}

export interface OrderResponse {
  id: string;
  orderNumber: string;
  restaurantId: string;
  restaurantName: string;
  customerId: string;
  status: OrderStatus;
  subtotal: string;
  taxAmount: string;
  deliveryFee: string;
  discountAmount: string;
  totalAmount: string;
  customerNotes?: string | null;
  restaurantNotes?: string | null;
  estimatedPreparationMinutes?: number | null;
  estimatedDeliveryMinutes?: number | null;
  confirmedAt?: string | null;
  preparingAt?: string | null;
  readyAt?: string | null;
  pickedUpAt?: string | null;
  deliveredAt?: string | null;
  cancelledAt?: string | null;
  cancellationReason?: string | null;
  items: OrderItemResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderStatusHistoryResponse {
  id: string;
  fromStatus?: OrderStatus | null;
  toStatus: OrderStatus;
  changedBy: string;
  changeReason?: string | null;
  metadata?: string | null;
  createdAt: string;
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
  reason?: string;
}

export interface CreateOrderRequest {
  restaurantId: string;
  addressId: string;
  idempotencyKey: string;
  customerNotes?: string;
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  productId: string;
  quantity: number;
  notes?: string;
}