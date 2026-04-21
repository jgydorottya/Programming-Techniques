package business;

import data.SerializationManagement;
import model.ComplexTask;
import model.Employee;
import model.SimpleTask;
import model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksManagement implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Employee, List<Task>> taskManager;
    private final List<Task> availableTasks;

    private transient SerializationManagement serializationManagement;

    public TasksManagement() {
        this.taskManager = new HashMap<>();
        this.availableTasks = new ArrayList<>();
    }

    public void setSerializationManagement(SerializationManagement serializationManagement) {
        this.serializationManagement = serializationManagement;
    }

    private void save() {
        if (serializationManagement != null) {
            serializationManagement.saveData(this);
        }
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

    public void addEmployee(int idEmployee, String name) {
        addEmployee(new Employee(idEmployee, name));
        save();
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

    public void addSimpleTask(int idTask, String statusTask, int startHour, int endHour) {
        addAvailableTask(new SimpleTask(idTask, statusTask, startHour, endHour));
        save();
    }

    public void addComplexTask(int idTask, String statusTask, List<Task> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            throw new IllegalArgumentException("Select at least one subtask for the complex task.");
        }

        ComplexTask complexTask = new ComplexTask(idTask, statusTask);

        for (Task task : subtasks) {
            complexTask.addTask(task);
        }

        for (Task task : subtasks) {
            availableTasks.remove(task);
        }

        addAvailableTask(complexTask);
        save();
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        Employee employee = findEmployeeById(idEmployee);
        taskManager.get(employee).add(task);
        availableTasks.remove(task);
    }

    public void assignTaskToEmployeeAndSave(int idEmployee, Task task) {
        assignTaskToEmployee(idEmployee, task);
        save();
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

    public void modifyTaskStatusAndSave(int idEmployee, int idTask) {
        modifyTaskStatus(idEmployee, idTask);
        save();
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = findEmployeeById(idEmployee);

        for (Task task : taskManager.get(employee)) {
            if (task.getIdTask() == idTask) {
                String newStatus;

                if (task.getStatusTask().equals("Completed")) {
                    newStatus = "Uncompleted";
                } else {
                    newStatus = "Completed";
                }

                setTaskStatusRecursively(task, newStatus);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Only top-level assigned tasks can be toggled. Task with ID = " + idTask + " was not found among them."
        );
    }

    private void setTaskStatusRecursively(Task task, String status) {
        task.setStatusTask(status);

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                setTaskStatusRecursively(subtask, status);
            }
        }
    }

    /*private boolean modifyTaskStatusRecursive(Task task, int idTask) {
        if (task.getIdTask() == idTask) {
            String newStatus;

            if (task.getStatusTask().equals("Completed")) {
                newStatus = "Uncompleted";
            } else {
                newStatus = "Completed";
            }

            setTaskStatusRecursively(task, newStatus);
            return true;
        }

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                if (modifyTaskStatusRecursive(subtask, idTask)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void setTaskStatusRecursively(Task task, String status) {
        task.setStatusTask(status);

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                setTaskStatusRecursively(subtask, status);
            }
        }
    }*/

    public List<Task> getEmployeeTasks(int idEmployee) {
        Employee employee = findEmployeeById(idEmployee);
        return Collections.unmodifiableList(taskManager.get(employee));
    }

    public List<Task> getAvailableTasks() {
        return Collections.unmodifiableList(availableTasks);
    }

    public Map<Employee, List<Task>> getTaskManager() {
        return Collections.unmodifiableMap(taskManager);
    }

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
            if (containsTaskId(task, idTask)) {
                return true;
            }
        }

        for (List<Task> tasks : taskManager.values()) {
            for (Task task : tasks) {
                if (containsTaskId(task, idTask)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsTaskId(Task task, int idTask) {
        if (task.getIdTask() == idTask) {
            return true;
        }

        if (task instanceof ComplexTask complexTask) {
            for (Task subtask : complexTask.getTasks()) {
                if (containsTaskId(subtask, idTask)) {
                    return true;
                }
            }
        }

        return false;
    }
}
