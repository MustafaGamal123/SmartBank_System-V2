/**
 * DataPersistence: Application settings persistence layer
 * 
 * Responsibilities:
 * - Save application settings to SQLite database
 * - Load persisted settings
 * - Handle database transactions
 * 
 * Features:
 * - UPSERT operations for reliable data storage
 * - Parameterized queries to prevent SQL injection
 * - Automatic connection management
 * - Comprehensive error handling
 * 
 * Usage:
 *   DataPersistence dp = new DataPersistence();
 *   dp.saveSetting("theme", "dark");
 *   String theme = dp.loadSetting("theme");
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataPersistence {
    /**
     * Saves a key-value setting to the database
     * Uses REPLACE INTO for UPSERT functionality (insert or update)
     * 
     * @param key The setting key
     * @param value The setting value
     */
    public void saveSetting(String key, String value) {
        String upsertSQL = "REPLACE INTO settings(key, value) VALUES(?, ?);";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement ps = conn.prepareStatement(upsertSQL)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a setting value from the database
     * Returns null if setting doesn't exist
     * 
     * @param key The setting key to retrieve
     * @return The setting value or null if not found
     */
    public String loadSetting(String key) {
        String selectSQL = "SELECT value FROM settings WHERE key = ?;";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

