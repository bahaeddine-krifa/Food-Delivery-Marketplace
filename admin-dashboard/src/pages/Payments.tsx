import { useState, useEffect } from 'react';
import { Search, Eye, CreditCard, DollarSign, CheckCircle, AlertCircle } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { PaymentStatusBadge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import type { PaymentOverviewResponse, PaymentListParams, Column } from '@/types/api';

export function Payments() {
  const [payments, setPayments] = useState<PaymentOverviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<PaymentListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');

  // Modal state
  const [selectedPayment, setSelectedPayment] = useState<PaymentOverviewResponse | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  const fetchPayments = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as PaymentListParams['status'];

      const response = await api.getPayments(queryParams);
      setPayments(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load payments';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
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

  const openDetail = (payment: PaymentOverviewResponse) => {
    setSelectedPayment(payment);
    setShowDetailModal(true);
  };

  const getMethodIcon = (method: string) => {
    switch (method) {
      case 'CREDIT_CARD':
      case 'DEBIT_CARD':
        return <CreditCard className="w-4 h-4" />;
      case 'PAYPAL':
        return <DollarSign className="w-4 h-4" />;
      case 'APPLE_PAY':
        return <CheckCircle className="w-4 h-4" />;
      case 'GOOGLE_PAY':
        return <AlertCircle className="w-4 h-4" />;
      case 'CASH':
        return <DollarSign className="w-4 h-4" />;
      default:
        return <CreditCard className="w-4 h-4" />;
    }
  };

  const columns: Column<PaymentOverviewResponse>[] = [
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
      key: 'amount',
      header: 'Amount',
      sortable: true,
      render: (row: PaymentOverviewResponse) => (
        <div className="font-medium">${Number(row.amount).toFixed(2)}</div>
      ),
    },
    {
      key: 'method',
      header: 'Method',
      render: (row: PaymentOverviewResponse) => (
        <div className="flex items-center gap-2">
          {getMethodIcon(row.method)}
          <span>{row.method.replace('_', ' ')}</span>
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: PaymentOverviewResponse) => <PaymentStatusBadge status={row.status} />,
    },
    {
      key: 'createdAt',
      header: 'Created',
      sortable: true,
      render: (row: PaymentOverviewResponse) => format(new Date(row.createdAt), 'MMM d, yyyy HH:mm'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: PaymentOverviewResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)} aria-label="View details">
            <Eye className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  const paymentStatuses = ['PENDING', 'COMPLETED', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Payments</h1>
          <p className="text-neutral-500 mt-1">View and manage all platform payments</p>
        </div>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search payments..."
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
              {paymentStatuses.map((status) => (
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
            data={payments}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No payments found"
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
        title="Payment Details"
        size="lg"
      >
        {selectedPayment && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Payment Information</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Order Number</dt>
                    <dd className="font-medium">{selectedPayment.orderNumber}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Amount</dt>
                    <dd className="font-medium text-lg">${Number(selectedPayment.amount).toFixed(2)}</dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Method</dt>
                    <dd className="flex items-center gap-2">
                      {getMethodIcon(selectedPayment.method)}
                      <span>{selectedPayment.method.replace('_', ' ')}</span>
                    </dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Status</dt>
                    <dd><PaymentStatusBadge status={selectedPayment.status} /></dd>
                  </div>
                  <div>
                    <dt className="text-neutral-500">Created</dt>
                    <dd>{format(new Date(selectedPayment.createdAt), 'MMM d, yyyy HH:mm')}</dd>
                  </div>
                </dl>
              </div>
              <div>
                <h4 className="font-medium text-neutral-900 mb-2">Customer</h4>
                <dl className="space-y-3 text-sm">
                  <div>
                    <dt className="text-neutral-500">Name</dt>
                    <dd className="font-medium">{selectedPayment.customerName}</dd>
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