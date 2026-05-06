CREATE DATABASE food_delivery_db;

USE food_delivery_db;

CREATE TABLE Customers (
	id INT PRIMARY KEY auto_increment,
    FirstName VARCHAR(20),
    LastName VARCHAR(20)
);

INSERT INTO Customers(FirstName, LastName)
VALUES ('Alice', 'Fields'), ('Bob', 'Tegget'), ('Carol', 'DaVine');