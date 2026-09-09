-- =============================================================
-- ShopSmart - Oracle Seed Data Script
-- Run this in SQL*Plus / SQL Developer against c##shopsmart_db
-- Uses MERGE so it is safe to re-run without duplicate errors.
-- =============================================================

-- -------------------------------------------------------
-- 1. USERS  (passwords are BCrypt of the plain-text shown)
--    ADMIN  -> password: admin123
--    STAFF  -> password: staff123
-- -------------------------------------------------------
MERGE INTO USERS u
USING (SELECT 1 AS dummy FROM DUAL) d ON (u.email = 'admin@shopsmart.com')
WHEN NOT MATCHED THEN
  INSERT (id, name, email, password, role, created_at)
  VALUES (USER_SEQ.NEXTVAL, 'Admin User', 'admin@shopsmart.com',
          '$2a$10$N/0EilGX4tqnFjTfCR6ske1F4lzSuT1dKjlMmE0y5qGl3J7HWgr1K',
          'ADMIN', SYSDATE);

MERGE INTO USERS u
USING (SELECT 1 AS dummy FROM DUAL) d ON (u.email = 'staff@shopsmart.com')
WHEN NOT MATCHED THEN
  INSERT (id, name, email, password, role, created_at)
  VALUES (USER_SEQ.NEXTVAL, 'Staff User', 'staff@shopsmart.com',
          '$2a$10$rqRhxz3Y7qbOtJ6OX3oTxO.n5K5mC5CJGpMCUJgqAqVrHjXXoWODO',
          'STAFF', SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- 2. CATEGORIES
-- -------------------------------------------------------
MERGE INTO CATEGORIES c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.name = 'Electronics')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, is_active, created_at, updated_at)
  VALUES (CATEGORY_SEQ.NEXTVAL, 'Electronics',
          'Mobile phones, laptops, accessories, and other electronic gadgets',
          1, SYSDATE, SYSDATE);

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.name = 'Groceries')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, is_active, created_at, updated_at)
  VALUES (CATEGORY_SEQ.NEXTVAL, 'Groceries',
          'Rice, pulses, flour, spices, and other daily grocery items',
          1, SYSDATE, SYSDATE);

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.name = 'Beverages')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, is_active, created_at, updated_at)
  VALUES (CATEGORY_SEQ.NEXTVAL, 'Beverages',
          'Juices, cold drinks, water, tea, and coffee',
          1, SYSDATE, SYSDATE);

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.name = 'Stationery')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, is_active, created_at, updated_at)
  VALUES (CATEGORY_SEQ.NEXTVAL, 'Stationery',
          'Notebooks, pens, files, and office supplies',
          1, SYSDATE, SYSDATE);

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.name = 'Medicines')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, is_active, created_at, updated_at)
  VALUES (CATEGORY_SEQ.NEXTVAL, 'Medicines',
          'OTC medicines, vitamins, and health supplements',
          1, SYSDATE, SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- 3. CUSTOMER GROUPS
-- -------------------------------------------------------
MERGE INTO customer_group cg
USING (SELECT 1 AS dummy FROM DUAL) d ON (cg.name = 'Regular Members')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, discount_percentage, is_active, created_at, updated_at)
  VALUES (customer_group_seq.NEXTVAL, 'Regular Members',
          'Standard loyalty members with basic discount',
          5.00, 1, SYSDATE, SYSDATE);

MERGE INTO customer_group cg
USING (SELECT 1 AS dummy FROM DUAL) d ON (cg.name = 'Wholesale Partners')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, discount_percentage, is_active, created_at, updated_at)
  VALUES (customer_group_seq.NEXTVAL, 'Wholesale Partners',
          'Bulk buyers with premium discount rates',
          15.00, 1, SYSDATE, SYSDATE);

