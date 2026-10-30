package main;

import javax.swing.*;
import database.FileDatabase;
import gui.LoginScreen;

public class MainApp {
    public static void main(String[] args) {
        // Initialize database
        FileDatabase.initializeDatabase();
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch login screen
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen();
            }
        });
        
        System.out.println("\n========================================");
        System.out.println("   MINI PROJECT TRACKER SYSTEM");
        System.out.println("========================================");
        System.out.println("Default Admin Credentials:");
        System.out.println("  Username: admin");
        System.out.println("  Password: admin123");
        System.out.println("========================================");
        System.out.println("Application started successfully!");
        System.out.println("Data stored in text files:");
        System.out.println("  - students.txt");
        System.out.println("  - projects.txt");
        System.out.println("  - admins.txt");
        System.out.println("========================================\n");
    }
}