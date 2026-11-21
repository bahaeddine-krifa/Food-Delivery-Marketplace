import { useState, useEffect } from 'react';
import { Search, Eye } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { OrderStatusBadge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import type { OrderOverviewResponse, OrderListParams, Column } from '@/types/api';

export function Orders() {
  const [orders, setOrders] = useState<OrderOverviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<OrderListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  // Modal state
  const [selectedOrder, setSelectedOrder] = useState<OrderOverviewResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as OrderListParams['status'];

      const response = await api.getOrders(queryParams);
      setOrders(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load orders';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [params.page, params.size, params.sort, statusFilter]);

  const handleSort = (key: string) => {
    const [currentSortKey, currentSortOrder] = params.sort?.split(',') || ['createdAt', 'desc'];
    const newOrder = currentSortKey === key && currentSortOrder === 'asc' ? 'desc' : 'asc';
    setParams((prev) => ({ ...prev, sort: `${key},${newOrder}` }));
  };

  const handlePageChange = (page: number) => {
    setParams((prev) => ({ ...prev, page }));
  };

  const handlePageSizeChange = (size: number) => {
    setParams((prev) => ({ ...prev, size, page: 0 }));
  };

  const openDetail = (order: OrderOverviewResponse) => {
    setSelectedOrder(order);
    setShowDetailModal(true);
  };

  const columns: Column<OrderOverviewResponse>[] = [
    {
      key: 'orderNumber',
      header: 'Order #',
      sortable: true,
    },
    {
      key: 'customerName',
      header: 'Customer',
      sortable: true,
    },
    {
      key: 'restaurantName',
      header: 'Restaurant',
      sortable: true,
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: OrderOverviewResponse) => <OrderStatusBadge status={row.status} />,
    },
    {
      key: 'totalAmount',
      header: 'Amount',
      sortable: true,
      render: (row: OrderOverviewResponse) => (
        <div className="font-medium">${Number(row.totalAmount).toFixed(2)}</div>
      ),
    },
    {
      key: 'createdAt',
      header: 'Order Time',
      sortable: true,
      render: (row: OrderOverviewResponse) => format(new Date(row.createdAt), 'MMM d, yyyy HH:mm'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: OrderOverviewResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  const orderStatuses = ['PENDING', 'ACCEPTED', 'PREPARING', 'READY', 'PICKED_UP', 'DELIVERED', 'CANCELLED', 'REFUNDED'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Orders</h1>
          <p className="text-neutral-500 mt-1">View and manage all platform orders</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search orders..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-2 border border-neutral-300 rounded-lg focus-ring bg-white w-full sm:w-56"
            >
              <option value="">All Statuses</option>
              {orderStatuses.map((status) => (
                <option key={status} value={status}>{status.replace('_', ' ')}</option>
              ))}
            </select>
          </form>
        </CardContent>
      </Card>

      {/* Table */}
      <Card>
        <CardContent className="p-0">
          {error && (
            <div className="p-4 bg-red-50 border-b border-red-200 text-red-700">
              {error}
            </div>
          )}
          <Table
            columns={columns}
            data={orders}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No orders found"
            sortBy={params.sort?.split(',')[0]}
            sortOrder={(params.sort?.split(',')[1] as 'asc' | 'desc') || 'desc'}
            onSort={handleSort}
          />
          <Pagination
            currentPage={params.page ?? 0}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={params.size ?? 20}
            onPageChange={handlePageChange}
            onPageSizeChange={handlePageSizeChange}
          />
        </CardContent>
      </Card>

      {/* Detail Modal */}
      <Modal
        isOpen={showDetailModal}
        onClose={() => setShowDetailModal(false)}
        title="Order Details"
        size="lg"
      >
        {selectedOrder && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Order Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Order Number</dt>
                    <dd className="font-medium">{selectedOrder.orderNumber}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Status</dt>
                    <dd><OrderStatusBadge status={selectedOrder.status} /></dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Total Amount</dt>
                    <dd className="font-medium text-lg">${Number(selectedOrder.totalAmount).toFixed(2)}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Order Time</dt>
                    <dd>{format(new Date(selectedOrder.createdAt), 'MMM d, yyyy HH:mm')}</dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Customer</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Name</dt>
                    <dd className="font-medium">{selectedOrder.customerName}</dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Restaurant</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Name</dt>
                    <dd className="font-medium">{selectedOrder.restaurantName}</dd>
                  </div>
                </dl>
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button variant="ghost" onClick={() => setShowDetailModal(false)}>Close</Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}