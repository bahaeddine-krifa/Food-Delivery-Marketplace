import { Menu, Bell, Sun, ChevronDown } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';

export function TopNav({ onMenuClick }: { onMenuClick: () => void }) {
  const { user } = useAuth();

  return (
    <header className="fixed top-0 left-0 right-0 z-30 h-16 bg-white border-b border-neutral-200 flex items-center justify-between px-4">
      <button
        onClick={onMenuClick}
        className="lg:hidden p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 transition-colors"
        aria-label="Toggle menu"
      >
        <Menu className="w-6 h-6" />
      </button>

      <div className="flex-1 lg:flex-none" />

      <div className="flex items-center gap-4">
        {/* Notifications */}
        <button className="relative p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 transition-colors">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
        </button>

        {/* Theme toggle */}
        <button className="p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 transition-colors">
          <Sun className="w-5 h-5" />
        </button>

        {/* User menu */}
        <div className="relative">
          <button className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-neutral-100 transition-colors">
            <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </div>
            <span className="hidden sm:block text-sm font-medium text-neutral-700">
              {user?.firstName} {user?.lastName}
            </span>
            <ChevronDown className="w-4 h-4 text-neutral-500 hidden sm:block" />
          </button>
        </div>
      </div>
    </header>
  );
}