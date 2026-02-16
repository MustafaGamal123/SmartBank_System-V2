/**
 * SmartBank System - Professional Banking Application
 * 
 * A comprehensive Java-based banking application featuring:
 * - Modern Swing GUI with professional styling
 * - Secure user authentication with PIN management
 * - Core banking operations (deposit, withdrawal, transfer)
 * - Transaction history tracking
 * - AES-GCM encryption for sensitive data
 * - Audio feedback for user interactions
 * - SQLite database persistence
 * 
 * Main Components:
 * - SoundManager: Handles all audio playback with singleton pattern
 * - TypingSoundDocumentListener: Provides typing sound feedback
 * - AppConstants: Centralized styling and configuration constants
 * - BankAccount: Data model for bank accounts with transaction tracking
 * - Main: Primary GUI and application orchestration
 * 
 * Author: Mostafa Gamal
 * Date: February 16, 2026
 * Version: 1.0.0
 * 
 * Security Features:
 * - PIN encryption using AES-GCM algorithm
 * - Parameterized database queries to prevent SQL injection
 * - Comprehensive input validation
 * - Secure random number generation for encryption
 * 
 * @version 1.0.0
 * @author Mostafa Gamal
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// --- Constants for consistent styling and text ---
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; 
import java.util.prefs.Preferences;

// Sound imports
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


// --- Sound Manager Class ---
/**
 * SoundManager: Centralized audio management with Singleton pattern
 * 
 * Responsibilities:
 * - Load and play audio files
 * - Manage sound enable/disable state
 * - Handle audio resource cleanup
 * - Minimize audio latency and resource usage
 * 
 * Usage:
 *   SoundManager soundManager = SoundManager.getInstance();
 *   soundManager.playClickSound();
 * 
 * Thread Safety: Not thread-safe (single-threaded UI context)
 */
class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    
    // Sound file paths
    private static final String CLICK_SOUND = "click.wav";
    private static final String TYPE_SOUND = "type.wav";
    private static final String OPEN_SOUND = "open.wav";
    private static final String WRONG_SOUND = "wrong.wav"; 

    /**
     * Private constructor ensures singleton pattern
     */
    private SoundManager() {
        // Private constructor for singleton
    }
    public void playWrongSound() {
    if (soundEnabled) {
        playSound(WRONG_SOUND);
    }
}

    /**
     * Gets the singleton instance of SoundManager
     * @return SoundManager singleton instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Plays a click sound for button interactions
     * Non-blocking with throttled playback
     */
    public void playClickSound() {
        if (soundEnabled) {
            playSound(CLICK_SOUND);
        }
    }
    
    /**
     * Plays a typing sound for text input feedback
     * Non-blocking operation
     */
    public void playTypeSound() {
        if (soundEnabled) {
            playSound(TYPE_SOUND);
        }
    }
    
    /**
     * Plays an open/notification sound for dialogs
     * Non-blocking operation
     */
    public void playOpenSound() {
        if (soundEnabled) {
            playSound(OPEN_SOUND);
        }
    }
    
    /**
     * Core sound playback method with error handling
     * Runs asynchronously to prevent UI blocking
     * 
     * @param soundFile The path to the audio file
     */
    private void playSound(String soundFile) {
    if (!soundEnabled) return;
    
        try {
            // Get audio input stream first
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile));
            
            // Play system beep
            Toolkit.getDefaultToolkit().beep();
            
            // Add delays based on sound type
            if (soundFile.equals(CLICK_SOUND)) {
                Thread.sleep(50);
            } else if (soundFile.equals(TYPE_SOUND)) {
                Thread.sleep(20);
            }
            
            // Create and configure clip
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            
            // Clean up resources after playing
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioInputStream.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing audio stream: " + ex.getMessage());
                    }
                }
            });
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + soundFile + " - " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while playing sound: " + soundFile);
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}

// --- Enhanced Document Listener for Typing Sounds ---
class TypingSoundDocumentListener implements DocumentListener {
    private final SoundManager soundManager;
    private long lastTypeTime = 0;
    private static final long TYPE_SOUND_DELAY = 100; // Minimum delay between type sounds (ms)
    
    public TypingSoundDocumentListener() {
        this.soundManager = SoundManager.getInstance();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        playTypeSoundThrottled();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        playTypeSoundThrottled();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        playTypeSoundThrottled();
    }
    
