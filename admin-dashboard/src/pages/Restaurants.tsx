import { useState, useEffect } from 'react';
import { Search, Eye, MoreVertical, User, Star, Mail } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { StatusBadge, Badge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input, Textarea } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import type { RestaurantListResponse, RestaurantListParams, UpdateRestaurantStatusRequest, Column } from '@/types/api';

export function Restaurants() {
  const [restaurants, setRestaurants] = useState<RestaurantListResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<RestaurantListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  // Modal states
  const [selectedRestaurant, setSelectedRestaurant] = useState<RestaurantListResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [newStatus, setNewStatus] = useState<string>('');
  const [statusReason, setStatusReason] = useState('');

  const fetchRestaurants = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as RestaurantListParams['status'];

      const response = await api.getRestaurants(queryParams);
      setRestaurants(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load restaurants';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRestaurants();
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

  const openDetail = (restaurant: RestaurantListResponse) => {
    setSelectedRestaurant(restaurant);
    setShowDetailModal(true);
  };

  const openStatusModal = (restaurant: RestaurantListResponse) => {
    setSelectedRestaurant(restaurant);
    setNewStatus(restaurant.status);
    setStatusReason('');
    setShowStatusModal(true);
  };

  const handleStatusSubmit = async () => {
    if (!selectedRestaurant || !newStatus) return;
    setSaving(true);
    try {
      const request: UpdateRestaurantStatusRequest = {
        status: newStatus as RestaurantListResponse['status'],
        reason: statusReason || undefined,
      };
      await api.updateRestaurantStatus(selectedRestaurant.id, request);
      setShowStatusModal(false);
      fetchRestaurants();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to update status';
      alert(message);
    } finally {
      setSaving(false);
    }
  };

  const columns: Column<RestaurantListResponse>[] = [
    {
      key: 'name',
      header: 'Restaurant',
      sortable: true,
    },
    {
      key: 'owner',
      header: 'Owner',
      render: (row: RestaurantListResponse) => (
        <div>
          <p className="font-medium">{row.name}</p>
          <p className="text-sm text-neutral-500 flex items-center gap-1">
            <User className="w-3 h-3" /> {row.ownerName}
          </p>
          <p className="text-sm text-neutral-500 flex items-center gap-1">
            <Mail className="w-3 h-3" /> {row.ownerEmail}
          </p>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: RestaurantListResponse) => <StatusBadge status={row.status} />,
    },
    {
      key: 'rating',
      header: 'Rating',
      sortable: true,
      render: (row: RestaurantListResponse) => (
        <div className="flex items-center gap-1">
          <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
          <span>{row.rating.toFixed(1)}</span>
          <span className="text-neutral-500">({row.reviewCount})</span>
        </div>
      ),
    },
    {
      key: 'isOpen',
      header: 'Open Now',
      render: (row: RestaurantListResponse) => (
        <Badge variant={row.isOpen ? 'success' : 'neutral'}>
          {row.isOpen ? 'Open' : 'Closed'}
        </Badge>
      ),
    },
    {
      key: 'createdAt',
      header: 'Created',
      sortable: true,
      render: (row: RestaurantListResponse) => format(new Date(row.createdAt), 'MMM d, yyyy'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: RestaurantListResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
          <Button variant="ghost" size="sm" onClick={() => openStatusModal(row)} aria-label="Change status">
            <MoreVertical className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  const restaurantStatuses = ['PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED', 'CLOSED'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Restaurants</h1>
          <p className="text-neutral-500 mt-1">Manage restaurants on the platform</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search restaurants..."
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
              {restaurantStatuses.map((status) => (
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
            data={restaurants}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No restaurants found"
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
        title="Restaurant Details"
        size="lg"
      >
        {selectedRestaurant && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Basic Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Name</dt>
                    <dd className="font-medium">{selectedRestaurant.name}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Owner</dt>
                    <dd>{selectedRestaurant.ownerName}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Owner Email</dt>
                    <dd>{selectedRestaurant.ownerEmail}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Status</dt>
                    <dd><StatusBadge status={selectedRestaurant.status} /></dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Metrics</h4>
                <dl className="space-y-3 text-sm">
                  <div className="flex items-center gap-2">
                    <dt className="text-neutral-500">Rating</dt>
                    <dd className="flex items-center gap-1">
                      <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                      <span className="font-medium">{selectedRestaurant.rating.toFixed(1)}</span>
                      <span className="text-neutral-500">({selectedRestaurant.reviewCount} reviews)</span>
                    </dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Currently</dt>
                    <dd>
                      <Badge variant={selectedRestaurant.isOpen ? 'success' : 'neutral'}>
                        {selectedRestaurant.isOpen ? 'Open' : 'Closed'}
                      </Badge>
                    </dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Joined</dt>
                    <dd className="font-medium">{format(new Date(selectedRestaurant.createdAt), 'MMM d, yyyy')}</dd>
                  </div>
                </dl>
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button variant="ghost" onClick={() => { setShowDetailModal(false); openStatusModal(selectedRestaurant); }}>
                <MoreVertical className="w-4 h-4 mr-2" /> Change Status
              </Button>
              <Button variant="ghost" onClick={() => setShowDetailModal(false)}>Close</Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Status Modal */}
      <Modal
        isOpen={showStatusModal}
        onClose={() => setShowStatusModal(false)}
        title="Update Restaurant Status"
        size="sm"
      >
        {selectedRestaurant && (
          <div className="space-y-4">
            <p className="text-neutral-600">Update status for <strong>{selectedRestaurant.name}</strong></p>
            <select
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
              className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus-ring"
            >
              {restaurantStatuses.map((status) => (
                <option key={status} value={status}>{status.replace('_', ' ')}</option>
              ))}
            </select>
            <Textarea
              label="Reason (optional)"
              value={statusReason}
              onChange={(e) => setStatusReason(e.target.value)}
              placeholder="Reason for status change..."
              rows={3}
            />
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="ghost" onClick={() => setShowStatusModal(false)} disabled={saving}>
                Cancel
              </Button>
              <Button variant={newStatus === 'SUSPENDED' || newStatus === 'REJECTED' || newStatus === 'CLOSED' ? 'danger' : 'primary'} onClick={handleStatusSubmit} loading={saving}>
                Update Status
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}

