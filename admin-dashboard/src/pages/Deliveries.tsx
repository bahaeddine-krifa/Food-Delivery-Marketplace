import { useState, useEffect } from 'react';
import { Search, Eye, User } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { DeliveryStatusBadge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import type { DeliveryOverviewResponse, DeliveryListParams, Column } from '@/types/api';

export function Deliveries() {
  const [deliveries, setDeliveries] = useState<DeliveryOverviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<DeliveryListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  // Modal state
  const [selectedDelivery, setSelectedDelivery] = useState<DeliveryOverviewResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  const fetchDeliveries = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as DeliveryListParams['status'];

      const response = await api.getDeliveries(queryParams);
      setDeliveries(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load deliveries';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDeliveries();
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

  const openDetail = (delivery: DeliveryOverviewResponse) => {
    setSelectedDelivery(delivery);
    setShowDetailModal(true);
  };

  const columns: Column<DeliveryOverviewResponse>[] = [
    {
      key: 'orderNumber',
      header: 'Order #',
      sortable: true,
    },
    {
      key: 'deliveryPersonName',
      header: 'Delivery Person',
      sortable: true,
      render: (row: DeliveryOverviewResponse) => (
        <div className="flex items-center gap-2">
          <User className="w-4 h-4 text-neutral-400" />
          <span>{row.deliveryPersonName}</span>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: DeliveryOverviewResponse) => <DeliveryStatusBadge status={row.status} />,
    },
    {
      key: 'distanceKm',
      header: 'Distance',
      sortable: true,
      render: (row: DeliveryOverviewResponse) => row.distanceKm ? `${Number(row.distanceKm).toFixed(1)} km` : '—',
    },
    {
      key: 'createdAt',
      header: 'Created',
      sortable: true,
      render: (row: DeliveryOverviewResponse) => format(new Date(row.createdAt), 'MMM d, yyyy HH:mm'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: DeliveryOverviewResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  const deliveryStatuses = ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'FAILED'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Deliveries</h1>
          <p className="text-neutral-500 mt-1">Track and manage all deliveries</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search deliveries..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-2 border border-neutral-300 rounded-lg focus-ring bg-white w-full sm:w-48"
            >
              <option value="">All Statuses</option>
              {deliveryStatuses.map((status) => (
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
            data={deliveries}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No deliveries found"
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
        title="Delivery Details"
        size="lg"
      >
        {selectedDelivery && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Delivery Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Order Number</dt>
                    <dd className="font-medium">{selectedDelivery.orderNumber}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Status</dt>
                    <dd><DeliveryStatusBadge status={selectedDelivery.status} /></dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Distance</dt>
                    <dd>{selectedDelivery.distanceKm ? `${Number(selectedDelivery.distanceKm).toFixed(1)} km` : 'Not available'}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Created</dt>
                    <dd>{format(new Date(selectedDelivery.createdAt), 'MMM d, yyyy HH:mm')}</dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Delivery Person</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Name</dt>
                    <dd className="font-medium flex items-center gap-2">
                      <User className="w-4 h-4 text-neutral-400" />
                      {selectedDelivery.deliveryPersonName}
                    </dd>
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