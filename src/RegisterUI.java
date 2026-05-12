import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterUI extends JFrame {

    String url = "jdbc:mysql://localhost:3306/food_delivery_db";
    String user = "root";
    String password = DbConfig.getPassword();

    JTextField emailField, nameField, phoneField, addressField;
    JPasswordField passwordField;
    JComboBox<String> roleBox;
    JTextArea output;

    public RegisterUI() {
        setTitle("Create Account");
        setSize(450, 350);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(6, 2));

        top.add(new JLabel("Email:"));
        emailField = new JTextField();
        top.add(emailField);

        top.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        top.add(passwordField);

        top.add(new JLabel("Name:"));
        nameField = new JTextField();
        top.add(nameField);

        top.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        top.add(phoneField);

        top.add(new JLabel("Address:"));
        addressField = new JTextField();
        top.add(addressField);

        top.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[] { "Customer", "Vendor", "Driver" });
        top.add(roleBox);

        add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 1));
        JButton createBtn = new JButton("Create Account");
        bottom.add(createBtn);
        add(bottom, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> createAccount());

        setVisible(true);
    }

    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void createAccount() {
        output.setText("");

        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());
        String name = nameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String role = roleBox.getSelectedItem().toString();

        if (email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            output.setText("Email, password, and name required.");
            return;
        }

        try (Connection conn = getConn()) {
            PreparedStatement ps;

            if (role.equals("Customer")) {
                ps = conn.prepareStatement(
                        "INSERT INTO Customers(email, password, customer_name, phone, address) " +
                                "VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, pass);
                ps.setString(3, name);
                ps.setString(4, phone);
                ps.setString(5, address);

            } else if (role.equals("Vendor")) {
                ps = conn.prepareStatement(
                        "INSERT INTO Vendors(email, password, restaurant_name, address, distance_miles) " +
                                "VALUES (?, ?, ?, ?, 5)");
                ps.setString(1, email);
                ps.setString(2, pass);
                ps.setString(3, name);
                ps.setString(4, address);

            } else {
                ps = conn.prepareStatement(
                        "INSERT INTO Drivers(email, password, driver_name, phone) " +
                                "VALUES (?, ?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, pass);
                ps.setString(3, name);
                ps.setString(4, phone);
            }

            ps.executeUpdate();
            ps.close();

            output.setText("Account created.");

        } catch (Exception ex) {
            output.setText(ex.getMessage());
        }
    }
}