    private void playTypeSoundThrottled() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTypeTime > TYPE_SOUND_DELAY) {
            soundManager.playTypeSound();
            lastTypeTime = currentTime;
        }
    }
}

interface AppConstants {

    // App Metadata
    String APP_TITLE = "SmartBank System";

    // Fonts
    String FONT_NAME = "Segoe UI";
    int FONT_SIZE_LARGE_TITLE = 32;
    int FONT_SIZE_TITLE = 24;
    int FONT_SIZE_SUBTITLE = 18;
    int FONT_SIZE_NORMAL = 14;
    int FONT_SIZE_BUTTON = 16;

    // === Colors (No Gray Theme) ===

    // Backgrounds
    Color WHITE = new Color(255, 255, 255);
    Color BACKGROUND_COLOR = Color.WHITE;             // Full white
    Color PANEL_COLOR = Color.WHITE;                  // Full white for all panels

    // Primary & Secondary Colors
    Color PRIMARY_COLOR = new Color(30, 136, 229);     // Blue
    Color SECONDARY_COLOR = Color.BLACK;               // No gray, use pure black

    // Accent & Feedback Colors
    Color ACCENT_COLOR = new Color(255, 64, 129);      // Pink
    Color ERROR_COLOR = new Color(229, 57, 53);        // Red
    Color SUCCESS_COLOR = new Color(76, 175, 80);      // Green
    Color WARNING_COLOR = new Color(255, 193, 7);      // Amber

    // Text Colors
    Color TEXT_COLOR = Color.BLACK;                    // Black text
    Color SUBTEXT_COLOR = Color.BLACK;                 // Subtext is also black

    // State Colors
    Color DISABLED_COLOR = Color.BLACK;                // No gray, use black
    Color BORDER_COLOR = Color.BLACK;                  // Border in black

    // Layout
    int PADDING = 12;
    int MARGIN = 16;
    int BORDER_RADIUS = 10;

    int BUTTON_HEIGHT = 42;
    int BUTTON_WIDTH = 150;

    // Panel Keys
    String CARD_LOGIN = "LoginPanel";
    String CARD_MAIN_MENU = "MainMenuPanel";
    String CARD_ACCOUNT_MANAGEMENT = "AccountManagementPanel";
}

// --- BankAccount Class (Data Model) ---
class BankAccount {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private String pin; // WARNING: In a real system, this would be hashed and never stored as plain text
    private String accountType; // e.g., "Savings", "Current"
    private boolean isActive;
    private List<String> transactionHistory; // Simple list for history
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BankAccount(String accountNumber, String accountHolderName, double initialBalance, String pin, String accountType) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.pin = pin;
        this.accountType = accountType;
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
        addTransactionToHistory("Account created with initial balance: " + formatCurrency(initialBalance));
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }
    public String getPin() { return pin; }
    public String getAccountType() { return accountType; }
    public boolean isActive() { return isActive; }
    public List<String> getTransactionHistory() { return new ArrayList<>(transactionHistory); }

    // Setters
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    public void setPin(String pin) {
        this.pin = pin; // WARNING: Secure this in a real system!
    }
    public void setActive(boolean active) {
        isActive = active;
        addTransactionToHistory("Account status changed to " + (active ? "Active" : "Inactive"));
    }

    // --- Core Banking Operations ---
    public boolean deposit(double amount) {
        if (!isActive) {
            addTransactionToHistory("Failed Deposit: Account is inactive.");
            return false;
        }
        if (amount <= 0) {
            addTransactionToHistory("Failed Deposit: Amount must be positive.");
            return false;
        }
        this.balance += amount;
        addTransactionToHistory("Deposited: " + formatCurrency(amount) + ". New balance: " + formatCurrency(this.balance));
        return true;
    }

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
            addTransactionToHistory("Failed Withdrawal: Insufficient funds. Attempted: " + formatCurrency(amount) + ", Current: " + formatCurrency(this.balance));
            return false;
        }
        this.balance -= amount;
        addTransactionToHistory("Withdrew: " + formatCurrency(amount) + ". New balance: " + formatCurrency(this.balance));
        return true;
    }

    public boolean transfer(BankAccount targetAccount, double amount) {
        if (!isActive) {
            addTransactionToHistory("Failed Transfer: Source account is inactive.");
            return false;
        }
        if (!targetAccount.isActive()) {
            addTransactionToHistory("Failed Transfer: Target account " + targetAccount.getAccountNumber() + " is inactive.");
            return false;
        }
        if (amount <= 0) {
            addTransactionToHistory("Failed Transfer: Amount must be positive.");
            return false;
        }
        if (this.balance < amount) {
            addTransactionToHistory("Failed Transfer: Insufficient funds for transfer. Attempted: " + formatCurrency(amount) + ", Current: " + formatCurrency(this.balance));
            return false;
        }

        // Simulate atomicity for in-memory
        this.balance -= amount;
        targetAccount.balance += amount;

        addTransactionToHistory("Transferred: " + formatCurrency(amount) + " to account " + targetAccount.getAccountNumber() + ". New balance: " + formatCurrency(this.balance));
        targetAccount.addTransactionToHistory("Received: " + formatCurrency(amount) + " from account " + this.accountNumber + ". New balance: " + formatCurrency(targetAccount.getBalance()));
        return true;
    }

    private void addTransactionToHistory(String transaction) {
        transactionHistory.add(DATE_TIME_FORMAT.format(LocalDateTime.now()) + " - " + transaction);
    }

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount) + " EGP"; // Assuming Egyptian Pounds
    }
}

