-- Flyway Migration V1: Initial Schema
-- Food Delivery Platform Database Schema

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- Note: Enum types are handled by JPA AttributeConverters using VARCHAR columns
-- This avoids issues with PostgreSQL enum types and Hibernate mapping

-- ============================================
-- USERS & AUTHENTICATION
-- ============================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMPTZ,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    preferences JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role_status ON users(role, status);
CREATE INDEX idx_users_email_verification_token ON users(email_verification_token) WHERE email_verification_token IS NOT NULL;
CREATE INDEX idx_users_password_reset_token ON users(password_reset_token) WHERE password_reset_token IS NOT NULL;

-- Customer Profile
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    default_address_id UUID,
    preferences JSONB NOT NULL DEFAULT '{}',
    loyalty_points INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Delivery Profile
CREATE TABLE delivery_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(20) NOT NULL,
    vehicle_plate VARCHAR(20),
    vehicle_color VARCHAR(30),
    license_number VARCHAR(50) NOT NULL,
    license_expiry DATE NOT NULL,
    id_document_url VARCHAR(500),
    background_check_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_available BOOLEAN NOT NULL DEFAULT FALSE,
    current_latitude DECIMAL(10, 8),
    current_longitude DECIMAL(11, 8),
    last_location_update TIMESTAMPTZ,
    average_rating DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    total_deliveries INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_delivery_profiles_available ON delivery_profiles(is_available) WHERE is_available = TRUE;
CREATE INDEX idx_delivery_profiles_location ON delivery_profiles(current_latitude, current_longitude)
    WHERE current_latitude IS NOT NULL AND current_longitude IS NOT NULL;

-- Restaurant Owner Profile
CREATE TABLE restaurant_owner_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(200) NOT NULL,
    tax_id VARCHAR(50) NOT NULL,
    bank_account_iban VARCHAR(34),
    bank_account_name VARCHAR(200),
    commission_rate DECIMAL(5, 2) NOT NULL DEFAULT 15.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX idx_restaurant_owner_tax_id ON restaurant_owner_profiles(tax_id);

-- ============================================
-- ADDRESSES
-- ============================================

CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label VARCHAR(50) NOT NULL,
    street VARCHAR(200) NOT NULL,
    building VARCHAR(50),
    floor VARCHAR(20),
    apartment VARCHAR(20),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(2) NOT NULL DEFAULT 'TN',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    instructions TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    address_type VARCHAR(20) NOT NULL DEFAULT 'HOME',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_user_default ON addresses(user_id, is_default) WHERE is_default = TRUE;
CREATE INDEX idx_addresses_coordinates ON addresses(latitude, longitude)
    WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Add FK for customer_profiles.default_address_id
ALTER TABLE customer_profiles
    ADD CONSTRAINT fk_customer_profile_default_address
    FOREIGN KEY (default_address_id) REFERENCES addresses(id) ON DELETE SET NULL;

-- ============================================
-- RESTAURANTS
-- ============================================

CREATE TABLE restaurants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL UNIQUE,
    description TEXT,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    logo_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    cuisine_type VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    delivery_radius_km DECIMAL(5, 2) NOT NULL DEFAULT 5.00,
    minimum_order_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    preparation_time_minutes INTEGER NOT NULL DEFAULT 30,
    average_rating DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    is_open_now BOOLEAN NOT NULL DEFAULT FALSE,
    rejection_reason TEXT,
    approved_at TIMESTAMPTZ,
    approved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_restaurants_owner_id ON restaurants(owner_id);
CREATE INDEX idx_restaurants_status ON restaurants(status);
CREATE INDEX idx_restaurants_status_open ON restaurants(status, is_open_now) WHERE status = 'APPROVED';
CREATE INDEX idx_restaurants_cuisine ON restaurants(cuisine_type);
CREATE INDEX idx_restaurants_location ON restaurants(address_id);

-- Restaurant Hours
CREATE TABLE restaurant_hours (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) NOT NULL,
    open_time TIME,
    close_time TIME,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (restaurant_id, day_of_week)
);

CREATE INDEX idx_restaurant_hours_restaurant ON restaurant_hours(restaurant_id);

-- Restaurant Applications
CREATE TABLE restaurant_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    proposed_name VARCHAR(200) NOT NULL,
    proposed_cuisine VARCHAR(100),
    proposed_address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    business_license_url VARCHAR(500) NOT NULL,
    tax_id_document_url VARCHAR(500) NOT NULL,
    owner_id_document_url VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    review_notes TEXT,
    reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    reviewed_at TIMESTAMPTZ,
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_restaurant_applications_owner ON restaurant_applications(owner_id);
CREATE INDEX idx_restaurant_applications_status ON restaurant_applications(status);

-- ============================================
-- CATEGORIES & PRODUCTS
-- ============================================

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_categories_restaurant ON categories(restaurant_id, display_order);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    image_url VARCHAR(500),
    additional_images JSONB,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_vegetarian BOOLEAN NOT NULL DEFAULT FALSE,
    is_vegan BOOLEAN NOT NULL DEFAULT FALSE,
    is_gluten_free BOOLEAN NOT NULL DEFAULT FALSE,
    allergens TEXT,
    tags JSONB,
    preparation_time_minutes INTEGER,
    display_order INTEGER NOT NULL DEFAULT 0,
    average_rating DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_products_restaurant ON products(restaurant_id, is_available, display_order);
