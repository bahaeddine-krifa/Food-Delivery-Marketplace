import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '@/context/AuthContext';
import { restaurantApi } from '@/lib/restaurantApi';
import type { Restaurant, RestaurantHours, UpdateRestaurantRequest } from '@/types/api';

export function useRestaurants() {
  const { user } = useAuth();
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRestaurants = useCallback(async () => {
    if (!user) return;
    try {
      setLoading(true);
      const data = await restaurantApi.getMyRestaurants();
      setRestaurants(data);
      setError(null);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load restaurants';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    fetchRestaurants();
  }, [fetchRestaurants]);

  return { restaurants, loading, error, refetch: fetchRestaurants };
}

export function useRestaurant(restaurantId: string | null) {
  const { user } = useAuth();
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRestaurant = useCallback(async () => {
    if (!restaurantId || !user) return;
    try {
      setLoading(true);
      const data = await restaurantApi.getRestaurant(restaurantId);
      setRestaurant(data);
      setError(null);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load restaurant';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [restaurantId, user]);

  useEffect(() => {
    fetchRestaurant();
  }, [fetchRestaurant]);

  const updateRestaurant = async (data: UpdateRestaurantRequest): Promise<Restaurant> => {
    if (!restaurantId) throw new Error('No restaurant ID');
    const updated = await restaurantApi.updateRestaurant(restaurantId, data);
    setRestaurant(updated);
    return updated;
  };

  const updateHours = async (hours: RestaurantHours[]): Promise<RestaurantHours[]> => {
    if (!restaurantId) throw new Error('No restaurant ID');
    const formattedHours = hours.map(h => ({
      dayOfWeek: h.dayOfWeek,
      openTime: h.openTime || undefined,
      closeTime: h.closeTime || undefined,
      isClosed: h.isClosed,
    }));
    return restaurantApi.updateHours(restaurantId, formattedHours);
  };

  return { restaurant, loading, error, refetch: fetchRestaurant, updateRestaurant, updateHours };
}