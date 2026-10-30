package gui;

import javax.swing.*;
import java.awt.*;
import database.FileDatabase;

public class RegistrationScreen extends JDialog {
    private JTextField regNumberField, nameField;
    private JComboBox<String> departmentCombo, sectionCombo;
    private JPasswordField passwordField, confirmPasswordField;

    public RegistrationScreen(JFrame parent) {
        super(parent, "Student Registration", true);
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("📝 Student Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Register Number
        gbc.gridwidth = 1; gbc.gridy = 1;
        addLabel(mainPanel, "Register Number:", gbc);
        regNumberField = new JTextField(20);
        regNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(regNumberField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 2;
        addLabel(mainPanel, "Full Name:", gbc);
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Department
        gbc.gridx = 0; gbc.gridy = 3;
        addLabel(mainPanel, "Department:", gbc);
        String[] departments = {"CSE", "ECE", "EEE", "MECH", "CIVIL", "IT", "AI&DS", "CSE(AI&ML)", "AIDS"};
        departmentCombo = new JComboBox<>(departments);
        departmentCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(departmentCombo, gbc);

        // Section
        gbc.gridx = 0; gbc.gridy = 4;
        addLabel(mainPanel, "Section:", gbc);
        String[] sections = {"A", "B", "C", "D", "E", "F"};
        sectionCombo = new JComboBox<>(sections);
        sectionCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(sectionCombo, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 5;
        addLabel(mainPanel, "Password:", gbc);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 6;
        addLabel(mainPanel, "Confirm Password:", gbc);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(46, 204, 113));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(new Dimension(120, 35));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setPreferredSize(new Dimension(120, 35));

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Actions
        registerBtn.addActionListener(e -> handleRegistration());
        cancelBtn.addActionListener(e -> dispose());

        add(mainPanel);
        setVisible(true);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(44, 62, 80));
        panel.add(label, gbc);
    }

    private void handleRegistration() {
        String regNo = regNumberField.getText().trim();
        String name = nameField.getText().trim();
        String dept = (String) departmentCombo.getSelectedItem();
        String section = (String) sectionCombo.getSelectedItem();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();

        // Validation
        if (regNo.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 4 characters!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register
        if (FileDatabase.registerStudent(regNo, name, dept, section, password)) {
            JOptionPane.showMessageDialog(this, 
                "Registration Successful!\n\nYour Details:\n" +
                "Register No: " + regNo + "\n" +
                "Name: " + name + "\n" +
                "Department: " + dept + "\n" +
                "Section: " + section + "\n\n" +
                "You can now login with your credentials!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration Failed!\n" +
                "This register number already exists.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}