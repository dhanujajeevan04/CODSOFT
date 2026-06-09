import java.util.ArrayList;
import java.util.Scanner;

public class StudentGradeCalculator {

    static Scanner input = new Scanner(System.in);

    static final int[][] GRADE_THRESHOLDS = {
        {90, 100}, // A+
        {80, 89},  // A
        {70, 79},  // B
        {60, 69},  // C
        {50, 59},  // D
        {0,  49},  // F
    };
    static final String[] GRADE_LABELS = {"A+", "A", "B", "C", "D", "F"};
    static final String[] GRADE_REMARKS = {
        "Outstanding!", "Excellent!", "Good", "Satisfactory", "Needs Improvement", "Fail"
    };

    public static void main(String[] args) {
        printHeader();

        System.out.print("Enter student name: ");
        String studentName = input.nextLine().trim();

        System.out.print("How many subjects? ");
        int subjectCount = readPositiveInt();

        ArrayList<String> subjectNames = new ArrayList<>();
        ArrayList<Integer> marks = new ArrayList<>();

        for (int i = 1; i <= subjectCount; i++) {
            System.out.print("Enter name of subject " + i + ": ");
            subjectNames.add(input.nextLine().trim());

            System.out.print("Enter marks for " + subjectNames.get(i - 1) + " (0–100): ");
            marks.add(readMarkInRange());
        }

        displayReport(studentName, subjectNames, marks);
        input.close();
    }

    static void displayReport(String name, ArrayList<String> subjects, ArrayList<Integer> marks) {
        int total = 0;
        for (int m : marks) total += m;

        double average = (double) total / marks.size();
        String grade = getGrade(average);
        String remark = getRemark(average);

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("         📋 STUDENT RESULT CARD");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("  Student Name : %s%n", name);
        System.out.printf("  Total Subjects: %d%n", subjects.size());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("  %-20s  %s%n", "SUBJECT", "MARKS");
        System.out.println("  ─────────────────────────────────────");

        for (int i = 0; i < subjects.size(); i++) {
            String bar = buildBar(marks.get(i));
            System.out.printf("  %-20s  %3d  %s%n", subjects.get(i), marks.get(i), bar);
        }

        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("  Total Marks   : %d / %d%n", total, subjects.size() * 100);
        System.out.printf("  Average       : %.2f%%%n", average);
        System.out.printf("  Grade         : %s%n", grade);
        System.out.printf("  Remark        : %s%n", remark);
        System.out.println("╚══════════════════════════════════════╝");
    }

    static String buildBar(int marks) {
        // Visual bar out of 10 blocks
        int filled = marks / 10;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "█" : "░");
        bar.append("]");
        return bar.toString();
    }

    static String getGrade(double avg) {
        for (int i = 0; i < GRADE_THRESHOLDS.length; i++) {
            if (avg >= GRADE_THRESHOLDS[i][0] && avg <= GRADE_THRESHOLDS[i][1]) {
                return GRADE_LABELS[i];
            }
        }
        return "F";
    }

    static String getRemark(double avg) {
        for (int i = 0; i < GRADE_THRESHOLDS.length; i++) {
            if (avg >= GRADE_THRESHOLDS[i][0] && avg <= GRADE_THRESHOLDS[i][1]) {
                return GRADE_REMARKS[i];
            }
        }
        return "Fail";
    }

    static int readPositiveInt() {
        while (true) {
            try {
                int val = Integer.parseInt(input.nextLine().trim());
                if (val > 0) return val;
                System.out.print("  ⚠ Must be greater than 0. Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid input. Enter a whole number: ");
            }
        }
    }

    static int readMarkInRange() {
        while (true) {
            try {
                int val = Integer.parseInt(input.nextLine().trim());
                if (val >= 0 && val <= 100) return val;
                System.out.print("  ⚠ Marks must be between 0 and 100. Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid input. Enter a number: ");
            }
        }
    }

    static void printHeader() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    📚 STUDENT GRADE CALCULATOR 📚   ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
