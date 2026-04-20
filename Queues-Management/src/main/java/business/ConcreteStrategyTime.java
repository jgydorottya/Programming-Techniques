package business;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        if (servers == null || t == null) {
            throw new IllegalArgumentException("Servers list or task is invalid.");
        }
        Server bestServer = servers.getFirst();
        for (int i = 1; i < servers.size(); i++) {
            if (servers.get(i).getWaitingPeriod() < bestServer.getWaitingPeriod()) {
                bestServer = servers.get(i);
            }
        }
        bestServer.addTask(t);
    }
}