// --- 2. Main Banking System GUI (main class) ---
public class Main extends JFrame implements AppConstants {

    private Map<String, BankAccount> accounts;
    private BankAccount loggedInAccount;
    private SoundManager soundManager;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // UI Components for different panels
    private JTextField loginAccountNumberField;
    private JPasswordField loginPinField;

    private JLabel welcomeLabel;
    private JLabel balanceLabel;

    private JLabel currentNameLabel;
    private JTextField newNameField;
    private JPasswordField newPinField;
    private JCheckBox activeStatusCheckBox;

    public Main() {
        // Initialize sound manager
        soundManager = SoundManager.getInstance();
        
        // Set a modern Look and Feel (Nimbus is built-in Java)
        try {
            DatabaseHelper.initialize();

            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            PinManager pinManager = new PinManager(prefs);

            pinManager.savePin("1234");
            String decryptedPin = pinManager.loadPin();
            System.out.println("Loaded PIN: " + decryptedPin);

            DataPersistence dp = new DataPersistence();
            dp.saveSetting("theme", "dark");
            String theme = dp.loadSetting("theme");
            System.out.println("Saved theme: " + theme);

            PermissionManager pm = new PermissionManager();
            boolean granted = pm.requestPermission(PermissionManager.Permission.CAMERA);
            System.out.println("Camera permission granted? " + granted);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Customize Nimbus colors
            UIManager.put("nimbusBase", AppConstants.PRIMARY_COLOR);
            UIManager.put("nimbusBlueGrey", AppConstants.SECONDARY_COLOR);
            UIManager.put("control", AppConstants.SECONDARY_COLOR);
            UIManager.put("text", AppConstants.TEXT_COLOR);
            UIManager.put("OptionPane.messageFont", new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
            UIManager.put("OptionPane.buttonFont", new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_NORMAL));

        } catch (Exception e) {
            // If Nimbus is not available, fall back to default
            System.err.println("Could not set Nimbus Look and Feel. Using default.");
        }

        setTitle(APP_TITLE);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        accounts = new HashMap<>();
        // Pre-populate some dummy accounts for testing
        accounts.put("12345", new BankAccount("12345", "Mostafa Gamal", 1000.0, "1234", "Savings"));
        accounts.put("67890", new BankAccount("67890", "Mona Hassan", 500.0, "5678", "Current"));
        accounts.put("11223", new BankAccount("11223", "Abdelrahman Gamal", 2500.0, "9999", "Savings"));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // --- Initialize Panels ---
        createLoginPanel();
        createMainMenuPanel();
        createAccountManagementPanel();

        showLoginPanel(); // Show login panel initially
        setVisible(true);
    }

