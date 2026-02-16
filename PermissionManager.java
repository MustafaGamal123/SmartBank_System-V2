/**
 * PermissionManager: Application permission request simulation
 * 
 * Responsibilities:
 * - Manage application permissions
 * - Simulate user permission dialogs
 * - Track granted permissions
 * 
 * Note: This is a simulation of Android-style permission management
 * for demonstration purposes on desktop
 * 
 * Supported Permissions:
 * - CAMERA: Simulated camera access
 * - LOCATION: Simulated location access
 * - STORAGE: Simulated storage access
 * 
 * Usage:
 *   PermissionManager pm = new PermissionManager();
 *   boolean granted = pm.requestPermission(PermissionManager.Permission.CAMERA);
 *   if (pm.hasPermission(PermissionManager.Permission.CAMERA)) {
 *       // Access camera
 *   }
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 */

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    /**
     * Enumeration of supported permissions
     */
    public enum Permission { CAMERA, LOCATION, STORAGE }

    private Map<Permission, Boolean> grantedPermissions = new HashMap<>();

    /**
     * Requests a specific permission from the user
     * Simulates permission dialog and stores the result
     * 
     * @param permission The permission to request
     * @return true if permission granted, false if denied
     */
    public boolean requestPermission(Permission permission) {
        boolean granted = simulateUserPermissionDialog(permission);
        grantedPermissions.put(permission, granted);
        return granted;
    }

    /**
     * Checks if a specific permission was previously granted
     * 
     * @param permission The permission to check
     * @return true if permission is granted, false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return grantedPermissions.getOrDefault(permission, false);
    }

    /**
     * Simulates user responding to permission dialog
     * Currently always grants permission (can be modified for testing)
     * 
     * @param permission The permission being requested
     * @return true to simulate permission granted
     */
    private boolean simulateUserPermissionDialog(Permission permission) {
        System.out.println("Requesting permission: " + permission);
        return true; // Simulate permission granted
    }
}

