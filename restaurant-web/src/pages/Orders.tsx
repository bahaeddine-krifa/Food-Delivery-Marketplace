import { useState, useEffect, useCallback } from 'react';
import { format } from 'date-fns';
import { RefreshCw } from 'lucide-react';
import { useRestaurants } from '@/hooks/useRestaurant';
import { restaurantApi } from '@/lib/restaurantApi';
import { Card, CardContent } from '@/components/ui/Card';
import { Table, type Column, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Select } from '@/components/ui/Input';
import { OrderStatusBadge } from '@/components/ui/Badge';
import { Modal, ConfirmDialog } from '@/components/ui/Modal';
import type { OrderSummaryResponse, OrderResponse, OrderStatus } from '@/types/api';

const STATUS_OPTIONS: { value: string; label: string }[] = [
  { value: '', label: 'All Statuses' },
  { value: 'PENDING', label: 'Pending' },
  { value: 'ACCEPTED', label: 'Accepted' },
  { value: 'PREPARING', label: 'Preparing' },
  { value: 'READY', label: 'Ready' },
  { value: 'ASSIGNED', label: 'Assigned' },
  { value: 'PICKED_UP', label: 'Picked Up' },
  { value: 'DELIVERING', label: 'Delivering' },
  { value: 'DELIVERED', label: 'Delivered' },
  { value: 'CANCELLED', label: 'Cancelled' },
  { value: 'REJECTED', label: 'Rejected' },
];

