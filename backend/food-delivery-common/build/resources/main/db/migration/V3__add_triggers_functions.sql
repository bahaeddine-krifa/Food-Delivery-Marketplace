-- Flyway Migration V3: Triggers and Functions
-- Food Delivery Platform Database Functions

-- ============================================
-- UPDATE_TIMESTAMP TRIGGER FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply updated_at trigger to all tables with updated_at column
DO $$
DECLARE
    tbl record;
BEGIN
    FOR tbl IN
        SELECT table_name
        FROM information_schema.columns
        WHERE column_name = 'updated_at'
        AND table_schema = 'public'
        AND table_name NOT IN ('order_status_history', 'delivery_location_updates', 'notifications', 'reviews')
    LOOP
        EXECUTE format('
            DROP TRIGGER IF EXISTS update_%s_updated_at ON %s;
            CREATE TRIGGER update_%s_updated_at
                BEFORE UPDATE ON %s
                FOR EACH ROW
                EXECUTE FUNCTION update_updated_at_column();
        ', tbl.table_name, tbl.table_name, tbl.table_name, tbl.table_name);
    END LOOP;
END $$;

-- ============================================
-- ORDER NUMBER GENERATION
-- ============================================

CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TEXT AS $$
DECLARE
    seq_num INTEGER;
    order_num TEXT;
BEGIN
    SELECT COALESCE(MAX(RIGHT(order_number, 6)::INTEGER), 0) + 1
    INTO seq_num
    FROM orders
    WHERE order_number LIKE 'ORD-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-%';

    order_num := 'ORD-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(seq_num::TEXT, 6, '0');
    RETURN order_num;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- VERIFICATION CODE GENERATION
-- ============================================

CREATE OR REPLACE FUNCTION generate_verification_code()
RETURNS TEXT AS $$
BEGIN
    RETURN LPAD(FLOOR(RANDOM() * 1000000)::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- RESTAURANT RATING UPDATE FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION update_restaurant_rating(p_restaurant_id UUID)
RETURNS VOID AS $$
BEGIN
    UPDATE restaurants
    SET average_rating = COALESCE((
        SELECT ROUND(AVG(rating)::NUMERIC, 2)
        FROM reviews
        WHERE target_type = 'RESTAURANT'
        AND target_id = p_restaurant_id
        AND moderation_status = 'APPROVED'
    ), 0),
    total_reviews = COALESCE((
        SELECT COUNT(*)
        FROM reviews
        WHERE target_type = 'RESTAURANT'
        AND target_id = p_restaurant_id
        AND moderation_status = 'APPROVED'
    ), 0)
    WHERE id = p_restaurant_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- DELIVERY PERSON RATING UPDATE FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION update_delivery_person_rating(p_delivery_person_id UUID)
RETURNS VOID AS $$
BEGIN
    UPDATE delivery_profiles
    SET average_rating = COALESCE((
        SELECT ROUND(AVG(rating)::NUMERIC, 2)
        FROM reviews
        WHERE target_type = 'DELIVERY_PERSON'
        AND target_id = p_delivery_person_id
        AND moderation_status = 'APPROVED'
    ), 0),
    total_deliveries = COALESCE((
        SELECT COUNT(*)
        FROM deliveries
        WHERE delivery_person_id = p_delivery_person_id
        AND status = 'DELIVERED'
    ), 0)
    WHERE user_id = p_delivery_person_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- PRODUCT RATING UPDATE FUNCTION
-- ============================================

CREATE OR REPLACE FUNCTION update_product_rating(p_product_id UUID)
RETURNS VOID AS $$
BEGIN
    UPDATE products
    SET average_rating = COALESCE((
        SELECT ROUND(AVG(rating)::NUMERIC, 2)
        FROM reviews
        WHERE target_type = 'PRODUCT'
        AND target_id = p_product_id
        AND moderation_status = 'APPROVED'
    ), 0),
    total_reviews = COALESCE((
        SELECT COUNT(*)
        FROM reviews
        WHERE target_type = 'PRODUCT'
        AND target_id = p_product_id
        AND moderation_status = 'APPROVED'
    ), 0)
    WHERE id = p_product_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- REVIEW RATING TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION trigger_update_ratings()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        IF NEW.target_type = 'RESTAURANT' THEN
            PERFORM update_restaurant_rating(NEW.target_id);
        ELSIF NEW.target_type = 'DELIVERY_PERSON' THEN
            PERFORM update_delivery_person_rating(NEW.target_id);
        ELSIF NEW.target_type = 'PRODUCT' THEN
            PERFORM update_product_rating(NEW.target_id);
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_review_rating_update ON reviews;
CREATE TRIGGER trigger_review_rating_update
    AFTER INSERT OR UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION trigger_update_ratings();

-- ============================================
-- ORDER STATUS HISTORY TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION trigger_order_status_history()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, change_reason)
        VALUES (NEW.id, NULL, NEW.status, NEW.customer_id, 'Order created');
    ELSIF TG_OP = 'UPDATE' AND OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, change_reason)
        VALUES (NEW.id, OLD.status, NEW.status,
                COALESCE(NEW.cancelled_by, NEW.customer_id),
                CASE
                    WHEN NEW.status = 'CANCELLED' THEN NEW.cancellation_reason
                    ELSE 'Status updated'
                END);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_order_status_history ON orders;
