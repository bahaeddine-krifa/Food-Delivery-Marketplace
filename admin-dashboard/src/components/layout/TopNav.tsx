import { Menu, Bell, User, LogOut, Moon, Sun, ChevronDown } from 'lucide-react';
import { useState, useRef, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';

export function TopNav({ onMenuClick }: { onMenuClick: () => void }) {
  const { user, logout } = useAuth();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const userMenuRef = useRef<HTMLDivElement>(null);
  const notificationsRef = useRef<HTMLDivElement>(null);

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
      if (notificationsRef.current && !notificationsRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Load dark mode preference
  useEffect(() => {
    const saved = localStorage.getItem('darkMode');
    if (saved === 'true') {
      setDarkMode(true);
      document.documentElement.classList.add('dark');
    }
  }, []);

  const toggleDarkMode = () => {
    const newValue = !darkMode;
    setDarkMode(newValue);
    localStorage.setItem('darkMode', String(newValue));
    if (newValue) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  const handleLogout = () => {
    setShowUserMenu(false);
    logout();
  };

  return (
    <header
      className={`
        fixed top-0 right-0 h-16 bg-white border-b border-neutral-200 z-30 transition-all duration-300
        ml-64
      `}
      role="banner"
    >
      <div className="flex items-center justify-between h-full px-6">
        {/* Left side - Mobile menu button */}
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" onClick={onMenuClick} aria-label="Toggle menu" className="lg:hidden">
            <Menu className="w-5 h-5" />
          </Button>
        </div>

        {/* Right side - Actions */}
        <div className="flex items-center gap-3">
          {/* Dark mode toggle */}
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleDarkMode}
            aria-label={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          >
            {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
          </Button>

          {/* Notifications */}
          <div className="relative" ref={notificationsRef}>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowNotifications(!showNotifications)}
              aria-label="Notifications"
              aria-expanded={showNotifications}
            >
              <Bell className="w-5 h-5 text-neutral-600" />
            </Button>

            {showNotifications && (
              <Modal
                isOpen={showNotifications}
                onClose={() => setShowNotifications(false)}
                title="Notifications"
                size="md"
                showCloseButton={false}
              >
                <div className="text-center py-8 text-neutral-500">
                  <Bell className="mx-auto h-12 w-12 text-neutral-300 mb-4" />
                  <p>No notifications yet</p>
                </div>
              </Modal>
            )}
          </div>

          {/* User menu */}
          <div className="relative" ref={userMenuRef}>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowUserMenu(!showUserMenu)}
              aria-label="User menu"
              aria-expanded={showUserMenu}
              className="gap-2"
            >
              <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                <User className="w-5 h-5 text-primary-700" />
              </div>
              <span className="hidden sm:block text-sm font-medium text-neutral-700">
                {user?.firstName} {user?.lastName}
              </span>
              <ChevronDown className="w-4 h-4 text-neutral-500 hidden sm:block" />
            </Button>

            {showUserMenu && (
              <Modal
                isOpen={showUserMenu}
                onClose={() => setShowUserMenu(false)}
                title=""
                size="sm"
                showCloseButton={false}
              >
                <div className="space-y-1">
                  <div className="px-4 py-3 border-b border-neutral-200">
                    <p className="text-sm font-medium text-neutral-900">
                      {user?.firstName} {user?.lastName}
                    </p>
                    <p className="text-xs text-neutral-500">{user?.email}</p>
                    <span className="inline-flex items-center px-2 py-1 mt-1 text-xs font-medium bg-primary-100 text-primary-700 rounded-full">
                      Admin
                    </span>
                  </div>
                  <button
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-sm text-neutral-700 hover:bg-neutral-50 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                    Sign Out
                  </button>
                </div>
              </Modal>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}