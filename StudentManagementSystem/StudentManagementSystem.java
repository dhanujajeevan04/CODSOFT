import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentManagementSystem {

    public static void main(String[] args) {
        StudentRegistry registry = new StudentRegistry("students.csv");
        registry.loadFromFile();
        ConsoleUI ui = new ConsoleUI(registry);
        ui.launch();
    }
}

class Student {
    private static int idCounter = 1000;

    private int rollNumber;
    private String name;
    private int age;
    private String grade;
    private double gpa;

    public Student(String name, int age, String grade, double gpa) {
        this.rollNumber = ++idCounter;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.gpa = gpa;
    }

    // Constructor for loading from file (roll number already assigned)
    public Student(int rollNumber, String name, int age, String grade, double gpa) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.gpa = gpa;
        if (rollNumber >= idCounter) idCounter = rollNumber;
    }

    public int getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGrade() { return grade; }
    public double getGpa() { return gpa; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String toCSV() {
        return rollNumber + "," + name + "," + age + "," + grade + "," + gpa;
    }

    @Override
    public String toString() {
        return String.format("  | %-6d | %-20s | %-4d | %-7s | %.2f |",
            rollNumber, name, age, grade, gpa);
    }
}

class StudentRegistry {
    private ArrayList<Student> students;
    private String filePath;

    public StudentRegistry(String filePath) {
        this.filePath = filePath;
        this.students = new ArrayList<>();
    }

    public boolean addStudent(Student s) {
        if (s.getName().isBlank()) return false;
        students.add(s);
        saveToFile();
        return true;
    }

    public boolean removeStudent(int rollNumber) {
        for (Student s : students) {
            if (s.getRollNumber() == rollNumber) {
                students.remove(s);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public Student findByRoll(int rollNumber) {
        for (Student s : students) {
            if (s.getRollNumber() == rollNumber) return s;
        }
        return null;
    }

    public ArrayList<Student> searchByName(String keyword) {
        ArrayList<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(s);
            }
        }
        return results;
    }

    public ArrayList<Student> getAllStudents() { return students; }

    public int getTotalCount() { return students.size(); }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Student s : students) {
                pw.println(s.toCSV());
            }
        } catch (IOException e) {
            System.out.println("  ⚠ Warning: Could not save to file. " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int roll = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    String grade = parts[3].trim();
                    double gpa = Double.parseDouble(parts[4].trim());
                    students.add(new Student(roll, name, age, grade, gpa));
                }
            }
            System.out.println("  ✅ Loaded " + students.size() + " student(s) from file.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("  ⚠ Could not load existing data: " + e.getMessage());
        }
    }
}


class ConsoleUI {
    private StudentRegistry registry;
    private Scanner input;

    public ConsoleUI(StudentRegistry registry) {
        this.registry = registry;
        this.input = new Scanner(System.in);
    }

    public void launch() {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            switch (input.nextLine().trim()) {
                case "1": addStudent(); break;
                case "2": removeStudent(); break;
                case "3": searchStudent(); break;
                case "4": displayAllStudents(); break;
                case "5": editStudent(); break;
                case "6": running = false; break;
                default: System.out.println("  ⚠ Invalid choice. Select 1–6.");
            }
        }

