package database;

import java.io.*;
import java.util.*;

public class FileDatabase {
    private static final String STUDENTS_FILE = "students.txt";
    private static final String PROJECTS_FILE = "projects.txt";
    private static final String ADMINS_FILE = "admins.txt";

    public static void initializeDatabase() {
        try {
            createFileIfNotExists(STUDENTS_FILE);
            createFileIfNotExists(PROJECTS_FILE);
            createFileIfNotExists(ADMINS_FILE);
            
            boolean adminExists = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(ADMINS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("admin|")) {
                        adminExists = true;
                        break;
                    }
                }
            }
            
            if (!adminExists) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(ADMINS_FILE, true))) {
                    writer.println("admin|admin123|System Administrator");
                }
            }
            
            System.out.println("Database initialized!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileIfNotExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static boolean validateStudent(String registerNumber, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(registerNumber) && parts[4].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateAdmin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMINS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].trim().equals(username.trim()) && parts[1].trim().equals(password.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerStudent(String regNo, String name, String dept, String section, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(regNo)) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE, true))) {
            writer.println(regNo + "|" + name + "|" + dept + "|" + section + "|" + password);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String[] getStudentInfo(String registerNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(registerNumber)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean submitProject(String regNo, String name, String dept, String section,
                                       String projectName, String description, String date, String pdfPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROJECTS_FILE, true))) {
            int projectId = getNextProjectId();
            writer.println(projectId + "|" + regNo + "|" + name + "|" + dept + "|" + section + "|" +
                          projectName + "|" + description + "|" + date + "|Pending||" + pdfPath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static int getNextProjectId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) {
                    maxId = id;
                }
            }
        } catch (IOException | NumberFormatException e) {
        }
        return maxId + 1;
    }

    public static List<String[]> getStudentProjects(String registerNumber) {
        List<String[]> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts[1].equals(registerNumber)) {
                    projects.add(parts);
                }
            }
        } catch (IOException e) {
        }
        Collections.reverse(projects);
        return projects;
    }

    public static List<String[]> getAllProjects() {
        List<String[]> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                projects.add(parts);
            }
        } catch (IOException e) {
        }
        Collections.reverse(projects);
        return projects;
    }

    public static boolean updateProjectStatus(int projectId, String status, String comments) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (Integer.parseInt(parts[0]) == projectId) {
                    parts[8] = status;
                    parts[9] = comments;
                    line = String.join("|", parts);
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        if (updated) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(PROJECTS_FILE))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("approved", 0);
        stats.put("rejected", 0);

        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                stats.put("total", stats.get("total") + 1);
                String status = parts[8];
                if (status.equals("Pending")) {
                    stats.put("pending", stats.get("pending") + 1);
                } else if (status.equals("Approved")) {
                    stats.put("approved", stats.get("approved") + 1);
                } else if (status.equals("Rejected")) {
                    stats.put("rejected", stats.get("rejected") + 1);
                }
            }
        } catch (IOException e) {
        }
        return stats;
    }
}
/*package database;

import java.io.*;
import java.util.*;

public class FileDatabase {
    private static final String STUDENTS_FILE = "students.txt";
    private static final String PROJECTS_FILE = "projects.txt";
    private static final String ADMINS_FILE = "admins.txt";

    // Initialize files and create default admin
    public static void initializeDatabase() {
        try {
            // Create files if they don't exist
            createFileIfNotExists(STUDENTS_FILE);
            createFileIfNotExists(PROJECTS_FILE);
            createFileIfNotExists(ADMINS_FILE);
            
            // Create default admin if not exists
            if (isFileEmpty(ADMINS_FILE)) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(ADMINS_FILE, true))) {
                    writer.println("admin|admin123|System Administrator");
                    System.out.println("✓ Default admin created!");
                    System.out.println("  Username: admin");
                    System.out.println("  Password: admin123");
                }
            }
            System.out.println("✓ Database initialized successfully!");
        } catch (IOException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void createFileIfNotExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static boolean isFileEmpty(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.readLine() == null;
        }
    }

    // Validate student login
    public static boolean validateStudent(String registerNumber, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(registerNumber) && parts[4].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error validating student: " + e.getMessage());
        }
        return false;
    }

    // Validate admin login
    public static boolean validateAdmin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMINS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error validating admin: " + e.getMessage());
        }
        return false;
    }

    // Register new student
    public static boolean registerStudent(String regNo, String name, String dept, String section, String password) {
        // Check if register number already exists
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(regNo)) {
                    return false; // Already exists
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking student: " + e.getMessage());
            return false;
        }

        // Add new student
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE, true))) {
            writer.println(regNo + "|" + name + "|" + dept + "|" + section + "|" + password);
            return true;
        } catch (IOException e) {
            System.err.println("Error registering student: " + e.getMessage());
            return false;
        }
    }

    // Get student information
    public static String[] getStudentInfo(String registerNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(registerNumber)) {
                    return parts; // [regNo, name, dept, section, password]
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting student info: " + e.getMessage());
        }
        return null;
    }

    // Submit project
    public static boolean submitProject(String regNo, String name, String dept, String section,
                                       String projectName, String description, String date) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROJECTS_FILE, true))) {
            int projectId = getNextProjectId();
            // Format: ID|RegNo|Name|Dept|Section|ProjectName|Description|Date|Status|Comments
            writer.println(projectId + "|" + regNo + "|" + name + "|" + dept + "|" + section + "|" +
                          projectName + "|" + description + "|" + date + "|Pending|");
            return true;
        } catch (IOException e) {
            System.err.println("Error submitting project: " + e.getMessage());
            return false;
        }
    }

    // Get next project ID
    private static int getNextProjectId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) {
                    maxId = id;
                }
            }
        } catch (IOException e) {
            // File might be empty
        }
        return maxId + 1;
    }

    // Get student's projects
    public static List<String[]> getStudentProjects(String registerNumber) {
        List<String[]> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1); // -1 to keep empty strings
                if (parts[1].equals(registerNumber)) {
                    projects.add(parts);
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting projects: " + e.getMessage());
        }
        Collections.reverse(projects); // Show newest first
        return projects;
    }

    // Get all projects (for admin)
    public static List<String[]> getAllProjects() {
        List<String[]> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                projects.add(parts);
            }
        } catch (IOException e) {
            System.err.println("Error getting all projects: " + e.getMessage());
        }
        Collections.reverse(projects); // Show newest first
        return projects;
    }

    // Update project status
    public static boolean updateProjectStatus(int projectId, String status, String comments) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (Integer.parseInt(parts[0]) == projectId) {
                    // Update status and comments
                    parts[8] = status;
                    parts[9] = comments;
                    line = String.join("|", parts);
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading projects: " + e.getMessage());
            return false;
        }

        // Write back all lines
        if (updated) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(PROJECTS_FILE))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error updating project: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // Get statistics for admin
    public static Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("approved", 0);
        stats.put("rejected", 0);

        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                stats.put("total", stats.get("total") + 1);
                String status = parts[8];
                if (status.equals("Pending")) {
                    stats.put("pending", stats.get("pending") + 1);
                } else if (status.equals("Approved")) {
                    stats.put("approved", stats.get("approved") + 1);
                } else if (status.equals("Rejected")) {
                    stats.put("rejected", stats.get("rejected") + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting statistics: " + e.getMessage());
        }
        return stats;
    }
}*/