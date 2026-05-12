-- ============================================================
-- SQL Operations Script
-- Food Delivery Database Application
-- COSC 457 Final Project
--
-- Purpose: Representative SQL operations used in the application.
-- Run AFTER schema.sql and sample_data.sql have been executed.
-- ============================================================

USE food_delivery_db;

-- ============================================================
-- SELECT WITH WHERE (filtering)
-- ============================================================

-- View restaurants within the 20-mile delivery zone (CustomerUI)
SELECT vendor_id, restaurant_name, address, distance_miles
FROM Vendors
WHERE distance_miles <= 20
ORDER BY restaurant_name;

-- View menu items for a specific vendor (CustomerUI dropdown)
SELECT item_id, item_name, price
FROM MenuItems
WHERE vendor_id = 1
ORDER BY item_name;

-- View a specific customer's order status (CustomerUI)
SELECT order_id, status, total
FROM Orders
WHERE order_id = 1 AND customer_id = 1;

-- View all orders for a specific vendor (VendorUI)
SELECT order_id, customer_id, status, total
FROM Orders
WHERE vendor_id = 1;

-- ============================================================
-- SELECT WITH JOIN
-- ============================================================

-- Customer: view all my orders with restaurant name and date (CustomerUI)
SELECT o.order_id, v.restaurant_name, o.status, o.total, o.order_date
FROM Orders o
JOIN Vendors v ON o.vendor_id = v.vendor_id
WHERE o.customer_id = 1
ORDER BY o.order_id DESC;

-- Vendor: view incoming orders with customer names (VendorUI)
SELECT o.order_id, c.customer_name, o.status, o.total
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
WHERE o.vendor_id = 1;

-- Driver: view assigned deliveries with restaurant info (DriverUI)
SELECT d.delivery_id, d.order_id, d.delivery_status, v.restaurant_name
FROM Deliveries d
JOIN Orders o ON d.order_id = o.order_id
JOIN Vendors v ON o.vendor_id = v.vendor_id
WHERE d.driver_id = 1;

-- Driver: view full pickup and delivery info for a specific delivery (DriverUI)
SELECT d.delivery_id, v.restaurant_name, v.address AS pickup_address,
       c.customer_name, c.address AS delivery_address, d.delivery_status
FROM Deliveries d
JOIN Orders o ON d.order_id = o.order_id
JOIN Vendors v ON o.vendor_id = v.vendor_id
JOIN Customers c ON o.customer_id = c.customer_id
WHERE d.delivery_id = 1 AND d.driver_id = 1;

-- Admin: full report of all orders with customer, restaurant, driver, and delivery info (AdminUI)
SELECT o.order_id, c.customer_name, v.restaurant_name, dr.driver_name,
       o.status, d.delivery_status
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
JOIN Vendors v ON o.vendor_id = v.vendor_id
LEFT JOIN Deliveries d ON o.order_id = d.order_id
LEFT JOIN Drivers dr ON d.driver_id = dr.driver_id;

-- Admin: view all deliveries with driver name (AdminUI)
SELECT d.delivery_id, d.order_id, dr.driver_name, d.delivery_status
FROM Deliveries d
JOIN Drivers dr ON d.driver_id = dr.driver_id;

-- ============================================================
-- SELECT WITH AGGREGATE (COUNT, SUM)
-- ============================================================

-- Admin: summary report of total orders and total revenue (AdminUI)
SELECT COUNT(*) AS total_orders, SUM(total) AS total_revenue
FROM Orders;

-- ============================================================
-- SELECT WITH GROUP BY
-- ============================================================

-- Number of orders per restaurant
SELECT v.restaurant_name, COUNT(o.order_id) AS order_count
FROM Orders o
JOIN Vendors v ON o.vendor_id = v.vendor_id
GROUP BY v.vendor_id, v.restaurant_name
ORDER BY order_count DESC;

-- Total revenue per restaurant
SELECT v.restaurant_name, SUM(o.total) AS total_revenue
FROM Orders o
JOIN Vendors v ON o.vendor_id = v.vendor_id
GROUP BY v.vendor_id, v.restaurant_name
ORDER BY total_revenue DESC;

-- Number of deliveries per driver
SELECT dr.driver_name, COUNT(d.delivery_id) AS delivery_count
FROM Deliveries d
JOIN Drivers dr ON d.driver_id = dr.driver_id
GROUP BY dr.driver_id, dr.driver_name;

-- ============================================================
-- INSERT
-- ============================================================

-- Customer places an order (CustomerUI)
INSERT INTO Orders (customer_id, vendor_id, status, total)
VALUES (1, 1, 'Placed', 12.99);

-- Add order line item (CustomerUI)
INSERT INTO OrderItems (order_id, item_id, quantity)
VALUES (LAST_INSERT_ID(), 1, 1);

-- Assign delivery to a driver (CustomerUI)
INSERT INTO Deliveries (order_id, driver_id, delivery_status)
VALUES (LAST_INSERT_ID(), 1, 'Assigned');

-- Vendor adds a new menu item (VendorUI / AdminUI)
INSERT INTO MenuItems (vendor_id, item_name, price)
VALUES (1, 'Caesar Salad', 7.99);

-- ============================================================
-- UPDATE
-- ============================================================

-- Vendor updates order preparation status (VendorUI)
UPDATE Orders
SET status = 'Preparing'
WHERE order_id = 1 AND vendor_id = 1;

-- Vendor advances order to ready for pickup (VendorUI)
UPDATE Orders
SET status = 'Ready for Pickup'
WHERE order_id = 1 AND vendor_id = 1;

-- Driver updates delivery status (DriverUI)
UPDATE Deliveries
SET delivery_status = 'Picked Up'
WHERE delivery_id = 1 AND driver_id = 1;

-- Driver marks delivery as delivered; also syncs Orders status (DriverUI)
UPDATE Deliveries
SET delivery_status = 'Delivered'
WHERE delivery_id = 1 AND driver_id = 1;

UPDATE Orders o
JOIN Deliveries d ON o.order_id = d.order_id
SET o.status = 'Delivered'
WHERE d.delivery_id = 1;

-- ============================================================
-- DELETE
-- ============================================================

-- Admin deletes an order by ID (AdminUI)
DELETE FROM Orders WHERE order_id = 4;

-- Admin deletes a customer by ID (AdminUI)
DELETE FROM Customers WHERE customer_id = 2;