MERGE INTO customer_group cg
USING (SELECT 1 AS dummy FROM DUAL) d ON (cg.name = 'VIP Customers')
WHEN NOT MATCHED THEN
  INSERT (id, name, description, discount_percentage, is_active, created_at, updated_at)
  VALUES (customer_group_seq.NEXTVAL, 'VIP Customers',
          'High-value customers with exclusive benefits',
          10.00, 1, SYSDATE, SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- 4. PRODUCTS  (category_id values match insert order above)
--    Electronics=1, Groceries=2, Beverages=3, Stationery=4, Medicines=5
--    Adjust IDs if your sequences started differently.
-- -------------------------------------------------------
MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'ELEC-SAM-A54-001')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Samsung Galaxy A54 5G Smartphone', 'ELEC-SAM-A54-001',
          '8901234567890',
          '6.4-inch AMOLED display, 128GB storage, 5G enabled, 50MP camera',
          36999.0000, 29000.0000, 18.00,
          'PIECE', 5, 100, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Electronics'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'ELEC-BOAT-EP-001')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'boAt Rockerz 450 Bluetooth Headphone', 'ELEC-BOAT-EP-001',
          '8901234567899',
          'On-ear wireless headphone, 15h battery life, 40mm drivers',
          1599.0000, 950.0000, 18.00,
          'PIECE', 10, 200, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Electronics'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'GROC-RICE-BAS-001')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Organic Basmati Rice', 'GROC-RICE-BAS-001',
          '8901234567891',
          'Premium long-grain basmati rice, aged 2 years, 1 kg pack',
          180.0000, 130.0000, 5.00,
          'KG', 50, 500, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Groceries'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'GROC-OIL-SUN-1L')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Fortune Sunflower Oil', 'GROC-OIL-SUN-1L',
          '8901234567895',
          'Refined sunflower cooking oil, 1 litre bottle',
          165.0000, 120.0000, 5.00,
          'LITER', 30, 300, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Groceries'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'BEV-TROP-OJ-1L')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Tropicana Orange Juice 1L', 'BEV-TROP-OJ-1L',
          '8901234567892',
          '100% pure squeezed orange juice, no added sugar, 1 litre pack',
          120.0000, 85.0000, 12.00,
          'LITER', 30, 200, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Beverages'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'BEV-COKE-500ML')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Coca-Cola 500ml Bottle', 'BEV-COKE-500ML',
          '8901234567896',
          'Refreshing cola drink, 500ml PET bottle',
          40.0000, 25.0000, 28.00,
          'ML', 48, 500, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Beverages'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'STAT-NB-CM-A4-12')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Classmate A4 Ruled Notebook Pack', 'STAT-NB-CM-A4-12',
          '8901234567893',
          'Pack of 12 ruled notebooks, 200 pages each, A4 size',
          250.0000, 180.0000, 0.00,
          'PACK', 20, 300, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Stationery'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'STAT-PEN-CELLO-10')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Cello Pinpoint Ball Pen Pack of 10', 'STAT-PEN-CELLO-10',
          '8901234567897',
          'Smooth writing blue ballpoint pens, pack of 10',
          45.0000, 28.0000, 0.00,
          'PACK', 50, 1000, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Stationery'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'MED-PARA-500-10')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Paracetamol 500mg Tablets', 'MED-PARA-500-10',
          '8901234567894',
          'Strip of 10 tablets, fever and mild pain relief',
          22.0000, 12.0000, 0.00,
          'BOX', 100, 1000, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Medicines'),
          SYSDATE, SYSDATE);

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dummy FROM DUAL) d ON (p.sku = 'MED-VIT-C-60')
WHEN NOT MATCHED THEN
  INSERT (id, name, sku, barcode, description, price, cost_price, tax_rate,
          unit, reorder_level, max_stock_level, is_active, category_id, created_at, updated_at)
  VALUES (PRODUCT_SEQ.NEXTVAL, 'Vitamin C 500mg Tablets 60s', 'MED-VIT-C-60',
          '8901234567898',
          'Vitamin C effervescent tablets, 500mg, 60 tablet bottle',
          399.0000, 250.0000, 12.00,
          'BOX', 20, 200, 1,
          (SELECT id FROM CATEGORIES WHERE name = 'Medicines'),
          SYSDATE, SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- 5. INVENTORY  (one row per product, initial stock)
-- -------------------------------------------------------
MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'ELEC-SAM-A54-001') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 45, 0, 45, 5, 100, 'SHELF-A1', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'ELEC-BOAT-EP-001') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 80, 5, 75, 10, 200, 'SHELF-A2', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'GROC-RICE-BAS-001') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 200, 10, 190, 50, 500, 'SHELF-B1', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'GROC-OIL-SUN-1L') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 3, 0, 3, 30, 300, 'SHELF-B2', SYSDATE, SYSDATE);
  -- NOTE: qty=3 < reorder_level=30  ->  triggers LOW STOCK alert

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'BEV-TROP-OJ-1L') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 60, 0, 60, 30, 200, 'SHELF-C1', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'BEV-COKE-500ML') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 144, 0, 144, 48, 500, 'SHELF-C2', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'STAT-NB-CM-A4-12') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 75, 0, 75, 20, 300, 'SHELF-D1', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'STAT-PEN-CELLO-10') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 8, 0, 8, 50, 1000, 'SHELF-D2', SYSDATE, SYSDATE);
  -- NOTE: qty=8 < reorder_level=50  ->  triggers LOW STOCK alert

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'MED-PARA-500-10') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 500, 20, 480, 100, 1000, 'SHELF-E1', SYSDATE, SYSDATE);

