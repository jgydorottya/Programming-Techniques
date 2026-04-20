package business;

import model.Employee;
import model.Task;

import java.io.Serializable;
import java.util.*;

public class TasksManagement implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Employee, List<Task>> taskManager;
    private final List<Task> availableTasks;

    public TasksManagement() {
        this.taskManager = new HashMap<>();
        this.availableTasks = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        for (Employee existingEmployee : taskManager.keySet()) {
            if (existingEmployee.getIdEmployee() == employee.getIdEmployee()) {
                throw new IllegalArgumentException("Employee ID already exists.");
            }
        }
        taskManager.put(employee, new ArrayList<>());
    }

    public void addAvailableTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (taskIdExists(task.getIdTask())) {
            throw new IllegalArgumentException("Task ID already exists.");
        }
        availableTasks.add(task);
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        Employee employee = findEmployeeById(idEmployee);
        taskManager.get(employee).add(task);
        availableTasks.remove(task);
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = findEmployeeById(idEmployee);
        int totalDuration = 0;
        for (Task task : taskManager.get(employee)) {
            if (task.getStatusTask().equals("Completed")) {
                totalDuration += task.estimateDuration();
            }
        }
        return totalDuration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = findEmployeeById(idEmployee);
        for (Task task : taskManager.get(employee)) {
            if (task.getIdTask() == idTask) {
                if (task.getStatusTask().equals("Completed")) {
                    task.setStatusTask("Uncompleted");
                } else {
                    task.setStatusTask("Completed");
                }
                return;
            }
        }
        throw new IllegalArgumentException("Task with ID = " + idTask + " was not found.");
    }


    public List<Task> getEmployeeTasks(int idEmployee) {
        Employee e = findEmployeeById(idEmployee);
        return Collections.unmodifiableList(taskManager.get(e));
    }

    public List<Task> getAvailableTasks() {
        if (availableTasks == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(availableTasks);
    }

    public Map<Employee, List<Task>> getTaskManager() {
        return Collections.unmodifiableMap(taskManager);
    }

    // helper methods
    public Employee findEmployeeById(int idEmployee) {
        for (Employee employee : taskManager.keySet()) {
            if (employee.getIdEmployee() == idEmployee) {
                return employee;
            }
        }
        throw new IllegalArgumentException("Employee with ID = " + idEmployee + " was not found.");
    }

    private boolean taskIdExists(int idTask) {
        for (Task task : availableTasks) {
            if (task.getIdTask() == idTask) {
                return true;
            }
        }
        for (List<Task> tasks : taskManager.values()) {
            for (Task task : tasks) {
                if (task.getIdTask() == idTask) {
                    return true;
                }
            }
        }
        return false;
    }
}
