import { useState, useEffect } from 'react';
import { Search, Eye, CheckCircle, XCircle, User, Phone } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { StatusBadge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import { ConfirmDialog } from '@/components/ui/Modal';
import { Textarea } from '@/components/ui/Input';
import type { RestaurantApplicationListResponse, RestaurantApplicationListParams, Column } from '@/types/api';

export function RestaurantApplications() {
  const [applications, setApplications] = useState<RestaurantApplicationListResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<RestaurantApplicationListParams>({
    page: 0,
    size: 20,
    sort: 'submittedAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  // Modal states
  const [selectedApplication, setSelectedApplication] = useState<RestaurantApplicationListResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [rejectReason, setRejectReason] = useState('');

  const fetchApplications = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as RestaurantApplicationListParams['status'];

      const response = await api.getRestaurantApplications(queryParams);
      setApplications(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load applications';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApplications();
  }, [params.page, params.size, params.sort, statusFilter]);

  const handleSort = (key: string) => {
    const [currentSortKey, currentSortOrder] = params.sort?.split(',') || ['submittedAt', 'desc'];
    const newOrder = currentSortKey === key && currentSortOrder === 'asc' ? 'desc' : 'asc';
    setParams((prev) => ({ ...prev, sort: `${key},${newOrder}` }));
  };

  const handlePageChange = (page: number) => {
    setParams((prev) => ({ ...prev, page }));
  };

  const handlePageSizeChange = (size: number) => {
    setParams((prev) => ({ ...prev, size, page: 0 }));
  };

  const openDetail = (app: RestaurantApplicationListResponse) => {
    setSelectedApplication(app);
    setShowDetailModal(true);
  };

  const openApprove = (app: RestaurantApplicationListResponse) => {
    setSelectedApplication(app);
    setShowApproveModal(true);
  };

  const openReject = (app: RestaurantApplicationListResponse) => {
    setSelectedApplication(app);
    setRejectReason('');
    setShowRejectModal(true);
  };

  const handleApprove = async () => {
    if (!selectedApplication) return;
    setSaving(true);
    try {
      await api.approveRestaurantApplication(selectedApplication.id);
      setShowApproveModal(false);
      fetchApplications();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to approve application';
      alert(message);
    } finally {
      setSaving(false);
    }
  };

  const handleReject = async () => {
    if (!selectedApplication || !rejectReason.trim()) return;
    setSaving(true);
    try {
      await api.rejectRestaurantApplication(selectedApplication.id, rejectReason);
      setShowRejectModal(false);
      fetchApplications();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to reject application';
      alert(message);
    } finally {
      setSaving(false);
    }
  };

  const columns: Column<RestaurantApplicationListResponse>[] = [
    {
      key: 'restaurantName',
      header: 'Restaurant',
      sortable: true,
    },
    {
      key: 'owner',
      header: 'Owner',
      render: (row: RestaurantApplicationListResponse) => (
        <div>
          <p className="font-medium">{row.restaurantName}</p>
          <p className="text-sm text-neutral-500 flex items-center gap-1">
            <User className="w-3 h-3" /> {row.ownerEmail}
          </p>
          <p className="text-sm text-neutral-500 flex items-center gap-1">
            <Phone className="w-3 h-3" /> {row.ownerPhone}
          </p>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: RestaurantApplicationListResponse) => <StatusBadge status={row.status} />,
    },
    {
      key: 'submittedAt',
      header: 'Submitted',
      sortable: true,
      render: (row: RestaurantApplicationListResponse) => format(new Date(row.submittedAt), 'MMM d, yyyy HH:mm'),
    },
    {
      key: 'reviewedAt',
      header: 'Reviewed',
      render: (row: RestaurantApplicationListResponse) => row.reviewedAt ? format(new Date(row.reviewedAt), 'MMM d, yyyy HH:mm') : '—',
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: RestaurantApplicationListResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
          {row.status === 'PENDING' || row.status === 'UNDER_REVIEW' ? (
            <>
              <Button variant="ghost" size="sm" onClick={() => openApprove(row)} aria-label="Approve" className="text-green-600 hover:bg-green-50">
                <CheckCircle className="w-4 h-4" />
              </Button>
              <Button variant="ghost" size="sm" onClick={() => openReject(row)} aria-label="Reject" className="text-red-600 hover:bg-red-50">
                <XCircle className="w-4 h-4" />
              </Button>
            </>
          ) : (
            <span className="text-sm text-neutral-500">Processed</span>
          )}
        </div>
      ),
    },
  ];

  const applicationStatuses = ['PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Restaurant Applications</h1>
          <p className="text-neutral-500 mt-1">Review and manage restaurant registration applications</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search applications..."
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
              {applicationStatuses.map((status) => (
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
            data={applications}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No applications found"
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
        title="Application Details"
        size="lg"
      >
        {selectedApplication && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Restaurant Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Restaurant Name</dt>
                    <dd className="font-medium">{selectedApplication.restaurantName}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Owner Email</dt>
                    <dd>{selectedApplication.ownerEmail}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Owner Phone</dt>
                    <dd>{selectedApplication.ownerPhone}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Status</dt>
                    <dd><StatusBadge status={selectedApplication.status} /></dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Timeline</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Submitted</dt>
                    <dd className="font-medium">{format(new Date(selectedApplication.submittedAt), 'MMM d, yyyy HH:mm')}</dd>
                  </div>
                  {selectedApplication.reviewedAt && (
                    <div>
                      <dt className="text-neutral-500">Reviewed</dt>
                      <dd className="font-medium">{format(new Date(selectedApplication.reviewedAt), 'MMM d, yyyy HH:mm')}</dd>
                    </div>
                  )}
                  {selectedApplication.reviewedBy && (
                    <div>
                      <dt className="text-neutral-500">Reviewed By</dt>
                      <dd>{selectedApplication.reviewedBy}</dd>
                    </div>
                  )}
                </dl>
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-4 border-t">
              {selectedApplication.status === 'PENDING' || selectedApplication.status === 'UNDER_REVIEW' ? (
                <>
                  <Button variant="ghost" onClick={() => { setShowDetailModal(false); openApprove(selectedApplication); }}>
                    <CheckCircle className="w-4 h-4 mr-2" /> Approve
                  </Button>
                  <Button variant="ghost" onClick={() => { setShowDetailModal(false); openReject(selectedApplication); }} className="text-red-600 hover:bg-red-50">
                    <XCircle className="w-4 h-4 mr-2" /> Reject
                  </Button>
                </>
              ) : (
                <Button variant="ghost" onClick={() => setShowDetailModal(false)}>Close</Button>
              )}
            </div>
          </div>
        )}
      </Modal>

      {/* Approve Modal */}
      <ConfirmDialog
        isOpen={showApproveModal}
        onClose={() => setShowApproveModal(false)}
        onConfirm={handleApprove}
        title="Approve Application"
        message={`Are you sure you want to approve "${selectedApplication?.restaurantName}"? This will create the restaurant and notify the owner.`}
        confirmText="Approve"
        variant="primary"
        loading={saving}
      />

      {/* Reject Modal */}
      <Modal
        isOpen={showRejectModal}
        onClose={() => setShowRejectModal(false)}
        title="Reject Application"
        size="md"
      >
        {selectedApplication && (
          <div className="space-y-4">
            <p className="text-neutral-600">Reject application for <strong>{selectedApplication.restaurantName}</strong></p>
            <Textarea
              label="Rejection Reason (required)"
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              placeholder="Provide a reason for rejection..."
              rows={4}
              required
            />
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="ghost" onClick={() => setShowRejectModal(false)} disabled={saving}>
                Cancel
              </Button>
              <Button variant="danger" onClick={handleReject} loading={saving} disabled={!rejectReason.trim()}>
                Reject
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}

