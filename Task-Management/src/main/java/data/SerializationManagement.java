package data;

import business.TasksManagement;

import java.io.*;

public class SerializationManagement {
    private final String filePath;

    public SerializationManagement(String filePath) {
        this.filePath = filePath;
    }

    public void saveData(TasksManagement taskManager) {
        if (taskManager == null) {
            throw new IllegalArgumentException("Task manager cannot be null.");
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            objectOutputStream.writeObject(taskManager);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving data.", e);
        }
    }

    public TasksManagement loadData() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            return (TasksManagement) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new TasksManagement();
        }
    }
}