CREATE TRIGGER trigger_order_status_history
    AFTER INSERT OR UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION trigger_order_status_history();

-- ============================================
-- DEFAULT ADDRESS UNIQUENESS
-- ============================================

CREATE OR REPLACE FUNCTION enforce_single_default_address()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_default THEN
        UPDATE addresses
        SET is_default = FALSE
        WHERE user_id = NEW.user_id
        AND id != NEW.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS enforce_single_default_address ON addresses;
CREATE TRIGGER enforce_single_default_address
    BEFORE INSERT OR UPDATE ON addresses
    FOR EACH ROW EXECUTE FUNCTION enforce_single_default_address();

-- ============================================
-- CUSTOMER PROFILE CREATION TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION create_customer_profile()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.role = 'CUSTOMER' THEN
        INSERT INTO customer_profiles (user_id) VALUES (NEW.id)
        ON CONFLICT (user_id) DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS create_customer_profile ON users;
CREATE TRIGGER create_customer_profile
    AFTER INSERT ON users
    FOR EACH ROW EXECUTE FUNCTION create_customer_profile();

-- ============================================
-- DELIVERY PROFILE CREATION TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION create_delivery_profile()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.role = 'DELIVERY_PERSON' THEN
        INSERT INTO delivery_profiles (user_id, vehicle_type, license_number, license_expiry)
        VALUES (NEW.id, 'MOTORCYCLE', 'TEMP-' || SUBSTRING(NEW.id::TEXT, 1, 8), CURRENT_DATE + INTERVAL '1 year')
        ON CONFLICT (user_id) DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS create_delivery_profile ON users;
CREATE TRIGGER create_delivery_profile
    AFTER INSERT ON users
    FOR EACH ROW EXECUTE FUNCTION create_delivery_profile();

-- ============================================
-- RESTAURANT OWNER PROFILE CREATION TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION create_restaurant_owner_profile()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.role = 'RESTAURANT_OWNER' THEN
        INSERT INTO restaurant_owner_profiles (user_id, business_name, tax_id)
        VALUES (NEW.id, 'Pending Setup', 'PENDING-' || SUBSTRING(NEW.id::TEXT, 1, 8))
        ON CONFLICT (user_id) DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS create_restaurant_owner_profile ON users;
CREATE TRIGGER create_restaurant_owner_profile
    AFTER INSERT ON users
    FOR EACH ROW EXECUTE FUNCTION create_restaurant_owner_profile();

-- ============================================
-- RESTAURANT SLUG GENERATION
-- ============================================

CREATE OR REPLACE FUNCTION generate_restaurant_slug()
RETURNS TRIGGER AS $$
DECLARE
    base_slug TEXT;
    counter INTEGER := 0;
    new_slug TEXT;
BEGIN
    IF NEW.slug IS NULL OR NEW.slug = '' THEN
        base_slug := LOWER(REGEXP_REPLACE(NEW.name, '[^a-z0-9]+', '-', 'g'));
        base_slug := TRIM(BOTH '-' FROM base_slug);
        new_slug := base_slug;

        WHILE EXISTS(SELECT 1 FROM restaurants WHERE slug = new_slug) LOOP
            counter := counter + 1;
            new_slug := base_slug || '-' || counter;
        END LOOP;

        NEW.slug := new_slug;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS generate_restaurant_slug ON restaurants;
