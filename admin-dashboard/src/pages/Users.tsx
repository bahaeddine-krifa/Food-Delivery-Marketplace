import { useState, useEffect } from 'react';
import { Search, MoreVertical, UserPlus } from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/Card';
import { StatusBadge, RoleBadge } from '@/components/ui/Badge';
import { Table, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import type { UserListResponse, AdminListParams, UpdateUserStatusRequest, Column } from '@/types/api';

export function Users() {
  const [users, setUsers] = useState<UserListResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<AdminListParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [roleFilter, setRoleFilter] = useState<string>('');

  // Modal states
  const [editUser, setEditUser] = useState<UserListResponse | null>(null);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [newStatus, setNewStatus] = useState<string>('');
  const [statusReason, setStatusReason] = useState('');

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const queryParams = { ...params };
      if (statusFilter) queryParams.status = statusFilter as AdminListParams['status'];
      if (roleFilter) queryParams.role = roleFilter as AdminListParams['role'];

      const response = await api.getUsers(queryParams);
      setUsers(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load users';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [params.page, params.size, params.sort, statusFilter, roleFilter]);

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

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setParams((prev) => ({ ...prev, page: 0 }));
    // Note: backend would need search param support
  };

  const openStatusModal = (user: UserListResponse) => {
    setEditUser(user);
    setNewStatus(user.status);
    setStatusReason('');
    setShowStatusModal(true);
  };

  const handleStatusSubmit = async () => {
    if (!editUser || !newStatus) return;
    setSaving(true);
    try {
      const request: UpdateUserStatusRequest = {
        status: newStatus as UserListResponse['status'],
        reason: statusReason || undefined,
      };
      await api.updateUserStatus(editUser.id, request);
      setShowStatusModal(false);
      fetchUsers();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to update status';
      alert(message);
    } finally {
      setSaving(false);
    }
  };

  const columns: Column<UserListResponse>[] = [
    {
      key: 'email',
      header: 'Email',
      sortable: true,
    },
    {
      key: 'name',
      header: 'Name',
      sortable: true,
      render: (row: UserListResponse) => (
        <div>
          <p className="font-medium">{row.firstName} {row.lastName}</p>
          <p className="text-sm text-neutral-500">{row.phone}</p>
        </div>
      ),
    },
    {
      key: 'role',
      header: 'Role',
      sortable: true,
      render: (row: UserListResponse) => <RoleBadge role={row.role} />,
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      render: (row: UserListResponse) => <StatusBadge status={row.status} />,
    },
    {
      key: 'verification',
      header: 'Verified',
      render: (row: UserListResponse) => (
        <div className="flex items-center gap-2">
          <span className={row.emailVerified ? 'text-green-600' : 'text-red-600'}>
            {row.emailVerified ? '✓ Email' : '✗ Email'}
          </span>
          <span className={row.phoneVerified ? 'text-green-600' : 'text-red-600'}>
            {row.phoneVerified ? '✓ Phone' : '✗ Phone'}
          </span>
        </div>
      ),
    },
    {
      key: 'createdAt',
      header: 'Joined',
      sortable: true,
      render: (row: UserListResponse) => format(new Date(row.createdAt), 'MMM d, yyyy'),
    },
    {
      key: 'lastLoginAt',
      header: 'Last Login',
      render: (row: UserListResponse) => row.lastLoginAt ? format(new Date(row.lastLoginAt), 'MMM d, yyyy HH:mm') : 'Never',
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row: UserListResponse) => (
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="sm" onClick={() => openStatusModal(row)} aria-label="Change status">
            <MoreVertical className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  const userRoles = ['CUSTOMER', 'RESTAURANT_OWNER', 'DELIVERY_PERSON', 'ADMIN'] as const;
  const userStatuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Users</h1>
          <p className="text-neutral-500 mt-1">Manage platform users and their permissions</p>
        </div>
        <Button>
          <UserPlus className="w-4 h-4 mr-2" />
          Add User
        </Button>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
              <Input
                placeholder="Search users..."
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
              {userStatuses.map((status) => (
                <option key={status} value={status}>{status.replace('_', ' ')}</option>
              ))}
            </select>
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="px-3 py-2 border border-neutral-300 rounded-lg focus-ring bg-white w-full sm:w-48"
            >
              <option value="">All Roles</option>
              {userRoles.map((role) => (
                <option key={role} value={role}>{role.replace('_', ' ')}</option>
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
            data={users}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No users found"
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

      {/* Status Modal */}
      <Modal
        isOpen={showStatusModal}
        onClose={() => setShowStatusModal(false)}
        title="Update User Status"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-neutral-600">Update status for <strong>{editUser?.firstName} {editUser?.lastName}</strong></p>
          <select
            value={newStatus}
            onChange={(e) => setNewStatus(e.target.value)}
            className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus-ring"
          >
            {userStatuses.map((status) => (
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
            <Button variant={newStatus === 'SUSPENDED' || newStatus === 'INACTIVE' ? 'danger' : 'primary'} onClick={handleStatusSubmit} loading={saving}>
              Update Status
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

// Need to import Textarea
import { Textarea } from '@/components/ui/Input';