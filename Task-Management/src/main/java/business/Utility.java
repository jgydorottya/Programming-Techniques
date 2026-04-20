package business;

import model.Employee;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {
    public List<String> findEmployeesWithOver40Hours(TasksManagement taskManager) {
        if (taskManager == null) {
            throw new IllegalArgumentException("Task manager cannot be null.");
        }

        Map<String, Integer> durationByEmployee = new HashMap<>();
        for (Employee employee : taskManager.getTaskManager().keySet()) {
            int duration = taskManager.calculateEmployeeWorkDuration(employee.getIdEmployee());
            if (duration > 40) {
                durationByEmployee.put(employee.getName(), duration);
            }
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(durationByEmployee.entrySet());
        entries.sort(Map.Entry.comparingByValue());

        List<String> employeeNames = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries) {
            employeeNames.add(entry.getKey());
        }
        return employeeNames;
    }

    public Map<String, Map<String, Integer>> computeTaskStatusStatistics(TasksManagement taskManager) {
        if (taskManager == null) {
            throw new IllegalArgumentException("Task manager cannot be null.");
        }

        Map<String, Map<String, Integer>> statistics = new HashMap<>();
        for (Map.Entry<Employee, List<Task>> entry : taskManager.getTaskManager().entrySet()) {
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            int completedCount = 0;
            int uncompletedCount = 0;

            for (Task task : tasks) {
                if (task.getStatusTask().equals("Completed")) {
                    completedCount++;
                } else {
                    uncompletedCount++;
                }
            }

            Map<String, Integer> employeeStatistics = new HashMap<>();
            employeeStatistics.put("Completed", completedCount);
            employeeStatistics.put("Uncompleted", uncompletedCount);

            statistics.put(employee.getName(), employeeStatistics);
        }
        return statistics;
    }
}
