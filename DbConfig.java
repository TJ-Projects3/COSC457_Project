import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DbConfig {

    private static final String ENV_FILE = ".env.local";
    private static final String PASSWORD_KEY = "MY_SQL_PASSWORD";
    private static String password;

    public static String getPassword() {
        if (password == null) {
            password = System.getenv(PASSWORD_KEY);
            if (password == null || password.isEmpty()) {
                password = loadPasswordFromEnvFile();
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalStateException(PASSWORD_KEY + " is not set in environment or .env.local");
            }
        }
        return password;
    }

    private static String loadPasswordFromEnvFile() {
        File envFile = new File(ENV_FILE);
        if (!envFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (PASSWORD_KEY.equals(key)) {
                    return stripQuotes(value);
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    private static String stripQuotes(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
