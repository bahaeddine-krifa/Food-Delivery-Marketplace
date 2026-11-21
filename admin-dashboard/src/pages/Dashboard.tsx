import { useEffect, useState } from 'react';
import {
  Users,
  Store,
  ShoppingBag,
  DollarSign,
  Truck,
  CreditCard,
  MessageSquare,
  ArrowUpRight,
  ArrowDownRight,
  AlertCircle,
} from 'lucide-react';
import { format } from 'date-fns';
import { api } from '@/lib/api';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { StatusBadge } from '@/components/ui/Badge';
import { Table } from '@/components/ui/Table';
import type { DashboardStatsResponse, UserListResponse, RestaurantListResponse, OrderOverviewResponse, Column } from '@/types/api';

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ComponentType<{ className?: string }>;
  color: string;
  trend?: string;
  trendUp?: boolean;
  href?: string;
}

function StatCard({ title, value, icon: Icon, color, trend, trendUp, href }: StatCardProps) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-6">
        {href ? (
          <a href={href} className="block">
            <StatContent />
          </a>
        ) : (
          <StatContent />
        )}
      </CardContent>
    </Card>
  );

  function StatContent() {
    return (
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-neutral-500">{title}</p>
          <p className="text-3xl font-bold text-neutral-900 mt-1">{value}</p>
          {trend && (
            <div className="flex items-center gap-1 mt-2">
              {trendUp ? (
                <ArrowUpRight className="w-4 h-4 text-green-500" />
              ) : (
                <ArrowDownRight className="w-4 h-4 text-red-500" />
              )}
              <span className={`text-sm ${trendUp ? 'text-green-600' : 'text-red-600'}`}>
                {trend}
              </span>
            </div>
          )}
        </div>
        <div className={`${color} p-3 rounded-xl`}>
          <Icon className="w-6 h-6 text-white" />
        </div>
      </div>
    );
  }
}

