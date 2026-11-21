-- Flyway Migration V2: Additional Indexes
-- Performance optimization indexes for Food Delivery Platform

-- Additional indexes for users table
CREATE INDEX IF NOT EXISTS idx_users_status_created ON users(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone) WHERE phone IS NOT NULL;

-- Additional indexes for delivery_profiles
CREATE INDEX IF NOT EXISTS idx_delivery_profiles_rating ON delivery_profiles(average_rating DESC) WHERE average_rating > 0;
CREATE INDEX IF NOT EXISTS idx_delivery_profiles_vehicle ON delivery_profiles(vehicle_type);

-- Additional indexes for restaurants
CREATE INDEX IF NOT EXISTS idx_restaurants_rating ON restaurants(average_rating DESC) WHERE status = 'APPROVED' AND average_rating > 0;
CREATE INDEX IF NOT EXISTS idx_restaurants_delivery_fee ON restaurants(delivery_fee) WHERE status = 'APPROVED';
CREATE INDEX IF NOT EXISTS idx_restaurants_min_order ON restaurants(minimum_order_amount) WHERE status = 'APPROVED';
CREATE INDEX IF NOT EXISTS idx_restaurants_prep_time ON restaurants(preparation_time_minutes) WHERE status = 'APPROVED';

-- Additional indexes for categories
CREATE INDEX IF NOT EXISTS idx_categories_active ON categories(restaurant_id, is_active, display_order) WHERE is_active = TRUE;

-- Additional indexes for products
CREATE INDEX IF NOT EXISTS idx_products_available ON products(restaurant_id, category_id, is_available, display_order) WHERE is_available = TRUE;
CREATE INDEX IF NOT EXISTS idx_products_dietary ON products(restaurant_id, is_vegetarian, is_vegan, is_gluten_free)
    WHERE is_vegetarian = TRUE OR is_vegan = TRUE OR is_gluten_free = TRUE;
CREATE INDEX IF NOT EXISTS idx_products_price ON products(restaurant_id, price) WHERE is_available = TRUE;

-- Additional indexes for orders
CREATE INDEX IF NOT EXISTS idx_orders_date_range ON orders(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_customer_status ON orders(customer_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant_date ON orders(restaurant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_delivered ON orders(delivered_at DESC) WHERE status = 'DELIVERED';

-- Additional indexes for order_items
CREATE INDEX IF NOT EXISTS idx_order_items_product_order ON order_items(product_id, order_id);

-- Additional indexes for order_status_history
CREATE INDEX IF NOT EXISTS idx_order_status_history_status ON order_status_history(to_status, created_at);

-- Additional indexes for deliveries
CREATE INDEX IF NOT EXISTS idx_deliveries_pickup ON deliveries(pickup_address_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_delivery ON deliveries(delivery_address_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_person_date ON deliveries(delivery_person_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_deliveries_completed ON deliveries(delivered_at DESC) WHERE status = 'DELIVERED';

-- Additional indexes for payments
CREATE INDEX IF NOT EXISTS idx_payments_method ON payments(method, status);
CREATE INDEX IF NOT EXISTS idx_payments_paid ON payments(paid_at DESC) WHERE status = 'PAID';

-- Additional indexes for notifications
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(user_id, type, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_priority ON notifications(user_id, priority, created_at DESC);

-- Additional indexes for reviews
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(target_type, target_id, rating);
CREATE INDEX IF NOT EXISTS idx_reviews_verified ON reviews(target_type, target_id, is_verified) WHERE is_verified = TRUE;

-- Partial indexes for common query patterns
-- Active restaurants with their open status
CREATE INDEX IF NOT EXISTS idx_restaurants_active_open ON restaurants(id)
    WHERE status = 'APPROVED' AND is_open_now = TRUE;

-- Pending orders for restaurant dashboard
CREATE INDEX IF NOT EXISTS idx_orders_pending_restaurant ON orders(restaurant_id, created_at)
    WHERE status = 'PENDING';

-- Active deliveries for tracking
CREATE INDEX IF NOT EXISTS idx_deliveries_active ON deliveries(delivery_person_id, status, updated_at DESC)
    WHERE status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'DELIVERING');

-- Unassigned deliveries for assignment queue
CREATE INDEX IF NOT EXISTS idx_deliveries_unassigned ON deliveries(created_at)
    WHERE status = 'PENDING_ASSIGNMENT';