CREATE TRIGGER generate_restaurant_slug
    BEFORE INSERT ON restaurants
    FOR EACH ROW EXECUTE FUNCTION generate_restaurant_slug();

-- ============================================
-- ORDER NUMBER GENERATION TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION set_order_number()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.order_number IS NULL OR NEW.order_number = '' THEN
        NEW.order_number := generate_order_number();
    END IF;
    IF NEW.idempotency_key IS NULL OR NEW.idempotency_key = '' THEN
        NEW.idempotency_key := 'IDEMP-' || gen_random_uuid()::TEXT;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_order_number ON orders;
CREATE TRIGGER set_order_number
    BEFORE INSERT ON orders
    FOR EACH ROW EXECUTE FUNCTION set_order_number();

-- ============================================
-- DELIVERY CODES GENERATION TRIGGER
-- ============================================

CREATE OR REPLACE FUNCTION set_delivery_codes()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.pickup_code IS NULL OR NEW.pickup_code = '' THEN
        NEW.pickup_code := generate_verification_code();
    END IF;
    IF NEW.delivery_code IS NULL OR NEW.delivery_code = '' THEN
        NEW.delivery_code := generate_verification_code();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_delivery_codes ON deliveries;
CREATE TRIGGER set_delivery_codes
    BEFORE INSERT ON deliveries
    FOR EACH ROW EXECUTE FUNCTION set_delivery_codes();

-- ============================================
-- RESTAURANT OPEN STATUS UPDATE
-- ============================================

CREATE OR REPLACE FUNCTION update_restaurant_open_status()
RETURNS TRIGGER AS $$
DECLARE
    current_day day_of_week;
    current_time TIME;
    hours_record RECORD;
BEGIN
    current_day := CASE EXTRACT(DOW FROM NOW())::INTEGER
        WHEN 0 THEN 'SUNDAY' WHEN 1 THEN 'MONDAY' WHEN 2 THEN 'TUESDAY'
        WHEN 3 THEN 'WEDNESDAY' WHEN 4 THEN 'THURSDAY' WHEN 5 THEN 'FRIDAY'
        WHEN 6 THEN 'SATURDAY' END;
    current_time := NOW()::TIME;

    SELECT * INTO hours_record
    FROM restaurant_hours
    WHERE restaurant_id = NEW.id
    AND day_of_week = current_day;

    IF hours_record IS NOT NULL AND NOT hours_record.is_closed
       AND hours_record.open_time IS NOT NULL
       AND hours_record.close_time IS NOT NULL
       AND current_time >= hours_record.open_time
       AND current_time <= hours_record.close_time THEN
        NEW.is_open_now := TRUE;
    ELSE
        NEW.is_open_now := FALSE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_restaurant_open_status ON restaurants;
CREATE TRIGGER update_restaurant_open_status
    BEFORE INSERT OR UPDATE ON restaurants
    FOR EACH ROW EXECUTE FUNCTION update_restaurant_open_status();

-- Also trigger when hours change
DROP TRIGGER IF EXISTS update_restaurant_open_status_hours ON restaurant_hours;
CREATE TRIGGER update_restaurant_open_status_hours
    AFTER INSERT OR UPDATE OR DELETE ON restaurant_hours
    FOR EACH ROW EXECUTE FUNCTION update_restaurant_open_status();

-- ============================================
-- HELPER FUNCTIONS
-- ============================================

-- Calculate distance between two points (Haversine formula)
CREATE OR REPLACE FUNCTION calculate_distance(
    lat1 DECIMAL, lon1 DECIMAL,
    lat2 DECIMAL, lon2 DECIMAL
) RETURNS DECIMAL AS $$
DECLARE
    R DECIMAL := 6371; -- Earth radius in km
    dlat DECIMAL := RADIANS(lat2 - lat1);
    dlon DECIMAL := RADIANS(lon2 - lon1);
    a DECIMAL;
    c DECIMAL;
