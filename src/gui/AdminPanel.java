package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import database.FileDatabase;

public class AdminPanel extends JFrame {
    private JTable projectTable;
    private DefaultTableModel tableModel;

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(255, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 53, 69));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("ADMIN CONTROL PANEL");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(139, 0, 0));
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Arial", Font.BOLD, 14));

        header.add(title, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // STATS
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        stats.setBackground(new Color(255, 240, 245));

        Map<String, Integer> st = FileDatabase.getStatistics();
        JLabel statsLabel = new JLabel(String.format(
            "Total: %d  |  Pending: %d  |  Approved: %d  |  Rejected: %d",
            st.get("total"), st.get("pending"), st.get("approved"), st.get("rejected")
        ));
        statsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stats.add(statsLabel);

        // BUTTONS
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(new Color(255, 240, 245));

        JButton approve = new JButton("APPROVE");
        approve.setBackground(new Color(40, 167, 69));
        approve.setForeground(Color.WHITE);
        approve.setFont(new Font("Arial", Font.BOLD, 16));
        approve.setPreferredSize(new Dimension(150, 40));

        JButton reject = new JButton("REJECT");
        reject.setBackground(new Color(220, 53, 69));
        reject.setForeground(Color.WHITE);
        reject.setFont(new Font("Arial", Font.BOLD, 16));
        reject.setPreferredSize(new Dimension(150, 40));

        JButton refresh = new JButton("Refresh");
        refresh.setBackground(new Color(0, 123, 255));
        refresh.setForeground(Color.WHITE);
        refresh.setFont(new Font("Arial", Font.BOLD, 16));
        refresh.setPreferredSize(new Dimension(130, 40));

        btnPanel.add(approve);
        btnPanel.add(reject);
        btnPanel.add(refresh);

        // TABLE
        String[] cols = {"ID", "Reg No", "Name", "Dept", "Sec", "Project Title", "Description", "Date", "Status", "Feedback"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        projectTable = new JTable(tableModel);
        projectTable.setFont(new Font("Arial", Font.PLAIN, 13));
        projectTable.setRowHeight(40);
        projectTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        projectTable.getTableHeader().setBackground(new Color(52, 58, 64));
        projectTable.getTableHeader().setForeground(Color.WHITE);

        projectTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(Color.BLACK);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 8);
                    if ("Approved".equals(status)) {
                        c.setBackground(new Color(144, 238, 144));
                    } else if ("Rejected".equals(status)) {
                        c.setBackground(new Color(255, 160, 160));
                    } else {
                        c.setBackground(new Color(255, 255, 153));
                    }
                }
                if (col == 8) setFont(new Font("Arial", Font.BOLD, 14));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(projectTable);

        JPanel top = new JPanel(new BorderLayout());
        top.add(stats, BorderLayout.NORTH);
        top.add(btnPanel, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(top, BorderLayout.CENTER);
        mainPanel.add(scroll, BorderLayout.SOUTH);

        approve.addActionListener(e -> updateStatus("Approved", statsLabel));
        reject.addActionListener(e -> updateStatus("Rejected", statsLabel));
        refresh.addActionListener(e -> { loadProjects(); updateStats(statsLabel); });
        logout.addActionListener(e -> { dispose(); new LoginScreen(); });

        add(mainPanel);
        loadProjects();
        setVisible(true);
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        List<String[]> projects = FileDatabase.getAllProjects();
        
        for (String[] p : projects) {
            tableModel.addRow(new Object[]{
                p[0], p[1], p[2], p[3], p[4], p[5], 
                p[6].isEmpty() ? "No description" : p[6], 
                p[7], p[8], 
                p[9].isEmpty() ? "No feedback" : p[9]
            });
        }
    }

    private void updateStatus(String status, JLabel statsLabel) {
        int row = projectTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a project first!");
            return;
        }

        int id = Integer.parseInt(projectTable.getValueAt(row, 0).toString());
        String currentStatus = projectTable.getValueAt(row, 8).toString();

        if (!"Pending".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Already " + currentStatus + "!");
            return;
        }

        String comment = JOptionPane.showInputDialog(this, "Enter feedback:");
        if (comment == null) return;

        if (FileDatabase.updateProjectStatus(id, status, comment.isEmpty() ? status + " by admin" : comment)) {
            JOptionPane.showMessageDialog(this, "Status Updated!");
            loadProjects();
            updateStats(statsLabel);
        }
    }

    private void updateStats(JLabel statsLabel) {
        Map<String, Integer> st = FileDatabase.getStatistics();
        statsLabel.setText(String.format(
            "Total: %d  |  Pending: %d  |  Approved: %d  |  Rejected: %d",
            st.get("total"), st.get("pending"), st.get("approved"), st.get("rejected")
        ));
    }
}
/*package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import database.FileDatabase;

public class AdminPanel extends JFrame {
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    public AdminPanel() {
        setTitle("Admin Panel - Mini Project Tracker");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel (Header)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(231, 76, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🔐 Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statsPanel.add(statsLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton approveBtn = new JButton("✅ Approve Selected");
        approveBtn.setBackground(new Color(46, 204, 113));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton rejectBtn = new JButton("❌ Reject Selected");
        rejectBtn.setBackground(new Color(231, 76, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 14));
        rejectBtn.setFocusPainted(false);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(statsPanel, BorderLayout.NORTH);
        topContainer.add(buttonPanel, BorderLayout.CENTER);

        // Table
        String[] columns = {"ID", "Reg No", "Student Name", "Dept", "Sec", "Project Name", 
                           "Description", "Date", "Status", "Comments"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        projectTable = new JTable(tableModel);
        projectTable.setFont(new Font("Arial", Font.PLAIN, 13));
        projectTable.setRowHeight(35);
        projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        projectTable.getTableHeader().setBackground(new Color(52, 73, 94));
        projectTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        projectTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        projectTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        projectTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        projectTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        projectTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        projectTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        projectTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        projectTable.getColumnModel().getColumn(7).setPreferredWidth(130);
        projectTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        projectTable.getColumnModel().getColumn(9).setPreferredWidth(150);

        // Color code status
        projectTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 8);
                    if ("Approved".equals(status)) {
                        c.setBackground(new Color(212, 239, 223));
                    } else if ("Rejected".equals(status)) {
                        c.setBackground(new Color(248, 215, 218));
                    } else {
                        c.setBackground(new Color(255, 243, 205));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            "All Project Submissions",
            0, 0, new Font("Arial", Font.BOLD, 16), new Color(52, 73, 94)
        ));

        // Add to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(topContainer, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Actions
        approveBtn.addActionListener(e -> updateStatus("Approved"));
        rejectBtn.addActionListener(e -> updateStatus("Rejected"));
        refreshBtn.addActionListener(e -> loadProjects());
        logoutBtn.addActionListener(e -> logout());

        // Double click to view details
        projectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewProjectDetails();
                }
            }
        });

        add(mainPanel);
        loadProjects();
        setVisible(true);
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        List<String[]> projects = FileDatabase.getAllProjects();
        
        for (String[] project : projects) {
            tableModel.addRow(new Object[]{
                project[0],  // ID
                project[1],  // Reg No
                project[2],  // Student Name
                project[3],  // Department
                project[4],  // Section
                project[5],  // Project Name
                project[6],  // Description
                project[7],  // Date
                project[8],  // Status
                project[9].isEmpty() ? "No comments" : project[9]  // Comments
            });
        }
        
        updateStatistics();
    }

    private void updateStatistics() {
        Map<String, Integer> stats = FileDatabase.getStatistics();
        statsLabel.setText(String.format(
            "📊 Total Submissions: %d  |  ⏳ Pending: %d  |  ✅ Approved: %d  |  ❌ Rejected: %d",
            stats.get("total"), stats.get("pending"), stats.get("approved"), stats.get("rejected")
        ));
    }

    private void updateStatus(String status) {
        int selectedRow = projectTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a project first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int projectId = Integer.parseInt(projectTable.getValueAt(selectedRow, 0).toString());
        String currentStatus = projectTable.getValueAt(selectedRow, 8).toString();

        if (!currentStatus.equals("Pending")) {
            JOptionPane.showMessageDialog(this, 
                "This project has already been " + currentStatus.toLowerCase() + "!", 
                "Already Processed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get comments
        String comments = JOptionPane.showInputDialog(this, 
            "Enter comments for this " + status.toLowerCase() + " decision:",
            "Add Comments",
            JOptionPane.PLAIN_MESSAGE);

        if (comments == null) return; // Cancelled

        if (comments.trim().isEmpty()) {
            comments = status + " by admin";
        }

        if (FileDatabase.updateProjectStatus(projectId, status, comments)) {
            JOptionPane.showMessageDialog(this, 
                "Project " + status.toLowerCase() + " successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadProjects();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update status!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewProjectDetails() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) return;

        String details = String.format(
            "PROJECT DETAILS\n\n" +
            "ID: %s\n" +
            "Register Number: %s\n" +
            "Student Name: %s\n" +
            "Department: %s\n" +
            "Section: %s\n" +
            "Project Name: %s\n" +
            "Description: %s\n" +
            "Submission Date: %s\n" +
            "Status: %s\n" +
            "Admin Comments: %s",
            projectTable.getValueAt(selectedRow, 0),
            projectTable.getValueAt(selectedRow, 1),
            projectTable.getValueAt(selectedRow, 2),
            projectTable.getValueAt(selectedRow, 3),
            projectTable.getValueAt(selectedRow, 4),
            projectTable.getValueAt(selectedRow, 5),
            projectTable.getValueAt(selectedRow, 6),
            projectTable.getValueAt(selectedRow, 7),
            projectTable.getValueAt(selectedRow, 8),
            projectTable.getValueAt(selectedRow, 9)
        );

        JTextArea textArea = new JTextArea(details);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, 
            "Project Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen();
        }
    }
}*/