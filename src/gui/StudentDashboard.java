package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import database.FileDatabase;

public class StudentDashboard extends JFrame {
    private String registerNumber;
    private String studentName, department, section;
    private JTable projectTable;
    private DefaultTableModel tableModel;

    public StudentDashboard(String registerNumber) {
        this.registerNumber = registerNumber;
        loadStudentInfo();
        
        setTitle("Student Dashboard - " + studentName);
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 123, 255));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome " + studentName + " (" + registerNumber + ")");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setForeground(Color.WHITE);

        JButton logout = new JButton("Logout");
        logout.setBackground(Color.RED);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Arial", Font.BOLD, 14));

        header.add(welcome, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // BUTTONS
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBackground(new Color(240, 248, 255));

        JButton submit = new JButton("Submit New Project");
        submit.setBackground(new Color(40, 167, 69));
        submit.setForeground(Color.WHITE);
        submit.setFont(new Font("Arial", Font.BOLD, 16));
        submit.setPreferredSize(new Dimension(200, 40));

        JButton refresh = new JButton("Refresh");
        refresh.setBackground(new Color(0, 123, 255));
        refresh.setForeground(Color.WHITE);
        refresh.setFont(new Font("Arial", Font.BOLD, 16));
        refresh.setPreferredSize(new Dimension(150, 40));

        btnPanel.add(submit);
        btnPanel.add(refresh);

        // TABLE
        String[] cols = {"ID", "Project Title", "Description", "Date", "Status", "Admin Feedback", "PDF"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };

        projectTable = new JTable(tableModel);
        projectTable.setFont(new Font("Arial", Font.PLAIN, 14));
        projectTable.setRowHeight(40);
        projectTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        projectTable.getTableHeader().setBackground(new Color(52, 58, 64));
        projectTable.getTableHeader().setForeground(Color.WHITE);

        projectTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(Color.BLACK);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 4);
                    if ("Approved".equals(status)) {
                        c.setBackground(new Color(144, 238, 144));
                    } else if ("Rejected".equals(status)) {
                        c.setBackground(new Color(255, 160, 160));
                    } else {
                        c.setBackground(new Color(255, 255, 153));
                    }
                }
                if (col == 4) {
                    setFont(new Font("Arial", Font.BOLD, 14));
                } else {
                    setFont(new Font("Arial", Font.PLAIN, 14));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(projectTable);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(btnPanel, BorderLayout.CENTER);
        mainPanel.add(scroll, BorderLayout.SOUTH);

        submit.addActionListener(e -> openSubmitDialog());
        refresh.addActionListener(e -> loadProjects());
        logout.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        add(mainPanel);
        loadProjects();
        setVisible(true);
    }

    private void loadStudentInfo() {
        String[] info = FileDatabase.getStudentInfo(registerNumber);
        if (info != null) {
            studentName = info[1];
            department = info[2];
            section = info[3];
        }
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        List<String[]> projects = FileDatabase.getStudentProjects(registerNumber);
        
        for (String[] p : projects) {
            String pdfInfo = "No PDF";
            if (p.length > 10 && !p[10].isEmpty()) {
                pdfInfo = "PDF Attached";
            }
            
            tableModel.addRow(new Object[]{
                p[0], 
                p[5], 
                p[6].isEmpty() ? "No description" : p[6], 
                p[7], 
                p[8], 
                p[9].isEmpty() ? "Waiting..." : p[9],
                pdfInfo
            });
        }
        
        if (projects.isEmpty()) {
            tableModel.addRow(new Object[]{"", "No projects yet!", "Submit your first project", "", "", "", ""});
        }
    }

    private void openSubmitDialog() {
        JDialog dialog = new JDialog(this, "Submit New Project", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLbl = new JLabel("Project Title:");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; 
        gbc.gridy = 0;
        panel.add(titleLbl, gbc);

        JTextField titleField = new JTextField(20);
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Description
        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; 
        gbc.gridy = 1;
        panel.add(descLbl, gbc);

        JTextArea descArea = new JTextArea(5, 20);
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(sp, gbc);

        // PDF Upload
        JLabel pdfLbl = new JLabel("Upload PDF (Optional):");
        pdfLbl.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; 
        gbc.gridy = 2;
        panel.add(pdfLbl, gbc);

        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pdfPanel.setOpaque(false);
        
        JTextField pdfField = new JTextField(15);
        pdfField.setEditable(false);
        pdfField.setBackground(Color.WHITE);
        
        JButton browseBtn = new JButton("Browse PDF");
        browseBtn.setBackground(new Color(0, 123, 255));
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        browseBtn.setFocusPainted(false);
        
        pdfPanel.add(pdfField);
        pdfPanel.add(browseBtn);
        
        gbc.gridx = 1;
        panel.add(pdfPanel, gbc);

        // Buttons
        JButton submitBtn = new JButton("Submit Project");
        submitBtn.setBackground(new Color(40, 167, 69));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.RED);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; 
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        // Browse PDF Action
        final String[] selectedPDF = {""};
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
            int result = chooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedPDF[0] = chooser.getSelectedFile().getAbsolutePath();
                pdfField.setText(chooser.getSelectedFile().getName());
            }
        });

        // Submit Action
        submitBtn.addActionListener(e -> {
            String projName = titleField.getText().trim();
            String description = descArea.getText().trim();

            if (projName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter project title!", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            if (FileDatabase.submitProject(registerNumber, studentName, department, section, 
                    projName, description, date, selectedPDF[0])) {
                String msg = "Project Submitted Successfully!";
                if (!selectedPDF[0].isEmpty()) {
                    msg += "\nPDF attached: " + pdfField.getText();
                }
                JOptionPane.showMessageDialog(dialog, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadProjects();
            } else {
                JOptionPane.showMessageDialog(dialog, "Submission Failed! Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }
}