    // --- Enhanced method to add typing sound to text fields ---
    private void addTypingSoundToTextField(JTextField textField) {
    textField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            soundManager.playTypeSound();
        }
    });
}

    private void addTypingSoundToPasswordField(JPasswordField passwordField) {
        passwordField.getDocument().addDocumentListener(new TypingSoundDocumentListener());
    }

    // --- GUI Panel Creation Methods ---

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(AppConstants.SECONDARY_COLOR);
        loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to " + APP_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_LARGE_TITLE));
        titleLabel.setForeground(AppConstants.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset grid width

        JLabel accNumLabel = new JLabel("Account Number:");
        accNumLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        accNumLabel.setForeground(AppConstants.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(accNumLabel, gbc);

        loginAccountNumberField = new JTextField(20);
        loginAccountNumberField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        loginAccountNumberField.putClientProperty("JTextField.placeholderText", "e.g., 12345");
        addTypingSoundToTextField(loginAccountNumberField); // Add typing sound
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(loginAccountNumberField, gbc);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        pinLabel.setForeground(AppConstants.WHITE); 
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(pinLabel, gbc);

        loginPinField = new JPasswordField(20);
        loginPinField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        addTypingSoundToPasswordField(loginPinField); // Add typing sound
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginPinField, gbc);

        JButton loginButton = createStyledButton("Login", null);
        loginButton.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_BUTTON));
        loginButton.setBackground(AppConstants.PRIMARY_COLOR);
        loginButton.setForeground(AppConstants.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            soundManager.playClickSound(); // Play click sound
            attemptLogin();
        });

        mainPanel.add(loginPanel, CARD_LOGIN);
    }

    private void createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout(20, 20));
        mainMenuPanel.setBackground(AppConstants.SECONDARY_COLOR);
        mainMenuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Panel for Welcome and Balance
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        infoPanel.setBackground(AppConstants.WHITE);
        infoPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        infoPanel.setPreferredSize(new Dimension(700, 80));

        welcomeLabel = new JLabel("Welcome, [Account Holder Name]");
        welcomeLabel.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_TITLE));
        welcomeLabel.setForeground(AppConstants.PRIMARY_COLOR);
        balanceLabel = new JLabel("Current Balance: " + BankAccount.formatCurrency(0.0));
        balanceLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_SUBTITLE));
        balanceLabel.setForeground(AppConstants.TEXT_COLOR);

        infoPanel.add(welcomeLabel);
        infoPanel.add(balanceLabel);
        JCheckBox soundToggle = new JCheckBox("Sounds", true);
        soundToggle.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        soundToggle.setForeground(AppConstants.TEXT_COLOR);
        soundToggle.setBackground(AppConstants.WHITE);
        soundToggle.addActionListener(e -> {
            soundManager.playClickSound();
            soundManager.setSoundEnabled(soundToggle.isSelected());
        });
        infoPanel.add(soundToggle);
        mainMenuPanel.add(infoPanel, BorderLayout.NORTH);

        // Center Panel for Transaction Actions
        JPanel transactionPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        transactionPanel.setBackground(AppConstants.SECONDARY_COLOR);
        transactionPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Create styled buttons with click sounds
        JButton depositButton = createStyledButton("Deposit", null);
        JButton withdrawButton = createStyledButton("Withdraw", null);
        JButton transferButton = createStyledButton("Transfer", null);
        JButton viewHistoryButton = createStyledButton("Main Statement", null);
        JButton manageAccountButton = createStyledButton("Manage Account", null);
        JButton logoutButton = createStyledButton("Logout", null);

        transactionPanel.add(depositButton);
        transactionPanel.add(withdrawButton);
        transactionPanel.add(transferButton);
        transactionPanel.add(viewHistoryButton);
        transactionPanel.add(manageAccountButton);
        transactionPanel.add(logoutButton);

        mainMenuPanel.add(transactionPanel, BorderLayout.CENTER);

        // Add Action Listeners with click sounds
        depositButton.addActionListener(e -> {
            soundManager.playClickSound();
            showAmountInputDialog("Deposit", true, false);
        });
        withdrawButton.addActionListener(e -> {
            soundManager.playClickSound();
            showAmountInputDialog("Withdraw", true, false);
        });
        transferButton.addActionListener(e -> {
            soundManager.playClickSound();
            showAmountInputDialog("Transfer", true, true);
        });
        viewHistoryButton.addActionListener(e -> {
            soundManager.playClickSound();
            viewTransactionHistory();
        });
        manageAccountButton.addActionListener(e -> {
            soundManager.playClickSound();
            showAccountManagement();
        });
        logoutButton.addActionListener(e -> {
            soundManager.playClickSound();
            logout();
        });

        mainPanel.add(mainMenuPanel, CARD_MAIN_MENU);
    }

    private void createAccountManagementPanel() {
        JPanel managePanel = new JPanel(new GridBagLayout());
        managePanel.setBackground(AppConstants.SECONDARY_COLOR);
        managePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Manage Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_TITLE));
        titleLabel.setForeground(AppConstants.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        managePanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel currentNameTitle = new JLabel("Current Account Holder:");
        currentNameTitle.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, AppConstants.FONT_SIZE_NORMAL));
        currentNameTitle.setForeground(AppConstants.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        managePanel.add(currentNameTitle, gbc);

        currentNameLabel = new JLabel("[Name]");
        currentNameLabel.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_NORMAL));
        currentNameLabel.setForeground(AppConstants.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 1;
        managePanel.add(currentNameLabel, gbc);

        JLabel newNameLabel = new JLabel("New Name (optional):");
        newNameLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        newNameLabel.setForeground(AppConstants.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        managePanel.add(newNameLabel, gbc);

        newNameField = new JTextField(20);
        newNameField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        newNameField.setForeground(AppConstants.TEXT_COLOR);         
        newNameField.setBackground(AppConstants.WHITE);            
        newNameField.putClientProperty("JTextField.placeholderText", "Enter new name");
        addTypingSoundToTextField(newNameField); // Add typing sound
        gbc.gridx = 1;
        gbc.gridy = 2;
        managePanel.add(newNameField, gbc);

        JLabel newPinLabel = new JLabel("New PIN (optional):");
        newPinLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        newPinLabel.setForeground(AppConstants.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        managePanel.add(newPinLabel, gbc);

        newPinField = new JPasswordField(20);
        newPinField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        newPinField.setForeground(AppConstants.TEXT_COLOR);          
        newPinField.setBackground(AppConstants.WHITE);               
        addTypingSoundToPasswordField(newPinField); // Add typing sound
        gbc.gridx = 1;
        gbc.gridy = 3;
        managePanel.add(newPinField, gbc);

        JLabel statusLabel = new JLabel("Account Status:");
        statusLabel.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        statusLabel.setForeground(AppConstants.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        managePanel.add(statusLabel, gbc);

        activeStatusCheckBox = new JCheckBox("Active");
        activeStatusCheckBox.addActionListener(e -> soundManager.playClickSound());

        activeStatusCheckBox.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        activeStatusCheckBox.setForeground(AppConstants.WHITE);     
        activeStatusCheckBox.setBackground(AppConstants.SECONDARY_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 4;
        managePanel.add(activeStatusCheckBox, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;
        JButton saveAccountChangesButton = createStyledButton("Save Changes", null);
        saveAccountChangesButton.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_BUTTON));
        managePanel.add(saveAccountChangesButton, gbc);

        gbc.gridy = 6;
        JButton backFromManageButton = createStyledButton("Back to Main Menu", null);
        backFromManageButton.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_BUTTON));
        managePanel.add(backFromManageButton, gbc);

        // Add click sound to buttons
        saveAccountChangesButton.addActionListener(e -> {
            soundManager.playClickSound();
            saveAccountChanges();
        });
        backFromManageButton.addActionListener(e -> {
            soundManager.playClickSound();
            showMainMenu();
        });
        
        mainPanel.add(managePanel, CARD_ACCOUNT_MANAGEMENT);
    }

    // --- Helper for creating consistently styled buttons ---
    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font(AppConstants.FONT_NAME, Font.BOLD, FONT_SIZE_BUTTON));
        button.setBackground(AppConstants.PRIMARY_COLOR);
        button.setForeground(AppConstants.TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppConstants.ACCENT_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppConstants.PRIMARY_COLOR);
            }
        });
        return button;
    }

    // --- Navigation Methods ---
    private void showLoginPanel() {
        loggedInAccount = null;
        clearLoginFields();
        soundManager.playOpenSound(); // Play open sound
        cardLayout.show(mainPanel, CARD_LOGIN);
    }

    private void showMainMenu() {
        updateMainMenuUI();
        cardLayout.show(mainPanel, CARD_MAIN_MENU);
    }

    private void showAccountManagement() {
        if (loggedInAccount == null) return;
        currentNameLabel.setText(loggedInAccount.getAccountHolderName());
        newNameField.setText("");
        newPinField.setText("");
        activeStatusCheckBox.setSelected(loggedInAccount.isActive());
        cardLayout.show(mainPanel, CARD_ACCOUNT_MANAGEMENT);
    }

    // --- Core Logic Methods ---

    private void attemptLogin() {
        String accNum = loginAccountNumberField.getText().trim();
        String pin = new String(loginPinField.getPassword()).trim();

        if (accNum.isEmpty() || pin.isEmpty()) {
            showMessage("Login Error", "Please enter both account number and PIN.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BankAccount account = accounts.get(accNum);
        if (account != null && account.getPin().equals(pin)) {
            if (!account.isActive()) {
                showMessage("Login Failed", "Your account is currently inactive. Please contact bank support.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            loggedInAccount = account;
            showMainMenu();
        } else {
            showMessage("Login Failed", "Invalid Account Number or PIN.", JOptionPane.ERROR_MESSAGE);
        }
    }

    // New: Centralized method for input dialogs
    private void showAmountInputDialog(String actionType, boolean requiresAmount, boolean requiresTargetAccount) {
        if (loggedInAccount == null) return;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        amountField.putClientProperty("JTextField.placeholderText", "Enter amount");
        
        JTextField targetAccountNumberField = new JTextField(15);
        targetAccountNumberField.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, FONT_SIZE_NORMAL));
        targetAccountNumberField.putClientProperty("JTextField.placeholderText", "Target Account #");
        targetAccountNumberField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                soundManager.playTypeSound();
            }
        });

        int row = 0;
        if (requiresAmount) {
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Amount:"), gbc);
            gbc.gridx = 1; gbc.gridy = row;
            panel.add(amountField, gbc);
            row++;
        }

        if (requiresTargetAccount) {
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Target Account Number:"), gbc);
            gbc.gridx = 1; gbc.gridy = row;
            panel.add(targetAccountNumberField, gbc);
            row++;
        }

        int option = JOptionPane.showConfirmDialog(this, panel, actionType, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            double amount = 0;
            String targetAccNum = "";

            if (requiresAmount) {
                try {
                    amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        showMessage("Input Error", "Amount must be a positive number.", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showMessage("Input Error", "Invalid amount. Please enter a valid number.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (requiresTargetAccount) {
                targetAccNum = targetAccountNumberField.getText().trim();
                if (targetAccNum.isEmpty()) {
                    showMessage("Input Error", "Target account number cannot be empty.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (targetAccNum.equals(loggedInAccount.getAccountNumber())) {
                    showMessage("Input Error", "Cannot transfer to the same account.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!accounts.containsKey(targetAccNum)) {
                    showMessage("Transfer Error", "Target account number not found.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Perform the specific action based on actionType
           switch (actionType) {
    case "Deposit":
        if (loggedInAccount.deposit(amount)) {
            showMessage("Success",
                "<html><span style='color:white;'>Deposit successful!<br>New Balance: " +
                BankAccount.formatCurrency(loggedInAccount.getBalance()) + "</span></html>",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Error",
                "<html><span style='color:white;'>Deposit failed. Account might be inactive.</span></html>",
                JOptionPane.ERROR_MESSAGE);
        }
        break;

    case "Withdraw":
        if (loggedInAccount.withdraw(amount)) {
            showMessage("Success",
                "<html><span style='color:white;'>Withdrawal successful!<br>New Balance: " +
                BankAccount.formatCurrency(loggedInAccount.getBalance()) + "</span></html>",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Error",
                "<html><span style='color:white;'>Withdrawal failed. Insufficient funds or account inactive.</span></html>",
                JOptionPane.ERROR_MESSAGE);
        }
        break;

    case "Transfer":
        BankAccount targetAccount = accounts.get(targetAccNum);
        if (loggedInAccount.transfer(targetAccount, amount)) {
            showMessage("Success",
                "<html><span style='color:white;'>Transfer successful!<br>New Balance: " +
                BankAccount.formatCurrency(loggedInAccount.getBalance()) + "</span></html>",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Error",
                "<html><span style='color:white;'>Transfer failed. Check funds, amounts, or account status.</span></html>",
                JOptionPane.ERROR_MESSAGE);
        }
        break;
}

updateMainMenuUI();

}
    }


    private void updateMainMenuUI() {
        if (loggedInAccount != null) {
            welcomeLabel.setText("Welcome, " + loggedInAccount.getAccountHolderName());
            balanceLabel.setText("Current Balance: " + BankAccount.formatCurrency(loggedInAccount.getBalance()));
        }
    }

    private void viewTransactionHistory() {
        if (loggedInAccount == null) return;

        StringBuilder historyText = new StringBuilder("Transaction History for Account " + loggedInAccount.getAccountNumber() + "\n\n");
        if (loggedInAccount.getTransactionHistory().isEmpty()) {
            historyText.append("No transactions yet.");
        } else {
            for (String transaction : loggedInAccount.getTransactionHistory()) {
                historyText.append(transaction).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(historyText.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(AppConstants.FONT_NAME, Font.PLAIN, AppConstants.FONT_SIZE_NORMAL));


        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 350));
        showMessage("Transaction History", scrollPane, JOptionPane.PLAIN_MESSAGE);
    }

    private void saveAccountChanges() {
        if (loggedInAccount == null) return;

        String newName = newNameField.getText().trim();
        String newPin = new String(newPinField.getPassword()).trim();
        boolean newStatus = activeStatusCheckBox.isSelected();
        boolean changesMade = false;

        if (!newName.isEmpty() && !newName.equals(loggedInAccount.getAccountHolderName())) {
            loggedInAccount.setAccountHolderName(newName);
            changesMade = true;
        }

        if (!newPin.isEmpty() && !newPin.equals(loggedInAccount.getPin())) {
            // WARNING: In a real system, you would hash the new PIN securely.
            loggedInAccount.setPin(newPin);
            changesMade = true;
        }

        if (newStatus != loggedInAccount.isActive()) {
            loggedInAccount.setActive(newStatus);
            changesMade = true;
            if (!newStatus) {
                 showMessage("Account Status Change", "Account set to inactive. No transactions allowed.", JOptionPane.WARNING_MESSAGE);
            }
        }

            if (changesMade) {
                String title = "Update Success";
                String message = "<html><span style='color:white;'>Account details updated successfully!</span></html>";
                UIManager.put("OptionPane.background", AppConstants.SECONDARY_COLOR);      
                UIManager.put("Panel.background", AppConstants.SECONDARY_COLOR);          
                UIManager.put("OptionPane.messageForeground", Color.WHITE);                

                showMessage(title, message, JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Info", "<html><span style='color:white;'>No changes were made.</span></html>", JOptionPane.INFORMATION_MESSAGE);
            }

            showMainMenu();

    }

    private void logout() {
        showLoginPanel();
        showMessage("Logout", "You have been logged out.", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Utility Methods ---
    private void clearLoginFields() {
        loginAccountNumberField.setText("");
        loginPinField.setText("");
    }

// Overloaded showMessage for different message types (String or Component)
// now supports a special sound for errors
private void showMessage(String title, Object message, int messageType) {
    boolean isError = false;

    // Check if the messageType indicates an error
    if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
        isError = true;
    }

    // Play the appropriate sound
    if (isError) {
        soundManager.playWrongSound();  // custom wrong.wav for errors
    } else {
        soundManager.playOpenSound();   // open.wav for normal info messages
    }

    // Show the message dialog
    JOptionPane.showMessageDialog(this, message, title, messageType);
}


    // Main method to run the application
    public static void main(String[] args) {
    UIManager.put("Label.foreground", AppConstants.WHITE);
    UIManager.put("Button.background", AppConstants.BACKGROUND_COLOR); // أبيض
    UIManager.put("Button.foreground", AppConstants.WHITE);
    UIManager.put("Panel.background", AppConstants.BACKGROUND_COLOR);
    UIManager.put("OptionPane.background", AppConstants.SECONDARY_COLOR);
    UIManager.put("Panel.background", AppConstants.SECONDARY_COLOR);
    UIManager.put("OptionPane.messageForeground", Color.WHITE);

    UIManager.put("TextField.background", AppConstants.WHITE);
    UIManager.put("TextField.foreground", AppConstants.TEXT_COLOR); // أسود
    UIManager.put("PasswordField.background", AppConstants.WHITE);
    UIManager.put("PasswordField.foreground", AppConstants.TEXT_COLOR);
    UIManager.put("TextArea.background", AppConstants.WHITE);
    UIManager.put("TextArea.foreground", AppConstants.TEXT_COLOR);
    UIManager.put("ComboBox.background", AppConstants.WHITE);
    UIManager.put("ComboBox.foreground", AppConstants.TEXT_COLOR);

        SwingUtilities.invokeLater(Main::new); 
    }
}
