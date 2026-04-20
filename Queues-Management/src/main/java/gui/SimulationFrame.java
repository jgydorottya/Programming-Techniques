package gui;

import business.SelectionPolicy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JTextField timeLimitField;
    private JTextField minArrivalField;
    private JTextField maxArrivalField;
    private JTextField minServiceField;
    private JTextField maxServiceField;
    private JTextField numberOfClientsField;
    private JTextField numberOfServersField;
    private JComboBox<SelectionPolicy> strategyComboBox;

    private JButton validateButton;
    private JButton startButton;
    private JButton stopButton;

    private JTextArea logArea;
    private JPanel statisticsPanel;
    private JTextArea statisticsArea;

    public SimulationFrame() {
        initializeFrame();
        initializeComponents();
        layoutComponents();
        attachDefaultState();

        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Queues Management Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        timeLimitField = new JTextField("20", 8);
        minArrivalField = new JTextField("1", 8);
        maxArrivalField = new JTextField("10", 8);
        minServiceField = new JTextField("2", 8);
        maxServiceField = new JTextField("4", 8);
        numberOfClientsField = new JTextField("6", 8);
        numberOfServersField = new JTextField("2", 8);

        strategyComboBox = new JComboBox<>(SelectionPolicy.values());

        validateButton = new JButton("Validate");
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        logArea.setLineWrap(false);

        statisticsPanel = new JPanel(new BorderLayout());
        statisticsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        statisticsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        statisticsArea.setLineWrap(true);
        statisticsArea.setWrapStyleWord(true);
        statisticsArea.setText("Final statistics will be displayed here...");
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainPanel.add(createInputPanel(), BorderLayout.NORTH);
        mainPanel.add(createLogPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatisticsPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Input"));

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        fieldsPanel.add(new JLabel("Time limit:"));
        fieldsPanel.add(timeLimitField);

        fieldsPanel.add(new JLabel("Min arrival time:"));
        fieldsPanel.add(minArrivalField);

        fieldsPanel.add(new JLabel("Max arrival time:"));
        fieldsPanel.add(maxArrivalField);

        fieldsPanel.add(new JLabel("Min service time:"));
        fieldsPanel.add(minServiceField);

        fieldsPanel.add(new JLabel("Max service time:"));
        fieldsPanel.add(maxServiceField);

        fieldsPanel.add(new JLabel("Number of clients:"));
        fieldsPanel.add(numberOfClientsField);

        fieldsPanel.add(new JLabel("Number of servers:"));
        fieldsPanel.add(numberOfServersField);

        fieldsPanel.add(new JLabel("Strategy:"));
        fieldsPanel.add(strategyComboBox);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.add(validateButton);
        buttonsPanel.add(startButton);
        buttonsPanel.add(stopButton);

        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Simulation Output"));

        JScrollPane scrollPane = new JScrollPane(
                logArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(900, 400));

        return logPanel;
    }

    private JPanel createStatisticsPanel() {
        JScrollPane scrollPane = new JScrollPane(
                statisticsArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        statisticsPanel.add(scrollPane, BorderLayout.CENTER);
        statisticsPanel.setPreferredSize(new Dimension(900, 140));
        return statisticsPanel;
    }

    private void attachDefaultState() {
        startButton.setEnabled(false);
    }

    public void appendLog(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void clearLog() {
        logArea.setText("");
    }

    public void clearStatistics() {
        statisticsArea.setText("Final statistics will be displayed here...");
    }

    public void setStatistics(String text) { SwingUtilities.invokeLater(() -> statisticsArea.setText(text)); }

    public void setStartEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public int getTimeLimit() {
        return Integer.parseInt(timeLimitField.getText().trim());
    }

    public int getMinArrivalTime() {
        return Integer.parseInt(minArrivalField.getText().trim());
    }

    public int getMaxArrivalTime() {
        return Integer.parseInt(maxArrivalField.getText().trim());
    }

    public int getMinServiceTime() {
        return Integer.parseInt(minServiceField.getText().trim());
    }

    public int getMaxServiceTime() {
        return Integer.parseInt(maxServiceField.getText().trim());
    }

    public int getNumberOfClients() {
        return Integer.parseInt(numberOfClientsField.getText().trim());
    }

    public int getNumberOfServers() {
        return Integer.parseInt(numberOfServersField.getText().trim());
    }

    public SelectionPolicy getSelectedPolicy() {
        return (SelectionPolicy) strategyComboBox.getSelectedItem();
    }

    public JButton getValidateButton() {
        return validateButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}