export function Dashboard() {
  const [stats, setStats] = useState<DashboardStatsResponse | null>(null);
  const [recentUsers, setRecentUsers] = useState<UserListResponse[]>([]);
  const [recentRestaurants, setRecentRestaurants] = useState<RestaurantListResponse[]>([]);
  const [recentOrders, setRecentOrders] = useState<OrderOverviewResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [statsData, usersData, restaurantsData, ordersData] = await Promise.all([
          api.getDashboardStats(),
          api.getUsers({ page: 0, size: 5, sort: 'createdAt,desc' }),
          api.getRestaurants({ page: 0, size: 5, sort: 'createdAt,desc' }),
          api.getOrders({ page: 0, size: 5, sort: 'createdAt,desc' }),
        ]);

        setStats(statsData);
        setRecentUsers(usersData.content);
        setRecentRestaurants(restaurantsData.content);
        setRecentOrders(ordersData.content);
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Failed to load dashboard';
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const statCards = [
    {
      title: 'Total Users',
      value: stats?.totalUsers.toLocaleString() || '0',
      icon: Users,
      color: 'bg-blue-500',
      href: '/users',
    },
    {
      title: 'Restaurants',
      value: stats?.totalRestaurants.toLocaleString() || '0',
      icon: Store,
      color: 'bg-green-500',
      href: '/restaurants',
    },
    {
      title: 'Pending Applications',
      value: stats?.pendingRestaurantApplications.toLocaleString() || '0',
      icon: AlertCircle,
      color: 'bg-yellow-500',
      href: '/restaurant-applications',
    },
    {
      title: 'Total Orders',
      value: stats?.totalOrders.toLocaleString() || '0',
      icon: ShoppingBag,
      color: 'bg-purple-500',
      href: '/orders',
    },
    {
      title: 'Revenue This Month',
      value: `$${Number(stats?.revenueThisMonth || 0).toLocaleString()}`,
      icon: DollarSign,
      color: 'bg-emerald-500',
    },
    {
      title: 'Active Deliveries',
      value: stats?.activeDeliveries.toLocaleString() || '0',
      icon: Truck,
      color: 'bg-orange-500',
      href: '/deliveries',
    },
    {
      title: 'Pending Payments',
      value: stats?.pendingPayments.toLocaleString() || '0',
      icon: CreditCard,
      color: 'bg-red-500',
      href: '/payments',
    },
    {
      title: 'Flagged Reviews',
      value: stats?.flaggedReviews.toLocaleString() || '0',
      icon: MessageSquare,
      color: 'bg-pink-500',
      href: '/reviews',
    },
  ];

  const userColumns: Column<UserListResponse>[] = [
    { key: 'email', header: 'Email' },
    { key: 'firstName', header: 'Name', render: (row) => `${row.firstName} ${row.lastName}` },
    { key: 'role', header: 'Role', render: (row) => <StatusBadge status={row.role} /> },
    { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
    { key: 'createdAt', header: 'Joined', render: (row) => format(new Date(row.createdAt), 'MMM d, yyyy') },
  ];

  const restaurantColumns: Column<RestaurantListResponse>[] = [
    { key: 'name', header: 'Restaurant' },
    { key: 'ownerName', header: 'Owner' },
    { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
    { key: 'rating', header: 'Rating', render: (row) => `${row.rating.toFixed(1)} (${row.reviewCount})` },
    { key: 'createdAt', header: 'Created', render: (row) => format(new Date(row.createdAt), 'MMM d, yyyy') },
  ];

  const orderColumns: Column<OrderOverviewResponse>[] = [
    { key: 'orderNumber', header: 'Order #' },
    { key: 'customerName', header: 'Customer' },
    { key: 'restaurantName', header: 'Restaurant' },
    { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
    { key: 'totalAmount', header: 'Amount', render: (row) => `$${Number(row.totalAmount).toFixed(2)}` },
    { key: 'createdAt', header: 'Time', render: (row) => format(new Date(row.createdAt), 'MMM d, HH:mm') },
  ];

  if (loading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-8 gap-4">
          {[...Array(8)].map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-neutral-200 p-6">
              <div className="h-4 bg-neutral-200 rounded w-1/2 mb-2" />
              <div className="h-8 bg-neutral-200 rounded w-1/3" />
            </div>
          ))}
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-neutral-200 p-6">
              <div className="h-4 bg-neutral-200 rounded w-1/4 mb-4" />
              <div className="space-y-3">
                {[...Array(3)].map((_, j) => (
                  <div key={j} className="h-12 bg-neutral-200 rounded" />
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <AlertCircle className="mx-auto h-12 w-12 text-red-500" />
        <p className="mt-4 text-neutral-600">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-neutral-900">Dashboard</h1>
        <p className="text-neutral-500 mt-1">Overview of platform activity and key metrics</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-8 gap-4">
        {statCards.map((stat) => (
          <StatCard key={stat.title} {...stat} />
        ))}
      </div>

      {/* Recent Activity Tables */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Users */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Users</CardTitle>
              <p className="text-sm text-neutral-500">Latest user registrations</p>
            </div>
            <a href="/users" className="text-sm text-primary-600 hover:text-primary-700 font-medium">
              View All
            </a>
          </CardHeader>
          <CardContent>
            <Table
              columns={userColumns}
              data={recentUsers}
              keyExtractor={(row) => row.id}
              emptyMessage="No users yet"
            />
          </CardContent>
        </Card>

        {/* Recent Restaurants */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Restaurants</CardTitle>
              <p className="text-sm text-neutral-500">Latest restaurant registrations</p>
            </div>
            <a href="/restaurants" className="text-sm text-primary-600 hover:text-primary-700 font-medium">
              View All
            </a>
          </CardHeader>
          <CardContent>
            <Table
              columns={restaurantColumns}
              data={recentRestaurants}
              keyExtractor={(row) => row.id}
              emptyMessage="No restaurants yet"
            />
          </CardContent>
        </Card>

        {/* Recent Orders */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Orders</CardTitle>
              <p className="text-sm text-neutral-500">Latest orders across platform</p>
            </div>
            <a href="/orders" className="text-sm text-primary-600 hover:text-primary-700 font-medium">
              View All
            </a>
          </CardHeader>
          <CardContent>
            <Table
              columns={orderColumns}
              data={recentOrders}
              keyExtractor={(row) => row.id}
              emptyMessage="No orders yet"
            />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}