# SmartBank System - Development Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Code Standards](#code-standards)
4. [Security Guidelines](#security-guidelines)
5. [Testing Procedures](#testing-procedures)
6. [Compilation & Deployment](#compilation--deployment)
7. [Troubleshooting](#troubleshooting)
8. [Future Enhancements](#future-enhancements)

---

## Project Overview

SmartBank System is a professional-grade banking application demonstrating:
- **Enterprise-level security**: AES-256-GCM encryption
- **Clean architecture**: Separation of concerns with MVC pattern
- **Professional GUI**: Swing-based modern interface
- **Data persistence**: SQLite database integration
- **Interactive UX**: Audio feedback and responsive design

### Key Stakeholders
- **Users**: Bank customers performing transactions
- **Developers**: Java developers extending functionality
- **System Administrators**: Deployment and maintenance personnel

---

## Architecture

### Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                  SmartBank System                        │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌───────────────────────────────────────────────────┐   │
│  │             User Interface (Main.java)             │   │
│  │  • LoginPanel         • MainMenuPanel              │   │
│  │  • AccountMgmtPanel   • Transaction Dialogs        │   │
│  └───────────────────────────────────────────────────┘   │
│                          │                                │
│  ┌───────────────────────┴───────────────────────────┐   │
│  │                                                     │   │
│  ├──────────────────┬────────────────┬────────────────┤   │
│  │                  │                │                │   │
│  │  Core Services   │  Data Layer    │  Security      │   │
│  ├──────────────────┼────────────────┼────────────────┤   │
│  │• SoundManager    │• DatabaseHelper│• EncryptionUtil│   │
│  │• BankAccount     │• DataPersist   │• PinManager    │   │
│  │• AppConstants    │• Preferences   │                │   │
│  └──────────────────┴────────────────┴────────────────┘   │
│                          │                                │
│  ┌───────────────────────┴───────────────────────────┐   │
│  │         Support Services                          │   │
│  ├───────────────────────────────────────────────────┤   │
│  │• SoundPlayer          • PermissionManager         │   │
│  │• Resource Management  • Permission Simulation     │   │
│  └───────────────────────────────────────────────────┘   │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Class Hierarchy

```
Main (extends JFrame, implements AppConstants)
├── SoundManager (Singleton)
├── TypingSoundDocumentListener (DocumentListener)
├── BankAccount (Data Model)
│   ├── balance (double)
│   ├── pin (String)
│   ├── transactionHistory (List)
│   └── Account Operations
│       ├── deposit()
│       ├── withdraw()
│       └── transfer()
└── AppConstants (Interface)
    ├── Color Definitions
    ├── Font Configurations
    ├── Size Constants
    └── Layout Parameters

Supporting Classes:
├── DatabaseHelper (Database Connection)
├── DataPersistence (Settings Persistence)
├── EncryptionUtil (AES-256-GCM Encryption)
├── PinManager (Secure PIN Management)
├── PermissionManager (Permission Simulation)
└── SoundPlayer (Audio Playback)
```

### Data Flow

```
User Input (GUI)
    ↓
Event Handler (ActionListener)
    ↓
Business Logic (BankAccount Methods)
    ↓
Data Validation
    ↓
Transaction Processing
    ↓
Database Persistence (if needed)
    ↓
UI Update & User Feedback (Sound + Visual)
```

---

## Code Standards

### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Classes | PascalCase | `BankAccount`, `EncryptionUtil` |
| Methods | camelCase | `savePin()`, `playClickSound()` |
| Variables | camelCase | `accountNumber`, `soundEnabled` |
| Constants | UPPER_SNAKE_CASE | `CLICK_SOUND`, `KEY_SIZE` |
| Interfaces | PascalCase (Adjective) | `DocumentListener`, `AppConstants` |
| Packages | lowercase | `com.smartbank.core` |

### Code Style Guide

```java
// ✓ GOOD: Clear, well-formatted code
public boolean withdraw(double amount) {
    if (!isActive) {
        addTransactionToHistory("Failed Withdrawal: Account is inactive.");
        return false;
    }
    if (amount <= 0) {
        addTransactionToHistory("Failed Withdrawal: Amount must be positive.");
        return false;
    }
    if (this.balance < amount) {
        addTransactionToHistory("Failed Withdrawal: Insufficient funds.");
        return false;
    }
    this.balance -= amount;
    addTransactionToHistory("Withdrew: " + formatCurrency(amount));
    return true;
}

// ✗ BAD: Unclear, poor formatting
public boolean withdraw(double amt){
if(!act)return false;
bal-=amt;
return true;
}
```

### Documentation Standards

```java
/**
 * Brief description of the class/method (one liner)
 * 
 * Detailed explanation of what this class/method does,
 * including any important behaviors or side effects.
 * 
 * @param paramName Description of parameter
 * @return Description of return value
 * @throws ExceptionType Description of when this exception is thrown
 * 
 * Example:
 *   Type result = method(param);
 * 
 * @see RelatedClass#relatedMethod()
 */
public Type method(Type paramName) throws ExceptionType {
    // Implementation
}
```

### Comment Guidelines

```java
// Use block comments for complex logic
/* 
 * Combine IV and ciphertext for decryption later
 * The IV (Initialization Vector) is needed to decrypt the encrypted data
 */
byte[] combined = new byte[iv.length + cipherText.length];
System.arraycopy(iv, 0, combined, 0, iv.length);
System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

// Use inline comments for "why", not "what"
return Base64.getEncoder().encodeToString(combined); // For storage compatibility
```

---

## Security Guidelines

### Password & PIN Security

```java
// ✓ DO: Use encryption for sensitive data
String encryptedPin = EncryptionUtil.encrypt(pin);
prefs.put(PIN_KEY, encryptedPin);

// ✗ DON'T: Store sensitive data in plaintext
String pin = "1234";
prefs.put("pin", pin); // NEVER do this
```

### Database Security

```java
// ✓ DO: Use parameterized queries
String sql = "SELECT * FROM users WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setInt(1, userId);
ResultSet rs = ps.executeQuery();

// ✗ DON'T: Use string concatenation
String sql = "SELECT * FROM users WHERE id = " + userId; // SQL Injection vulnerability
```

### Input Validation

```java
// ✓ DO: Validate all user inputs
public boolean deposit(double amount) {
    if (amount <= 0) {
        return false; // Invalid: amount must be positive
    }
    if (!isActive) {
        return false; // Invalid: account must be active
    }
    // Process deposit
}

// ✗ DON'T: Trust user input without validation
public boolean deposit(double amount) {
    this.balance += amount; // No validation - dangerous!
}
```

### Error Handling

```java
// ✓ DO: Handle exceptions appropriately
try {
    String decrypted = EncryptionUtil.decrypt(encrypted);
    // Process decrypted data
} catch (Exception e) {
    System.err.println("Decryption failed: " + e.getMessage());
    e.printStackTrace();
    // Log to file in production
}

// ✗ DON'T: Ignore exceptions silently
try {
    String decrypted = EncryptionUtil.decrypt(encrypted);
} catch (Exception e) {
    // Silently ignore - data loss!
}
```

---

## Testing Procedures

### Test Checklist

#### Login Functionality
- [ ] Valid credentials → Login successful
- [ ] Invalid account number → Error message displayed
- [ ] Invalid PIN → Error message displayed
- [ ] Inactive account → Appropriate warning shown
- [ ] Blank fields → Validation error shown

#### Deposit Operations
- [ ] Valid amount → Balance increases correctly
- [ ] Negative amount → Error shown
- [ ] Zero amount → Error shown
- [ ] Inactive account → Transaction rejected
- [ ] Transaction history updated → Verify in history view

#### Withdrawal Operations
- [ ] Valid amount (sufficient funds) → Success
- [ ] Amount exceeds balance → Transaction rejected
- [ ] Inactive account → Transaction rejected
- [ ] Negative amount → Error shown
- [ ] Transaction history recorded → Check history

#### Transfer Operations
- [ ] Valid transfer (both accounts active) → Success
- [ ] Transfer to self → Rejected with message
- [ ] Target account doesn't exist → Error shown
- [ ] Insufficient funds → Transaction rejected
- [ ] Both accounts updated correctly → Verify balances

#### UI/UX Features
- [ ] Sound toggle works → Audio on/off as selected
- [ ] All buttons responsive → No lag or freezes
- [ ] Color scheme consistent → Professional appearance
- [ ] Text readable → Font sizes appropriate
- [ ] Dialogs display correctly → No overlapping elements

#### Data Persistence
- [ ] Settings saved to database → Verify in appdata.db
- [ ] PIN encrypted properly → Check in database (should be Base64)
- [ ] Account data persisted → Data survives restarts
- [ ] Transaction history saved → History complete

### Manual Testing Process

1. **Compile the Project**
   ```bash
   javac -cp ".;sqlite-jdbc-3.50.2.0.jar" *.java
   ```

2. **Run the Application**
   ```bash
   java -cp ".;sqlite-jdbc-3.50.2.0.jar" Main
   ```

3. **Execute Test Cases**
   - Follow the test checklist above
   - Document any failures
   - Take screenshots of issues

4. **Test with Sample Accounts**
   - Account 1: 12345 / PIN: 1234
   - Account 2: 67890 / PIN: 5678
   - Account 3: 11223 / PIN: 9999

---

## Compilation & Deployment

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- SQLite JDBC driver (sqlite-jdbc-3.50.2.0.jar)
- Audio files (click.wav, type.wav, open.wav)

### Build Steps

```bash
# 1. Navigate to project directory
cd SmartBank_System

# 2. Compile all Java files
javac -cp ".;sqlite-jdbc-3.50.2.0.jar" *.java

# 3. Verify no compilation errors
echo "Compilation complete"

# 4. Run the application
java -cp ".;sqlite-jdbc-3.50.2.0.jar" Main
```

### Deployment Package

Create a distribution package with:
```
SmartBank_System/
├── *.class (compiled files)
├── *.jar (dependencies)
├── README.md
├── DEVELOPMENT.md
├── sounds/
│   ├── click.wav
│   ├── type.wav
│   └── open.wav
└── LICENSE
```

### Distribution

```bash
# Create JAR file (optional)
jar cvfe SmartBank.jar Main *.class *.jar sounds/

# Run from JAR
java -jar SmartBank.jar
```

---

## Troubleshooting

### Common Issues

#### Issue: "Compilation fails with 'duplicate class' error"
**Solution**: 
- Ensure only one public class per file
- Check for duplicate class definitions
- Use proper file naming (ClassName.java)

#### Issue: "Cannot find symbol" compilation error
**Solution**:
- Verify all imports are correct
- Check classpath includes all necessary files
- Ensure SQLite JDBC jar is in classpath

#### Issue: "Sound files not found" at runtime
**Solution**:
- Verify audio files exist in project root
- Check file names: click.wav, type.wav, open.wav
- Application runs without sound if files missing (not critical)

#### Issue: "Database locked" or persistence errors
**Solution**:
- Check file permissions on appdata.db
- Ensure database isn't open in another application
- Delete appdata.db to force recreation
- Verify sufficient disk space

#### Issue: "GUI elements not displaying correctly"
**Solution**:
- Check screen resolution (minimum 800x600)
- Verify font availability (Segoe UI)
- Test on different screen scaling
- Check color definitions in AppConstants

### Debug Mode

```java
// Enable debug logging
public static final boolean DEBUG = true;

// Debug prints
if (DEBUG) {
    System.out.println("DEBUG: Account number: " + accountNumber);
    System.out.println("DEBUG: New balance: " + balance);
}
```

### Performance Optimization

If experiencing lag:
1. **Reduce sound complexity**: Disable sound feedback
2. **Optimize database queries**: Add indexes
3. **Profile code**: Use Java Flight Recorder
4. **Increase heap memory**: `java -Xmx512m`

---

## Future Enhancements

### Short-term (1-2 months)
1. **Database Persistence**: Move accounts from HashMap to SQLite
2. **User Registration**: Self-service account creation
3. **Enhanced Logging**: Audit trail for all transactions
4. **Error Recovery**: Better error handling and user guidance
5. **Unit Tests**: JUnit test suite for core classes

### Medium-term (3-6 months)
1. **Transaction Scheduling**: Recurring transactions
2. **Multi-currency Support**: Support multiple currencies
3. **Advanced Reports**: Transaction analytics
4. **API Integration**: RESTful API for external systems
5. **Performance Metrics**: Dashboard with statistics

### Long-term (6-12 months)
1. **Mobile App**: Cross-platform mobile application
2. **Biometric Authentication**: Fingerprint/face recognition
3. **Network Banking**: Multi-institution support
4. **Machine Learning**: Fraud detection algorithms
5. **Blockchain**: Immutable transaction ledger

### Code Improvements
1. Replace deprecated Swing components
2. Implement proper logging framework
3. Add comprehensive unit tests
4. Implement design patterns (Observer, Strategy)
5. Database connection pooling
6. Internationalization (i18n) support
7. Accessibility improvements (WCAG compliance)
8. Code refactoring and cleanup

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | Feb 16, 2026 | Initial release with core banking features |
| 0.9.0 | Feb 15, 2026 | Beta release for testing |
| 0.1.0 | Feb 1, 2026 | Project initialization |

---

## Useful Resources

### Java Documentation
- [Java Cryptography Architecture](https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html)
- [Swing GUI Components](https://docs.oracle.com/javase/tutorial/uiswing/)
- [JDBC Database Access](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)

### Security References
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/)
- [Java Security Best Practices](https://www.securecoding.cert.org/confluence/display/java/)

### Tools
- Eclipse IDE - Java Development
- SQLiteStudio - Database Management
- Audacity - Audio File Editing
- Git - Version Control

---

## Contact & Support

For questions or issues:
- **Developer Team**: development@smartbank.local
- **Issues Tracker**: [Project Repository]
- **Documentation**: See README.md

---

**Document Version**: 1.0  
**Last Updated**: February 16, 2026  
**Status**: Active
