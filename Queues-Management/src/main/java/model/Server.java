package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile boolean running;

    public Server() {
        tasks = new LinkedBlockingDeque<>();
        waitingPeriod = new AtomicInteger(0);
        running = true;
    }

    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("New task cannot be null.");
        }
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public void stopServer() {
        running = false;
    }

    public void run() {
        while (running) {
            try {
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    Thread.sleep(1000);
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    waitingPeriod.decrementAndGet();
                    if (currentTask.getServiceTime() == 0) {
                        tasks.poll();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    public void setTasks(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

}
