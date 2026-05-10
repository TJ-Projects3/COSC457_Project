import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = "yourpasswordhere";

    JTextArea output;

    // Customer fields
    JTextField customerIdField, vendorIdField, itemIdField, quantityField, orderIdField;

    // Restaurant owner fields
    JTextField ownerVendorIdField, ownerOrderIdField, newItemNameField, newItemPriceField;

    // Driver fields
    JTextField driverIdField, deliveryIdField;

    public Main() {
        setTitle("Food Delivery Database Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Customer", customerPanel());
        tabs.add("Restaurant Owner", ownerPanel());
        tabs.add("Delivery Driver", driverPanel());

        add(tabs, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private JPanel customerPanel() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel fields = new JPanel(new GridLayout(5, 2));

        customerIdField = new JTextField();
        vendorIdField = new JTextField();
        itemIdField = new JTextField();
        quantityField = new JTextField();
        orderIdField = new JTextField();

        fields.add(new JLabel("Customer ID:"));
        fields.add(customerIdField);

        fields.add(new JLabel("Vendor ID:"));
        fields.add(vendorIdField);

        fields.add(new JLabel("Item ID:"));
        fields.add(itemIdField);

        fields.add(new JLabel("Quantity:"));
        fields.add(quantityField);

        fields.add(new JLabel("Order ID for Status:"));
        fields.add(orderIdField);

        JPanel buttons = new JPanel(new GridLayout(1, 4));

        JButton viewRestaurantsBtn = new JButton("View Restaurants");
        JButton viewMenuBtn = new JButton("View Menu Items");
        JButton placeOrderBtn = new JButton("Place Order");
        JButton viewStatusBtn = new JButton("View Order Status");

        buttons.add(viewRestaurantsBtn);
        buttons.add(viewMenuBtn);
        buttons.add(placeOrderBtn);
        buttons.add(viewStatusBtn);

        viewRestaurantsBtn.addActionListener(e -> viewRestaurants());
        viewMenuBtn.addActionListener(e -> viewMenuItems());
        placeOrderBtn.addActionListener(e -> placeOrder());
        viewStatusBtn.addActionListener(e -> viewOrderStatus());

        main.add(fields, BorderLayout.NORTH);
        main.add(buttons, BorderLayout.SOUTH);

        return main;
    }

    private JPanel ownerPanel() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel fields = new JPanel(new GridLayout(4, 2));

        ownerVendorIdField = new JTextField();
        ownerOrderIdField = new JTextField();
        newItemNameField = new JTextField();
        newItemPriceField = new JTextField();

        fields.add(new JLabel("Vendor ID:"));
        fields.add(ownerVendorIdField);

        fields.add(new JLabel("Order ID to Update:"));
        fields.add(ownerOrderIdField);

        fields.add(new JLabel("New Menu Item Name:"));
        fields.add(newItemNameField);

        fields.add(new JLabel("New Menu Item Price:"));
        fields.add(newItemPriceField);

        JPanel buttons = new JPanel(new GridLayout(1, 4));

        JButton viewOrdersBtn = new JButton("View Incoming Orders");
        JButton addItemBtn = new JButton("Add Menu Item");
        JButton preparingBtn = new JButton("Set Preparing");
        JButton readyBtn = new JButton("Set Ready for Pickup");

        buttons.add(viewOrdersBtn);
        buttons.add(addItemBtn);
        buttons.add(preparingBtn);
        buttons.add(readyBtn);

        viewOrdersBtn.addActionListener(e -> viewIncomingOrders());
        addItemBtn.addActionListener(e -> addMenuItem());
        preparingBtn.addActionListener(e -> updateOrderStatus("Preparing"));
        readyBtn.addActionListener(e -> updateOrderStatus("Ready for Pickup"));

        main.add(fields, BorderLayout.NORTH);
        main.add(buttons, BorderLayout.SOUTH);

        return main;
    }

    private JPanel driverPanel() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel fields = new JPanel(new GridLayout(2, 2));

        driverIdField = new JTextField();
        deliveryIdField = new JTextField();

        fields.add(new JLabel("Driver ID:"));
        fields.add(driverIdField);

        fields.add(new JLabel("Delivery ID to Update:"));
        fields.add(deliveryIdField);

        JPanel buttons = new JPanel(new GridLayout(1, 4));

        JButton viewDeliveriesBtn = new JButton("View Assigned Deliveries");
        JButton pickedUpBtn = new JButton("Set Picked Up");
        JButton outForDeliveryBtn = new JButton("Set Out for Delivery");
        JButton deliveredBtn = new JButton("Set Delivered");

        buttons.add(viewDeliveriesBtn);
        buttons.add(pickedUpBtn);
        buttons.add(outForDeliveryBtn);
        buttons.add(deliveredBtn);

        viewDeliveriesBtn.addActionListener(e -> viewAssignedDeliveries());
        pickedUpBtn.addActionListener(e -> updateDeliveryStatus("Picked Up"));
        outForDeliveryBtn.addActionListener(e -> updateDeliveryStatus("Out for Delivery"));
        deliveredBtn.addActionListener(e -> updateDeliveryStatus("Delivered"));

        main.add(fields, BorderLayout.NORTH);
        main.add(buttons, BorderLayout.SOUTH);

        return main;
    }

    private void viewRestaurants() {
        output.setText("");

        try (
                Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT vendor_id, restaurant_name, address, distance_miles FROM Vendors"
                )
        ) {
            output.append("RESTAURANTS\n");
            output.append("------------------------------------\n");

            while (rs.next()) {
                output.append(
                        rs.getInt("vendor_id") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("address") + " | " +
                        rs.getDouble("distance_miles") + " miles\n"
                );
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewMenuItems() {
        output.setText("");

        try (
                Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT item_id, vendor_id, item_name, price FROM MenuItems"
                )
        ) {
            output.append("MENU ITEMS\n");
            output.append("------------------------------------\n");

            while (rs.next()) {
                output.append(
                        rs.getInt("item_id") + " | Vendor " +
                        rs.getInt("vendor_id") + " | " +
                        rs.getString("item_name") + " | $" +
                        rs.getDouble("price") + "\n"
                );
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void placeOrder() {
        output.setText("");

        try {
            int customerId = Integer.parseInt(customerIdField.getText());
            int vendorId = Integer.parseInt(vendorIdField.getText());
            int itemId = Integer.parseInt(itemIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            try (Connection conn = getConn()) {

                PreparedStatement priceStmt = conn.prepareStatement(
                        "SELECT price FROM MenuItems WHERE item_id = ?"
                );

                priceStmt.setInt(1, itemId);
                ResultSet rs = priceStmt.executeQuery();

                if (!rs.next()) {
                    output.setText("Item not found.");
                    return;
                }

                double price = rs.getDouble("price");
                double total = price * quantity;

                PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO Orders(customer_id, vendor_id, status, total) VALUES (?, ?, 'Placed', ?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                orderStmt.setInt(1, customerId);
                orderStmt.setInt(2, vendorId);
                orderStmt.setDouble(3, total);
                orderStmt.executeUpdate();

                ResultSet keys = orderStmt.getGeneratedKeys();
                int newOrderId = 0;

                if (keys.next()) {
                    newOrderId = keys.getInt(1);
                }

                PreparedStatement itemStmt = conn.prepareStatement(
                        "INSERT INTO OrderItems(order_id, item_id, quantity) VALUES (?, ?, ?)"
                );

                itemStmt.setInt(1, newOrderId);
                itemStmt.setInt(2, itemId);
                itemStmt.setInt(3, quantity);
                itemStmt.executeUpdate();

                PreparedStatement deliveryStmt = conn.prepareStatement(
                        "INSERT INTO Deliveries(order_id, driver_id, delivery_status) VALUES (?, 1, 'Assigned')"
                );

                deliveryStmt.setInt(1, newOrderId);
                deliveryStmt.executeUpdate();

                output.setText(
                        "Order placed successfully!\n" +
                        "New Order ID: " + newOrderId + "\n" +
                        "Status: Placed\n" +
                        "Delivery Status: Assigned\n" +
                        "Total: $" + total
                );
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewOrderStatus() {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT order_id, customer_id, vendor_id, status, total FROM Orders WHERE order_id = ?"
                )
        ) {
            ps.setInt(1, Integer.parseInt(orderIdField.getText()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                output.append("ORDER STATUS\n");
                output.append("------------------------------------\n");
                output.append("Order ID: " + rs.getInt("order_id") + "\n");
                output.append("Customer ID: " + rs.getInt("customer_id") + "\n");
                output.append("Vendor ID: " + rs.getInt("vendor_id") + "\n");
                output.append("Status: " + rs.getString("status") + "\n");
                output.append("Total: $" + rs.getDouble("total") + "\n");
            } else {
                output.setText("Order not found.");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewIncomingOrders() {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT order_id, customer_id, vendor_id, status, total FROM Orders WHERE vendor_id = ?"
                )
        ) {
            ps.setInt(1, Integer.parseInt(ownerVendorIdField.getText()));

            ResultSet rs = ps.executeQuery();

            output.append("INCOMING ORDERS FOR VENDOR " + ownerVendorIdField.getText() + "\n");
            output.append("------------------------------------\n");

            while (rs.next()) {
                output.append(
                        "Order " + rs.getInt("order_id") +
                        " | Customer " + rs.getInt("customer_id") +
                        " | Status: " + rs.getString("status") +
                        " | Total: $" + rs.getDouble("total") + "\n"
                );
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void addMenuItem() {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO MenuItems(vendor_id, item_name, price) VALUES (?, ?, ?)"
                )
        ) {
            ps.setInt(1, Integer.parseInt(ownerVendorIdField.getText()));
            ps.setString(2, newItemNameField.getText());
            ps.setDouble(3, Double.parseDouble(newItemPriceField.getText()));

            ps.executeUpdate();

            output.setText("Menu item added successfully.\n\nRun View Menu Items to see it.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void updateOrderStatus(String newStatus) {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Orders SET status = ? WHERE order_id = ?"
                )
        ) {
            ps.setString(1, newStatus);
            ps.setInt(2, Integer.parseInt(ownerOrderIdField.getText()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                output.setText(
                        "Order status updated successfully.\n" +
                        "Order ID: " + ownerOrderIdField.getText() + "\n" +
                        "New Status: " + newStatus + "\n\n" +
                        "Customer can now check View Order Status and see the same update."
                );
            } else {
                output.setText("No order found with that ID.");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewAssignedDeliveries() {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT " +
                                "d.delivery_id, " +
                                "d.order_id, " +
                                "d.driver_id, " +
                                "d.delivery_status, " +
                                "v.restaurant_name, " +
                                "v.address AS pickup_address, " +
                                "c.customer_name, " +
                                "c.address AS delivery_address " +
                        "FROM Deliveries d " +
                        "JOIN Orders o ON d.order_id = o.order_id " +
                        "JOIN Vendors v ON o.vendor_id = v.vendor_id " +
                        "JOIN Customers c ON o.customer_id = c.customer_id " +
                        "WHERE d.driver_id = ?"
                )
        ) {
            ps.setInt(1, Integer.parseInt(driverIdField.getText()));

            ResultSet rs = ps.executeQuery();

            output.append("ASSIGNED DELIVERIES FOR DRIVER " + driverIdField.getText() + "\n");
            output.append("------------------------------------\n");

            while (rs.next()) {
                output.append(
                        "Delivery ID: " + rs.getInt("delivery_id") + "\n" +
                        "Order ID: " + rs.getInt("order_id") + "\n" +
                        "Driver ID: " + rs.getInt("driver_id") + "\n" +
                        "Delivery Status: " + rs.getString("delivery_status") + "\n" +
                        "Pickup Restaurant: " + rs.getString("restaurant_name") + "\n" +
                        "Pickup Address: " + rs.getString("pickup_address") + "\n" +
                        "Customer: " + rs.getString("customer_name") + "\n" +
                        "Delivery Address: " + rs.getString("delivery_address") + "\n" +
                        "------------------------------------\n"
                );
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void updateDeliveryStatus(String newStatus) {
        output.setText("");

        try (
                Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Deliveries SET delivery_status = ? WHERE delivery_id = ?"
                )
        ) {
            ps.setString(1, newStatus);
            ps.setInt(2, Integer.parseInt(deliveryIdField.getText()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                output.setText(
                        "Delivery status updated successfully.\n" +
                        "Delivery ID: " + deliveryIdField.getText() + "\n" +
                        "New Status: " + newStatus + "\n\n" +
                        "Verify in MariaDB/MySQL:\n" +
                        "SELECT * FROM Deliveries;"
                );
            } else {
                output.setText("No delivery found with that ID.");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
