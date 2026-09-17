-- =============================================================
-- ShopSmart - H2 Test Seed Data (for integration tests only)
-- Loaded via @Sql on each test class before each test method.
--
-- Uses explicit literal IDs so FK relationships are always
-- consistent, regardless of sequence state between tests.
-- Sequences are reset AFTER inserts so Hibernate doesn't
-- conflict when the test itself creates new rows.
-- =============================================================

-- -------------------------------------------------------
-- 1. USERS  (passwords: admin123 / staff123)
-- -------------------------------------------------------
INSERT INTO USERS (id, name, email, password, role, created_at) VALUES (1, 'Admin User', 'admin@shopsmart.com', '$2a$10$N/0EilGX4tqnFjTfCR6ske1F4lzSuT1dKjlMmE0y5qGl3J7HWgr1K', 'ADMIN', CURRENT_TIMESTAMP);
INSERT INTO USERS (id, name, email, password, role, created_at) VALUES (2, 'Staff User', 'staff@shopsmart.com', '$2a$10$rqRhxz3Y7qbOtJ6OX3oTxO.n5K5mC5CJGpMCUJgqAqVrHjXXoWODO', 'STAFF', CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 2. CATEGORIES
-- -------------------------------------------------------
INSERT INTO CATEGORIES (id, name, description, is_active, created_at, updated_at) VALUES (1, 'Electronics', 'Mobile phones, laptops, accessories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO CATEGORIES (id, name, description, is_active, created_at, updated_at) VALUES (2, 'Groceries', 'Rice, pulses, flour, spices', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO CATEGORIES (id, name, description, is_active, created_at, updated_at) VALUES (3, 'Beverages', 'Juices, cold drinks, water', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO CATEGORIES (id, name, description, is_active, created_at, updated_at) VALUES (4, 'Stationery', 'Notebooks, pens, files', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO CATEGORIES (id, name, description, is_active, created_at, updated_at) VALUES (5, 'Medicines', 'OTC medicines, vitamins', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 3. CUSTOMER GROUPS
-- -------------------------------------------------------
INSERT INTO customer_group (id, name, description, discount_percentage, is_active, created_at, updated_at) VALUES (1, 'Regular Members', 'Standard loyalty members with basic discount', 5.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_group (id, name, description, discount_percentage, is_active, created_at, updated_at) VALUES (2, 'Wholesale Partners', 'Bulk buyers with premium discount rates', 15.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_group (id, name, description, discount_percentage, is_active, created_at, updated_at) VALUES (3, 'VIP Customers', 'High-value customers with exclusive benefits', 10.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 4. PRODUCTS  (category_id: 1=Electronics, 2=Groceries, 3=Beverages, 4=Stationery, 5=Medicines)
-- -------------------------------------------------------
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (1, 'Samsung Galaxy A54 5G', 'ELEC-SAM-A54-001', '8901234567890', '6.4" AMOLED, 128GB, 5G, 50MP', 36999.0000, 29000.0000, 18.00, 'PIECE', 5, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (2, 'boAt Rockerz 450', 'ELEC-BOAT-EP-001', '8901234567899', 'On-ear wireless, 15h battery', 1599.0000, 950.0000, 18.00, 'PIECE', 10, 200, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (3, 'Organic Basmati Rice 1kg', 'GROC-RICE-BAS-001', '8901234567891', 'Premium aged basmati rice', 180.0000, 130.0000, 5.00, 'KG', 50, 500, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (4, 'Fortune Sunflower Oil 1L', 'GROC-OIL-SUN-1L', '8901234567895', 'Refined sunflower oil', 165.0000, 120.0000, 5.00, 'LITER', 30, 300, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (5, 'Tropicana Orange Juice 1L', 'BEV-TROP-OJ-1L', '8901234567892', '100% pure orange juice', 120.0000, 85.0000, 12.00, 'LITER', 30, 200, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (6, 'Coca-Cola 500ml', 'BEV-COKE-500ML', '8901234567896', 'Refreshing cola drink', 40.0000, 25.0000, 28.00, 'ML', 48, 500, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (7, 'Classmate A4 Notebook 12pk', 'STAT-NB-CM-A4-12', '8901234567893', 'Pack of 12 ruled notebooks', 250.0000, 180.0000, 0.00, 'PACK', 20, 300, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (8, 'Cello Pinpoint Pen 10pk', 'STAT-PEN-CELLO-10', '8901234567897', 'Blue ballpoint pens', 45.0000, 28.0000, 0.00, 'PACK', 50, 1000, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (9, 'Paracetamol 500mg 10s', 'MED-PARA-500-10', '8901234567894', 'Fever/pain relief tablets', 22.0000, 12.0000, 0.00, 'BOX', 100, 1000, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO PRODUCTS (id, name, sku, barcode, description, price, cost_price, tax_rate, unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at) VALUES (10, 'Vitamin C 500mg 60s', 'MED-VIT-C-60', '8901234567898', 'Effervescent vitamin C', 399.0000, 250.0000, 12.00, 'BOX', 20, 200, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 5. INVENTORY  (product_id matches insert order above: 1-10)
-- -------------------------------------------------------
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (1, 1, 45, 0, 45, 5, 100, 'SHELF-A1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (2, 2, 80, 5, 75, 10, 200, 'SHELF-A2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (3, 3, 200, 10, 190, 50, 500, 'SHELF-B1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (4, 4, 3, 0, 3, 30, 300, 'SHELF-B2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (5, 5, 60, 0, 60, 30, 200, 'SHELF-C1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (6, 6, 144, 0, 144, 48, 500, 'SHELF-C2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (7, 7, 75, 0, 75, 20, 300, 'SHELF-D1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (8, 8, 8, 0, 8, 50, 1000, 'SHELF-D2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (9, 9, 500, 20, 480, 100, 1000, 'SHELF-E1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO INVENTORY (id, product_id, quantity_on_hand, quantity_reserved, quantity_available, reorder_level, max_stock_level, location, created_at, updated_at) VALUES (10, 10, 90, 0, 90, 20, 200, 'SHELF-E2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 6. CUSTOMERS
-- -------------------------------------------------------
INSERT INTO customer (id, phone, email, name, type, group_id, loyalty_points, credit_limit, credit_used, gst_number, is_active, created_at, updated_at) VALUES (1, '9876543210', NULL, 'Rahul Sharma', 'WALK_IN', NULL, 0, NULL, NULL, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer (id, phone, email, name, type, group_id, loyalty_points, credit_limit, credit_used, gst_number, is_active, created_at, updated_at) VALUES (2, '9123456789', 'priya.patel@gmail.com', 'Priya Patel', 'REGULAR', 1, 120, 10000.00, 2500.00, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer (id, phone, email, name, type, group_id, loyalty_points, credit_limit, credit_used, gst_number, is_active, created_at, updated_at) VALUES (3, '9988776655', 'orders@krishnatraders.com', 'Krishna Traders Pvt Ltd', 'WHOLESALE', 2, 0, 250000.00, 15000.00, '29ABCDE1234F1Z5', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer (id, phone, email, name, type, group_id, loyalty_points, credit_limit, credit_used, gst_number, is_active, created_at, updated_at) VALUES (4, '7890123456', 'amit.kumar@yahoo.com', 'Amit Kumar', 'REGULAR', NULL, 80, 5000.00, 1200.00, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer (id, phone, email, name, type, group_id, loyalty_points, credit_limit, credit_used, gst_number, is_active, created_at, updated_at) VALUES (5, '8765432100', 'sunita.retail@gmail.com', 'Sunita Retail Store', 'WHOLESALE', 3, 500, 100000.00, 97500.00, '27XYZAB5678C2D3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 7. CUSTOMER ADDRESSES
-- -------------------------------------------------------
INSERT INTO customer_address (id, customer_id, type, address_line1, address_line2, city, state, pincode, country, is_default, is_active, created_at, updated_at) VALUES (1, 2, 'BOTH', '42, Shivaji Nagar', 'Near HDFC Bank', 'Pune', 'Maharashtra', '411005', 'India', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_address (id, customer_id, type, address_line1, address_line2, city, state, pincode, country, is_default, is_active, created_at, updated_at) VALUES (2, 3, 'BILLING', 'Plot 7, Industrial Area Phase 2', 'Peenya', 'Bengaluru', 'Karnataka', '560058', 'India', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_address (id, customer_id, type, address_line1, address_line2, city, state, pincode, country, is_default, is_active, created_at, updated_at) VALUES (3, 3, 'SHIPPING', 'Warehouse 3, Dobbespet Road', NULL, 'Nelamangala', 'Karnataka', '562123', 'India', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_address (id, customer_id, type, address_line1, address_line2, city, state, pincode, country, is_default, is_active, created_at, updated_at) VALUES (4, 4, 'BOTH', '15, MG Road', NULL, 'Jaipur', 'Rajasthan', '302001', 'India', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customer_address (id, customer_id, type, address_line1, address_line2, city, state, pincode, country, is_default, is_active, created_at, updated_at) VALUES (5, 5, 'BOTH', '88, Gandhi Market', 'Opp. Central Bank', 'Mumbai', 'Maharashtra', '400001', 'India', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------------------------------------
-- 8. RESET SEQUENCES
-- Advance all sequences past the seeded IDs so Hibernate
-- doesn't generate IDs that conflict with seed data.
-- @Transactional rolls back the rows but NOT the sequences,
-- so we need a high enough gap between runs (1000 gap per test).
-- -------------------------------------------------------
ALTER SEQUENCE USER_SEQ RESTART WITH 100;
ALTER SEQUENCE CATEGORY_SEQ RESTART WITH 100;
ALTER SEQUENCE customer_group_seq RESTART WITH 100;
ALTER SEQUENCE PRODUCT_SEQ RESTART WITH 100;
ALTER SEQUENCE INVENTORY_SEQ RESTART WITH 100;
ALTER SEQUENCE customer_seq RESTART WITH 100;
ALTER SEQUENCE customer_address_seq RESTART WITH 100;
ALTER SEQUENCE order_seq RESTART WITH 100;
ALTER SEQUENCE order_item_seq RESTART WITH 100;
ALTER SEQUENCE payment_seq RESTART WITH 100;