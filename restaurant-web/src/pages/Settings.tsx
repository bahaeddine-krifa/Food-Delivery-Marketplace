import { useState } from 'react';
import { User, Shield, Bell, LogOut, AlertCircle, CheckCircle } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';

export function Settings() {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<'account' | 'notifications' | 'security'>('account');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [accountData, setAccountData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    phone: user?.phone || '',
    email: user?.email || '',
  });

  const [notificationPrefs, setNotificationPrefs] = useState({
    emailOrders: true,
    emailPromotions: false,
    pushOrders: true,
    pushPromotions: false,
    smsOrders: false,
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handleAccountSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));

    setSuccess('Account information updated successfully');
    setSaving(false);
  };

  const handleNotificationSave = async () => {
    setSaving(true);
    setError(null);
    setSuccess(null);

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));

    setSuccess('Notification preferences updated successfully');
    setSaving(false);
  };

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setError('New passwords do not match');
      return;
    }

    if (passwordData.newPassword.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }

    setSaving(true);

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));

    setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
    setSuccess('Password changed successfully');
    setSaving(false);
  };

  const handleLogout = async () => {
    await logout();
  };

  return (
    <div className="space-y-6 max-w-3xl">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-neutral-900">Settings</h1>
        <p className="text-neutral-500 mt-1">Manage your account preferences and security</p>
      </div>

      {/* User Info Card */}
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center">
              <span className="text-2xl font-bold text-primary-700">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </span>
            </div>
            <div className="flex-1">
              <h2 className="text-lg font-semibold text-neutral-900">
                {user?.firstName} {user?.lastName}
              </h2>
              <p className="text-sm text-neutral-500">{user?.email}</p>
              <div className="flex items-center gap-2 mt-2">
                <Badge variant="info">{user?.role.replace('_', ' ')}</Badge>
                <Badge variant={user?.status === 'ACTIVE' ? 'success' : 'neutral'}>
                  {user?.status?.toLowerCase().replace('_', ' ') || 'Active'}
                </Badge>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Messages */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg flex items-center gap-2 text-red-700" role="alert">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}
      {success && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg flex items-center gap-2 text-green-700" role="status">
          <CheckCircle className="w-5 h-5 flex-shrink-0" />
          <span>{success}</span>
        </div>
      )}

      {/* Tabs */}
      <div className="border-b border-neutral-200">
        <nav className="flex gap-8" aria-label="Settings tabs">
          {[
            { id: 'account', label: 'Account', icon: User },
            { id: 'notifications', label: 'Notifications', icon: Bell },
            { id: 'security', label: 'Security', icon: Shield },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as 'account' | 'notifications' | 'security')}
              className={`flex items-center gap-2 py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                activeTab === tab.id
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-neutral-500 hover:text-neutral-700'
              }`}
            >
              <tab.icon className="w-4 h-4" />
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Account Tab */}
      {activeTab === 'account' && (
        <Card>
          <CardHeader>
            <CardTitle>Account Information</CardTitle>
            <CardDescription>Update your personal details</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleAccountSave} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="First Name"
                  value={accountData.firstName}
                  onChange={(e) => setAccountData(prev => ({ ...prev, firstName: e.target.value }))}
                  required
                />
                <Input
                  label="Last Name"
                  value={accountData.lastName}
                  onChange={(e) => setAccountData(prev => ({ ...prev, lastName: e.target.value }))}
                  required
                />
              </div>
              <Input
                label="Email Address"
                type="email"
                value={accountData.email}
                onChange={(e) => setAccountData(prev => ({ ...prev, email: e.target.value }))}
                disabled
                helperText="Email cannot be changed. Contact support if you need to update it."
              />
              <Input
                label="Phone Number"
                type="tel"
                value={accountData.phone}
                onChange={(e) => setAccountData(prev => ({ ...prev, phone: e.target.value }))}
                placeholder="+1 234 567 8900"
              />
              <div className="flex justify-end pt-4 border-t border-neutral-200">
                <Button type="submit" loading={saving}>Save Changes</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {/* Notifications Tab */}
      {activeTab === 'notifications' && (
        <Card>
          <CardHeader>
            <CardTitle>Notification Preferences</CardTitle>
            <CardDescription>Choose how you want to be notified</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleNotificationSave} className="space-y-6">
              <div>
                <h3 className="text-sm font-medium text-neutral-700 mb-3">Order Notifications</h3>
                <div className="space-y-3">
                  <label className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-neutral-900">New Orders</p>
                      <p className="text-sm text-neutral-500">Get notified when a new order is placed</p>
                    </div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={notificationPrefs.emailOrders}
                        onChange={(e) => setNotificationPrefs(prev => ({ ...prev, emailOrders: e.target.checked }))}
                        className="w-5 h-5 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">Email</span>
                    </label>
                  </label>
                  <label className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-neutral-900">Push Notifications</p>
                      <p className="text-sm text-neutral-500">Receive push notifications for orders</p>
                    </div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={notificationPrefs.pushOrders}
                        onChange={(e) => setNotificationPrefs(prev => ({ ...prev, pushOrders: e.target.checked }))}
                        className="w-5 h-5 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">Push</span>
                    </label>
                  </label>
                  <label className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-neutral-900">SMS Alerts</p>
                      <p className="text-sm text-neutral-500">Receive SMS for urgent order updates</p>
                    </div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={notificationPrefs.smsOrders}
                        onChange={(e) => setNotificationPrefs(prev => ({ ...prev, smsOrders: e.target.checked }))}
                        className="w-5 h-5 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">SMS</span>
                    </label>
                  </label>
                </div>
              </div>

              <div className="border-t border-neutral-200 pt-6">
                <h3 className="text-sm font-medium text-neutral-700 mb-3">Promotional Notifications</h3>
                <div className="space-y-3">
                  <label className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-neutral-900">Email Promotions</p>
                      <p className="text-sm text-neutral-500">Receive marketing emails and special offers</p>
                    </div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={notificationPrefs.emailPromotions}
                        onChange={(e) => setNotificationPrefs(prev => ({ ...prev, emailPromotions: e.target.checked }))}
                        className="w-5 h-5 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">Email</span>
                    </label>
                  </label>
                  <label className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-neutral-900">Push Promotions</p>
                      <p className="text-sm text-neutral-500">Receive push notifications for promotions</p>
                    </div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={notificationPrefs.pushPromotions}
                        onChange={(e) => setNotificationPrefs(prev => ({ ...prev, pushPromotions: e.target.checked }))}
                        className="w-5 h-5 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">Push</span>
                    </label>
                  </label>
                </div>
              </div>

              <div className="flex justify-end pt-4 border-t border-neutral-200">
                <Button type="submit" loading={saving}>Save Preferences</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {/* Security Tab */}
      {activeTab === 'security' && (
        <Card>
          <CardHeader>
            <CardTitle>Security</CardTitle>
            <CardDescription>Change your password and manage sessions</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handlePasswordChange} className="space-y-4">
              <Input
                label="Current Password"
                type="password"
                value={passwordData.currentPassword}
                onChange={(e) => setPasswordData(prev => ({ ...prev, currentPassword: e.target.value }))}
                required
                autoComplete="current-password"
              />
              <Input
                label="New Password"
                type="password"
                value={passwordData.newPassword}
                onChange={(e) => setPasswordData(prev => ({ ...prev, newPassword: e.target.value }))}
                required
                autoComplete="new-password"
                helperText="Must be at least 8 characters"
              />
              <Input
                label="Confirm New Password"
                type="password"
                value={passwordData.confirmPassword}
                onChange={(e) => setPasswordData(prev => ({ ...prev, confirmPassword: e.target.value }))}
                required
                autoComplete="new-password"
              />
              <div className="flex justify-end pt-4 border-t border-neutral-200">
                <Button type="submit" loading={saving}>Change Password</Button>
              </div>
            </form>

            <div className="border-t border-neutral-200 pt-6 mt-6">
              <h3 className="text-sm font-medium text-neutral-700 mb-3">Danger Zone</h3>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-neutral-900">Sign out everywhere</p>
                  <p className="text-sm text-neutral-500">This will revoke all active sessions</p>
                </div>
                <Button variant="danger" onClick={handleLogout}>
                  <LogOut className="w-4 h-4 mr-2" /> Sign Out Everywhere
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}