import { type HTMLAttributes, forwardRef } from 'react';

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'neutral';
}

export const Badge = forwardRef<HTMLSpanElement, BadgeProps>(
  ({ className = '', variant = 'default', children, ...props }, ref) => {
    const variantStyles = {
      default: 'bg-primary-100 text-primary-700',
      success: 'bg-green-100 text-green-700',
      warning: 'bg-yellow-100 text-yellow-700',
      danger: 'bg-red-100 text-red-700',
      info: 'bg-blue-100 text-blue-700',
      neutral: 'bg-neutral-100 text-neutral-700',
    };

    return (
      <span
        ref={ref}
        className={`
          inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
          ${variantStyles[variant]} ${className}
        `}
        {...props}
      >
        {children}
      </span>
    );
  }
);

Badge.displayName = 'Badge';

// Role badge with specific colors
export function RoleBadge({ role }: { role: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    ADMIN: 'danger',
    RESTAURANT_OWNER: 'info',
    DELIVERY_PERSON: 'warning',
    CUSTOMER: 'success',
  };

  return <Badge variant={variantMap[role] || 'neutral'}>{role.replace('_', ' ')}</Badge>;
}

// Status badge with specific colors
export function StatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    ACTIVE: 'success',
    INACTIVE: 'neutral',
    SUSPENDED: 'danger',
    PENDING_VERIFICATION: 'warning',
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    UNDER_REVIEW: 'info',
    CLOSED: 'neutral',
  };

  return <Badge variant={variantMap[status] || 'neutral'}>{status.replace('_', ' ')}</Badge>;
}

// Order status badge
export function OrderStatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    PENDING: 'warning',
    ACCEPTED: 'info',
    PREPARING: 'info',
    READY: 'success',
    PICKED_UP: 'info',
    DELIVERED: 'success',
    CANCELLED: 'danger',
    REFUNDED: 'neutral',
  };

  return <Badge variant={variantMap[status] || 'default'}>{status.replace('_', ' ')}</Badge>;
}

// Delivery status badge
export function DeliveryStatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    ASSIGNED: 'info',
    PICKED_UP: 'warning',
    IN_TRANSIT: 'info',
    DELIVERED: 'success',
    FAILED: 'danger',
  };

  return <Badge variant={variantMap[status] || 'default'}>{status.replace('_', ' ')}</Badge>;
}

// Payment status badge
export function PaymentStatusBadge({ status }: { status: string }) {
  const variantMap: Record<string, BadgeProps['variant']> = {
    PENDING: 'warning',
    COMPLETED: 'success',
    FAILED: 'danger',
    REFUNDED: 'neutral',
    PARTIALLY_REFUNDED: 'info',
  };

  return <Badge variant={variantMap[status] || 'default'}>{status.replace('_', ' ')}</Badge>;
}