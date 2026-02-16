# SmartBank System

## Overview
SmartBank System is a professional-grade Java-based banking application with a modern Swing GUI, comprehensive security features, and persistent data storage capabilities. The system manages bank accounts, transactions, and user authentication with an intuitive and interactive user interface.

## Features

### Core Banking Features
- **Account Management**: Create and manage multiple bank accounts
- **User Authentication**: Secure PIN-based login system
- **Deposit & Withdrawal**: Handle deposits and withdrawals with balance validation
- **Fund Transfers**: Transfer funds between accounts with transaction logging
- **Transaction History**: Complete transaction tracking and history viewing
- **Account Status Management**: Activate/deactivate account privileges

### Security Features
- **AES-GCM Encryption**: Industry-standard encryption for sensitive data
- **PIN Encryption**: Secure PIN storage using EncryptionUtil
- **Permission Manager**: Simulated permission request system
- **Secure Data Persistence**: SQLite database with secure operations

### User Experience Features
- **Modern GUI**: Responsive Swing-based interface with professional styling
- **Audio Feedback
- **Click Sound**: `click.wav` – Button click feedback  
  `soundManager.playClickSound()`
- **Typing Sound**: `type.wav` – Text input feedback  
  `soundManager.playTypeSound()`
- **Notification/Dialog Sound**: `open.wav` – Window/dialog open feedback  
  `soundManager.playOpenSound()`
- **Error Sound**: `wrong.wav` – Error message feedback  
  `soundManager.playErrorSound()`

## Project Structure

```
SmartBank_System/
├── Main.java                      # Main application class with GUI
├── DatabaseHelper.java            # SQLite database connection management
├── DataPersistence.java           # Settings persistence layer
├── EncryptionUtil.java            # AES-GCM encryption utilities
├── PinManager.java                # Secure PIN management
├── PermissionManager.java         # Permission request simulation
├── SoundPlayer.java               # Sound playback utilities
├── README.md                      # This file
└── resources/
    ├── click.wav                  # UI click sound
    ├── type.wav                   # Typing sound
    ├── open.wav                   # Notification/dialog sound
    ├── wrong.wav                  # Error message sound
    ├── appdata.db                 # SQLite database
    └── accounts.dat               # Account data storage
```

## Getting Started

### Prerequisites
- Java 8 or higher
- SQLite JDBC driver (included: sqlite-jdbc-3.50.2.0.jar)
- Audio files (click.wav, type.wav, open.wav) in project root

### Compilation
```bash
javac -cp ".;sqlite-jdbc-3.50.2.0.jar" *.java
```

### Running the Application
```bash
java -cp ".;sqlite-jdbc-3.50.2.0.jar" Main
```

### Sample Login Credentials
| Account Number | PIN  | Holder Name        | Initial Balance | Account Type |
|---|---|---|---|---|
| 12345 | 1234 | Mostafa Gamal | 1000.0 EGP | Savings |
| 67890 | 5678 | Mona Hassan | 500.0 EGP | Current |
| 11223 | 9999 | Abdelrahman Gamal | 2500.0 EGP | Savings |

## Technical Architecture

### Design Patterns Used
1. **Singleton Pattern**: SoundManager maintains single instance
2. **MVC Model**: Separation of data (BankAccount), view (GUI panels), and controller (event handlers)
3. **Component Pattern**: Modular structure with separate utilities and managers

### Key Classes

#### Main Class
- **Purpose**: Primary application GUI and orchestration
- **Responsibilities**: 
  - User interface management
  - Transaction processing
  - Navigation between panels
  - Account management

#### BankAccount Class
- **Purpose**: Data model for bank accounts
- **Key Methods**:
  - `deposit(amount)`: Add funds to account
  - `withdraw(amount)`: Remove funds with validation
  - `transfer(targetAccount, amount)`: Inter-account transfers
  - Transaction history tracking

#### SoundManager Class
- **Purpose**: Centralized sound management
- **Features**:
  - Singleton pattern implementation
  - Sound toggle control
  - Throttled sound playback
  - Error handling

#### EncryptionUtil Class
- **Purpose**: Secure data encryption/decryption
- **Algorithm**: AES-GCM with 128-bit key
- **Features**:
  - IV (Initialization Vector) management
  - Base64 encoding
  - Secure random number generation

#### DatabaseHelper Class
- **Purpose**: SQLite database connection and initialization
- **Features**:
  - Connection pooling
  - Automatic schema creation
  - Error handling

#### DataPersistence Class
- **Purpose**: Application settings persistence
- **Operations**:
  - Save/load application settings
  - Database transaction management

#### PinManager Class
- **Purpose**: Secure PIN management
- **Features**:
  - PIN encryption using EncryptionUtil
  - User preference storage
  - Secure retrieval

## API Reference

### Main Banking Operations

#### Deposit
```java
boolean deposit(double amount)
```
- Deposits funds into the account
- Validates amount > 0
- Checks account active status
- Records transaction history
- Returns: true if successful, false otherwise

#### Withdraw
```java
boolean withdraw(double amount)
```
- Withdraws funds from account
- Validates sufficient balance
- Checks account active status
- Records transaction history
- Returns: true if successful, false otherwise

