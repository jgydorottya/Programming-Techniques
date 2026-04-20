import business.TasksManagement;
import business.Utility;
import data.SerializationManagement;
import presentation.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SerializationManagement serializationManager = new SerializationManagement("tasks_data.ser");

            TasksManagement taskManager = serializationManager.loadData();
            Utility utility = new Utility();

            MainFrame mainFrame = new MainFrame(taskManager, utility, serializationManager);

            mainFrame.setVisible(true);
        });
    }
}