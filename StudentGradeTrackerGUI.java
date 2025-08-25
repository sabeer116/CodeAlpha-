import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentGradeTrackerGUI extends JFrame {

    private List<Student> students = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField, gradeField;

    public StudentGradeTrackerGUI() {
        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Update"));

        nameField = new JTextField();
        gradeField = new JTextField();

        JButton addStudentBtn = new JButton("Add Student");
        JButton addGradeBtn = new JButton("Add Grade");

        addStudentBtn.addActionListener(e -> addStudent());
        addGradeBtn.addActionListener(e -> addGrade());

        inputPanel.add(new JLabel("Student Name:"));
        inputPanel.add(nameField);
        inputPanel.add(addStudentBtn);

        inputPanel.add(new JLabel("Grade:"));
        inputPanel.add(gradeField);
        inputPanel.add(addGradeBtn);

        // Table for students
        String[] columns = {"Name", "Grades", "Average", "Highest", "Lowest"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton removeBtn = new JButton("Remove Student");
        JButton summaryBtn = new JButton("Summary Report");
        JButton clearBtn = new JButton("Clear All");

        removeBtn.addActionListener(e -> removeStudent());
        summaryBtn.addActionListener(e -> showSummary());
        clearBtn.addActionListener(e -> clearAll());

        buttonPanel.add(removeBtn);
        buttonPanel.add(summaryBtn);
        buttonPanel.add(clearBtn);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Add new student
    private void addStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showMessage("Enter a student name.");
            return;
        }
        if (findStudent(name) != null) {
            showMessage("Student already exists.");
            return;
        }
        students.add(new Student(name));
        refreshTable();
        showMessage("Student added successfully.");
    }

    // Add grade to existing student
    private void addGrade() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();

        if (name.isEmpty() || gradeText.isEmpty()) {
            showMessage("Enter both student name and grade.");
            return;
        }
        Student s = findStudent(name);
        if (s == null) {
            showMessage("Student not found.");
            return;
        }
        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                showMessage("Grade must be between 0 and 100.");
                return;
            }
            s.grades.add(grade);
            refreshTable();
            showMessage("Grade added successfully.");
        } catch (NumberFormatException ex) {
            showMessage("Enter a valid number for grade.");
        }
    }

    // Remove student
    private void removeStudent() {
        String name = nameField.getText().trim();
        Student s = findStudent(name);
        if (s == null) {
            showMessage("Student not found.");
            return;
        }
        students.remove(s);
        refreshTable();
        showMessage("Student removed successfully.");
    }

    // Show summary report
    private void showSummary() {
        if (students.isEmpty()) {
            showMessage("No students available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        double globalHigh = Double.NEGATIVE_INFINITY;
        double globalLow = Double.POSITIVE_INFINITY;
        double totalSum = 0;
        int totalCount = 0;

        for (Student s : students) {
            sb.append(s.name).append(" ").append(s.grades).append("\n");
            if (!s.grades.isEmpty()) {
                double avg = average(s.grades);
                double hi = Collections.max(s.grades);
                double lo = Collections.min(s.grades);
                sb.append(String.format("  Avg: %.2f  High: %.2f  Low: %.2f%n", avg, hi, lo));

                globalHigh = Math.max(globalHigh, hi);
                globalLow = Math.min(globalLow, lo);
                for (double g : s.grades) {
                    totalSum += g;
                    totalCount++;
                }
            } else {
                sb.append("  (No grades yet)\n");
            }
        }
        if (totalCount > 0) {
            sb.append("\nOverall Average: ").append(String.format("%.2f", totalSum / totalCount));
            sb.append("\nOverall Highest: ").append(String.format("%.2f", globalHigh));
            sb.append("\nOverall Lowest : ").append(String.format("%.2f", globalLow));
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // Clear all data
    private void clearAll() {
        students.clear();
        refreshTable();
        showMessage("All data cleared.");
    }

    // Refresh table display
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : students) {
            String gradesStr = s.grades.isEmpty() ? "" : s.grades.toString();
            String avg = s.grades.isEmpty() ? "-" : String.format("%.2f", average(s.grades));
            String high = s.grades.isEmpty() ? "-" : String.format("%.2f", Collections.max(s.grades));
            String low = s.grades.isEmpty() ? "-" : String.format("%.2f", Collections.min(s.grades));
            tableModel.addRow(new Object[]{s.name, gradesStr, avg, high, low});
        }
    }

    // Find student by name
    private Student findStudent(String name) {
        for (Student s : students) {
            if (s.name.equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    // Calculate average
    private double average(List<Double> nums) {
        double sum = 0;
        for (double n : nums) sum += n;
        return nums.isEmpty() ? 0 : sum / nums.size();
    }

    // Show popup message
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // Student class
    static class Student {
        String name;
        List<Double> grades = new ArrayList<>();
        Student(String name) { this.name = name; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeTrackerGUI().setVisible(true));
    }
}
