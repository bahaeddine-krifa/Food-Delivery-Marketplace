import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { Layout } from '@/components/layout/Layout';
import { Login } from '@/pages/Login';
import { Dashboard } from '@/pages/Dashboard';
import { Users } from '@/pages/Users';
import { RestaurantApplications } from '@/pages/RestaurantApplications';
import { Restaurants } from '@/pages/Restaurants';
import { Orders } from '@/pages/Orders';
import { Deliveries } from '@/pages/Deliveries';
import { Payments } from '@/pages/Payments';
import { Reviews } from '@/pages/Reviews';
import { Config } from '@/pages/Config';

function ProtectedRoute() {
  const { user, loading, isAdmin } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    );
  }

  if (!user || !isAdmin) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

function PublicRoute() {
  const { user, loading, isAdmin } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
      </div>
    );
  }

  if (user && isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}

export default function App() {
  return (
    <Routes>
      <Route element={<PublicRoute />}>
        <Route path="/login" element={<Login />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/users" element={<Users />} />
          <Route path="/restaurant-applications" element={<RestaurantApplications />} />
          <Route path="/restaurants" element={<Restaurants />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/deliveries" element={<Deliveries />} />
          <Route path="/payments" element={<Payments />} />
          <Route path="/reviews" element={<Reviews />} />
          <Route path="/config" element={<Config />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}