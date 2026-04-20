package presentation;

import business.TasksManagement;
import business.Utility;
import data.SerializationManagement;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final TasksManagement taskManager;
    private final Utility utility;
    private final SerializationManagement serializationManager;

    private JComboBox<model.Employee> employeeCombo;
    private JComboBox<model.Task> taskCombo;

    public MainFrame(TasksManagement taskManager, Utility utility, SerializationManagement serializationManager) {
        this.taskManager = taskManager;
        this.utility = utility;
        this.serializationManager = serializationManager;

        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Task Management Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel assignmentPanel = createAssignmentPanel();
        JPanel employeesPanel = createEmployeesPanel();
        JPanel tasksPanel = createTasksPanel();
        JPanel statisticsPanel = createStatisticsPanel();

        tabbedPane.addTab("Employees", employeesPanel);
        tabbedPane.addTab("Tasks", tasksPanel);
        tabbedPane.addTab("Assignment", assignmentPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: input form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel idLabel = new JLabel("Employee ID:");
        JTextField idField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JButton addButton = new JButton("Add Employee");

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(new JLabel()); // empty cell
        formPanel.add(addButton);

        // Center: display area
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button logic
        addButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty.");
                }

                // create and add employee
                taskManager.addEmployee(new model.Employee(id, name));

                // save data
                serializationManager.saveData(taskManager);

                // refresh display
                refreshEmployeesDisplay(displayArea);
                refreshEmployeeCombo(employeeCombo);

                // clear fields
                idField.setText("");
                nameField.setText("");

                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // initial load
        refreshEmployeesDisplay(displayArea);
        refreshEmployeeCombo(employeeCombo);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshEmployeesDisplay(JTextArea displayArea) {
        StringBuilder sb = new StringBuilder();
        for (model.Employee employee : taskManager.getTaskManager().keySet()) {
            sb.append(employee.toString()).append("\n");
        }
        displayArea.setText(sb.toString());
    }

    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Input form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField statusField = new JTextField(); // Completed / Uncompleted
        JTextField startField = new JTextField();
        JTextField endField = new JTextField();

        JButton addSimpleButton = new JButton("Add Simple Task");
        JButton addComplexButton = new JButton("Add Complex Task");

        formPanel.add(new JLabel("Task ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Status (Completed/Uncompleted):"));
        formPanel.add(statusField);

        formPanel.add(new JLabel("Start Hour (Simple):"));
        formPanel.add(startField);

        formPanel.add(new JLabel("End Hour (Simple):"));
        formPanel.add(endField);

        formPanel.add(addSimpleButton);
        formPanel.add(addComplexButton);

        // Display area
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Simple task logic
        addSimpleButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String status = statusField.getText().trim();
                int start = Integer.parseInt(startField.getText().trim());
                int end = Integer.parseInt(endField.getText().trim());

                model.SimpleTask task = new model.SimpleTask(id, status, start, end);
                taskManager.addAvailableTask(task);
                serializationManager.saveData(taskManager);
                refreshTasksDisplay(displayArea);
                refreshTaskCombo(taskCombo);
                clearTaskFields(idField, statusField, startField, endField);
                JOptionPane.showMessageDialog(this, "Simple task added!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Complex task logic
        addComplexButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String status = statusField.getText().trim();
                model.ComplexTask task = new model.ComplexTask(id, status);
                taskManager.addAvailableTask(task);
                serializationManager.saveData(taskManager);
                refreshTasksDisplay(displayArea);
                refreshTaskCombo(taskCombo);
                clearTaskFields(idField, statusField, startField, endField);
                JOptionPane.showMessageDialog(this, "Complex task added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // initial display
        refreshTasksDisplay(displayArea);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshTasksDisplay(JTextArea displayArea) {
        StringBuilder sb = new StringBuilder();
        for (model.Task task : taskManager.getAvailableTasks()) {
            sb.append("ID: ").append(task.getIdTask())
                    .append(" | Status: ").append(task.getStatusTask())
                    .append(" | Duration: ").append(task.estimateDuration())
                    .append("\n");
        }

        displayArea.setText(sb.toString());
    }

    private void clearTaskFields(JTextField id, JTextField status, JTextField start, JTextField end) {
        id.setText("");
        status.setText("");
        start.setText("");
        end.setText("");
    }

    private JPanel createAssignmentPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Top: controls
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        employeeCombo = new JComboBox<>();
        taskCombo = new JComboBox<>();

        JButton assignButton = new JButton("Assign Task");
        JButton modifyStatusButton = new JButton("Toggle Task Status");

        controlPanel.add(new JLabel("Select Employee:"));
        controlPanel.add(employeeCombo);

        controlPanel.add(new JLabel("Select Task:"));
        controlPanel.add(taskCombo);

        controlPanel.add(assignButton);
        controlPanel.add(modifyStatusButton);

        // Center: display
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Load initial data
        refreshEmployeeCombo(employeeCombo);
        refreshTaskCombo(taskCombo);

        // Assign task
        assignButton.addActionListener(e -> {
            try {
                model.Employee employee = (model.Employee) employeeCombo.getSelectedItem();
                model.Task task = (model.Task) taskCombo.getSelectedItem();

                if (employee == null || task == null) {
                    throw new IllegalArgumentException("Select both employee and task.");
                }

                taskManager.assignTaskToEmployee(employee.getIdEmployee(), task);
                serializationManager.saveData(taskManager);
                refreshTaskCombo(taskCombo);
                refreshEmployeeTasksDisplay(displayArea, employee);
                JOptionPane.showMessageDialog(this, "Task assigned successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Modify status
        modifyStatusButton.addActionListener(e -> {
            try {
                model.Employee employee = (model.Employee) employeeCombo.getSelectedItem();

                if (employee == null) {
                    throw new IllegalArgumentException("Select an employee.");
                }

                String input = JOptionPane.showInputDialog(this, "Enter Task ID:");

                if (input == null || input.isBlank()) return;

                int taskId = Integer.parseInt(input);
                taskManager.modifyTaskStatus(employee.getIdEmployee(), taskId);
                serializationManager.saveData(taskManager);
                refreshEmployeeTasksDisplay(displayArea, employee);
                JOptionPane.showMessageDialog(this, "Task status updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // When employee changes
        employeeCombo.addActionListener(e -> {
            model.Employee employee = (model.Employee) employeeCombo.getSelectedItem();
            if (employee != null) {
                refreshEmployeeTasksDisplay(displayArea, employee);
            }
        });

        refreshTaskCombo(taskCombo);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Populate employees
    private void refreshEmployeeCombo(JComboBox<model.Employee> combo) {
        if (combo == null) return;

        combo.removeAllItems();
        for (model.Employee e : taskManager.getTaskManager().keySet()) {
            combo.addItem(e);
        }
    }

    // Populate tasks
    private void refreshTaskCombo(JComboBox<model.Task> combo) {
        if (combo == null) return;

        combo.removeAllItems();
        for (model.Task t : taskManager.getAvailableTasks()) {
            combo.addItem(t);
        }
    }

    // Display employee tasks
    private void refreshEmployeeTasksDisplay(JTextArea area, model.Employee employee) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks for ").append(employee.getName()).append(":\n\n");
        for (model.Task task : taskManager.getEmployeeTasks(employee.getIdEmployee())) {
            sb.append("ID: ").append(task.getIdTask())
                    .append(" | Status: ").append(task.getStatusTask())
                    .append(" | Duration: ").append(task.estimateDuration())
                    .append("\n");
        }
        area.setText(sb.toString());
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton over40Button = new JButton("Employees > 40h");
        JButton statusStatsButton = new JButton("Task Status Statistics");

        buttonPanel.add(over40Button);
        buttonPanel.add(statusStatsButton);

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        over40Button.addActionListener(e -> {
            try {
                java.util.List<String> employees = utility.findEmployeesWithOver40Hours(taskManager);
                StringBuilder sb = new StringBuilder();
                sb.append("Employees with workload greater than 40 hours:\n\n");

                if (employees.isEmpty()) {
                    sb.append("No employee found.");
                } else {
                    for (String employeeName : employees) {
                        sb.append(employeeName).append("\n");
                    }
                }
                displayArea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        statusStatsButton.addActionListener(e -> {
            try {
                java.util.Map<String, java.util.Map<String, Integer>> statistics =
                        utility.computeTaskStatusStatistics(taskManager);

                StringBuilder sb = new StringBuilder();
                sb.append("Completed / Uncompleted tasks for each employee:\n\n");

                if (statistics.isEmpty()) {
                    sb.append("No statistics available.");
                } else {
                    for (java.util.Map.Entry<String, java.util.Map<String, Integer>> entry : statistics.entrySet()) {
                        String employeeName = entry.getKey();
                        java.util.Map<String, Integer> employeeStats = entry.getValue();

                        sb.append(employeeName)
                                .append(" -> Completed: ")
                                .append(employeeStats.getOrDefault("Completed", 0))
                                .append(", Uncompleted: ")
                                .append(employeeStats.getOrDefault("Uncompleted", 0))
                                .append("\n");
                    }
                }
                displayArea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
