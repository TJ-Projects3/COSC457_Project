import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VendorUI extends JFrame {

    private DefaultTableModel orderModel;
    private DefaultTableModel menuModel;
    private JTable orderTable;
    private JTable menuTable;
    private int vendorId;

    public VendorUI() {
        this(1);
    }

    public VendorUI(int vendorId) {
        this.vendorId = vendorId;

        setTitle("Vendor Dashboard");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Vendor Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders", buildOrdersPanel());
        tabs.addTab("My Menu", buildMenuPanel());

        add(title, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        loadOrders();
        loadMenuItems();
        setVisible(true);
    }

    private JPanel buildOrdersPanel() {
        orderModel = new DefaultTableModel(
                new String[]{"Order ID", "Customer", "Status", "Total ($)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);

        JButton refreshBtn   = new JButton("Refresh");
        JButton preparingBtn = new JButton("Set Preparing");
        JButton readyBtn     = new JButton("Set Ready for Pickup");

        JPanel buttons = new JPanel();
        buttons.add(refreshBtn);
        buttons.add(preparingBtn);
        buttons.add(readyBtn);

        refreshBtn.addActionListener(e -> loadOrders());
        preparingBtn.addActionListener(e -> updateOrderStatus("Preparing"));
        readyBtn.addActionListener(e -> updateOrderStatus("Ready for Pickup"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMenuPanel() {
        menuModel = new DefaultTableModel(
                new String[]{"Item ID", "Item Name", "Price ($)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(menuModel);

        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn     = new JButton("Add Item");
        JButton editBtn    = new JButton("Edit Item");
        JButton removeBtn  = new JButton("Remove Item");

        JPanel buttons = new JPanel();
        buttons.add(refreshBtn);
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(removeBtn);

        refreshBtn.addActionListener(e -> loadMenuItems());
        addBtn.addActionListener(e -> addMenuItem());
        editBtn.addActionListener(e -> editMenuItem());
        removeBtn.addActionListener(e -> removeMenuItem());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/food_delivery_db",
                "root",
                DbConfig.getPassword()
        );
    }

    private void loadOrders() {
        orderModel.setRowCount(0);

        String sql = "SELECT o.order_id, c.customer_name, o.status, o.total " +
                     "FROM Orders o JOIN Customers c ON o.customer_id = c.customer_id " +
                     "WHERE o.vendor_id = ? ORDER BY o.order_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("status"),
                        rs.getDouble("total")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void loadMenuItems() {
        menuModel.setRowCount(0);

        String sql = "SELECT item_id, item_name, price FROM MenuItems " +
                     "WHERE vendor_id = ? ORDER BY item_name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                menuModel.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getDouble("price")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }

    private void updateOrderStatus(String newStatus) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.");
            return;
        }

        int orderId = Integer.parseInt(orderModel.getValueAt(row, 0).toString());

        String sql = "UPDATE Orders SET status = ? WHERE order_id = ? AND vendor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setInt(3, vendorId);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                        "Order #" + orderId + " status updated to: " + newStatus);
            } else {
                JOptionPane.showMessageDialog(this, "No order was updated.");
            }

            loadOrders();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage());
        }
    }

    private void addMenuItem() {
        JTextField nameField  = new JTextField();
        JTextField priceField = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Item Name:"));
        form.add(nameField);
        form.add(new JLabel("Price ($):"));
        form.add(priceField);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Add Menu Item", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String name      = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name and price are required.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a positive number.");
            return;
        }

        String sql = "INSERT INTO MenuItems (vendor_id, item_name, price) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "\"" + name + "\" added to your menu.");
            loadMenuItems();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage());
        }
    }

    private void editMenuItem() {
        int row = menuTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu item to edit.");
            return;
        }

        int    itemId       = Integer.parseInt(menuModel.getValueAt(row, 0).toString());
        String currentName  = menuModel.getValueAt(row, 1).toString();
        String currentPrice = menuModel.getValueAt(row, 2).toString();

        JTextField nameField  = new JTextField(currentName);
        JTextField priceField = new JTextField(currentPrice);

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Item Name:"));
        form.add(nameField);
        form.add(new JLabel("Price ($):"));
        form.add(priceField);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String name      = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name and price are required.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a positive number.");
            return;
        }

        String sql = "UPDATE MenuItems SET item_name = ?, price = ? " +
                     "WHERE item_id = ? AND vendor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, itemId);
            stmt.setInt(4, vendorId);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Menu item updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.");
            }

            loadMenuItems();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error editing item: " + e.getMessage());
        }
    }

    private void removeMenuItem() {
        int row = menuTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu item to remove.");
            return;
        }

        int    itemId   = Integer.parseInt(menuModel.getValueAt(row, 0).toString());
        String itemName = menuModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + itemName + "\" from your menu?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM MenuItems WHERE item_id = ? AND vendor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.setInt(2, vendorId);

            int deleted = stmt.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(this,
                        "\"" + itemName + "\" removed from your menu.");
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.");
            }

            loadMenuItems();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error removing item: " + e.getMessage());
        }
    }
}