#### Transfer
```java
boolean transfer(BankAccount targetAccount, double amount)
```
- Transfers funds between accounts
- Validates source and target account status
- Ensures sufficient funds
- Maintains transaction history for both accounts
- Returns: true if successful, false otherwise

### Sound Management

#### Play Sounds
```java
soundManager.playClickSound()     // Button click feedback
soundManager.playTypeSound()      // Text input feedback
soundManager.playOpenSound()      // Window/dialog open feedback
Error Sound**: `wrong.wav` – Error message feedback (`soundManager.playErrorSound()`)

```

#### Control Sounds
```java
soundManager.setSoundEnabled(boolean enabled)
boolean soundManager.isSoundEnabled()
```

## Security Considerations

### Data Protection
- **PIN Storage**: Encrypted using AES-GCM algorithm
- **Database**: SQLite with parameterized queries to prevent SQL injection
- **In-Memory Operations**: Transaction processing remains in-memory for speed

### Best Practices Implemented
1. **No Plain-Text Storage**: Sensitive data encrypted
2. **Parameterized Queries**: Protection against SQL injection
3. **Input Validation**: All user inputs validated before processing
4. **Error Handling**: Comprehensive exception handling throughout

### Future Security Enhancements
1. Implement password hashing (bcrypt/PBKDF2) for future PIN storage
2. Add audit logging for sensitive operations
3. Implement rate limiting for login attempts
4. Add transaction signing and verification
5. Implement role-based access control (RBAC)

## UI Components

### Panels
1. **Login Panel**: Account number and PIN authentication
2. **Main Menu Panel**: Transaction options and account information
3. **Account Management Panel**: Profile and settings management

### DialogsTransaction Input Dialogs - Contextual dialogs for deposits, withdrawals, and transfers

### Buttons
- Modern, interactive buttons with hover effects
- Click sound feedback
- Font-consistent styling

## Color Scheme
- **Primary Color**: Blue (#1E88E5) - Main interactive elements
- **Secondary Color**: Black (#000000) - Background
- **Accent Color**: Pink (#FF4081) - Hover effects
- **Success Color**: Green (#4CAF50) - Positive confirmations
- **Error Color**: Red (#E53935) - Error messages
- **Warning Color**: Amber (#FFC107) - Warnings

## Currency
All monetary amounts are displayed in **Egyptian Pounds (EGP)**

## Compilation & Execution Notes

### Dependencies
- Java Swing (built-in)
- Java AWT (built-in)
- Java NIO (built-in)
- SQLite JDBC (included)

### Sound Files
Ensure the following audio files are in the project root:
- `click.wav` - Button click feedback (suggested: 100-150ms)
- `type.wav` - Text typing feedback (suggested: 50-100ms)
- `open.wav` - Notification/dialog (suggested: 200-300ms)

## Known Limitations

1. **In-Memory Storage**: Accounts exist in HashMap, reset on application restart
2. **Single User Session**: Only one account can be logged in at a time
3. **Sound Dependency**: Application runs without sounds if audio files missing
4. **No Network**: Standalone desktop application, no network connectivity
5. **SQLite Limitations**: Not suitable for high-concurrency scenarios

## Future Enhancements

### Planned Features
1. **Database Persistence**: Store accounts in SQLite instead of HashMap
2. **User Registration**: Self-service account creation
3. **Advanced Reporting**: Enhanced transaction analytics and reports
4. **Multi-Currency Support**: Support for multiple currencies
5. **Mobile App**: Corresponding mobile application
6. **API Integration**: RESTful API for third-party integrations
7. **Biometric Authentication**: Fingerprint/face recognition login
8. **Transaction Scheduling**: Scheduled recurring transactions
9. **Notifications**: Email/SMS transaction alerts
10. **Admin Dashboard**: Administrative account and system management

## Testing

### Manual Testing Checklist
- [ ] Login with valid credentials (12345/1234)
- [ ] Attempt login with invalid credentials
- [ ] Perform deposit operation
- [ ] Perform withdrawal operation
- [ ] Transfer funds between accounts
- [ ] View transaction history
- [ ] Update account holder name
- [ ] Update PIN
- [ ] Disable/enable account
- [ ] Test sound toggle
- [ ] Verify balance updates in real-time
- [ ] Test UI responsiveness
- [ ] Verify error messages display correctly

## Code Quality & Best Practices

### Applied Principles
1. **DRY (Don't Repeat Yourself)**: Centralized methods for common tasks
2. **SOLID Principles**: Single responsibility for each class
3. **Clean Code**: Meaningful variable names, clear method purposes
4. **Error Handling**: Try-catch blocks with meaningful messages
5. **Javadoc Comments**: Documentation for public methods
6. **Resource Management**: Proper file and connection handling

### Coding Standards
- **Naming Conventions**: camelCase for variables/methods, PascalCase for classes
- **Indentation**: 4 spaces per level
- **Bracket Style**: Java convention (bracket placement)
- **Line Length**: Maximum 100 characters
- **Comments**: Clear, concise, and meaningful

## License
This project is developed for educational purposes.

## Author
Mostafa Gamal

## Contact
For questions or suggestions, please reach out to the development team.

---

**Version**: 1.0.0  
**Last Updated**: February 16, 2026  
**Status**: Production Ready
# SmartBank_System-V2
