package business;

import model.Server;
import model.Task;

import javax.swing.plaf.TableHeaderUI;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private List<Thread> serverThreads;
    private int nrOfServers;
    private Strategy strategy;

    public Scheduler(int nrOfServers) {
        setNrOfServers(nrOfServers);
        servers = new ArrayList<>();
        serverThreads = new ArrayList<>();

        for (int i = 0; i < nrOfServers; i++) {
            Server server = new Server();
            servers.add(server);

            Thread thread = new Thread(server);
            serverThreads.add(thread);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        } else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task t) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy was not set.");
        }
        strategy.addTask(servers, t);
    }

    public void stopAllServers() {
        for (Server server : servers) {
            server.stopServer();
        }
        for (Thread thread : serverThreads) {
            thread.interrupt();
        }
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        if (servers == null) {
            throw new IllegalArgumentException("Servers cannot be null.");
        }
        this.servers = servers;
    }

    public int getNrOfServers() {
        return nrOfServers;
    }

    public void setNrOfServers(int nrOfServers) {
        if (nrOfServers < 1) {
            throw new IllegalArgumentException("Number of servers must be greater than 0.");
        }
        this.nrOfServers = nrOfServers;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
