DROP DATABASE IF EXISTS food_delivery_db;

CREATE DATABASE food_delivery_db;

USE food_delivery_db;

-- =========================================
-- CUSTOMERS
-- =========================================
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(100)
);

-- =========================================
-- VENDORS / RESTAURANTS
-- =========================================
CREATE TABLE Vendors (
    vendor_id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_name VARCHAR(100) NOT NULL,
    address VARCHAR(100),
    distance_miles DECIMAL(5,2)
);

-- =========================================
-- DRIVERS
-- =========================================
CREATE TABLE Drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

-- =========================================
-- MENU ITEMS
-- =========================================
CREATE TABLE MenuItems (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    price DECIMAL(6,2) NOT NULL,

    FOREIGN KEY (vendor_id)
    REFERENCES Vendors(vendor_id)
);

-- =========================================
-- ORDERS
-- =========================================
CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    vendor_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'Placed',
    total DECIMAL(8,2),

    FOREIGN KEY (customer_id)
    REFERENCES Customers(customer_id),

    FOREIGN KEY (vendor_id)
    REFERENCES Vendors(vendor_id)
);

-- =========================================
-- ORDER ITEMS
-- =========================================
CREATE TABLE OrderItems (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,

    FOREIGN KEY (order_id)
    REFERENCES Orders(order_id),

    FOREIGN KEY (item_id)
    REFERENCES MenuItems(item_id)
);

-- =========================================
-- DELIVERIES
-- =========================================
CREATE TABLE Deliveries (
    delivery_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    driver_id INT NOT NULL,
    delivery_status VARCHAR(50) DEFAULT 'Assigned',

    FOREIGN KEY (order_id)
    REFERENCES Orders(order_id),

    FOREIGN KEY (driver_id)
    REFERENCES Drivers(driver_id)
);

-- =========================================
-- SAMPLE CUSTOMERS
-- =========================================
INSERT INTO Customers(customer_name, phone, address)
VALUES
('John Smith', '111-2222', 'Towson'),
('Maria Johnson', '111-3333', 'Baltimore');

-- =========================================
-- SAMPLE RESTAURANTS
-- =========================================
INSERT INTO Vendors(restaurant_name, address, distance_miles)
VALUES
('Towson Pizza', 'York Road', 2.5);

-- =========================================
-- SAMPLE DRIVERS
-- =========================================
INSERT INTO Drivers(driver_name, phone)
VALUES
('Mike Driver', '777-1111'),
('Sarah Driver', '777-2222');

-- =========================================
-- SAMPLE MENU ITEMS
-- =========================================
INSERT INTO MenuItems(vendor_id, item_name, price)
VALUES
(1, 'Pepperoni Pizza', 12.99),
(1, 'Cheese Pizza', 10.99),
(1, 'Garlic Knots', 5.99);
