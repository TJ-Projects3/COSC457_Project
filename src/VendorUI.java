import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VendorUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = "Shad1235!";

    int vendorId;
    JTextField orderIdField;
    JComboBox<String> statusBox;
    JTextArea output;

    public VendorUI() {
        this(1);
    }

    public VendorUI(int vendorId) {
        this.vendorId = vendorId;

        setTitle("Vendor Screen");
        setSize(500, 350);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(2, 2));

        top.add(new JLabel("Order ID:"));
        orderIdField = new JTextField();
        top.add(orderIdField);

        top.add(new JLabel("Status:"));
        statusBox = new JComboBox<>(new String[] {"Placed", "Preparing", "Ready for Pickup"});
        top.add(statusBox);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 3));

        JButton viewBtn = new JButton("View Orders");
        JButton menuBtn = new JButton("View Menu");
        JButton updateBtn = new JButton("Update");

        bottom.add(viewBtn);
        bottom.add(menuBtn);
        bottom.add(updateBtn);

        add(bottom, BorderLayout.SOUTH);

        viewBtn.addActionListener(e -> viewOrders());
        menuBtn.addActionListener(e -> viewMenu());
        updateBtn.addActionListener(e -> updateOrder());

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void viewOrders() {
        output.setText("");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT o.order_id, c.customer_name, o.status, o.total " +
                     "FROM Orders o JOIN Customers c ON o.customer_id=c.customer_id " +
                     "WHERE o.vendor_id=?")) {

            ps.setInt(1, vendorId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                output.append(rs.getInt("order_id") + " | " +
                        rs.getString("customer_name") + " | " +
                        rs.getString("status") + " | $" + rs.getDouble("total") + "\n");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void viewMenu() {
        output.setText("");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT item_name, price FROM MenuItems WHERE vendor_id=?")) {

            ps.setInt(1, vendorId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                output.append(rs.getString("item_name") + " | $" + rs.getDouble("price") + "\n");
            }

            rs.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    private void updateOrder() {
        output.setText("");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Orders SET status=? WHERE order_id=? AND vendor_id=?")) {

            ps.setString(1, statusBox.getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(orderIdField.getText()));
            ps.setInt(3, vendorId);

            int rows = ps.executeUpdate();
            output.setText(rows + " order updated.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }
}
