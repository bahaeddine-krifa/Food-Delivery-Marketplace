import axios, { type AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import type { ApiResponse, AuthResponse } from '@/types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

class ApiClient {
  private client: AxiosInstance;
  private isRefreshing = false;
  private failedQueue: Array<{
    resolve: (value: string) => void;
    reject: (reason: Error) => void;
  }> = [];

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor - add access token
    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const accessToken = localStorage.getItem('admin_access_token');
        if (accessToken && config.headers) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
      },
      (error: AxiosError) => Promise.reject(error)
    );

    // Response interceptor - handle token refresh
    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (error.response?.status === 401 && !originalRequest._retry) {
          if (this.isRefreshing) {
            // Queue the request
            return new Promise((resolve, reject) => {
              this.failedQueue.push({ resolve, reject });
            })
              .then((token) => {
                if (originalRequest.headers) {
                  originalRequest.headers.Authorization = `Bearer ${token}`;
                }
                return this.client(originalRequest);
              })
              .catch((err) => Promise.reject(err));
          }

          originalRequest._retry = true;
          this.isRefreshing = true;

          try {
            const refreshToken = localStorage.getItem('admin_refresh_token');
            if (!refreshToken) {
              throw new Error('No refresh token available');
            }

            const response = await axios.post<ApiResponse<AuthResponse>>(
              `${API_BASE_URL}/auth/refresh`,
              { refreshToken },
              { withCredentials: true }
            );

            const { accessToken, refreshToken: newRefreshToken } = response.data.data;
            localStorage.setItem('admin_access_token', accessToken);
            localStorage.setItem('admin_refresh_token', newRefreshToken);

            // Process queued requests
            this.failedQueue.forEach(({ resolve }) => resolve(accessToken));
            this.failedQueue = [];

            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            }

            return this.client(originalRequest);
          } catch (refreshError) {
            // Refresh failed - clear tokens and redirect to login
            this.failedQueue.forEach(({ reject }) => reject(refreshError as Error));
            this.failedQueue = [];
            this.clearAuth();
            window.location.href = '/login';
            return Promise.reject(refreshError);
          } finally {
            this.isRefreshing = false;
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private clearAuth(): void {
    localStorage.removeItem('admin_access_token');
    localStorage.removeItem('admin_refresh_token');
    localStorage.removeItem('admin_user');
  }

  // Auth methods
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await this.client.post<ApiResponse<AuthResponse>>('/auth/login', {
      email,
      password,
    });
    const { accessToken, refreshToken, user } = response.data.data;
    localStorage.setItem('admin_access_token', accessToken);
    localStorage.setItem('admin_refresh_token', refreshToken);
    localStorage.setItem('admin_user', JSON.stringify(user));
    return response.data.data;
  }

  async logout(): Promise<void> {
    const refreshToken = localStorage.getItem('admin_refresh_token');
    if (refreshToken) {
      try {
        await this.client.post('/auth/logout', { refreshToken });
      } catch {
        // Ignore logout errors
      }
    }
    this.clearAuth();
  }

  async getCurrentUser(): Promise<AuthResponse['user']> {
    const response = await this.client.get<ApiResponse<AuthResponse['user']>>('/auth/me');
    return response.data.data;
  }

  // Dashboard
  async getDashboardStats() {
    const response = await this.client.get<ApiResponse<import('@/types/api').DashboardStatsResponse>>('/admin/dashboard');
    return response.data.data;
  }

  // Users
  async getUsers(params: import('@/types/api').AdminListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').UserListResponse>>>('/admin/users', { params });
    return response.data.data;
  }

  async getUserById(userId: string) {
    const response = await this.client.get<ApiResponse<import('@/types/api').UserListResponse>>(`/admin/users/${userId}`);
    return response.data.data;
  }

  async updateUserStatus(userId: string, request: import('@/types/api').UpdateUserStatusRequest) {
    const response = await this.client.put<ApiResponse<import('@/types/api').UserListResponse>>(`/admin/users/${userId}/status`, request);
    return response.data.data;
  }

  async updateUserRole(userId: string, request: import('@/types/api').UpdateUserRoleRequest) {
    const response = await this.client.put<ApiResponse<import('@/types/api').UserListResponse>>(`/admin/users/${userId}/role`, request);
    return response.data.data;
  }

  // Restaurant Applications
  async getRestaurantApplications(params: import('@/types/api').RestaurantApplicationListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').RestaurantApplicationListResponse>>>('/admin/restaurant-applications', { params });
    return response.data.data;
  }

  async approveRestaurantApplication(applicationId: string) {
    const response = await this.client.post<ApiResponse<unknown>>(`/admin/restaurant-applications/${applicationId}/approve`);
    return response.data.data;
  }

  async rejectRestaurantApplication(applicationId: string, reason: string) {
    const response = await this.client.post<ApiResponse<unknown>>(`/admin/restaurant-applications/${applicationId}/reject`, null, { params: { reason } });
    return response.data.data;
  }

  // Restaurants
  async getRestaurants(params: import('@/types/api').RestaurantListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').RestaurantListResponse>>>('/admin/restaurants', { params });
    return response.data.data;
  }

  async updateRestaurantStatus(restaurantId: string, request: import('@/types/api').UpdateRestaurantStatusRequest) {
    const response = await this.client.put<ApiResponse<import('@/types/api').RestaurantListResponse>>(`/admin/restaurants/${restaurantId}/status`, request);
    return response.data.data;
  }

  // Orders
  async getOrders(params: import('@/types/api').OrderListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').OrderOverviewResponse>>>('/admin/orders', { params });
    return response.data.data;
  }

  // Deliveries
  async getDeliveries(params: import('@/types/api').DeliveryListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').DeliveryOverviewResponse>>>('/admin/deliveries', { params });
    return response.data.data;
  }

  // Payments
  async getPayments(params: import('@/types/api').PaymentListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').PaymentOverviewResponse>>>('/admin/payments', { params });
    return response.data.data;
  }

  // Reviews
  async getFlaggedReviews(params: import('@/types/api').ReviewListParams = {}) {
    const response = await this.client.get<ApiResponse<import('@/types/api').PageResponse<import('@/types/api').ReviewResponse>>>('/admin/reviews/flagged', { params });
    return response.data.data;
  }

  // System Config
  async getSystemConfig() {
    const response = await this.client.get<ApiResponse<import('@/types/api').SystemConfigResponse[]>>('/admin/config');
    return response.data.data;
  }

  async updateSystemConfig(key: string, request: import('@/types/api').UpdateConfigRequest) {
    const response = await this.client.put<ApiResponse<import('@/types/api').SystemConfigResponse>>(`/admin/config/${key}`, request);
    return response.data.data;
  }
}

export const api = new ApiClient();