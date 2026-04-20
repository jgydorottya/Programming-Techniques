package model;

public final class SimpleTask extends Task {
    private static final long serialVersionUID = 1L;

    private int startHour;
    private int endHour;

    public SimpleTask(int idTask, String statusTask, int startHour, int endHour) {
        super(idTask, statusTask);
        setStartHour(startHour);
        setEndHour(endHour);
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        if (startHour < 0 || startHour > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23.");
        }
        if (endHour != 0 && startHour > endHour) {
            throw new IllegalArgumentException("Start hour must be less than end hour.");
        }
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        if (endHour < 0 || endHour > 23) {
            throw new IllegalArgumentException("Invalid end hour format.");
        }
        if (endHour < startHour) {
            throw new IllegalArgumentException("End hour must be greater than start hour.");
        }
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {
        return endHour - startHour;
    }
}
