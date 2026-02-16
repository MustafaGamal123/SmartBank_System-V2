/**
 * DatabaseHelper: SQLite database connection management utility
 * 
 * Responsibilities:
 * - Manage SQLite database connections
 * - Initialize database schema on first run
 * - Provide connection pooling interface
 * 
 * Thread Safety: Thread-safe for connection creation
 * Connection Pooling: Can be extended for future connection pooling
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    // SQLite database file path
    private static final String URL = "jdbc:sqlite:appdata.db";

    /**
     * Creates and returns a connection to the SQLite database
     * Automatically creates the database file if it doesn't exist
     * 
     * @return Connection object for database operations
     * @throws SQLException if connection fails
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database by creating necessary tables
     * Creates 'settings' table if it doesn't exist
     * Safe to call multiple times (uses IF NOT EXISTS)
     * 
     * Operation:
     * - Creates settings table with key-value structure
     * - Used for application settings persistence
     */
    public static void initialize() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT);";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

