import { useState } from 'react';
import { Sidebar } from './Sidebar';
import { TopNav } from './TopNav';
import type { ReactNode } from 'react';

interface LayoutProps {
  children?: ReactNode;
}

export function Layout({ children }: LayoutProps) {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Mobile overlay */}
      {mobileMenuOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
          onClick={() => setMobileMenuOpen(false)}
          aria-hidden="true"
        />
      )}

      {/* Sidebar */}
      <Sidebar
        collapsed={sidebarCollapsed}
        onToggle={() => setSidebarCollapsed(!sidebarCollapsed)}
      />

      {/* Main content */}
      <div
        className={`flex-1 transition-all duration-300 lg:ml-64 ${sidebarCollapsed ? 'lg:ml-16' : ''}`}
      >
        {/* Top Navigation */}
        <TopNav
          onMenuClick={() => setMobileMenuOpen(true)}
        />

        {/* Page content */}
        <main className="pt-16 pb-8 px-4 lg:px-8">
          {children}
        </main>
      </div>
    </div>
  );
}