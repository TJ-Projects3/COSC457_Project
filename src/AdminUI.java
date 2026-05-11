import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = DbConfig.getPassword();

    JTextField deleteOrderField, deleteCustomerField;
    JTextArea output;

    public AdminUI() {
        setTitle("Admin Screen");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(2, 2));

        top.add(new JLabel("Delete Order ID:"));
        deleteOrderField = new JTextField();
        top.add(deleteOrderField);

        top.add(new JLabel("Delete Customer ID:"));
        deleteCustomerField = new JTextField();
        top.add(deleteCustomerField);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 4));

        JButton customersBtn = new JButton("Customers");
        JButton vendorsBtn = new JButton("Vendors");
        JButton ordersBtn = new JButton("Orders");
        JButton deliveriesBtn = new JButton("Deliveries");
        JButton reportBtn = new JButton("Report");
        JButton summaryBtn = new JButton("Summary");
        JButton deleteOrderBtn = new JButton("Delete Order");
        JButton deleteCustomerBtn = new JButton("Delete Customer");

        bottom.add(customersBtn);
        bottom.add(vendorsBtn);
        bottom.add(ordersBtn);
        bottom.add(deliveriesBtn);
        bottom.add(reportBtn);
        bottom.add(summaryBtn);
        bottom.add(deleteOrderBtn);
        bottom.add(deleteCustomerBtn);

        add(bottom, BorderLayout.SOUTH);

        customersBtn.addActionListener(e -> viewCustomers());
        vendorsBtn.addActionListener(e -> viewVendors());
        ordersBtn.addActionListener(e -> viewOrders());
        deliveriesBtn.addActionListener(e -> viewDeliveries());
        reportBtn.addActionListener(e -> fullReport());
        summaryBtn.addActionListener(e -> summaryReport());
        deleteOrderBtn.addActionListener(e -> deleteOrder());
        deleteCustomerBtn.addActionListener(e -> deleteCustomer());

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void viewCustomers() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT customer_id, customer_name, email FROM Customers")) {

            while (rs.next()) {
                output.append(rs.getInt("customer_id") + " | " +
                        rs.getString("customer_name") + " | " +
                        rs.getString("email") + "\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewVendors() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT vendor_id, restaurant_name, address FROM Vendors")) {

            while (rs.next()) {
                output.append(rs.getInt("vendor_id") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("address") + "\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewOrders() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT order_id, customer_id, vendor_id, status, total FROM Orders")) {

            while (rs.next()) {
                output.append(rs.getInt("order_id") + " | Customer " +
                        rs.getInt("customer_id") + " | Vendor " +
                        rs.getInt("vendor_id") + " | " +
                        rs.getString("status") + " | $" + rs.getDouble("total") + "\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewDeliveries() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT d.delivery_id, d.order_id, dr.driver_name, d.delivery_status " +
                                "FROM Deliveries d JOIN Drivers dr ON d.driver_id=dr.driver_id")) {

            while (rs.next()) {
                output.append(rs.getInt("delivery_id") + " | Order " +
                        rs.getInt("order_id") + " | " +
                        rs.getString("driver_name") + " | " +
                        rs.getString("delivery_status") + "\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void fullReport() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT o.order_id, c.customer_name, v.restaurant_name, dr.driver_name, " +
                                "o.status, d.delivery_status " +
                                "FROM Orders o " +
                                "JOIN Customers c ON o.customer_id=c.customer_id " +
                                "JOIN Vendors v ON o.vendor_id=v.vendor_id " +
                                "LEFT JOIN Deliveries d ON o.order_id=d.order_id " +
                                "LEFT JOIN Drivers dr ON d.driver_id=dr.driver_id")) {

            while (rs.next()) {
                output.append(rs.getInt("order_id") + " | " +
                        rs.getString("customer_name") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("driver_name") + " | " +
                        rs.getString("status") + " | " +
                        rs.getString("delivery_status") + "\n");
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void summaryReport() {
        output.setText("");

        try (Connection conn = getConn();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT COUNT(*) AS total_orders, SUM(total) AS total_revenue FROM Orders")) {

            if (rs.next()) {
                output.setText("Orders: " + rs.getInt("total_orders") +
                        "\nRevenue: $" + rs.getDouble("total_revenue"));
            }

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void deleteOrder() {
        output.setText("");

        try (Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Orders WHERE order_id=?")) {

            ps.setInt(1, Integer.parseInt(deleteOrderField.getText()));
            int rows = ps.executeUpdate();
            output.setText(rows + " order deleted.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void deleteCustomer() {
        output.setText("");

        try (Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Customers WHERE customer_id=?")) {

            ps.setInt(1, Integer.parseInt(deleteCustomerField.getText()));
            int rows = ps.executeUpdate();
            output.setText(rows + " customer deleted.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }
}
