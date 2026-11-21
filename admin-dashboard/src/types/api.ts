// Admin API Types - Must match backend AdminDtos.kt exactly

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: {
    code: string;
    message: string;
    details?: Record<string, string[]>;
  };
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export type UserRole = 'CUSTOMER' | 'RESTAURANT_OWNER' | 'DELIVERY_PERSON' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION';
export type ApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'UNDER_REVIEW';
export type RestaurantStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' | 'CLOSED';
export type OrderStatus = 'PENDING' | 'ACCEPTED' | 'PREPARING' | 'READY' | 'PICKED_UP' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
export type DeliveryStatus = 'ASSIGNED' | 'PICKED_UP' | 'IN_TRANSIT' | 'DELIVERED' | 'FAILED';
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED' | 'PARTIALLY_REFUNDED';
export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'PAYPAL' | 'APPLE_PAY' | 'GOOGLE_PAY' | 'CASH';

export interface Column<T> {
  key: keyof T | string;
  header: string;
  render?: (row: T, index: number) => React.ReactNode;
  sortable?: boolean;
  className?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  status: UserStatus;
  emailVerified: boolean;
  phoneVerified: boolean;
}

export interface DashboardStatsResponse {
  totalUsers: number;
  totalCustomers: number;
  totalDeliveryPersons: number;
  totalRestaurantOwners: number;
  totalRestaurants: number;
  pendingRestaurantApplications: number;
  totalOrders: number;
  ordersToday: number;
  revenueToday: string;
  revenueThisMonth: string;
  activeDeliveries: number;
  pendingPayments: number;
  flaggedReviews: number;
}

export interface UserListResponse {
  id: string;
  email: string;
  phone: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  status: UserStatus;
  emailVerified: boolean;
  phoneVerified: boolean;
  createdAt: string;
  lastLoginAt: string | null;
}

export interface UpdateUserStatusRequest {
  status: UserStatus;
  reason?: string;
}

export interface UpdateUserRoleRequest {
  role: UserRole;
}

export interface RestaurantApplicationListResponse {
  id: string;
  restaurantName: string;
  ownerEmail: string;
  ownerPhone: string;
  status: ApplicationStatus;
  submittedAt: string;
  reviewedAt: string | null;
  reviewedBy: string | null;
}

export interface RestaurantListResponse {
  id: string;
  name: string;
  ownerName: string;
  ownerEmail: string;
  status: RestaurantStatus;
  rating: number;
  reviewCount: number;
  isOpen: boolean;
  createdAt: string;
}

export interface UpdateRestaurantStatusRequest {
  status: RestaurantStatus;
  reason?: string;
}

export interface OrderOverviewResponse {
  id: string;
  orderNumber: string;
  customerName: string;
  restaurantName: string;
  status: OrderStatus;
  totalAmount: string;
  createdAt: string;
}

export interface DeliveryOverviewResponse {
  id: string;
  orderNumber: string;
  deliveryPersonName: string;
  status: DeliveryStatus;
  distanceKm: string | null;
  createdAt: string;
}

export interface PaymentOverviewResponse {
  id: string;
  orderNumber: string;
  customerName: string;
  amount: string;
  method: PaymentMethod;
  status: PaymentStatus;
  createdAt: string;
}

export interface ReviewResponse {
  id: string;
  orderId: string;
  customerName: string;
  restaurantName: string;
  rating: number;
  comment: string | null;
  flagged: boolean;
  flagReason: string | null;
  createdAt: string;
}

export interface SystemConfigResponse {
  key: string;
  value: string;
  description: string;
  isEditable: boolean;
}

export interface UpdateConfigRequest {
  value: string;
}

// Request types for pagination
export interface AdminListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: string;
  role?: UserRole;
}

export interface RestaurantApplicationListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: ApplicationStatus;
}

export interface RestaurantListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: RestaurantStatus;
}

export interface OrderListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: OrderStatus;
}

export interface DeliveryListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: DeliveryStatus;
}

export interface PaymentListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: PaymentStatus;
}

export interface ReviewListParams {
  page?: number;
  size?: number;
  sort?: string;
}