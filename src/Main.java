import java.sql.*;
// Import all JDBC classes (Connection, Statement, ResultSet, etc.)

public class Main {
    public static void main(String[] args) {

        // --- 1. Database connection details ---
        String url = "jdbc:mysql://localhost:3306/food_delivery_db";
        // jdbc:mysql://host:port/databaseName

        String user = "root";
        // MySQL username

        String password = "Shad1235!";
        // Replace with your MySQL root password

        try {
            // --- 2. Load MySQL JDBC Driver ---
            // Modern Java loads it automatically,
            // but calling Class.forName() ensures it is explicitly loaded.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // --- 3. Establish a connection to MySQL ---
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL!");

            // --- 4. Write the SQL query you want to run ---
            String query = "SELECT * FROM Customers";

            // --- 5. Create a Statement object to send SQL to the database ---
            Statement stmt = conn.createStatement();

            // --- 6. Execute the query and get the result set ---
            ResultSet rs = stmt.executeQuery(query);

            // --- 7. Process (read) each row returned by the query ---
            while (rs.next()) {
                // rs.getInt("id") reads the 'id' column from the current row
                // rs.getString("name") reads the 'name' column
                // rs.getString("major") reads the 'major' column
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("FirstName") + " | " +
                                rs.getString("LastName"));
            }

            // --- 8. Close all objects to release resources ---
            rs.close(); // closes the ResultSet
            stmt.close(); // closes the Statement
            conn.close(); // closes the Connection

        } catch (Exception e) {

            // --- 9. Print any error if something goes wrong ---
            e.printStackTrace();
        }
    }
}