BEGIN
    a := SIN(dlat/2) * SIN(dlat/2) +
         COS(RADIANS(lat1)) * COS(RADIANS(lat2)) *
         SIN(dlon/2) * SIN(dlon/2);
    c := 2 * ATAN2(SQRT(a), SQRT(1-a));
    RETURN ROUND(R * c, 2);
END;
$$ LANGUAGE plpgsql;

-- Find available delivery persons near a location
CREATE OR REPLACE FUNCTION find_nearby_delivery_persons(
    p_latitude DECIMAL,
    p_longitude DECIMAL,
    p_radius_km DECIMAL DEFAULT 5.0,
    p_limit INTEGER DEFAULT 10
) RETURNS TABLE(
    user_id UUID,
    distance_km DECIMAL,
    average_rating DECIMAL,
    total_deliveries INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        dp.user_id,
        calculate_distance(p_latitude, p_longitude, dp.current_latitude, dp.current_longitude) AS distance_km,
        dp.average_rating,
        dp.total_deliveries
    FROM delivery_profiles dp
    JOIN users u ON u.id = dp.user_id
    WHERE dp.is_available = TRUE
    AND dp.background_check_status = 'APPROVED'
    AND u.status = 'ACTIVE'
    AND dp.current_latitude IS NOT NULL
    AND dp.current_longitude IS NOT NULL
    AND calculate_distance(p_latitude, p_longitude, dp.current_latitude, dp.current_longitude) <= p_radius_km
    ORDER BY distance_km ASC, dp.average_rating DESC, dp.total_deliveries ASC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;

-- Check if restaurant is open now
CREATE OR REPLACE FUNCTION is_restaurant_open_now(p_restaurant_id UUID)
RETURNS BOOLEAN AS $$
DECLARE
    current_day day_of_week;
    current_time TIME;
    hours_record RECORD;
BEGIN
    SELECT status INTO current_day FROM restaurants WHERE id = p_restaurant_id;
    IF current_day != 'APPROVED' THEN
        RETURN FALSE;
    END IF;

    current_day := CASE EXTRACT(DOW FROM NOW())::INTEGER
        WHEN 0 THEN 'SUNDAY' WHEN 1 THEN 'MONDAY' WHEN 2 THEN 'TUESDAY'
        WHEN 3 THEN 'WEDNESDAY' WHEN 4 THEN 'THURSDAY' WHEN 5 THEN 'FRIDAY'
        WHEN 6 THEN 'SATURDAY' END;
    current_time := NOW()::TIME;

    SELECT * INTO hours_record
    FROM restaurant_hours
    WHERE restaurant_id = p_restaurant_id
    AND day_of_week = current_day;

    IF hours_record IS NOT NULL AND NOT hours_record.is_closed
       AND hours_record.open_time IS NOT NULL
       AND hours_record.close_time IS NOT NULL
       AND current_time >= hours_record.open_time
       AND current_time <= hours_record.close_time THEN
        RETURN TRUE;
    END IF;

    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Get order count by status for a restaurant
CREATE OR REPLACE FUNCTION get_restaurant_order_counts(p_restaurant_id UUID)
RETURNS TABLE(status order_status, count BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT o.status, COUNT(*)
    FROM orders o
    WHERE o.restaurant_id = p_restaurant_id
    GROUP BY o.status;
END;
$$ LANGUAGE plpgsql;

-- Get delivery person earnings
CREATE OR REPLACE FUNCTION get_delivery_earnings(
    p_delivery_person_id UUID,
    p_from_date TIMESTAMPTZ,
    p_to_date TIMESTAMPTZ
) RETURNS TABLE(
    total_deliveries BIGINT,
    total_earnings DECIMAL,
    avg_earnings_per_delivery DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT AS total_deliveries,
        COALESCE(SUM(p.amount), 0) AS total_earnings,
        CASE WHEN COUNT(*) > 0 THEN ROUND(COALESCE(SUM(p.amount), 0) / COUNT(*), 2) ELSE 0 END AS avg_earnings_per_delivery
    FROM deliveries d
    JOIN payments p ON p.order_id = d.order_id
    WHERE d.delivery_person_id = p_delivery_person_id
    AND d.status = 'DELIVERED'
    AND d.delivered_at BETWEEN p_from_date AND p_to_date;
END;
$$ LANGUAGE plpgsql;