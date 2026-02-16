/**
 * EncryptionUtil: Cryptographic encryption/decryption utility
 * 
 * Security Algorithm: AES-256-GCM (Advanced Encryption Standard with Galois/Counter Mode)
 * - Industry-standard authenticated encryption
 * - Provides both confidentiality and authenticity
 * - Resistant to known cryptographic attacks
 * - Authenticated encryption prevents tampering
 * 
 * Key Features:
 * - 256-bit (32-byte) encryption key
 * - 96-bit (12-byte) random IV (Initialization Vector) per encryption
 * - 128-bit authentication tag
 * - Base64 encoding for storage/transmission
 * - Static key initialization (can be enhanced with key derivation)
 * 
 * Security Considerations:
 * 1. IV is randomly generated for each encryption operation
 * 2. IV is prepended to ciphertext for decryption
 * 3. GCM mode provides authenticated encryption
 * 4. Base64 encoding ensures compatibility
 * 
 * Usage:
 *   String plaintext = "sensitive_data";
 *   String encrypted = EncryptionUtil.encrypt(plaintext);
 *   String decrypted = EncryptionUtil.decrypt(encrypted);
 * 
 * Thread Safety: Thread-safe (all methods are static)
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 * @see javax.crypto.Cipher
 * @see javax.crypto.SecretKey
 */

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    // AES-GCM encryption algorithm specification
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    
    // 256-bit key size (32 bytes)
    private static final int KEY_SIZE = 256;
    
    // 128-bit authentication tag length
    private static final int TAG_LENGTH_BIT = 128;
    
    // Static encryption key (generated once at class load)
    private static SecretKey secretKey;

    /**
     * Static initializer: Generates the encryption key at class loading time
     * Uses Java's built-in Key Generator with SecureRandom
     * 
     * Note: In a production system, consider:
     * - Loading key from secure key store
     * - Using key derivation function (PBKDF2)
     * - Rotating keys periodically
     */
    static {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE, new SecureRandom());
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts plaintext using AES-256-GCM
     * 
     * Process:
     * 1. Generate random 96-bit IV
     * 2. Initialize cipher in ENCRYPT mode with IV
     * 3. Process plaintext through cipher
     * 4. Combine IV + ciphertext (IV needed for decryption)
     * 5. Base64 encode for storage
     * 
     * @param plaintext The text to encrypt
     * @return Base64-encoded string containing IV + ciphertext
     * @throws Exception if encryption fails
     */
    public static String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        
        // Generate random IV (Initialization Vector)
        byte[] iv = new byte[12]; // 96-bit IV for GCM
        new SecureRandom().nextBytes(iv);
        
        // Set up GCM parameters
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        
        // Encrypt the plaintext
        byte[] cipherText = cipher.doFinal(plaintext.getBytes());
        
        // Combine IV and ciphertext for decryption later
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        
        // Encode to Base64 for storage/transmission
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts Base64-encoded ciphertext using AES-256-GCM
     * 
     * Process:
     * 1. Base64 decode the input
     * 2. Extract IV from first 12 bytes
     * 3. Extract ciphertext from remaining bytes
     * 4. Initialize cipher in DECRYPT mode with IV
     * 5. Decrypt ciphertext to plaintext
     * 
     * @param encrypted Base64-encoded string containing IV + ciphertext
     * @return Decrypted plaintext
     * @throws Exception if decryption fails or authentication fails
     */
    public static String decrypt(String encrypted) throws Exception {
        // Base64 decode the input
        byte[] combined = Base64.getDecoder().decode(encrypted);
        
        // Extract IV (first 12 bytes)
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        
        // Set up GCM parameters with extracted IV
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        
        // Decrypt ciphertext (everything after the IV)
        byte[] plaintext = cipher.doFinal(combined, iv.length, combined.length - iv.length);
        
        return new String(plaintext);
    }
}

