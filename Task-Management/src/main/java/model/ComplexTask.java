package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ComplexTask extends Task {
    private static final long serialVersionUID = 1L;

    private final List<Task> tasks;

    public ComplexTask(int idTask, String statusTask) {
        super(idTask, statusTask);
        this.tasks = new ArrayList<>();
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(task);
    }

    public void deleteTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.remove(task);
    }

    @Override
    public int estimateDuration() {
        int totalDuration = 0;
        for (Task task : tasks) {
            totalDuration += task.estimateDuration();
        }
        return totalDuration;
    }
}
