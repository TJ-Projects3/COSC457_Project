import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VendorUI extends JFrame {

    private JTable orderTable;
    private DefaultTableModel model;
    private int vendorId;

    public VendorUI() {
        this(1);
    }

    public VendorUI(int vendorId) {
        this.vendorId = vendorId;

        setTitle("Vendor Dashboard");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Vendor Dashboard - Manage Orders", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {
                "Order ID",
                "Customer ID",
                "Status",
                "Total"
        });

        orderTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        JButton refreshButton = new JButton("Refresh Orders");
        JButton preparingButton = new JButton("Set Preparing");
        JButton readyButton = new JButton("Set Ready for Pickup");
        JButton deliveredButton = new JButton("Set Delivered");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(preparingButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(deliveredButton);

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadOrders());
        preparingButton.addActionListener(e -> updateOrderStatus("Preparing"));
        readyButton.addActionListener(e -> updateOrderStatus("Ready for Pickup"));
        deliveredButton.addActionListener(e -> updateOrderStatus("Delivered"));

        loadOrders();
        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/food_delivery_db",
                "root",
                DbConfig.getPassword()
        );
    }

    private void loadOrders() {
        model.setRowCount(0);

        String sql = "SELECT order_id, customer_id, status, total FROM Orders WHERE vendor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getString("status"),
                        rs.getDouble("total")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void updateOrderStatus(String newStatus) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.");
            return;
        }

        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());

        String sql = "UPDATE Orders SET status = ? WHERE order_id = ? AND vendor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setInt(3, vendorId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Order status updated to: " + newStatus);
            } else {
                JOptionPane.showMessageDialog(this, "No order was updated.");
            }

            loadOrders();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage());
        }
    }
}