# Task Management Application

This project was developed for **Assignment 1** in the **Fundamental Programming Techniques** course at TUCN.  
The application manages employees and their tasks through a Java Swing graphical interface, following the requirements from the assignment specification.

## Features
- add employees
- add simple tasks and complex tasks
- assign tasks to employees
- view employees and their assigned tasks
- estimate task duration
- toggle task status between **Completed** and **Uncompleted**
- view statistics:
  - employees with more than 40 completed work hours
  - number of completed and uncompleted tasks for each employee
- persist application data using Java serialization

## Project Structure
The project follows a **layered architecture**:
- **model** - `Employee`, `Task`, `SimpleTask`, `ComplexTask`
- **business** - `TasksManagement`, `Utility`
- **data** - `SerializationManagement`
- **presentation** - `MainFrame`

## Technologies Used
- Java
- Java Swing
- Java Serialization
- Java Collections Framework

## Implementation Notes
- `Task` is implemented as a **sealed abstract class**
- `ComplexTask` follows the **Composite Design Pattern**
- employee IDs and task IDs are validated to remain unique
- data is loaded at application startup and saved after operations in the GUI

## How to Run
1. Open the project in **IntelliJ IDEA**
2. Build the project
3. Run the `Main` class

## Data Persistence
The application stores serialized data in:
```text
tasks_data.ser