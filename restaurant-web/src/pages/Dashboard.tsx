import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  ShoppingBag,
  Clock,
  CheckCircle,
  DollarSign,
  Package,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';
import { format } from 'date-fns';
import { useRestaurants } from '@/hooks/useRestaurant';
import { restaurantApi } from '@/lib/restaurantApi';
import { useAuth } from '@/context/AuthContext';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Table, type Column } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import type { OrderSummaryResponse } from '@/types/api';

interface DashboardStats {
  totalOrders: number;
  pendingOrders: number;
  preparingOrders: number;
  completedOrders: number;
  totalSales: string;
  recentOrders: OrderSummaryResponse[];
}

export function Dashboard() {
  const { user } = useAuth();
  const { restaurants, loading: restaurantsLoading } = useRestaurants();
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<string>('');
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Auto-select first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurantId) {
      setSelectedRestaurantId(restaurants[0].id);
    }
  }, [restaurants, selectedRestaurantId]);

  // Fetch dashboard stats when restaurant changes
  useEffect(() => {
    if (!selectedRestaurantId) return;

    const fetchStats = async () => {
      setLoading(true);
      setError(null);
      try {
        // Get recent orders (last 7 days)
        const orders = await restaurantApi.listOrders(selectedRestaurantId, {
          page: 0,
          size: 10
        });

        // Calculate stats
        const allOrders = orders.content;
        const totalOrders = orders.totalElements;

        const pendingOrders = allOrders.filter(o => o.status === 'PENDING').length;
        const preparingOrders = allOrders.filter(o =>
          ['ACCEPTED', 'PREPARING', 'READY'].includes(o.status)
        ).length;
        const completedOrders = allOrders.filter(o => o.status === 'DELIVERED').length;
        const totalSales = allOrders
          .filter(o => o.status === 'DELIVERED')
          .reduce((sum, o) => sum + parseFloat(o.totalAmount), 0)
          .toFixed(2);

        setStats({
          totalOrders,
          pendingOrders,
          preparingOrders,
          completedOrders,
          totalSales,
          recentOrders: allOrders.slice(0, 5),
        });
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Failed to load dashboard';
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, [selectedRestaurantId]);

  const statCards = [
    {
      title: 'Total Orders',
      value: stats?.totalOrders.toString() || '0',
      icon: ShoppingBag,
      color: 'bg-blue-500',
      trend: '+12%',
      trendUp: true,
    },
    {
      title: 'Pending',
      value: stats?.pendingOrders.toString() || '0',
      icon: Clock,
      color: 'bg-yellow-500',
      trend: 'Requires action',
      trendUp: false,
    },
    {
      title: 'Preparing',
      value: stats?.preparingOrders.toString() || '0',
      icon: Package,
      color: 'bg-orange-500',
      trend: 'In progress',
      trendUp: false,
    },
    {
      title: 'Completed Today',
      value: stats?.completedOrders.toString() || '0',
      icon: CheckCircle,
      color: 'bg-green-500',
      trend: '+8%',
      trendUp: true,
    },
    {
      title: 'Total Sales',
      value: `$${stats?.totalSales || '0.00'}`,
      icon: DollarSign,
      color: 'bg-purple-500',
      trend: '+15%',
      trendUp: true,
    },
  ];

  const recentOrdersColumns: Column<OrderSummaryResponse>[] = [
    { key: 'orderNumber', header: 'Order #' },
    { key: 'status', header: 'Status', render: (row) => <Badge variant="default">{row.status}</Badge> },
    { key: 'totalAmount', header: 'Amount', render: (row) => `$${row.totalAmount}` },
    { key: 'itemCount', header: 'Items' },
    { key: 'createdAt', header: 'Time', render: (row) => format(new Date(row.createdAt), 'HH:mm') },
  ];

  if (restaurantsLoading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-neutral-200 p-6">
              <div className="h-4 bg-neutral-200 rounded w-1/2 mb-2" />
              <div className="h-8 bg-neutral-200 rounded w-1/3" />
            </div>
          ))}
        </div>
        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="h-4 bg-neutral-200 rounded w-1/4 mb-4" />
          <div className="space-y-3">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-12 bg-neutral-200 rounded" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Dashboard</h1>
          <p className="text-neutral-500 mt-1">Welcome back, {user?.firstName}! Here's your restaurant overview.</p>
        </div>
        <div className="flex items-center gap-4">
          <select
            value={selectedRestaurantId}
            onChange={(e) => setSelectedRestaurantId(e.target.value)}
            className="px-4 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white"
            disabled={restaurants.length <= 1}
          >
            {restaurants.map((restaurant) => (
              <option key={restaurant.id} value={restaurant.id}>
                {restaurant.name} {restaurant.status !== 'APPROVED' && `(${restaurant.status})`}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Error message */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          {error}
        </div>
      )}

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        {statCards.map((stat) => (
          <Card key={stat.title} className="hover:shadow-md transition-shadow">
            <CardContent className="p-6">
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-sm font-medium text-neutral-500">{stat.title}</p>
                  <p className="text-3xl font-bold text-neutral-900 mt-1">{stat.value}</p>
                  <div className="flex items-center gap-1 mt-2">
                    {stat.trendUp ? (
                      <ArrowUpRight className="w-4 h-4 text-green-500" />
                    ) : (
                      <ArrowDownRight className="w-4 h-4 text-red-500" />
                    )}
                    <span className={`text-sm ${stat.trendUp ? 'text-green-600' : 'text-red-600'}`}>
                      {stat.trend}
                    </span>
                  </div>
                </div>
                <div className={`${stat.color} p-3 rounded-xl`}>
                  <stat.icon className="w-6 h-6 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Recent Orders */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Recent Orders</CardTitle>
            <p className="text-sm text-neutral-500">Latest orders for {restaurants.find(r => r.id === selectedRestaurantId)?.name}</p>
          </div>
          <Link to="/orders">
            <Button variant="ghost" size="sm">View All</Button>
          </Link>
        </CardHeader>
        <CardContent>
          {loading ? (
            <Table
              columns={recentOrdersColumns}
              data={[]}
              keyExtractor={(row) => row.id}
              isLoading
            />
          ) : !stats || stats.recentOrders.length === 0 ? (
            <div className="text-center py-12">
              <ShoppingBag className="mx-auto h-12 w-12 text-neutral-300" />
              <p className="mt-2 text-sm text-neutral-500">No recent orders</p>
            </div>
          ) : (
            <Table
              columns={recentOrdersColumns}
              data={stats.recentOrders}
              keyExtractor={(row) => row.id}
              onRowClick={(row) => window.open(`/orders/${row.id}`, '_blank')}
            />
          )}
        </CardContent>
      </Card>
    </div>
  );
}