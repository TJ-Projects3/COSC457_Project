import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = "Shad1235!";

    int customerId;
    JComboBox<String> restaurantBox, menuBox;
    JTextField quantityField, orderIdField;
    JTextArea output;

    public CustomerUI() {
        this(1);
    }

    public CustomerUI(int customerId) {
        this.customerId = customerId;

        setTitle("Customer Screen");
        setSize(550, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(4, 2));

        top.add(new JLabel("Restaurant:"));
        restaurantBox = new JComboBox<>();
        top.add(restaurantBox);

        top.add(new JLabel("Menu Item:"));
        menuBox = new JComboBox<>();
        top.add(menuBox);

        top.add(new JLabel("Quantity:"));
        quantityField = new JTextField("1");
        top.add(quantityField);

        top.add(new JLabel("Order ID:"));
        orderIdField = new JTextField();
        top.add(orderIdField);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 5));

        JButton viewBtn = new JButton("View Restaurants");
        JButton menuBtn = new JButton("View Menu");
        JButton orderBtn = new JButton("Place Order");
        JButton statusBtn = new JButton("Order Status");
        JButton myOrdersBtn = new JButton("My Orders");

        bottom.add(viewBtn);
        bottom.add(menuBtn);
        bottom.add(orderBtn);
        bottom.add(statusBtn);
        bottom.add(myOrdersBtn);

        add(bottom, BorderLayout.SOUTH);

        restaurantBox.addActionListener(e -> loadMenuItems());
        viewBtn.addActionListener(e -> viewRestaurants());
        menuBtn.addActionListener(e -> viewMenuItems());
        orderBtn.addActionListener(e -> placeOrder());
        statusBtn.addActionListener(e -> viewOrderStatus());
        myOrdersBtn.addActionListener(e -> viewMyOrders());

        loadRestaurants();
        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private int selectedId(JComboBox<String> box) {
        String selected = box.getSelectedItem().toString();
        return Integer.parseInt(selected.substring(0, selected.indexOf(" - ")));
    }

    private void loadRestaurants() {
        restaurantBox.removeAllItems();

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT vendor_id, restaurant_name FROM Vendors " +
                     "WHERE distance_miles <= 20 ORDER BY restaurant_name");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                restaurantBox.addItem(rs.getInt("vendor_id") + " - " + rs.getString("restaurant_name"));
            }

            loadMenuItems();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void loadMenuItems() {
        menuBox.removeAllItems();

        if (restaurantBox.getSelectedItem() == null) {
            return;
        }

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT item_id, item_name, price FROM MenuItems " +
                     "WHERE vendor_id=? ORDER BY item_name")) {

            ps.setInt(1, selectedId(restaurantBox));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                menuBox.addItem(rs.getInt("item_id") + " - " +
                        rs.getString("item_name") + " ($" + rs.getDouble("price") + ")");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewRestaurants() {
        output.setText("");

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT vendor_id, restaurant_name, address, distance_miles " +
                     "FROM Vendors WHERE distance_miles <= 20")) {

            while (rs.next()) {
                output.append(rs.getInt("vendor_id") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("address") + " | " +
                        rs.getDouble("distance_miles") + " miles\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewMenuItems() {
        output.setText("");

        if (restaurantBox.getSelectedItem() == null) {
            return;
        }

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT item_name, price FROM MenuItems WHERE vendor_id=?")) {

            ps.setInt(1, selectedId(restaurantBox));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                output.append(rs.getString("item_name") + " | $" + rs.getDouble("price") + "\n");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void placeOrder() {
        output.setText("");

        try (Connection conn = getConn()) {
            int quantity = Integer.parseInt(quantityField.getText());
            int vendorId = selectedId(restaurantBox);
            int itemId = selectedId(menuBox);

            PreparedStatement itemPs = conn.prepareStatement("SELECT price FROM MenuItems WHERE item_id=?");
            itemPs.setInt(1, itemId);

            ResultSet itemRs = itemPs.executeQuery();
            itemRs.next();

            double total = itemRs.getDouble("price") * quantity;

            itemRs.close();
            itemPs.close();

            if (total < 5) {
                output.setText("Order must be at least $5.");
                return;
            }

            PreparedStatement orderPs = conn.prepareStatement(
                    "INSERT INTO Orders(customer_id, vendor_id, status, total) " +
                    "VALUES (?, ?, 'Placed', ?)",
                    Statement.RETURN_GENERATED_KEYS);

            orderPs.setInt(1, customerId);
            orderPs.setInt(2, vendorId);
            orderPs.setDouble(3, total);
            orderPs.executeUpdate();

            ResultSet keys = orderPs.getGeneratedKeys();
            keys.next();

            int orderId = keys.getInt(1);

            keys.close();
            orderPs.close();

            PreparedStatement detailPs = conn.prepareStatement(
                    "INSERT INTO OrderItems(order_id, item_id, quantity) VALUES (?, ?, ?)");

            detailPs.setInt(1, orderId);
            detailPs.setInt(2, itemId);
            detailPs.setInt(3, quantity);
            detailPs.executeUpdate();
            detailPs.close();

            PreparedStatement driverPs = conn.prepareStatement("SELECT driver_id FROM Drivers ORDER BY driver_id LIMIT 1");
            ResultSet driverRs = driverPs.executeQuery();

            if (driverRs.next()) {
                PreparedStatement deliveryPs = conn.prepareStatement(
                        "INSERT INTO Deliveries(order_id, driver_id, delivery_status) VALUES (?, ?, 'Assigned')");

                deliveryPs.setInt(1, orderId);
                deliveryPs.setInt(2, driverRs.getInt("driver_id"));
                deliveryPs.executeUpdate();
                deliveryPs.close();
            }

            driverRs.close();
            driverPs.close();

            output.setText("Order placed. Order ID: " + orderId + " Total: $" + total);

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewMyOrders() {
        output.setText("");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT o.order_id, v.restaurant_name, o.status, o.total, o.order_date " +
                     "FROM Orders o JOIN Vendors v ON o.vendor_id=v.vendor_id " +
                     "WHERE o.customer_id=? ORDER BY o.order_id DESC")) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                output.append(rs.getInt("order_id") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("status") + " | $" +
                        rs.getDouble("total") + " | " +
                        rs.getString("order_date") + "\n");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewOrderStatus() {
        output.setText("");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT order_id, status, total FROM Orders WHERE order_id=? AND customer_id=?")) {

            ps.setInt(1, Integer.parseInt(orderIdField.getText()));
            ps.setInt(2, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                output.setText(rs.getInt("order_id") + " | " +
                        rs.getString("status") + " | $" + rs.getDouble("total"));
            } else {
                output.setText("Order not found.");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }
}