        System.out.println("\n  📂 Data saved. Goodbye!");
        input.close();
    }

    void addStudent() {
        System.out.println("\n  ── Add New Student ──");

        System.out.print("  Full Name: ");
        String name = input.nextLine().trim();
        if (name.isBlank()) { System.out.println("  ❌ Name cannot be empty."); return; }

        System.out.print("  Age: ");
        int age = readIntInRange(10, 35);

        System.out.print("  Grade/Class (e.g., 10th, B.Tech-2): ");
        String grade = input.nextLine().trim();
        if (grade.isBlank()) { System.out.println("  ❌ Grade cannot be empty."); return; }

        System.out.print("  GPA (0.0 – 10.0): ");
        double gpa = readGPA();

        Student s = new Student(name, age, grade, gpa);
        registry.addStudent(s);
        System.out.println("  ✅ Student added! Roll No: " + s.getRollNumber());
    }

    void removeStudent() {
        System.out.print("\n  Enter Roll Number to remove: ");
        int roll = readInt();
        Student s = registry.findByRoll(roll);
        if (s == null) { System.out.println("  ❌ Student not found."); return; }

        System.out.print("  Remove " + s.getName() + "? (yes/no): ");
        if (input.nextLine().trim().equalsIgnoreCase("yes")) {
            registry.removeStudent(roll);
            System.out.println("  ✅ Student removed.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    void searchStudent() {
        System.out.println("\n  Search by: 1. Roll Number   2. Name");
        System.out.print("  Choose: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("  Enter Roll Number: ");
            int roll = readInt();
            Student s = registry.findByRoll(roll);
            if (s != null) { printTableHeader(); System.out.println(s); printTableFooter(); }
            else System.out.println("  ❌ No student found with roll: " + roll);

        } else if (choice.equals("2")) {
            System.out.print("  Enter name keyword: ");
            String keyword = input.nextLine().trim();
            ArrayList<Student> results = registry.searchByName(keyword);
            if (results.isEmpty()) {
                System.out.println("  ❌ No matches for: " + keyword);
            } else {
                System.out.println("  Found " + results.size() + " result(s):");
                printTableHeader();
                for (Student s : results) System.out.println(s);
                printTableFooter();
            }
        } else {
            System.out.println("  ⚠ Invalid option.");
        }
    }

    void displayAllStudents() {
        ArrayList<Student> all = registry.getAllStudents();
        if (all.isEmpty()) { System.out.println("\n  📭 No students registered yet."); return; }

        System.out.println("\n  Total Students: " + registry.getTotalCount());
        printTableHeader();
        for (Student s : all) System.out.println(s);
        printTableFooter();
    }

    void editStudent() {
        System.out.print("\n  Enter Roll Number to edit: ");
        int roll = readInt();
        Student s = registry.findByRoll(roll);
        if (s == null) { System.out.println("  ❌ Student not found."); return; }

        System.out.println("  Editing: " + s.getName() + " (leave blank to keep current)");

        System.out.print("  New Name [" + s.getName() + "]: ");
        String newName = input.nextLine().trim();
        if (!newName.isBlank()) s.setName(newName);

        System.out.print("  New Age [" + s.getAge() + "]: ");
        String ageInput = input.nextLine().trim();
        if (!ageInput.isBlank()) {
            try { s.setAge(Integer.parseInt(ageInput)); } catch (NumberFormatException ignored) {}
        }

        System.out.print("  New Grade [" + s.getGrade() + "]: ");
        String newGrade = input.nextLine().trim();
        if (!newGrade.isBlank()) s.setGrade(newGrade);

        System.out.print("  New GPA [" + s.getGpa() + "]: ");
        String gpaInput = input.nextLine().trim();
        if (!gpaInput.isBlank()) {
            try { s.setGpa(Double.parseDouble(gpaInput)); } catch (NumberFormatException ignored) {}
        }

        registry.saveToFile();
        System.out.println("  ✅ Student record updated.");
    }


    void printMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│       STUDENT MANAGEMENT MENU     │");
        System.out.println("│  1. Add Student                   │");
        System.out.println("│  2. Remove Student                │");
        System.out.println("│  3. Search Student                │");
        System.out.println("│  4. Display All Students          │");
        System.out.println("│  5. Edit Student                  │");
        System.out.println("│  6. Exit                          │");
        System.out.println("└───────────────────────────────────┘");
        System.out.print("  Select: ");
    }

    void printTableHeader() {
        System.out.println("  +--------+----------------------+------+---------+------+");
        System.out.println("  | ROLL   | NAME                 | AGE  | GRADE   | GPA  |");
        System.out.println("  +--------+----------------------+------+---------+------+");
    }

    void printTableFooter() {
        System.out.println("  +--------+----------------------+------+---------+------+");
    }

    int readInt() {
        while (true) {
            try { return Integer.parseInt(input.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("  ⚠ Enter a whole number: "); }
        }
    }

    int readIntInRange(int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(input.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.print("  ⚠ Enter a value between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid. Enter a number: ");
            }
        }
    }

    double readGPA() {
        while (true) {
            try {
                double val = Double.parseDouble(input.nextLine().trim());
                if (val >= 0.0 && val <= 10.0) return val;
                System.out.print("  ⚠ GPA must be between 0.0 and 10.0: ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid. Enter a decimal number: ");
            }
        }
    }

    void printBanner() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   🎓 STUDENT MANAGEMENT SYSTEM 🎓    ║");
        System.out.println("║   Console Edition  |  File-Backed     ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }
}
