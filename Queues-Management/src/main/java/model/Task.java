package model;


public class Task implements Comparable<Task> {
    private int id;
    private int arrivalTime;
    private int serviceTime;

    public Task(int id, int arrivalTime, int serviceTime) {
        setId(id);
        setArrivalTime(arrivalTime);
        setServiceTime(serviceTime);
    }

    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + serviceTime + ")";
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.arrivalTime, o.arrivalTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Task ID must be greater than 0.");
        }
        this.id = id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        if (arrivalTime < 0) {
            throw new IllegalArgumentException("Arrival time must be greater than or equal to 0.");
        }
        this.arrivalTime = arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        if (serviceTime < 0) {
            throw new IllegalArgumentException("Service time must be greater than 0.");
        }
        this.serviceTime = serviceTime;
    }
}
