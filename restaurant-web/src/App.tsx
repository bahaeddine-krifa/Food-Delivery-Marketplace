import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
import { Layout } from '@/components/layout/Layout';
import { Login } from '@/pages/Login';
import { Dashboard } from '@/pages/Dashboard';
import { Orders } from '@/pages/Orders';
import { Products } from '@/pages/Products';
import { Categories } from '@/pages/Categories';
import { Profile } from '@/pages/Profile';
import { Settings } from '@/pages/Settings';

function PrivateRoutes() {
  return (
    <ProtectedRoute allowedRoles={['RESTAURANT_OWNER', 'ADMIN']}>
      <Layout>
        <Outlet />
      </Layout>
    </ProtectedRoute>
  );
}

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<div className="min-h-screen flex items-center justify-center"><p>Registration coming soon</p></div>} />
        <Route path="/forgot-password" element={<div className="min-h-screen flex items-center justify-center"><p>Password reset coming soon</p></div>} />
        <Route element={<PrivateRoutes />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/orders/*" element={<Orders />} />
          <Route path="/products" element={<Products />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
        </Route>
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;