package presentation;

import business.TasksManagement;
import business.Utility;
import model.ComplexTask;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final TasksManagement taskManager;
    private final Utility utility;

    private JComboBox<model.Employee> employeeCombo;
    private JComboBox<model.Task> taskCombo;

    public MainFrame(TasksManagement taskManager, Utility utility) {
        this.taskManager = taskManager;
        this.utility = utility;

        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Task Management Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel employeesPanel = createEmployeesPanel();
        JPanel tasksPanel = createTasksPanel();
        JPanel assignmentPanel = createAssignmentPanel();
        JPanel statisticsPanel = createStatisticsPanel();

        tabbedPane.addTab("Employees", employeesPanel);
        tabbedPane.addTab("Tasks", tasksPanel);
        tabbedPane.addTab("Assignment", assignmentPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

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
        formPanel.add(new JLabel());
        formPanel.add(addButton);

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        addButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty.");
                }

                taskManager.addEmployee(id, name);

                refreshEmployeesDisplay(displayArea);
                refreshEmployeeCombo(employeeCombo);

                idField.setText("");
                nameField.setText("");

                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshEmployeesDisplay(displayArea);
        refreshEmployeeCombo(employeeCombo);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshEmployeesDisplay(JTextArea displayArea) {
        StringBuilder sb = new StringBuilder();
        for (model.Employee employee : taskManager.getTaskManager().keySet()) {
            sb.append(employee).append("\n");
        }
        displayArea.setText(sb.toString());
    }

    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField statusField = new JTextField();
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

        DefaultListModel<Task> subtasksModel = new DefaultListModel<>();
        JList<Task> subtasksList = new JList<>(subtasksModel);
        subtasksList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel subtaskPanel = new JPanel(new BorderLayout());
        subtaskPanel.add(new JLabel("Select subtasks for Complex Task:"), BorderLayout.NORTH);
        subtaskPanel.add(new JScrollPane(subtasksList), BorderLayout.CENTER);

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, subtaskPanel);
        splitPane.setResizeWeight(0.7);

        addSimpleButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String status = statusField.getText().trim();
                int start = Integer.parseInt(startField.getText().trim());
                int end = Integer.parseInt(endField.getText().trim());

                taskManager.addSimpleTask(id, status, start, end);

                refreshTasksDisplay(displayArea);
                refreshTaskCombo(taskCombo);
                refreshSubtasksList(subtasksModel);

                clearTaskFields(idField, statusField, startField, endField);

                JOptionPane.showMessageDialog(this, "Simple task added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addComplexButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String status = statusField.getText().trim();

                List<Task> selectedSubtasks = subtasksList.getSelectedValuesList();
                taskManager.addComplexTask(id, status, selectedSubtasks);

                refreshTasksDisplay(displayArea);
                refreshTaskCombo(taskCombo);
                refreshSubtasksList(subtasksModel);

                clearTaskFields(idField, statusField, startField, endField);
                subtasksList.clearSelection();

                JOptionPane.showMessageDialog(this, "Complex task added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshTasksDisplay(displayArea);
        refreshSubtasksList(subtasksModel);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshSubtasksList(DefaultListModel<Task> model) {
        model.clear();
        for (Task task : taskManager.getAvailableTasks()) {
            model.addElement(task);
        }
    }

    private void refreshTasksDisplay(JTextArea displayArea) {
        StringBuilder sb = new StringBuilder();

        for (Task task : taskManager.getAvailableTasks()) {
            appendTaskDetails(sb, task, 0);
            sb.append("\n");
        }

        displayArea.setText(sb.toString());
    }

    private void appendTaskDetails(StringBuilder sb, Task task, int level) {
        String indent = "  ".repeat(level);

        sb.append(indent)
                .append("ID: ").append(task.getIdTask())
                .append(" | Type: ").append(task instanceof ComplexTask ? "Complex" : "Simple")
                .append(" | Status: ").append(task.getStatusTask())
                .append(" | Duration: ").append(task.estimateDuration())
                .append("\n");

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                appendTaskDetails(sb, subtask, level + 1);
            }
        }
    }

    private void clearTaskFields(JTextField id, JTextField status, JTextField start, JTextField end) {
        id.setText("");
        status.setText("");
        start.setText("");
        end.setText("");
    }

    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

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

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        refreshEmployeeCombo(employeeCombo);
        refreshTaskCombo(taskCombo);

        assignButton.addActionListener(e -> {
            try {
                model.Employee employee = (model.Employee) employeeCombo.getSelectedItem();
                model.Task task = (model.Task) taskCombo.getSelectedItem();

                if (employee == null || task == null) {
                    throw new IllegalArgumentException("Select both employee and task.");
                }

                taskManager.assignTaskToEmployeeAndSave(employee.getIdEmployee(), task);
                refreshTaskCombo(taskCombo);
                refreshEmployeeTasksDisplay(displayArea, employee);

                JOptionPane.showMessageDialog(this, "Task assigned successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modifyStatusButton.addActionListener(e -> {
            try {
                model.Employee employee = (model.Employee) employeeCombo.getSelectedItem();

                if (employee == null) {
                    throw new IllegalArgumentException("Select an employee.");
                }

                refreshEmployeeTasksDisplay(displayArea, employee);

                String input = JOptionPane.showInputDialog(
                        this,
                        "The selected employee's tasks are now displayed.\nEnter the Task ID to toggle its status:"
                );

                if (input == null || input.isBlank()) {
                    return;
                }

                int taskId = Integer.parseInt(input);
                taskManager.modifyTaskStatusAndSave(employee.getIdEmployee(), taskId);
                refreshEmployeeTasksDisplay(displayArea, employee);

                JOptionPane.showMessageDialog(this, "Task status updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

    private void refreshEmployeeCombo(JComboBox<model.Employee> combo) {
        if (combo == null) {
            return;
        }

        combo.removeAllItems();
        for (model.Employee employee : taskManager.getTaskManager().keySet()) {
            combo.addItem(employee);
        }
    }

    private void refreshTaskCombo(JComboBox<model.Task> combo) {
        if (combo == null) {
            return;
        }

        combo.removeAllItems();
        for (model.Task task : taskManager.getAvailableTasks()) {
            combo.addItem(task);
        }
    }

    private void refreshEmployeeTasksDisplay(JTextArea area, model.Employee employee) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks for ").append(employee.getName()).append(":\n\n");
        sb.append("Only top-level assigned task IDs can be toggled.\n");
        sb.append("Subtasks shown below are part of a complex task and cannot be toggled separately.\n\n");

        for (model.Task task : taskManager.getEmployeeTasks(employee.getIdEmployee())) {
            appendEmployeeTaskDetails(sb, task, 0, true);
            sb.append("\n");
        }

        area.setText(sb.toString());
    }

    private void appendEmployeeTaskDetails(StringBuilder sb, Task task, int level, boolean topLevel) {
        String indent = "  ".repeat(level);

        sb.append(indent)
                .append("ID: ").append(task.getIdTask())
                .append(" | Type: ").append(task instanceof ComplexTask ? "Complex" : "Simple")
                .append(" | Status: ").append(task.getStatusTask())
                .append(" | Duration: ").append(task.estimateDuration());

        if (topLevel) {
            sb.append(" | Toggleable: YES");
        } else {
            sb.append(" | Toggleable: NO");
        }

        sb.append("\n");

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                appendEmployeeTaskDetails(sb, subtask, level + 1, false);
            }
        }
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
                List<String> employees = utility.findEmployeesWithOver40Hours(taskManager);
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
                Map<String, Map<String, Integer>> statistics =
                        utility.computeTaskStatusStatistics(taskManager);

                StringBuilder sb = new StringBuilder();
                sb.append("Completed / Uncompleted tasks for each employee:\n\n");

                if (statistics.isEmpty()) {
                    sb.append("No statistics available.");
                } else {
                    for (Map.Entry<String, Map<String, Integer>> entry : statistics.entrySet()) {
                        String employeeName = entry.getKey();
                        Map<String, Integer> employeeStats = entry.getValue();

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
