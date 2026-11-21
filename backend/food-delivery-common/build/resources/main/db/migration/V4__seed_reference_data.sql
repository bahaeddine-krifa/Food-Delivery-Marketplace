-- Flyway Migration V4: Seed Reference Data
-- Development/Test data only - NEVER use in production

-- This migration is only for development profile
-- In production, use a separate seed script or manual setup

-- ============================================
-- DEVELOPMENT TEST ACCOUNTS
-- Password for all accounts: "Password123!"
-- BCrypt hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S
-- ============================================

-- Only insert if not exists (idempotent)
DO $$
DECLARE
    admin_id UUID;
    customer_id UUID;
    delivery_id UUID;
    owner_id UUID;
    password_hash TEXT := '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PZvO.S';
BEGIN
    -- Admin user
    INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, email_verified, created_at, updated_at)
    VALUES (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::UUID,
        'admin@fooddelivery.test',
        password_hash,
        'Admin',
        'User',
        '+21600000000',
        'ADMIN',
        'ACTIVE',
        TRUE,
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO NOTHING
    RETURNING id INTO admin_id;

    -- Customer user
    INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, email_verified, created_at, updated_at)
    VALUES (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::UUID,
        'customer@fooddelivery.test',
        password_hash,
        'John',
        'Customer',
        '+21611111111',
        'CUSTOMER',
        'ACTIVE',
        TRUE,
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO NOTHING
    RETURNING id INTO customer_id;

    -- Delivery person user
    INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, email_verified, created_at, updated_at)
    VALUES (
        'cccccccc-cccc-cccc-cccc-cccccccccccc'::UUID,
        'delivery@fooddelivery.test',
        password_hash,
        'Ahmed',
        'Delivery',
        '+21622222222',
        'DELIVERY_PERSON',
        'ACTIVE',
        TRUE,
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO NOTHING
    RETURNING id INTO delivery_id;

    -- Restaurant owner user
    INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, email_verified, created_at, updated_at)
    VALUES (
        'dddddddd-dddd-dddd-dddd-dddddddddddd'::UUID,
        'owner@fooddelivery.test',
        password_hash,
        'Maria',
        'Owner',
        '+21633333333',
        'RESTAURANT_OWNER',
        'ACTIVE',
        TRUE,
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO NOTHING
    RETURNING id INTO owner_id;

    -- Create profiles (triggers should handle this, but ensure they exist)
    INSERT INTO customer_profiles (user_id) VALUES (customer_id) ON CONFLICT DO NOTHING;
    INSERT INTO delivery_profiles (user_id, vehicle_type, license_number, license_expiry, background_check_status)
    VALUES (delivery_id, 'MOTORCYCLE', 'DL123456', CURRENT_DATE + INTERVAL '2 years', 'APPROVED') ON CONFLICT DO NOTHING;
    INSERT INTO restaurant_owner_profiles (user_id, business_name, tax_id, commission_rate)
    VALUES (owner_id, 'Maria Restaurant Group', 'TAX123456', 15.00) ON CONFLICT DO NOTHING;

    -- Create test addresses
    INSERT INTO addresses (id, user_id, label, street, city, state, postal_code, country, latitude, longitude, is_default, address_type)
    VALUES
    (
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'::UUID,
        customer_id,
        'Home',
        '123 Main Street',
        'Tunis',
        'Tunis',
        '1000',
        'TN',
        36.8065,
        10.1815,
        TRUE,
        'HOME'
    ) ON CONFLICT DO NOTHING;

    INSERT INTO addresses (id, user_id, label, street, city, state, postal_code, country, latitude, longitude, is_default, address_type)
    VALUES
    (
        'ffffffff-ffff-ffff-ffff-ffffffffffff'::UUID,
        owner_id,
        'Restaurant',
        '456 Avenue Habib Bourguiba',
        'Tunis',
        'Tunis',
        '1000',
        'TN',
        36.8010,
        10.1795,
        TRUE,
        'RESTAURANT'
    ) ON CONFLICT DO NOTHING;

    -- Update customer profile with default address
    UPDATE customer_profiles SET default_address_id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'::UUID WHERE user_id = customer_id;

    -- Create test restaurant
    INSERT INTO restaurants (id, owner_id, name, slug, description, phone, email, cuisine_type, status, address_id, delivery_radius_km, minimum_order_amount, delivery_fee, preparation_time_minutes, approved_at, approved_by)
    VALUES (
        '11111111-1111-1111-1111-111111111111'::UUID,
        owner_id,
        'Maria Italian Kitchen',
        'maria-italian-kitchen',
        'Authentic Italian cuisine with fresh ingredients',
        '+21633333333',
        'maria@italiankitchen.test',
        'Italian',
        'APPROVED',
        'ffffffff-ffff-ffff-ffff-ffffffffffff'::UUID,
        5.00,
        15.00,
        3.00,
        30,
        NOW(),
        admin_id
    ) ON CONFLICT (slug) DO NOTHING;

    -- Create restaurant hours
    INSERT INTO restaurant_hours (restaurant_id, day_of_week, open_time, close_time, is_closed)
    VALUES
    ('11111111-1111-1111-1111-111111111111'::UUID, 'MONDAY', '11:00', '23:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'TUESDAY', '11:00', '23:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'WEDNESDAY', '11:00', '23:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'THURSDAY', '11:00', '23:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'FRIDAY', '11:00', '00:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'SATURDAY', '11:00', '00:00', FALSE),
    ('11111111-1111-1111-1111-111111111111'::UUID, 'SUNDAY', '12:00', '22:00', FALSE)
    ON CONFLICT (restaurant_id, day_of_week) DO NOTHING;

    -- Create categories
    INSERT INTO categories (id, restaurant_id, name, description, display_order, is_active)
    VALUES
    ('22222222-2222-2222-2222-222222222222'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, 'Pizzas', 'Traditional wood-fired pizzas', 1, TRUE),
    ('33333333-3333-3333-3333-333333333333'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, 'Pasta', 'Fresh homemade pasta dishes', 2, TRUE),
    ('44444444-4444-4444-4444-444444444444'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, 'Salads', 'Fresh salads and appetizers', 3, TRUE),
    ('55555555-5555-5555-5555-555555555555'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, 'Desserts', 'Italian desserts and sweets', 4, TRUE),
    ('66666666-6666-6666-6666-666666666666'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, 'Drinks', 'Beverages and soft drinks', 5, TRUE)
    ON CONFLICT DO NOTHING;

    -- Create products
    INSERT INTO products (id, restaurant_id, category_id, name, description, price, is_available, is_vegetarian, is_vegan, is_gluten_free, allergens, preparation_time_minutes, display_order)
    VALUES
    -- Pizzas
    ('77777777-7777-7777-7777-777777777777'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '22222222-2222-2222-2222-222222222222'::UUID,
     'Margherita', 'San Marzano tomatoes, fresh mozzarella, basil, olive oil', 18.50, TRUE, TRUE, FALSE, FALSE, 'Gluten, Dairy', 15, 1),
    ('77777777-7777-7777-7777-777777777778'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '22222222-2222-2222-2222-222222222222'::UUID,
     'Pepperoni', 'San Marzano tomatoes, mozzarella, pepperoni, oregano', 22.00, TRUE, FALSE, FALSE, FALSE, 'Gluten, Dairy', 15, 2),
    ('77777777-7777-7777-7777-777777777779'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '22222222-2222-2222-2222-222222222222'::UUID,
     'Quattro Formaggi', 'Four cheese blend: mozzarella, gorgonzola, parmesan, goat cheese', 24.00, TRUE, TRUE, FALSE, FALSE, 'Gluten, Dairy', 15, 3),
    ('77777777-7777-7777-7777-777777777780'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '22222222-2222-2222-2222-222222222222'::UUID,
     'Vegetariana', 'Grilled vegetables, mozzarella, tomato sauce, basil', 20.00, TRUE, TRUE, FALSE, FALSE, 'Gluten, Dairy', 15, 4),

    -- Pasta
    ('88888888-8888-8888-8888-888888888888'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '33333333-3333-3333-3333-333333333333'::UUID,
     'Spaghetti Carbonara', 'Eggs, pecorino cheese, guanciale, black pepper', 19.50, TRUE, FALSE, FALSE, FALSE, 'Gluten, Dairy, Eggs', 12, 1),
    ('88888888-8888-8888-8888-888888888889'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '33333333-3333-3333-3333-333333333333'::UUID,
     'Penne Arrabbiata', 'Tomato sauce, garlic, chili, parsley', 16.00, TRUE, TRUE, TRUE, FALSE, 'Gluten', 10, 2),
    ('88888888-8888-8888-8888-888888888890'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '33333333-3333-3333-3333-333333333333'::UUID,
     'Lasagna Bolognese', 'Layers of pasta, meat ragù, béchamel, parmesan', 21.00, TRUE, FALSE, FALSE, FALSE, 'Gluten, Dairy', 20, 3),

    -- Salads
    ('99999999-9999-9999-9999-999999999999'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '44444444-4444-4444-4444-444444444444'::UUID,
     'Caesar Salad', 'Romaine, parmesan, croutons, Caesar dressing', 12.50, TRUE, FALSE, FALSE, FALSE, 'Dairy, Eggs, Gluten', 5, 1),
    ('99999999-9999-9999-9999-999999999998'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '44444444-4444-4444-4444-444444444444'::UUID,
     'Caprese Salad', 'Fresh mozzarella, tomatoes, basil, balsamic glaze', 14.00, TRUE, TRUE, FALSE, TRUE, 'Dairy', 5, 2),

    -- Desserts
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '55555555-5555-5555-5555-555555555555'::UUID,
     'Tiramisu', 'Classic Italian dessert with mascarpone, coffee, cocoa', 8.50, TRUE, TRUE, FALSE, FALSE, 'Dairy, Eggs, Gluten', 5, 1),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '55555555-5555-5555-5555-555555555555'::UUID,
     'Panna Cotta', 'Vanilla cream with berry compote', 7.00, TRUE, TRUE, FALSE, TRUE, 'Dairy', 5, 2),

    -- Drinks
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '66666666-6666-6666-6666-666666666666'::UUID,
     'Coca Cola', '330ml', 2.50, TRUE, TRUE, TRUE, TRUE, '', 1, 1),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbc'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '66666666-6666-6666-6666-666666666666'::UUID,
     'Sparkling Water', '500ml', 1.50, TRUE, TRUE, TRUE, TRUE, '', 1, 2),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbd'::UUID, '11111111-1111-1111-1111-111111111111'::UUID, '66666666-6666-6666-6666-666666666666'::UUID,
     'Espresso', 'Single shot', 2.00, TRUE, TRUE, TRUE, TRUE, '', 2, 3)
    ON CONFLICT DO NOTHING;

END $$;