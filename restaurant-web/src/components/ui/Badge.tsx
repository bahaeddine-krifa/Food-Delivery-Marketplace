import React from 'react';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  children: React.ReactNode;
}

export function Badge({ variant = 'default', className = '', children, ...props }: BadgeProps) {
  const variantClasses = {
    default: 'bg-primary-100 text-primary-800',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800',
    neutral: 'bg-neutral-100 text-neutral-800',
  };

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${variantClasses[variant]} ${className}`}
      {...props}
    >
      {children}
    </span>
  );
}

// Status-specific badges
export function OrderStatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    PENDING: 'warning',
    ACCEPTED: 'info',
    REJECTED: 'danger',
    PREPARING: 'warning',
    READY: 'info',
    ASSIGNED: 'info',
    PICKED_UP: 'success',
    DELIVERING: 'info',
    DELIVERED: 'success',
    CANCELLED: 'danger',
    FAILED: 'danger',
  };

  const labelMap: Record<string, string> = {
    PENDING: 'Pending',
    ACCEPTED: 'Accepted',
    REJECTED: 'Rejected',
    PREPARING: 'Preparing',
    READY: 'Ready',
    ASSIGNED: 'Assigned',
    PICKED_UP: 'Picked Up',
    DELIVERING: 'Delivering',
    DELIVERED: 'Delivered',
    CANCELLED: 'Cancelled',
    FAILED: 'Failed',
  };

  return <Badge variant={variantMap[status] || 'default'}>{labelMap[status] || status}</Badge>;
}

export function ProductAvailabilityBadge({ isAvailable }: { isAvailable: boolean }) {
  return isAvailable ? (
    <Badge variant="success">Available</Badge>
  ) : (
    <Badge variant="neutral">Unavailable</Badge>
  );
}

export function RestaurantStatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    DRAFT: 'neutral',
    PENDING_APPROVAL: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    SUSPENDED: 'danger',
    CLOSED: 'neutral',
  };

  const labelMap: Record<string, string> = {
    DRAFT: 'Draft',
    PENDING_APPROVAL: 'Pending Approval',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    SUSPENDED: 'Suspended',
    CLOSED: 'Closed',
  };

  return <Badge variant={variantMap[status] || 'default'}>{labelMap[status] || status}</Badge>;
}