export function Orders() {
  const { restaurants } = useRestaurants();
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<string>('');
  const [orders, setOrders] = useState<OrderSummaryResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pagination, setPagination] = useState({ page: 0, size: 20, totalPages: 1, totalElements: 0 });
  const [filters, setFilters] = useState({ status: '', search: '' });
  const [selectedOrder, setSelectedOrder] = useState<OrderResponse | null>(null);
  const [orderDetailLoading, setOrderDetailLoading] = useState(false);
  const [confirmDialog, setConfirmDialog] = useState<{
    isOpen: boolean;
    action: 'accept' | 'reject' | 'cancel';
    orderId: string;
    orderNumber: string;
  } | null>(null);

  // Auto-select first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurantId) {
      setSelectedRestaurantId(restaurants[0].id);
    }
  }, [restaurants, selectedRestaurantId]);

  const fetchOrders = useCallback(async () => {
    if (!selectedRestaurantId) return;
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: pagination.page,
        size: pagination.size,
        status: filters.status as OrderStatus | undefined,
      };
      const response = await restaurantApi.listOrders(selectedRestaurantId, params);
      setOrders(response.content);
      setPagination(prev => ({
        ...prev,
        totalPages: response.totalPages,
        totalElements: response.totalElements,
      }));
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load orders';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [selectedRestaurantId, pagination.page, pagination.size, filters.status]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handlePageChange = (page: number) => {
    setPagination(prev => ({ ...prev, page }));
  };

  const handlePageSizeChange = (size: number) => {
    setPagination(prev => ({ ...prev, size, page: 0 }));
  };

  const handleStatusFilterChange = (status: string) => {
    setFilters(prev => ({ ...prev, status }));
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const handleSearchChange = (search: string) => {
    // Debounced search would be better in production
    setFilters(prev => ({ ...prev, search }));
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const openOrderDetail = async (orderId: string) => {
    if (!selectedRestaurantId) return;
    setOrderDetailLoading(true);
    try {
      const order = await restaurantApi.getOrder(selectedRestaurantId, orderId);
      setSelectedOrder(order);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load order details';
      setError(message);
    } finally {
      setOrderDetailLoading(false);
    }
  };

  const handleActionClick = (action: 'accept' | 'reject' | 'cancel', order: OrderSummaryResponse) => {
    const validTransitions: Record<string, OrderStatus[]> = {
      accept: ['PENDING'],
      reject: ['PENDING'],
      cancel: ['PENDING', 'ACCEPTED'],
    };

    if (!validTransitions[action].includes(order.status)) {
      setError(`Cannot ${action} order in ${order.status} status`);
      return;
    }

    setConfirmDialog({
      isOpen: true,
      action,
      orderId: order.id,
      orderNumber: order.orderNumber,
    });
  };

  const handleConfirmAction = async () => {
    if (!confirmDialog || !selectedRestaurantId) return;

    try {
      const { action, orderId } = confirmDialog;

      if (action === 'accept') {
        await restaurantApi.acceptOrder(selectedRestaurantId, orderId);
      } else if (action === 'reject') {
        await restaurantApi.rejectOrder(selectedRestaurantId, orderId);
      } else if (action === 'cancel') {
        await restaurantApi.updateOrderStatus(selectedRestaurantId, orderId, { status: 'CANCELLED' });
      }

      setConfirmDialog(null);
      fetchOrders();
      if (selectedOrder?.id === orderId) {
        openOrderDetail(orderId);
      }
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : `Failed to ${confirmDialog.action} order`;
      setError(message);
    }
  };

  const getAllowedActions = (status: OrderStatus): Array<'accept' | 'reject' | 'cancel'> => {
    switch (status) {
      case 'PENDING':
        return ['accept', 'reject'];
      case 'ACCEPTED':
        return ['cancel'];
      default:
        return [];
    }
  };

  const orderColumns: Column<OrderSummaryResponse>[] = [
    { key: 'orderNumber', header: 'Order #' },
    { key: 'status', header: 'Status', render: (row) => <OrderStatusBadge status={row.status} /> },
    { key: 'totalAmount', header: 'Amount', render: (row) => `$${row.totalAmount}` },
    { key: 'itemCount', header: 'Items' },
    { key: 'createdAt', header: 'Time', render: (row) => format(new Date(row.createdAt), 'MMM d, HH:mm') },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-1">
          {getAllowedActions(row.status).map((action) => (
            <Button
              key={action}
              variant={action === 'reject' || action === 'cancel' ? 'danger' : 'primary'}
              size="sm"
              onClick={() => handleActionClick(action, row)}
              disabled={loading}
            >
              {action.charAt(0).toUpperCase() + action.slice(1)}
            </Button>
          ))}
          <Button
            variant="ghost"
            size="sm"
            onClick={() => openOrderDetail(row.id)}
            disabled={orderDetailLoading}
          >
            View
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Orders</h1>
          <p className="text-neutral-500 mt-1">Manage and track all restaurant orders</p>
        </div>
        <div className="flex items-center gap-4 flex-wrap">
          <select
            value={selectedRestaurantId}
            onChange={(e) => setSelectedRestaurantId(e.target.value)}
            className="px-4 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white"
            disabled={restaurants.length <= 1}
          >
            {restaurants.map((restaurant) => (
              <option key={restaurant.id} value={restaurant.id}>
                {restaurant.name}
              </option>
            ))}
          </select>
          <Button variant="secondary" onClick={fetchOrders} loading={loading} size="sm">
            <RefreshCw className="w-4 h-4 mr-1" /> Refresh
          </Button>
        </div>
      </div>

      {/* Filters */}
      <Card className="p-4">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 max-w-xs">
            <Input
              placeholder="Search orders..."
              value={filters.search}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="w-full"
            />
          </div>
          <Select
            value={filters.status}
            onChange={(e) => handleStatusFilterChange(e.target.value)}
            options={STATUS_OPTIONS}
            className="w-full max-w-xs"
          />
        </div>
      </Card>

      {/* Orders Table */}
      <Card>
        <CardContent className="p-0">
          {error && (
            <div className="p-4 bg-red-50 border-b border-red-200 text-red-700">
              {error}
            </div>
          )}
          <Table
            columns={orderColumns}
            data={orders}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No orders found"
          />
          <Pagination
            currentPage={pagination.page}
            totalPages={pagination.totalPages}
            onPageChange={handlePageChange}
            showPageSize
            pageSize={pagination.size}
            onPageSizeChange={handlePageSizeChange}
            totalElements={pagination.totalElements}
          />
        </CardContent>
      </Card>

      {/* Order Detail Modal */}
      <Modal
        isOpen={!!selectedOrder}
        onClose={() => setSelectedOrder(null)}
        title={`Order ${selectedOrder?.orderNumber}`}
        size="xl"
      >
        {orderDetailLoading ? (
          <div className="flex justify-center py-12">
            <div className="w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full animate-spin" />
          </div>
        ) : selectedOrder ? (
          <OrderDetailView order={selectedOrder} onClose={() => setSelectedOrder(null)} />
        ) : null}
      </Modal>

      {/* Confirm Dialog */}
      {confirmDialog && (
        <ConfirmDialog
          isOpen={confirmDialog.isOpen}
          onClose={() => setConfirmDialog(null)}
          onConfirm={handleConfirmAction}
          title={`${confirmDialog.action.charAt(0).toUpperCase() + confirmDialog.action.slice(1)} Order?`}
          message={`Are you sure you want to ${confirmDialog.action} order ${confirmDialog.orderNumber}?`}
          confirmText={confirmDialog.action.charAt(0).toUpperCase() + confirmDialog.action.slice(1)}
          variant={confirmDialog.action === 'reject' || confirmDialog.action === 'cancel' ? 'danger' : 'primary'}
        />
      )}
    </div>
  );
}

function OrderDetailView({ order, onClose }: { order: OrderResponse; onClose: () => void }) {
  return (
    <div className="space-y-6">
      {/* Order Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h3 className="text-lg font-semibold text-neutral-900">Order #{order.orderNumber}</h3>
          <p className="text-sm text-neutral-500">Placed {format(new Date(order.createdAt), 'MMM d, yyyy HH:mm')}</p>
        </div>
        <div className="flex items-center gap-4">
          <OrderStatusBadge status={order.status} />
          {order.estimatedPreparationMinutes && (
            <span className="text-sm text-neutral-500">Est. prep: {order.estimatedPreparationMinutes}min</span>
          )}
        </div>
      </div>

      {/* Customer Info */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="p-4 bg-neutral-50 rounded-lg">
          <h4 className="text-sm font-medium text-neutral-700 mb-2">Customer</h4>
          <p className="text-sm text-neutral-900">{order.customerNotes || 'Walk-in customer'}</p>
        </div>
        <div className="p-4 bg-neutral-50 rounded-lg">
          <h4 className="text-sm font-medium text-neutral-700 mb-2">Restaurant Notes</h4>
          <p className="text-sm text-neutral-900">{order.restaurantNotes || 'None'}</p>
        </div>
      </div>

      {/* Order Items */}
      <div>
        <h4 className="text-sm font-medium text-neutral-700 mb-3">Order Items</h4>
        <div className="space-y-2">
          {order.items.map((item) => (
            <div key={item.id} className="flex items-center justify-between p-3 bg-neutral-50 rounded-lg">
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-neutral-900 truncate">{item.productName}</p>
                {item.notes && <p className="text-xs text-neutral-500">{item.notes}</p>}
              </div>
              <div className="flex items-center gap-4 text-sm text-neutral-700">
                <span>Qty: {item.quantity}</span>
                <span>${item.unitPrice}</span>
                <span className="font-medium">${item.lineTotal}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Order Totals */}
      <div className="bg-neutral-50 rounded-lg p-4 space-y-2">
        <div className="flex justify-between text-sm">
          <span className="text-neutral-600">Subtotal</span>
          <span className="text-neutral-900">${order.subtotal}</span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-neutral-600">Tax</span>
          <span className="text-neutral-900">${order.taxAmount}</span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-neutral-600">Delivery Fee</span>
          <span className="text-neutral-900">${order.deliveryFee}</span>
        </div>
        {parseFloat(order.discountAmount) > 0 && (
          <div className="flex justify-between text-sm text-green-600">
            <span>Discount</span>
            <span>-${order.discountAmount}</span>
          </div>
        )}
        <div className="flex justify-between text-lg font-semibold border-t border-neutral-200 pt-2">
          <span>Total</span>
          <span>${order.totalAmount}</span>
        </div>
      </div>

      {/* Status History */}
      {order.createdAt && (
        <div>
          <h4 className="text-sm font-medium text-neutral-700 mb-3">Status History</h4>
          <div className="space-y-2">
            <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
              <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                <svg className="w-4 h-4 text-primary-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-medium text-neutral-900">Order Placed</p>
                <p className="text-xs text-neutral-500">{format(new Date(order.createdAt), 'MMM d, yyyy HH:mm')}</p>
              </div>
            </div>
            {order.confirmedAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Accepted</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.confirmedAt), 'MMM d, yyyy HH:mm')}</p>
                </div>
              </div>
            )}
            {order.preparingAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-orange-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-orange-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Preparing</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.preparingAt), 'MMM d, yyyy HH:mm')}</p>
                </div>
              </div>
            )}
            {order.readyAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-purple-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Ready for Pickup</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.readyAt), 'MMM d, yyyy HH:mm')}</p>
                </div>
              </div>
            )}
            {order.pickedUpAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-teal-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-teal-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Picked Up</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.pickedUpAt), 'MMM d, yyyy HH:mm')}</p>
                </div>
              </div>
            )}
            {order.deliveredAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Delivered</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.deliveredAt), 'MMM d, yyyy HH:mm')}</p>
                </div>
              </div>
            )}
            {order.cancelledAt && (
              <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                <div className="w-8 h-8 bg-red-100 rounded-full flex items-center justify-center">
                  <svg className="w-4 h-4 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-neutral-900">Cancelled</p>
                  <p className="text-xs text-neutral-500">{format(new Date(order.cancelledAt), 'MMM d, yyyy HH:mm')}</p>
                  {order.cancellationReason && (
                    <p className="text-xs text-red-600">{order.cancellationReason}</p>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200">
        <Button variant="secondary" onClick={onClose}>Close</Button>
      </div>
    </div>
  );
}