CREATE INDEX idx_products_category ON products(category_id, is_available, display_order);
CREATE INDEX idx_products_search ON products USING GIN (to_tsvector('simple', name || ' ' || COALESCE(description, '')));

-- ============================================
-- ORDERS
-- ============================================

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id) ON DELETE RESTRICT,
    address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (tax_amount >= 0),
    delivery_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (delivery_fee >= 0),
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (discount_amount >= 0),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    customer_notes TEXT,
    restaurant_notes TEXT,
    estimated_preparation_minutes INTEGER,
    estimated_delivery_minutes INTEGER,
    confirmed_at TIMESTAMPTZ,
    preparing_at TIMESTAMPTZ,
    ready_at TIMESTAMPTZ,
    picked_up_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT,
    cancelled_by UUID REFERENCES users(id) ON DELETE SET NULL,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_order_totals CHECK (
        subtotal + tax_amount + delivery_fee - discount_amount = total_amount
    )
);

CREATE INDEX idx_orders_customer ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_restaurant ON orders(restaurant_id, status, created_at DESC);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_active ON orders(status, created_at)
    WHERE status IN ('PENDING', 'ACCEPTED', 'PREPARING', 'READY', 'ASSIGNED', 'PICKED_UP', 'DELIVERING');
CREATE INDEX idx_orders_idempotency ON orders(idempotency_key);

-- Order Items
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit_price_snapshot DECIMAL(10, 2) NOT NULL CHECK (unit_price_snapshot >= 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    notes TEXT,
    line_total DECIMAL(10, 2) NOT NULL CHECK (line_total >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_line_total CHECK (unit_price_snapshot * quantity = line_total)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- Order Status History
CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    changed_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    change_reason TEXT,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id, created_at);

-- ============================================
-- DELIVERIES
-- ============================================

CREATE TABLE deliveries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    delivery_person_id UUID REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_ASSIGNMENT',
    pickup_address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    delivery_address_id UUID NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    pickup_latitude DECIMAL(10, 8),
    pickup_longitude DECIMAL(11, 8),
    delivery_latitude DECIMAL(10, 8),
    delivery_longitude DECIMAL(11, 8),
    pickup_code VARCHAR(6) NOT NULL,
    delivery_code VARCHAR(6) NOT NULL,
    pickup_photo_url VARCHAR(500),
    delivery_photo_url VARCHAR(500),
    customer_signature_url VARCHAR(500),
    distance_km DECIMAL(6, 2),
    estimated_duration_minutes INTEGER,
    actual_duration_minutes INTEGER,
    assigned_at TIMESTAMPTZ,
    accepted_at TIMESTAMPTZ,
    picked_up_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_deliveries_delivery_person ON deliveries(delivery_person_id, status, created_at DESC);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_assigned ON deliveries(assigned_at) WHERE assigned_at IS NOT NULL;

-- Delivery Location Updates
CREATE TABLE delivery_location_updates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    delivery_id UUID NOT NULL REFERENCES deliveries(id) ON DELETE CASCADE,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    accuracy DECIMAL(6, 2),
    speed DECIMAL(6, 2),
    heading DECIMAL(5, 2),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_delivery_location_updates_delivery ON delivery_location_updates(delivery_id, recorded_at DESC);
-- Partial index with NOW() removed - use a maintenance job or materialized view instead
-- CREATE INDEX idx_delivery_location_updates_recent ON delivery_location_updates(recorded_at DESC)
--     WHERE recorded_at > NOW() - INTERVAL '24 hours';

-- ============================================
-- PAYMENTS
-- ============================================

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(100),
    gateway_response JSONB,
    paid_at TIMESTAMPTZ,
    refunded_at TIMESTAMPTZ,
    refund_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction_ref ON payments(transaction_reference) WHERE transaction_reference IS NOT NULL;

-- ============================================
-- NOTIFICATIONS
-- ============================================

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT,
    data JSONB,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    sent_via_push BOOLEAN NOT NULL DEFAULT FALSE,
    sent_via_email BOOLEAN NOT NULL DEFAULT FALSE,
    sent_via_sms BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notifications_user ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications(user_id, created_at DESC) WHERE is_read = FALSE;

-- ============================================
-- REVIEWS
-- ============================================

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_type VARCHAR(20) NOT NULL,
    target_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    ratings_breakdown JSONB,
    response TEXT,
    responded_at TIMESTAMPTZ,
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    flag_reason TEXT,
    moderation_status VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    helpful_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_review_per_order_target UNIQUE (order_id, customer_id, target_type, target_id)
);

CREATE INDEX idx_reviews_target ON reviews(target_type, target_id, moderation_status, created_at DESC);
CREATE INDEX idx_reviews_customer ON reviews(customer_id, created_at DESC);
CREATE INDEX idx_reviews_order ON reviews(order_id);