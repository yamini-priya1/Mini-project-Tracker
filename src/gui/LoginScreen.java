package gui;

import javax.swing.*;
import java.awt.*;
import database.FileDatabase;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton studentRadio, adminRadio;

    public LoginScreen() {
        setTitle("Mini Project Tracker - Login");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), 
                                                     0, getHeight(), new Color(41, 129, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Mini Project Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Track Your Project Submissions");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Login type
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel typeLabel = new JLabel("Login As:");
        typeLabel.setForeground(Color.BLACK);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(typeLabel, gbc);

        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        studentRadio = new JRadioButton("Student", true);
        adminRadio = new JRadioButton("Admin");
        studentRadio.setForeground(Color.BLACK);
        adminRadio.setForeground(Color.BLACK);
        studentRadio.setOpaque(false);
        adminRadio.setOpaque(false);
        studentRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        adminRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(adminRadio);
        radioPanel.add(studentRadio);
        radioPanel.add(adminRadio);
        gbc.gridx = 1;
        mainPanel.add(radioPanel, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel userLabel = new JLabel("Username/Reg No:");
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField(18);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.BLACK);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Login button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginBtn, gbc);

        // Register button
        JButton regBtn = new JButton("New Student? Register Here");
        regBtn.setForeground(Color.BLACK);
        regBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        regBtn.setOpaque(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        mainPanel.add(regBtn, gbc);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> openRegistration());
        passwordField.addActionListener(e -> handleLogin());

        add(mainPanel);
        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password!", 
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (studentRadio.isSelected()) {
            // Student login
            if (FileDatabase.validateStudent(username, password)) {
                JOptionPane.showMessageDialog(this, 
                    "Login Successful! Welcome " + username, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new StudentDashboard(username);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials! Please check your register number and password.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Admin login
            if (FileDatabase.validateAdmin(username, password)) {
                JOptionPane.showMessageDialog(this, 
                    "Admin Login Successful!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new AdminPanel();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid admin credentials!", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openRegistration() {
        new RegistrationScreen(this);
    }
}


