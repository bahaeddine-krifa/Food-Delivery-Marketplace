import { useState, useEffect } from 'react';
import { Search, Eye, Flag, CheckCircle, XCircle, Star, User } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import { ConfirmDialog } from '@/components/ui/Modal';
import type { ReviewResponse, ReviewListParams, Column } from '@/types/api';

export function Reviews() {
  const [reviews, setReviews] = useState<ReviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<ReviewListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  // Modal states
  const [selectedReview, setSelectedReview] = useState<ReviewResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showResolveModal, setShowResolveModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [resolveAction, setResolveAction] = useState<'dismiss' | 'remove'>('dismiss');

  const fetchReviews = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.getFlaggedReviews(params);
      setReviews(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load flagged reviews';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReviews();
  }, [params.page, params.size, params.sort]);

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

  const openDetail = (review: ReviewResponse) => {
    setSelectedReview(review);
    setShowDetailModal(true);
  };

  const openResolve = (review: ReviewResponse, action: 'dismiss' | 'remove') => {
    setSelectedReview(review);
    setResolveAction(action);
    setShowResolveModal(true);
  };

  const handleResolve = async () => {
    if (!selectedReview) return;
    setSaving(true);
    try {
      // In a real implementation, there would be an API endpoint to resolve flagged reviews
      // For now, we'll just simulate the action
      await new Promise(resolve => setTimeout(resolve, 500));
      setShowResolveModal(false);
      fetchReviews();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to resolve review';
      alert(message);
    } finally {
      setSaving(false);
    }
  };

  const columns: Column<ReviewResponse>[] = [
    {
      key: 'id',
      header: 'Review ID',
      sortable: true,
      render: (row: ReviewResponse) => <span className="font-mono text-sm">{row.id.slice(0, 8)}...</span>,
    },
    {
      key: 'stars',
      header: 'Rating',
      render: (row: ReviewResponse) => (
        <div className="flex items-center gap-1">
          {[...Array(5)].map((_, i) => (
            <Star
              key={i}
              className={`w-4 h-4 ${i < row.rating ? 'text-yellow-500 fill-yellow-500' : 'text-neutral-300'}`}
            />
          ))}
          <span className="text-sm font-medium ml-1">{row.rating}/5</span>
        </div>
      ),
    },
    {
      key: 'customerName',
      header: 'Customer',
      sortable: true,
      render: (row: ReviewResponse) => (
        <div className="flex items-center gap-2">
          <User className="w-4 h-4 text-neutral-400" />
          <span>{row.customerName}</span>
        </div>
      ),
    },
    {
      key: 'restaurantName',
      header: 'Restaurant',
      sortable: true,
    },
    {
      key: 'comment',
      header: 'Comment',
      render: (row: ReviewResponse) => (
        <p className="max-w-xs truncate text-neutral-600">{row.comment || 'No comment'}</p>
      ),
    },
    {
      key: 'flagged',
      header: 'Flagged',
      render: (row: ReviewResponse) => (
        <Badge variant={row.flagged ? 'danger' : 'success'}>
          {row.flagged ? (
            <>
              <Flag className="w-3 h-3 mr-1" /> Flagged
            </>
          ) : (
            'Clean'
          )}
        </Badge>
      ),
    },
    {
      key: 'flagReason',
      header: 'Flag Reason',
      render: (row: ReviewResponse) => row.flagReason ? (
        <span className="text-sm text-red-600">{row.flagReason}</span>
      ) : (
        <span className="text-neutral-400">—</span>
      ),
    },
    {
      key: 'createdAt',
      header: 'Created',
      sortable: true,
      render: (row) => format(new Date(row.createdAt), 'MMM d, yyyy HH:mm'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
          {row.flagged && (
            <div className="flex items-center gap-1">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => openResolve(row, 'dismiss')}
                aria-label="Dismiss flag"
                className="text-green-600 hover:bg-green-50"
              >
                <CheckCircle className="w-4 h-4" />
              </Button>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => openResolve(row, 'remove')}
                aria-label="Remove review"
                className="text-red-600 hover:bg-red-50"
              >
                <XCircle className="w-4 h-4" />
              </Button>
            </div>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Flagged Reviews</h1>
          <p className="text-neutral-500 mt-1">Review and moderate flagged customer reviews</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search reviews..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
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
            data={reviews}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No flagged reviews found"
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
        title="Review Details"
        size="lg"
      >
        {selectedReview && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Review Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Rating</dt>
                    <dd className="flex items-center gap-1">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-5 h-5 ${i < selectedReview.rating ? 'text-yellow-500 fill-yellow-500' : 'text-neutral-300'}`}
                        />
                      ))}
                      <span className="font-medium ml-2">{selectedReview.rating}/5</span>
                    </dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Customer</dt>
                    <dd className="font-medium">{selectedReview.customerName}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Restaurant</dt>
                    <dd>{selectedReview.restaurantName}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Order</dt>
                    <dd className="font-mono text-sm">{selectedReview.orderId.slice(0, 8)}...</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Flagged</dt>
                    <dd>
                      <Badge variant={selectedReview.flagged ? 'danger' : 'success'}>
                        {selectedReview.flagged ? (
                          <>
                            <Flag className="w-3 h-3 mr-1" /> Flagged
                          </>
                        ) : (
                          'Clean'
                        )}
                      </Badge>
                    </dd>
                  </div>
                  {selectedReview.flagReason && (
                    <div>
                      <dt className="text-neutral-500">Flag Reason</dt>
                      <dd className="text-red-600">{selectedReview.flagReason}</dd>
                    </div>
                  )}
                  <div>
                    <dt className="text-neutral-500">Created</dt>
                    <dd>{format(new Date(selectedReview.createdAt), 'MMM d, yyyy HH:mm')}</dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Comment</h4>
                <div className="p-4 bg-neutral-50 rounded-lg">
                  <p className="text-neutral-700 whitespace-pre-wrap">{selectedReview.comment || 'No comment provided'}</p>
                </div>
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-4 border-t">
              {selectedReview.flagged && (
                <>
                  <Button variant="ghost" onClick={() => { setShowDetailModal(false); openResolve(selectedReview, 'dismiss'); }}>
                    <CheckCircle className="w-4 h-4 mr-2" /> Dismiss Flag
                  </Button>
                  <Button variant="danger" onClick={() => { setShowDetailModal(false); openResolve(selectedReview, 'remove'); }}>
                    <XCircle className="w-4 h-4 mr-2" /> Remove Review
                  </Button>
                </>
              )}
              <Button variant="ghost" onClick={() => setShowDetailModal(false)}>Close</Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Resolve Modal */}
      <ConfirmDialog
        isOpen={showResolveModal}
        onClose={() => setShowResolveModal(false)}
        onConfirm={handleResolve}
        title={resolveAction === 'dismiss' ? 'Dismiss Flag' : 'Remove Review'}
        message={
          resolveAction === 'dismiss'
            ? `Are you sure you want to dismiss the flag on this review? The review will remain visible but the flag will be removed.`
            : `Are you sure you want to remove this review entirely? This action cannot be undone.`
        }
        confirmText={resolveAction === 'dismiss' ? 'Dismiss Flag' : 'Remove Review'}
        variant={resolveAction === 'dismiss' ? 'primary' : 'danger'}
        loading={saving}
      />
    </div>
  );
}