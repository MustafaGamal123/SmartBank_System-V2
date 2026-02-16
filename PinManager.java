/**
 * PinManager: Secure PIN management with encryption
 * 
 * Responsibilities:
 * - Encrypt and store PINs using AES-GCM
 * - Retrieve and decrypt stored PINs
 * - Manage user preferences for PIN settings
 * 
 * Security Features:
 * - Uses AES-GCM encryption (military-grade)
 * - PINs never stored in plaintext
 * - Decryption on-demand only
 * - Base64 encoding for storage
 * 
 * Usage:
 *   Preferences prefs = Preferences.userRoot().node(getClass().getName());
 *   PinManager pinManager = new PinManager(prefs);
 *   pinManager.savePin("1234");
 *   String pin = pinManager.loadPin();
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 */

import java.util.prefs.Preferences;

public class PinManager {
    private static final String PIN_KEY = "user_pin";
    private Preferences prefs;

    /**
     * Creates a PinManager instance with Java Preferences backend
     * 
     * @param prefs Java Preferences object for storage
     */
    public PinManager(Preferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Encrypts and saves PIN to preferences storage
     * Uses AES-GCM encryption for security
     * 
     * @param pin The plaintext PIN to save
     */
    public void savePin(String pin) {
        try {
            String encryptedPin = EncryptionUtil.encrypt(pin);
            prefs.put(PIN_KEY, encryptedPin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and decrypts PIN from preferences storage
     * Returns null if PIN not found
     * 
     * @return The decrypted PIN or null if not found
     */
    public String loadPin() {
        try {
            String encryptedPin = prefs.get(PIN_KEY, null);
            if (encryptedPin != null) {
                return EncryptionUtil.decrypt(encryptedPin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

