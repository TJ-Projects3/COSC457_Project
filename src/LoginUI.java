import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = DbConfig.getPassword();

    JTextField emailField;
    JPasswordField passwordField;
    JComboBox<String> roleBox;
    JTextArea output;

    public LoginUI() {
        setTitle("Food Delivery Login");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(3, 2));

        top.add(new JLabel("Email:"));
        emailField = new JTextField();
        top.add(emailField);

        top.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        top.add(passwordField);

        top.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[] {
                "Customer",
                "Vendor",
                "Driver",
                "Admin"
        });
        top.add(roleBox);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2));

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create Account");

        bottom.add(loginBtn);
        bottom.add(registerBtn);

        add(bottom, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> new RegisterUI());

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void login() {
        output.setText("");

        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            output.setText("Email and password required.");
            return;
        }

        try (Connection conn = getConn()) {

            String sql;
            String idColumn;

            if (role.equals("Customer")) {
                sql = "SELECT customer_id FROM Customers WHERE email=? AND password=?";
                idColumn = "customer_id";
            } else if (role.equals("Vendor")) {
                sql = "SELECT vendor_id FROM Vendors WHERE email=? AND password=?";
                idColumn = "vendor_id";
            } else if (role.equals("Driver")) {
                sql = "SELECT driver_id FROM Drivers WHERE email=? AND password=?";
                idColumn = "driver_id";
            } else {
                sql = "SELECT admin_id FROM Admins WHERE email=? AND password=?";
                idColumn = "admin_id";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(idColumn);

                dispose();

                if (role.equals("Customer")) {
                    new CustomerUI(userId);
                } else if (role.equals("Vendor")) {
                    new VendorUI(userId);
                } else if (role.equals("Driver")) {
                    new DriverUI(userId);
                } else {
                    new AdminUI();
                }

            } else {
                output.setText("Invalid login.");
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}