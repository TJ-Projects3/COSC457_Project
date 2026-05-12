DROP DATABASE IF EXISTS food_delivery_db;

CREATE DATABASE food_delivery_db;

USE food_delivery_db;

CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255) NOT NULL
);

CREATE TABLE Vendors (
    vendor_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    restaurant_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    distance_miles DECIMAL(5,2) DEFAULT 5.00
);

CREATE TABLE Drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    driver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE Admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    admin_name VARCHAR(100) NOT NULL
);

CREATE TABLE MenuItems (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    price DECIMAL(6,2) NOT NULL,

    FOREIGN KEY (vendor_id)
        REFERENCES Vendors(vendor_id)
        ON DELETE CASCADE
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    vendor_id INT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    status ENUM(
        'Placed',
        'Preparing',
        'Ready for Pickup',
        'Delivered'
    ) DEFAULT 'Placed',

    total DECIMAL(8,2) NOT NULL,

    FOREIGN KEY (customer_id)
        REFERENCES Customers(customer_id)
        ON DELETE CASCADE,

    FOREIGN KEY (vendor_id)
        REFERENCES Vendors(vendor_id)
        ON DELETE CASCADE
);

CREATE TABLE OrderItems (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,

    FOREIGN KEY (order_id)
        REFERENCES Orders(order_id)
        ON DELETE CASCADE,

    FOREIGN KEY (item_id)
        REFERENCES MenuItems(item_id)
        ON DELETE CASCADE
);

CREATE TABLE Deliveries (
    delivery_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL UNIQUE,
    driver_id INT NOT NULL,

    delivery_status ENUM(
        'Assigned',
        'Picked Up',
        'Out for Delivery',
        'Delivered'
    ) DEFAULT 'Assigned',

    FOREIGN KEY (order_id)
        REFERENCES Orders(order_id)
        ON DELETE CASCADE,

    FOREIGN KEY (driver_id)
        REFERENCES Drivers(driver_id)
        ON DELETE CASCADE
);

INSERT INTO Customers (
    email,
    password,
    customer_name,
    phone,
    address
)
VALUES
(
    'customer@test.com',
    '1234',
    'John Smith',
    '111-2222',
    'Towson'
),
(
    'customer2@test.com',
    '1234',
    'Maria Johnson',
    '111-3333',
    'Baltimore'
);

INSERT INTO Vendors (
    email,
    password,
    restaurant_name,
    address,
    distance_miles
)
VALUES
(
    'vendor@test.com',
    '1234',
    'Towson Pizza',
    'York Road',
    2.50
),
(
    'burger@test.com',
    '1234',
    'Tiger Burgers',
    'Campus Ave',
    4.00
),
(
    'wings@test.com',
    '1234',
    'Campus Wings',
    'Towson Circle',
    6.25
),
(
    'vendornew@test.com',
    '1234',
    'Demo Pizza',
    'Towson',
    5.00
);

INSERT INTO Drivers (
    email,
    password,
    driver_name,
    phone
)
VALUES
(
    'driver@test.com',
    '1234',
    'Mike Driver',
    '777-1111'
),
(
    'driver2@test.com',
    '1234',
    'Sarah Driver',
    '777-2222'
);

INSERT INTO Admins (
    email,
    password,
    admin_name
)
VALUES
(
    'admin@test.com',
    '1234',
    'Main Admin'
);

INSERT INTO MenuItems (
    vendor_id,
    item_name,
    price
)
VALUES
(1, 'Pepperoni Pizza', 12.99),
(1, 'Cheese Pizza', 10.99),
(1, 'Garlic Knots', 5.99),
(2, 'Classic Burger', 8.99),
(2, 'French Fries', 3.99),
(2, 'Milkshake', 4.99),
(3, 'Buffalo Wings', 11.99),
(3, 'BBQ Wings', 12.99),
(4, 'Veggie Pizza', 13.99),
(4, 'Chicken Pizza', 15.99);

INSERT INTO Orders (
    customer_id,
    vendor_id,
    status,
    total
)
VALUES
(1, 1, 'Placed', 18.98),
(1, 2, 'Preparing', 12.98),
(2, 3, 'Ready for Pickup', 19.98),
(1, 4, 'Placed', 25.99),
(2, 4, 'Preparing', 16.99);

INSERT INTO OrderItems (
    order_id,
    item_id,
    quantity
)
VALUES
(1, 1, 1),
(1, 3, 1),
(2, 4, 1),
(2, 6, 1),
(3, 7, 1),
(4, 9, 2),
(5, 10, 1);

INSERT INTO Deliveries (
    order_id,
    driver_id,
    delivery_status
)
VALUES
(1, 1, 'Assigned'),
(2, 2, 'Picked Up'),
(3, 1, 'Out for Delivery'),
(4, 2, 'Assigned'),
(5, 1, 'Picked Up');

SELECT * FROM Customers;
SELECT * FROM Vendors;
SELECT * FROM Drivers;
SELECT * FROM MenuItems;
SELECT * FROM Orders;
SELECT * FROM OrderItems;
SELECT * FROM Deliveries;
