# Queue Management Application

This project was developed for **Assignment 2** in the **Fundamental Programming Techniques** course at TUCN. The application simulates clients arriving over time and being processed by multiple queues (servers), while computing performance statistics.

---

## Features

### Simulation Control
- configure simulation parameters:
  - time limit
  - number of clients
  - number of queues (servers)
  - arrival time interval
  - service time interval
  - scheduling strategy
- validate input before starting
- start / stop simulation
- automatic stop when:
  - all clients are processed
  - all queues become empty

### Scheduling Strategies
Implemented using the **Strategy Pattern**:
- **Shortest Queue** – assigns task to queue with minimum number of clients
- **Shortest Waiting Time** – assigns task to queue with smallest waiting period

---

## Multithreading Model
- each **Server** runs on its own thread :contentReference[oaicite:0]{index=0}
- a central **SimulationManager** controls the simulation lifecycle :contentReference[oaicite:1]{index=1}
- a separate **simulation thread** runs the time-based simulation loop
- thread-safe structures:
  - `BlockingQueue<Task>` for queues
  - `AtomicInteger` for waiting time

---

## GUI (Java Swing)
The application provides a full graphical interface with:
- input panel for simulation parameters
- real-time simulation log display
- statistics panel
- buttons:
  - Validate
  - Start
  - Stop

The GUI updates dynamically during simulation execution.

---

## Logging
- simulation state is displayed in real time in the GUI
- events are also written to:
```text
log.txt