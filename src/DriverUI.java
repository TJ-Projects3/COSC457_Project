import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DriverUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = DbConfig.getPassword();

    int driverId;
    JTextField deliveryIdField;
    JComboBox<String> statusBox;
    JTextArea output;

    public DriverUI() {
        this(1);
    }

    public DriverUI(int driverId) {
        this.driverId = driverId;

        setTitle("Driver Screen");
        setSize(550, 350);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(2, 2));

        top.add(new JLabel("Delivery ID:"));
        deliveryIdField = new JTextField();
        top.add(deliveryIdField);

        top.add(new JLabel("Status:"));
        statusBox = new JComboBox<>(new String[] { "Assigned", "Picked Up", "Out for Delivery", "Delivered" });
        top.add(statusBox);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 3));

        JButton viewBtn = new JButton("View Deliveries");
        JButton infoBtn = new JButton("View Info");
        JButton updateBtn = new JButton("Update");

        bottom.add(viewBtn);
        bottom.add(infoBtn);
        bottom.add(updateBtn);

        add(bottom, BorderLayout.SOUTH);

        viewBtn.addActionListener(e -> viewDeliveries());
        infoBtn.addActionListener(e -> viewInfo());
        updateBtn.addActionListener(e -> updateDelivery());

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void viewDeliveries() {
        output.setText("");

        try (Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT d.delivery_id, d.order_id, d.delivery_status, v.restaurant_name " +
                                "FROM Deliveries d " +
                                "JOIN Orders o ON d.order_id=o.order_id " +
                                "JOIN Vendors v ON o.vendor_id=v.vendor_id " +
                                "WHERE d.driver_id=?")) {

            ps.setInt(1, driverId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                output.append(rs.getInt("delivery_id") + " | Order " +
                        rs.getInt("order_id") + " | " +
                        rs.getString("restaurant_name") + " | " +
                        rs.getString("delivery_status") + "\n");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewInfo() {
        output.setText("");

        try (Connection conn = getConn();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT d.delivery_id, v.restaurant_name, v.address, c.customer_name, " +
                                "c.address AS customer_address, d.delivery_status " +
                                "FROM Deliveries d " +
                                "JOIN Orders o ON d.order_id=o.order_id " +
                                "JOIN Vendors v ON o.vendor_id=v.vendor_id " +
                                "JOIN Customers c ON o.customer_id=c.customer_id " +
                                "WHERE d.delivery_id=? AND d.driver_id=?")) {

            ps.setInt(1, Integer.parseInt(deliveryIdField.getText()));
            ps.setInt(2, driverId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                output.setText("Pickup: " + rs.getString("restaurant_name") +
                        " - " + rs.getString("address") + "\n" +
                        "Deliver to: " + rs.getString("customer_name") +
                        " - " + rs.getString("customer_address") + "\n" +
                        "Status: " + rs.getString("delivery_status"));
            } else {
                output.setText("Delivery not found.");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void updateDelivery() {
        output.setText("");

        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Deliveries SET delivery_status=? WHERE delivery_id=? AND driver_id=?");

            ps.setString(1, statusBox.getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(deliveryIdField.getText()));
            ps.setInt(3, driverId);

            int rows = ps.executeUpdate();
            ps.close();

            if (statusBox.getSelectedItem().toString().equals("Delivered")) {
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE Orders o JOIN Deliveries d ON o.order_id=d.order_id " +
                                "SET o.status='Delivered' WHERE d.delivery_id=?");

                ps2.setInt(1, Integer.parseInt(deliveryIdField.getText()));
                ps2.executeUpdate();
                ps2.close();
            }

            output.setText(rows + " delivery updated.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }
}
