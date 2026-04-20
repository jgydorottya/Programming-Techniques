package business;

import gui.SimulationFrame;
import model.Server;
import model.Task;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SimulationManager implements Runnable {
    private int timeLimit;
    private int minServiceTime;
    private int maxServiceTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int nrOfServers;
    private int nrOfClients;
    private SelectionPolicy selectionPolicy;

    private Scheduler scheduler;
    private final SimulationFrame frame;
    private List<Task> generatedTasks;

    private int totalWaitingTime;
    private int totalServiceTime;
    private int peakHour;
    private int maxClientsInQueues;

    private Thread simulationThread;
    private volatile boolean running;
    private volatile boolean stopped;

    public SimulationManager() {
        frame = new SimulationFrame();
        generatedTasks = new ArrayList<>();
        attachActions();
        resetStatistics();
    }

    private void attachActions() {
        frame.getValidateButton().addActionListener(e -> validateInput());
        frame.getStartButton().addActionListener(e -> startSimulation());
        frame.getStopButton().addActionListener(e -> stopSimulation());
    }

    private void resetStatistics() {
        totalWaitingTime = 0;
        totalServiceTime = 0;
        peakHour = 0;
        maxClientsInQueues = 0;
    }

    private void validateInput() {
        try {
            readInputFromFrame();
            validateIntervals();
            frame.setStartEnabled(true);
            frame.showMessage("Input data is valid.");
        } catch (IllegalArgumentException e) {
            frame.setStartEnabled(false);
            frame.showMessage(e.getMessage());
        }
    }

    private void readInputFromFrame() {
        timeLimit = frame.getTimeLimit();
        minArrivalTime = frame.getMinArrivalTime();
        maxArrivalTime = frame.getMaxArrivalTime();
        minServiceTime = frame.getMinServiceTime();
        maxServiceTime = frame.getMaxServiceTime();
        nrOfClients = frame.getNumberOfClients();
        nrOfServers = frame.getNumberOfServers();
        selectionPolicy = frame.getSelectedPolicy();
    }

    private void validateIntervals() {
        if (timeLimit <= 0) {
            throw new IllegalArgumentException("Time limit must be greater than 0.");
        }
        if (nrOfClients <= 0) {
            throw new IllegalArgumentException("Number of clients must be greater than 0.");
        }
        if (nrOfServers <= 0) {
            throw new IllegalArgumentException("Number of servers must be greater than 0.");
        }
        if (minArrivalTime < 0 || maxArrivalTime < 0 || minServiceTime < 0 || maxServiceTime < 0) {
            throw new IllegalArgumentException("Times cannot be negative.");
        }
        if (minArrivalTime > maxArrivalTime) {
            throw new IllegalArgumentException("Min arrival time cannot be greater than max arrival time.");
        }
        if (minServiceTime > maxServiceTime) {
            throw new IllegalArgumentException("Min service time cannot be greater than max service time.");
        }
    }

    private void startSimulation() {
        if (running) {
            frame.showMessage("A simulation is already running.");
            return;
        }

        try {
            readInputFromFrame();
            validateIntervals();
        } catch (Exception ex) {
            frame.showMessage("Cannot start simulation: " + ex.getMessage());
            return;
        }

        resetStatistics();
        stopped = false;
        frame.clearLog();
        frame.clearStatistics();
        clearLogFile();

        generatedTasks = new ArrayList<>();
        generateNRandomTasks();

        scheduler = new Scheduler(nrOfServers);
        scheduler.changeStrategy(selectionPolicy);

        running = true;
        simulationThread = new Thread(this);
        simulationThread.start();
    }

    private void stopSimulation() {
        if (!running && scheduler == null) {
            frame.clearLog();
            frame.clearStatistics();
            return;
        }

        stopped = true;
        running = false;
        if (scheduler != null) {
            scheduler.stopAllServers();
        }
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }

        frame.appendLog("Simulation stopped.\n\n");
        writeLogToFile("Simulation stopped.\n\n");
        frame.clearStatistics();
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        for (int i = 1; i <= nrOfClients; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
            totalServiceTime += serviceTime;
        }
        Collections.sort(generatedTasks);
    }

    private String buildLog(int currentTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("Time ").append(currentTime).append("\n");
        sb.append("Waiting clients:\n");

        if (generatedTasks.isEmpty()) {
            sb.append("none");
        } else {
            for (int i = 0; i < generatedTasks.size(); i++) {
                sb.append(generatedTasks.get(i));
                if (i < generatedTasks.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("\n");

        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++) {
            sb.append("Queue ").append(i + 1).append(": ");
            Task[] tasks = servers.get(i).getTasks();

            if (tasks.length == 0) {
                sb.append("closed");
            } else {
                for (Task task : tasks) {
                    sb.append(task).append(" ");
                }
            }
            sb.append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    private void clearLogFile() {
        try (PrintWriter clearFile = new PrintWriter(new FileWriter("log.txt"))) {
            clearFile.print("");
        } catch (IOException e) {
            frame.showMessage("Could not reset log.txt: " + e.getMessage());
        }
    }

    private void writeLogToFile(String log) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt", true))) {
            writer.print(log);
        } catch (IOException e) {
            SwingUtilities.invokeLater(() ->
                    frame.showMessage("Error writing to log.txt: " + e.getMessage()));
        }
    }

    private boolean allQueuesEmpty() {
        for (Server server : scheduler.getServers()) {
            if (server.getTasks().length > 0) {
                return false;
            }
        }
        return true;
    }

    private int getMinimumWaitingPeriod() {
        int minimum = scheduler.getServers().getFirst().getWaitingPeriod();
        for (int i = 1; i < scheduler.getServers().size(); i++) {
            if (scheduler.getServers().get(i).getWaitingPeriod() < minimum) {
                minimum = scheduler.getServers().get(i).getWaitingPeriod();
            }
        }
        return minimum;
    }

    private int getTotalClientsInQueues() {
        int total = 0;
        for (Server server : scheduler.getServers()) {
            total += server.getTasks().length;
        }
        return total;
    }

    private void updatePeakHour(int currentTime) {
        int clientsInQueue = getTotalClientsInQueues();
        if (clientsInQueue > maxClientsInQueues) {
            maxClientsInQueues = clientsInQueue;
            peakHour = currentTime;
        }
    }

    private String buildStatistics() {
        double averageWaitingTime = nrOfClients == 0 ? 0 : (double) totalWaitingTime / nrOfClients;
        double averageServiceTime = nrOfClients == 0 ? 0 : (double) totalServiceTime / nrOfClients;

        StringBuilder sb = new StringBuilder();
        sb.append("Average waiting time: ").append(String.format("%.2f", averageWaitingTime)).append("\n");
        sb.append("Average service time: ").append(String.format("%.2f", averageServiceTime)).append("\n");
        sb.append("Peak hour: ").append(peakHour).append("\n");
        return sb.toString();
    }

    @Override
    public void run() {
        int currentTime = 0;
        boolean naturalFinish = false;

        while (running & currentTime <= timeLimit) {
            Iterator<Task> iterator = generatedTasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getArrivalTime() == currentTime) {
                    totalWaitingTime += getMinimumWaitingPeriod();
                    scheduler.dispatchTask(task);
                    iterator.remove();
                }
            }

            updatePeakHour(currentTime);

            String log = buildLog(currentTime);
            frame.appendLog(log);
            writeLogToFile(log);

            if (generatedTasks.isEmpty() && allQueuesEmpty()) {
                naturalFinish = true;
                break;
            }

            currentTime++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        running = false;

        if (!stopped && scheduler != null) {
            scheduler.stopAllServers();
        }

        if (naturalFinish || (!stopped && currentTime > timeLimit)) {
            String statistics = buildStatistics();
            frame.setStatistics(statistics);
            writeLogToFile("\n--- Statistics ---\n" + statistics + "\n");
            frame.appendLog("Simulation finished.\n");
            writeLogToFile("Simulation finished.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulationManager::new);
    }
}