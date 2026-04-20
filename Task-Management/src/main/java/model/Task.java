package model;

import java.io.Serializable;

public sealed abstract class Task implements Serializable permits SimpleTask, ComplexTask {
    private static final long serialVersionUID = 1L;

    private int idTask;
    private String statusTask;

    public Task(int idTask, String statusTask) {
        setIdTasK(idTask);
        setStatusTask(statusTask);
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTasK(int idTask) {
        if (idTask <= 0) {
            throw new IllegalArgumentException("Task ID must be greater than 0.");
        }
        this.idTask = idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        if (!statusTask.equals("Completed") && !statusTask.equals("Uncompleted")) {
            throw new IllegalArgumentException("Status must be: 'Completed' or 'Uncompleted'.");
        }
        this.statusTask = statusTask;
    }

    public abstract int estimateDuration();

    @Override
    public String toString() {
        return "Task " + idTask + " (" + statusTask + ")";
    }
}