MERGE INTO INVENTORY inv
USING (SELECT id FROM PRODUCTS WHERE sku = 'MED-VIT-C-60') p ON (inv.product_id = p.id)
WHEN NOT MATCHED THEN
  INSERT (id, product_id, quantity_on_hand, quantity_reserved, quantity_available,
          reorder_level, max_stock_level, location, created_at, updated_at)
  VALUES (INVENTORY_SEQ.NEXTVAL, p.id, 90, 0, 90, 20, 200, 'SHELF-E2', SYSDATE, SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- 6. CUSTOMERS
-- -------------------------------------------------------
MERGE INTO customer c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.phone = '9876543210')
WHEN NOT MATCHED THEN
  INSERT (id, phone, email, name, type, group_id, loyalty_points,
          credit_limit, credit_used, gst_number, is_active, created_at, updated_at)
  VALUES (customer_seq.NEXTVAL, '9876543210', NULL, 'Rahul Sharma',
          'WALK_IN', NULL, 0, NULL, NULL, NULL, 1, SYSDATE, SYSDATE);

MERGE INTO customer c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.phone = '9123456789')
WHEN NOT MATCHED THEN
  INSERT (id, phone, email, name, type, group_id, loyalty_points,
          credit_limit, credit_used, gst_number, is_active, created_at, updated_at)
  VALUES (customer_seq.NEXTVAL, '9123456789', 'priya.patel@gmail.com', 'Priya Patel',
          'REGULAR',
          (SELECT id FROM customer_group WHERE name = 'Regular Members'),
          120, 10000.00, 2500.00, NULL, 1, SYSDATE, SYSDATE);

MERGE INTO customer c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.phone = '9988776655')
WHEN NOT MATCHED THEN
  INSERT (id, phone, email, name, type, group_id, loyalty_points,
          credit_limit, credit_used, gst_number, is_active, created_at, updated_at)
  VALUES (customer_seq.NEXTVAL, '9988776655', 'orders@krishnatraders.com', 'Krishna Traders Pvt Ltd',
          'WHOLESALE',
          (SELECT id FROM customer_group WHERE name = 'Wholesale Partners'),
          0, 250000.00, 15000.00, '29ABCDE1234F1Z5', 1, SYSDATE, SYSDATE);

MERGE INTO customer c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.phone = '7890123456')
WHEN NOT MATCHED THEN
  INSERT (id, phone, email, name, type, group_id, loyalty_points,
          credit_limit, credit_used, gst_number, is_active, created_at, updated_at)
  VALUES (customer_seq.NEXTVAL, '7890123456', 'amit.kumar@yahoo.com', 'Amit Kumar',
          'REGULAR', NULL, 80, 5000.00, 1200.00, NULL, 1, SYSDATE, SYSDATE);

MERGE INTO customer c
USING (SELECT 1 AS dummy FROM DUAL) d ON (c.phone = '8765432100')
WHEN NOT MATCHED THEN
  INSERT (id, phone, email, name, type, group_id, loyalty_points,
          credit_limit, credit_used, gst_number, is_active, created_at, updated_at)
  VALUES (customer_seq.NEXTVAL, '8765432100', 'sunita.retail@gmail.com', 'Sunita Retail Store',
          'WHOLESALE',
          (SELECT id FROM customer_group WHERE name = 'VIP Customers'),
          500, 100000.00, 97500.00, '27XYZAB5678C2D3', 1, SYSDATE, SYSDATE);
          -- NOTE: creditUsed 97500 vs limit 100000 -> triggers LOW CREDIT alert

COMMIT;

-- -------------------------------------------------------
-- 7. CUSTOMER ADDRESSES
-- -------------------------------------------------------
-- Priya Patel
MERGE INTO customer_address ca
USING (SELECT c.id FROM customer c WHERE c.phone = '9123456789') cust
  ON (ca.customer_id = cust.id AND ca.address_line1 = '42, Shivaji Nagar')
