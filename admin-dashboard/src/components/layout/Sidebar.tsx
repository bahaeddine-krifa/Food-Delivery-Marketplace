import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Store,
  FileText,
  ShoppingBag,
  Truck,
  CreditCard,
  MessageSquare,
  Settings,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/Button';

const navigation = [
  { name: 'Dashboard', href: '/', icon: LayoutDashboard },
  { name: 'Users', href: '/users', icon: Users },
  { name: 'Restaurant Applications', href: '/restaurant-applications', icon: FileText },
  { name: 'Restaurants', href: '/restaurants', icon: Store },
  { name: 'Orders', href: '/orders', icon: ShoppingBag },
  { name: 'Deliveries', href: '/deliveries', icon: Truck },
  { name: 'Payments', href: '/payments', icon: CreditCard },
  { name: 'Flagged Reviews', href: '/reviews', icon: MessageSquare },
  { name: 'System Config', href: '/config', icon: Settings },
];

export function Sidebar() {
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const { logout } = useAuth();

  return (
    <aside
      className={`
        fixed left-0 top-0 h-screen bg-white border-r border-neutral-200 transition-all duration-300 z-40
        ${collapsed ? 'w-20' : 'w-64'}
      `}
      aria-label="Main navigation"
    >
      {/* Logo */}
      <div className="flex items-center justify-between h-16 px-4 border-b border-neutral-200">
        {!collapsed && (
          <Link to="/" className="flex items-center gap-2">
            <div className="w-10 h-10 bg-primary-600 rounded-xl flex items-center justify-center">
              <LayoutDashboard className="w-6 h-6 text-white" />
            </div>
            <span className="text-xl font-bold text-neutral-900">AdminHub</span>
          </Link>
        )}
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setCollapsed(!collapsed)}
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          className="text-neutral-500 hover:text-neutral-700"
        >
          {collapsed ? <ChevronRight className="w-5 h-5" /> : <ChevronLeft className="w-5 h-5" />}
        </Button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto p-4 space-y-1" aria-label="Admin navigation">
        {navigation.map((item) => {
          const isActive = location.pathname === item.href || location.pathname.startsWith(item.href + '/');
          const Icon = item.icon;
          return (
            <Link
              key={item.name}
              to={item.href}
              className={`
                flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors
                ${isActive
                  ? 'bg-primary-50 text-primary-700'
                  : 'text-neutral-600 hover:bg-neutral-100 hover:text-neutral-900'}
                ${collapsed ? 'justify-center' : ''}
              `}
              aria-current={isActive ? 'page' : undefined}
              title={collapsed ? item.name : undefined}
            >
              <Icon className="w-5 h-5 flex-shrink-0" aria-hidden="true" />
              {!collapsed && <span>{item.name}</span>}
            </Link>
          );
        })}
      </nav>

      {/* Bottom section */}
      <div className="p-4 border-t border-neutral-200">
        {!collapsed && (
          <div className="space-y-2">
            <p className="text-xs font-medium text-neutral-500 uppercase tracking-wider">Account</p>
            <Button variant="outline" className="w-full justify-start gap-2" onClick={logout}>
              <ChevronRight className="w-4 h-4" />
              <span>Sign Out</span>
            </Button>
          </div>
        )}
        {collapsed && (
          <Button variant="ghost" size="sm" className="w-full" onClick={logout} aria-label="Sign out">
            <ChevronRight className="w-5 h-5" />
          </Button>
        )}
      </div>
    </aside>
  );
}