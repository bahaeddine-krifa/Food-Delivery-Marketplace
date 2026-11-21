import { Outlet, useLocation } from 'react-router-dom';
import { useState } from 'react';
import { Sidebar } from './Sidebar';
import { TopNav } from './TopNav';

export function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  // Close sidebar on navigation (mobile)
  if (location.pathname !== '/login') {
    // Sidebar closes automatically on route change in mobile
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Sidebar */}
      <Sidebar />

      {/* Top Navigation */}
      <TopNav onMenuClick={() => setSidebarOpen(true)} />

      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
          aria-hidden="true"
        />
      )}

      {/* Main content */}
      <main
        className={`
          pt-16 transition-all duration-300 min-h-screen
          ml-64
        `}
        role="main"
      >
        <div className="p-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
}