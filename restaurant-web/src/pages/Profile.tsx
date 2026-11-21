import { useState, useEffect, type FormEvent } from 'react';
import { MapPin, Clock, Save, AlertCircle, CheckCircle } from 'lucide-react';
import { useRestaurants, useRestaurant } from '@/hooks/useRestaurant';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input, Textarea } from '@/components/ui/Input';
import { RestaurantStatusBadge } from '@/components/ui/Badge';
import type { RestaurantHours, DayOfWeek } from '@/types/api';

const DAYS_OF_WEEK: { value: DayOfWeek; label: string }[] = [
  { value: 'MONDAY', label: 'Monday' },
  { value: 'TUESDAY', label: 'Tuesday' },
  { value: 'WEDNESDAY', label: 'Wednesday' },
  { value: 'THURSDAY', label: 'Thursday' },
  { value: 'FRIDAY', label: 'Friday' },
  { value: 'SATURDAY', label: 'Saturday' },
  { value: 'SUNDAY', label: 'Sunday' },
];

export function Profile() {
  const { restaurants } = useRestaurants();
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<string>('');
  const { restaurant, loading: restaurantLoading, error: restaurantError, updateRestaurant, updateHours } = useRestaurant(selectedRestaurantId || null);
  const [hours, setHours] = useState<RestaurantHours[]>([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'info' | 'hours'>('info');

  // Auto-select first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurantId) {
      setSelectedRestaurantId(restaurants[0].id);
    }
  }, [restaurants, selectedRestaurantId]);

  // Load hours when restaurant changes
  useEffect(() => {
    if (restaurant?.hours) {
      // Ensure all days are present
      const allHours = DAYS_OF_WEEK.map(day => {
        const existing = restaurant.hours?.find((h: RestaurantHours) => h.dayOfWeek === day.value);
        return existing || { id: '', restaurantId: restaurant.id, dayOfWeek: day.value, openTime: null, closeTime: null, isClosed: true, createdAt: '', updatedAt: '' };
      });
      setHours(allHours);
    }
  }, [restaurant]);

  const handleInfoSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!restaurant) return;

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const data = {
        name: formData.name,
        description: formData.description || undefined,
        phone: formData.phone,
        email: formData.email || undefined,
        cuisineType: formData.cuisineType || undefined,
        deliveryRadiusKm: formData.deliveryRadiusKm,
        minimumOrderAmount: formData.minimumOrderAmount,
        deliveryFee: formData.deliveryFee,
        preparationTimeMinutes: parseInt(formData.preparationTimeMinutes) || 30,
      };

      await updateRestaurant(data);
      setSuccess('Restaurant information updated successfully');
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to update restaurant';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const handleHoursSubmit = async () => {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await updateHours(hours);
      setSuccess('Opening hours updated successfully');
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to update hours';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    phone: '',
    email: '',
    cuisineType: '',
    deliveryRadiusKm: '5.00',
    minimumOrderAmount: '0.00',
    deliveryFee: '0.00',
    preparationTimeMinutes: '30',
  });

  // Update form data when restaurant loads
  useEffect(() => {
    if (restaurant) {
      setFormData({
        name: restaurant.name,
        description: restaurant.description || '',
        phone: restaurant.phone,
        email: restaurant.email || '',
        cuisineType: restaurant.cuisineType || '',
        deliveryRadiusKm: restaurant.deliveryRadiusKm,
        minimumOrderAmount: restaurant.minimumOrderAmount,
        deliveryFee: restaurant.deliveryFee,
        preparationTimeMinutes: restaurant.preparationTimeMinutes.toString(),
      });
    }
  }, [restaurant]);

  const handleHourChange = (dayOfWeek: DayOfWeek, field: 'openTime' | 'closeTime' | 'isClosed', value: string | boolean) => {
    setHours(prev => prev.map(h =>
      h.dayOfWeek === dayOfWeek ? { ...h, [field]: value } : h
    ));
  };

  const formatTime = (time?: string | null) => {
    if (!time) return '';
    return time.slice(0, 5);
  };

  if (restaurantLoading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="h-8 bg-neutral-200 rounded w-1/4" />
        <div className="bg-white rounded-xl border border-neutral-200 p-6">
          <div className="space-y-4">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="h-12 bg-neutral-200 rounded" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (restaurantError || !restaurant) {
    return (
      <div className="text-center py-12">
        <AlertCircle className="mx-auto h-12 w-12 text-red-500" />
        <p className="mt-4 text-neutral-600">Failed to load restaurant profile</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Restaurant Profile</h1>
          <p className="text-neutral-500 mt-1">Manage your restaurant information and settings</p>
        </div>
        <div className="flex items-center gap-4 flex-wrap">
          <select
            value={selectedRestaurantId}
            onChange={(e) => setSelectedRestaurantId(e.target.value)}
            className="px-4 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white"
            disabled={restaurants.length <= 1}
          >
            {restaurants.map((r) => (
              <option key={r.id} value={r.id}>{r.name}</option>
            ))}
          </select>
          <RestaurantStatusBadge status={restaurant.status} />
        </div>
      </div>

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
        <nav className="flex gap-8" aria-label="Profile tabs">
          <button
            onClick={() => setActiveTab('info')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeTab === 'info'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-neutral-500 hover:text-neutral-700'
            }`}
          >
            Restaurant Info
          </button>
          <button
            onClick={() => setActiveTab('hours')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeTab === 'hours'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-neutral-500 hover:text-neutral-700'
            }`}
          >
            Opening Hours
          </button>
        </nav>
      </div>

      {/* Restaurant Info Tab */}
      {activeTab === 'info' && (
        <Card>
          <CardHeader>
            <CardTitle>Basic Information</CardTitle>
            <CardDescription>Update your restaurant's public-facing details</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleInfoSubmit} className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <Input
                  label="Restaurant Name *"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  placeholder="Enter restaurant name"
                  required
                />
                <Input
                  label="Phone *"
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData(prev => ({ ...prev, phone: e.target.value }))}
                  placeholder="+1 234 567 8900"
                  required
                />
                <Input
                  label="Email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData(prev => ({ ...prev, email: e.target.value }))}
                  placeholder="restaurant@example.com"
                />
                <Input
                  label="Cuisine Type"
                  value={formData.cuisineType}
                  onChange={(e) => setFormData(prev => ({ ...prev, cuisineType: e.target.value }))}
                  placeholder="Italian, Mexican, etc."
                />
              </div>

              <Textarea
                label="Description"
                value={formData.description}
                onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                placeholder="Describe your restaurant..."
                rows={4}
              />

              <div className="border-t border-neutral-200 pt-6">
                <h3 className="text-lg font-medium text-neutral-900 mb-4">Delivery & Operations</h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                  <Input
                    label="Delivery Radius (km)"
                    type="number"
                    step="0.5"
                    min="0"
                    value={formData.deliveryRadiusKm}
                    onChange={(e) => setFormData(prev => ({ ...prev, deliveryRadiusKm: e.target.value }))}
                  />
                  <Input
                    label="Minimum Order Amount"
                    type="number"
                    step="0.01"
                    min="0"
                    value={formData.minimumOrderAmount}
                    onChange={(e) => setFormData(prev => ({ ...prev, minimumOrderAmount: e.target.value }))}
                  />
                  <Input
                    label="Delivery Fee"
                    type="number"
                    step="0.01"
                    min="0"
                    value={formData.deliveryFee}
                    onChange={(e) => setFormData(prev => ({ ...prev, deliveryFee: e.target.value }))}
                  />
                  <Input
                    label="Preparation Time (min)"
                    type="number"
                    min="0"
                    value={formData.preparationTimeMinutes}
                    onChange={(e) => setFormData(prev => ({ ...prev, preparationTimeMinutes: e.target.value }))}
                  />
                </div>
              </div>

              <div className="flex justify-end pt-4 border-t border-neutral-200">
                <Button type="submit" loading={saving}>
                  <Save className="w-4 h-4 mr-2" /> Save Changes
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {/* Opening Hours Tab */}
      {activeTab === 'hours' && (
        <Card>
          <CardHeader>
            <CardTitle>Opening Hours</CardTitle>
            <CardDescription>Set your restaurant's operating hours for each day of the week</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {hours.map((hour) => {
                const dayLabel = DAYS_OF_WEEK.find(d => d.value === hour.dayOfWeek)?.label || hour.dayOfWeek;
                return (
                  <div key={hour.dayOfWeek} className="flex flex-col sm:flex-row sm:items-center gap-4 p-4 bg-neutral-50 rounded-lg">
                    <div className="w-32 sm:w-24 font-medium text-neutral-900">{dayLabel}</div>
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={hour.isClosed}
                        onChange={(e) => handleHourChange(hour.dayOfWeek, 'isClosed', e.target.checked)}
                        className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-neutral-700">Closed</span>
                    </label>
                    {!hour.isClosed && (
                      <div className="flex items-center gap-2 flex-1 sm:flex-none">
                        <div className="flex items-center gap-2">
                          <MapPin className="w-4 h-4 text-neutral-400" />
                          <Input
                            label="Open"
                            type="time"
                            value={formatTime(hour.openTime)}
                            onChange={(e) => handleHourChange(hour.dayOfWeek, 'openTime', e.target.value)}
                            className="w-32"
                          />
                        </div>
                        <span className="text-neutral-400">-</span>
                        <div className="flex items-center gap-2">
                          <Clock className="w-4 h-4 text-neutral-400" />
                          <Input
                            label="Close"
                            type="time"
                            value={formatTime(hour.closeTime)}
                            onChange={(e) => handleHourChange(hour.dayOfWeek, 'closeTime', e.target.value)}
                            className="w-32"
                          />
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            <div className="flex justify-end pt-4 border-t border-neutral-200">
              <Button onClick={handleHoursSubmit} loading={saving}>
                <Save className="w-4 h-4 mr-2" /> Save Hours
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}