WHEN NOT MATCHED THEN
  INSERT (id, customer_id, type, address_line1, address_line2,
          city, state, pincode, country, is_default, is_active, created_at, updated_at)
  VALUES (customer_address_seq.NEXTVAL, cust.id, 'BOTH', '42, Shivaji Nagar',
          'Near HDFC Bank', 'Pune', 'Maharashtra', '411005', 'India', 1, 1, SYSDATE, SYSDATE);

-- Krishna Traders - Billing
MERGE INTO customer_address ca
USING (SELECT c.id FROM customer c WHERE c.phone = '9988776655') cust
  ON (ca.customer_id = cust.id AND ca.address_line1 = 'Plot 7, Industrial Area Phase 2')
WHEN NOT MATCHED THEN
  INSERT (id, customer_id, type, address_line1, address_line2,
          city, state, pincode, country, is_default, is_active, created_at, updated_at)
  VALUES (customer_address_seq.NEXTVAL, cust.id, 'BILLING', 'Plot 7, Industrial Area Phase 2',
          'Peenya', 'Bengaluru', 'Karnataka', '560058', 'India', 1, 1, SYSDATE, SYSDATE);

-- Krishna Traders - Shipping
MERGE INTO customer_address ca
USING (SELECT c.id FROM customer c WHERE c.phone = '9988776655') cust
  ON (ca.customer_id = cust.id AND ca.address_line1 = 'Warehouse 3, Dobbespet Road')
WHEN NOT MATCHED THEN
  INSERT (id, customer_id, type, address_line1, address_line2,
          city, state, pincode, country, is_default, is_active, created_at, updated_at)
  VALUES (customer_address_seq.NEXTVAL, cust.id, 'SHIPPING', 'Warehouse 3, Dobbespet Road',
          NULL, 'Nelamangala', 'Karnataka', '562123', 'India', 0, 1, SYSDATE, SYSDATE);

-- Amit Kumar
MERGE INTO customer_address ca
USING (SELECT c.id FROM customer c WHERE c.phone = '7890123456') cust
  ON (ca.customer_id = cust.id AND ca.address_line1 = '15, MG Road')
WHEN NOT MATCHED THEN
  INSERT (id, customer_id, type, address_line1, address_line2,
          city, state, pincode, country, is_default, is_active, created_at, updated_at)
  VALUES (customer_address_seq.NEXTVAL, cust.id, 'BOTH', '15, MG Road',
          NULL, 'Jaipur', 'Rajasthan', '302001', 'India', 1, 1, SYSDATE, SYSDATE);

-- Sunita Retail Store
MERGE INTO customer_address ca
USING (SELECT c.id FROM customer c WHERE c.phone = '8765432100') cust
  ON (ca.customer_id = cust.id AND ca.address_line1 = '88, Gandhi Market')
WHEN NOT MATCHED THEN
  INSERT (id, customer_id, type, address_line1, address_line2,
          city, state, pincode, country, is_default, is_active, created_at, updated_at)
  VALUES (customer_address_seq.NEXTVAL, cust.id, 'BOTH', '88, Gandhi Market',
          'Opp. Central Bank', 'Mumbai', 'Maharashtra', '400001', 'India', 1, 1, SYSDATE, SYSDATE);

COMMIT;

-- -------------------------------------------------------
-- VERIFICATION QUERIES  (uncomment to check after running)
-- -------------------------------------------------------
-- SELECT 'USERS'          AS tbl, COUNT(*) AS cnt FROM USERS          UNION ALL
-- SELECT 'CATEGORIES'     AS tbl, COUNT(*) AS cnt FROM CATEGORIES      UNION ALL
-- SELECT 'customer_group' AS tbl, COUNT(*) AS cnt FROM customer_group  UNION ALL
-- SELECT 'PRODUCTS'       AS tbl, COUNT(*) AS cnt FROM PRODUCTS        UNION ALL
-- SELECT 'INVENTORY'      AS tbl, COUNT(*) AS cnt FROM INVENTORY       UNION ALL
-- SELECT 'customer'       AS tbl, COUNT(*) AS cnt FROM customer        UNION ALL
-- SELECT 'customer_address' AS tbl, COUNT(*) AS cnt FROM customer_address;
