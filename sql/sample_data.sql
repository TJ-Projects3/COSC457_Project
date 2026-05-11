INSERT INTO Customers (email, password, customer_name, phone, address) VALUES
('customer@test.com', '1234', 'John Smith', '111-2222', 'Towson'),
('customer2@test.com', '1234', 'Maria Johnson', '111-3333', 'Baltimore');

INSERT INTO Vendors (email, password, restaurant_name, address, distance_miles) VALUES
('vendor@test.com', '1234', 'Towson Pizza', 'York Road', 2.50),
('burger@test.com', '1234', 'Tiger Burgers', 'Campus Ave', 4.00),
('wings@test.com', '1234', 'Campus Wings', 'Towson Circle', 6.25),
('far@test.com', '1234', 'Far Away Tacos', 'Outside Delivery Zone', 25.00);

INSERT INTO Drivers (email, password, driver_name, phone) VALUES
('driver@test.com', '1234', 'Mike Driver', '777-1111'),
('driver2@test.com', '1234', 'Sarah Driver', '777-2222');

INSERT INTO Admins (email, password, admin_name) VALUES
('admin@test.com', '1234', 'Main Admin');

INSERT INTO MenuItems (vendor_id, item_name, price) VALUES
(1, 'Pepperoni Pizza', 12.99),
(1, 'Cheese Pizza', 10.99),
(1, 'Garlic Knots', 5.99),
(2, 'Classic Burger', 8.99),
(2, 'French Fries', 3.99),
(2, 'Milkshake', 4.99),
(3, 'Buffalo Wings', 11.99),
(3, 'BBQ Wings', 12.99),
(3, 'Mozzarella Sticks', 6.99),
(4, 'Chicken Taco', 7.99),
(4, 'Steak Taco', 8.99);

INSERT INTO Orders (customer_id, vendor_id, status, total) VALUES
(1, 1, 'Placed', 18.98),
(1, 2, 'Preparing', 12.98),
(2, 3, 'Ready for Pickup', 19.98);

INSERT INTO OrderItems (order_id, item_id, quantity) VALUES
(1, 1, 1),
(1, 3, 1),
(2, 4, 1),
(2, 6, 1),
(3, 7, 1),
(3, 9, 1);

INSERT INTO Deliveries (order_id, driver_id, delivery_status) VALUES
(1, 1, 'Assigned'),
(2, 2, 'Picked Up'),
(3, 1, 'Out for Delivery');