package gui;

import core.Branch;
import core.DamageAssessment;
import core.MaintenanceStatus;
import core.MaintenanceTask;
import core.Mechanic;
import core.Vehicle;
import core.VehicleStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MechanicPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final DefaultTableModel queueModel = createTaskTableModel();
    private final DefaultTableModel completedModel = createTaskTableModel();
    private final JComboBox<Vehicle> vehicleCombo = new JComboBox<>();
    private final JTextArea vehicleInfoArea = new JTextArea(8, 34);

    public MechanicPanel(MainFrame frame) {
        DATE_FORMAT.setLenient(false);
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        refreshTables(frame);
        refreshVehicleCombo(frame);
        vehicleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vehicle) {
                    Vehicle vehicle = (Vehicle) value;
                    setText(vehicle.getPlateNumber() + " | " + vehicle.getBrand() + " "
                            + vehicle.getModel() + " | " + vehicle.getStatus());
                }
                return this;
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Maintenance Queue", buildQueueTab(frame));
        tabs.addTab("Schedule Task", buildScheduleTab(frame));
        tabs.addTab("Completed Tasks", buildCompletedTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(160, 80, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        Mechanic mechanic = frame.session().getMechanic();
        Branch branch = getMechanicBranch(frame);

        JLabel title = new JLabel("Mechanic Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String mechanicText = frame.session().getUsername();
        if (mechanic != null) {
            mechanicText = mechanic.getName() + " " + mechanic.getSurname() + " | " + mechanic.getEmail();
        }
        String branchText = branch == null ? "No branch" : branch.getName();
        JLabel user = new JLabel("Mechanic: " + mechanicText + " | Branch: " + branchText);
        user.setForeground(new Color(255, 220, 180));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(user);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> frame.showWelcome());
        right.add(logoutBtn);

        JButton switchUserBtn = new JButton("Switch User");
        switchUserBtn.addActionListener(e -> frame.showWelcome());
        right.add(switchUserBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        if (mechanic == null || branch == null) {
            JOptionPane.showMessageDialog(this,
                    "This mechanic is not assigned to a branch.",
                    "Mechanic branch",
                    JOptionPane.WARNING_MESSAGE);
        }
        return header;
    }

    private JPanel buildQueueTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(queueModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton completeButton = new JButton("Complete Selected Task");
        completeButton.addActionListener(e -> completeSelectedTask(frame, table));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshTables(frame);
            refreshVehicleCombo(frame);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(completeButton);
        actions.add(refreshButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildScheduleTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField dateField = new JTextField(DATE_FORMAT.format(new Date()), 12);
        JTextField descriptionField = new JTextField(28);

        vehicleInfoArea.setEditable(false);

        vehicleCombo.addActionListener(e -> updateVehicleInfo());
        updateVehicleInfo();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Vehicle"));
        form.add(vehicleCombo);
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(dateField);
        form.add(new JLabel("Description"));
        form.add(descriptionField);

        JButton scheduleButton = new JButton("Schedule Maintenance");
        scheduleButton.addActionListener(e -> scheduleMaintenance(frame, dateField, descriptionField));

        JButton refreshButton = new JButton("Refresh Vehicles");
        refreshButton.addActionListener(e -> refreshVehicleCombo(frame));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(scheduleButton);
        actions.add(refreshButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.NORTH);
        top.add(actions, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(vehicleInfoArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCompletedTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(completedModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void scheduleMaintenance(MainFrame frame, JTextField dateField, JTextField descriptionField) {
        Mechanic mechanic = frame.session().getMechanic();
        Vehicle vehicle = (Vehicle) vehicleCombo.getSelectedItem();
        frame.appState().applyMaintenanceStatusRules();
        if (mechanic == null || mechanic.getBranch() == null) {
            showWarning("This mechanic is not assigned to a branch.");
            return;
        }
        if (vehicle == null) {
            showWarning("Please select a vehicle eligible for maintenance.");
            return;
        }
        if (!isEligibleForMaintenance(frame, vehicle)) {
            showWarning("Only vehicles due for maintenance or with unresolved damage can be scheduled.");
            return;
        }
        if (vehicle.getStatus() == VehicleStatus.RENTED || vehicle.getStatus() == VehicleStatus.IN_MAINTENANCE) {
            showWarning("Rented or already-in-maintenance vehicles cannot be scheduled.");
            return;
        }

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            showWarning("Description cannot be empty.");
            return;
        }

        try {
            Date maintenanceDate = DATE_FORMAT.parse(dateField.getText().trim());
            MaintenanceTask task = new MaintenanceTask(
                    frame.appState().createNextMaintenanceID(),
                    maintenanceDate,
                    description,
                    MaintenanceStatus.SCHEDULED);
            task.setVehicle(vehicle);
            mechanic.performMaintenance(task);
            task.scheduleMaintenance();
            frame.appState().addMaintenanceTaskIfMissing(task);
            frame.appState().saveAll();
            descriptionField.setText("");
            refreshTables(frame);
            refreshVehicleCombo(frame);
            JOptionPane.showMessageDialog(this,
                    "Maintenance task " + task.getMaintenanceID() + " scheduled.",
                    "Maintenance",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ParseException ex) {
            showWarning("Date must use yyyy-MM-dd format.");
        } catch (FileNotFoundException ex) {
            showError("Maintenance scheduled but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void completeSelectedTask(MainFrame frame, JTable table) {
        MaintenanceTask task = getSelectedTask(table, queueModel);
        if (task == null) {
            showWarning("Please select a maintenance task.");
            return;
        }

        try {
            task.completeMaintenance();
            frame.appState().saveAll();
            refreshTables(frame);
            refreshVehicleCombo(frame);
            JOptionPane.showMessageDialog(this,
                    "Maintenance task " + task.getMaintenanceID() + " completed.",
                    "Maintenance",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Task completed but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void refreshTables(MainFrame frame) {
        fillQueueTable(frame);
        fillCompletedTable(frame);
    }

    private void fillQueueTable(MainFrame frame) {
        queueModel.setRowCount(0);
        Mechanic mechanic = frame.session().getMechanic();
        if (mechanic == null) {
            return;
        }
        for (MaintenanceTask task : mechanic.getMaintenanceTasks()) {
            if (task.getStatus() == MaintenanceStatus.SCHEDULED
                    || task.getStatus() == MaintenanceStatus.IN_PROGRESS) {
                addTaskRow(queueModel, task);
            }
        }
    }

    private void fillCompletedTable(MainFrame frame) {
        completedModel.setRowCount(0);
        Mechanic mechanic = frame.session().getMechanic();
        if (mechanic == null) {
            return;
        }
        for (MaintenanceTask task : mechanic.getMaintenanceTasks()) {
            if (task.getStatus() == MaintenanceStatus.COMPLETED) {
                addTaskRow(completedModel, task);
            }
        }
    }

    private void addTaskRow(DefaultTableModel model, MaintenanceTask task) {
        Vehicle vehicle = task.getVehicle();
        model.addRow(new Object[] {
                task.getMaintenanceID(),
                formatDate(task.getMaintenanceDate()),
                vehicle == null ? "-" : vehicle.getPlateNumber(),
                vehicle == null ? "-" : vehicle.getBrand() + " " + vehicle.getModel(),
                vehicle == null ? "-" : vehicle.getStatus(),
                task.getStatus(),
                task.getDescription(),
                vehicle == null ? "-" : vehicle.getCurrentMileage(),
                vehicle == null ? "-" : vehicle.getLastMaintenanceMileage()
        });
    }

    private void refreshVehicleCombo(MainFrame frame) {
        frame.appState().applyMaintenanceStatusRules();
        vehicleCombo.removeAllItems();
        Branch branch = getMechanicBranch(frame);
        if (branch == null) {
            updateVehicleInfo();
            return;
        }
        for (Vehicle vehicle : branch.getVehicles()) {
            if (isEligibleForMaintenance(frame, vehicle)
                    && vehicle.getStatus() != VehicleStatus.RENTED
                    && vehicle.getStatus() != VehicleStatus.IN_MAINTENANCE) {
                vehicleCombo.addItem(vehicle);
            }
        }
        updateVehicleInfo();
    }

    private void updateVehicleInfo() {
        Vehicle vehicle = (Vehicle) vehicleCombo.getSelectedItem();
        if (vehicle == null) {
            vehicleInfoArea.setText("No available vehicle selected.");
            return;
        }

        vehicleInfoArea.setText(
                "Vehicle: " + vehicle.getPlateNumber() + " | " + vehicle.getBrand() + " " + vehicle.getModel() + "\n"
                        + "Status: " + vehicle.getStatus() + "\n"
                        + "Current mileage: " + vehicle.getCurrentMileage() + "\n"
                        + "Last maintenance mileage: " + vehicle.getLastMaintenanceMileage() + "\n"
                        + "Maintenance interval: " + vehicle.getMaintenanceInterval() + "\n"
                        + "Distance to next maintenance: "
                        + String.format("%.0f", vehicle.calculateDistanceToNextMaintenance()) + "\n"
                        + "Needs maintenance: " + vehicle.needsMaintenance() + "\n"
                        + "Unresolved damage: " + hasUnresolvedDamage(vehicle));
    }

    private boolean isEligibleForMaintenance(MainFrame frame, Vehicle vehicle) {
        return vehicle != null
                && vehicle.getBranch() == getMechanicBranch(frame)
                && (vehicle.needsMaintenance() || hasUnresolvedDamage(vehicle));
    }

    private boolean hasUnresolvedDamage(Vehicle vehicle) {
        Date latestCompletedMaintenanceDate = null;
        for (MaintenanceTask task : vehicle.getMaintenanceTasks()) {
            if (task.getStatus() == MaintenanceStatus.COMPLETED && task.getMaintenanceDate() != null
                    && (latestCompletedMaintenanceDate == null
                            || task.getMaintenanceDate().after(latestCompletedMaintenanceDate))) {
                latestCompletedMaintenanceDate = task.getMaintenanceDate();
            }
        }

        ArrayList<DamageAssessment> assessments = vehicle.getDamageAssessments();
        for (DamageAssessment assessment : assessments) {
            if (assessment.getDamageCost() > 0
                    && assessment.getAssessmentDate() != null
                    && (latestCompletedMaintenanceDate == null
                            || assessment.getAssessmentDate().after(latestCompletedMaintenanceDate))) {
                return true;
            }
        }
        return false;
    }

    private MaintenanceTask getSelectedTask(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int taskID = (Integer) model.getValueAt(modelRow, 0);
        Mechanic mechanic = getRootFrame().session().getMechanic();
        if (mechanic == null) {
            return null;
        }
        for (MaintenanceTask task : mechanic.getMaintenanceTasks()) {
            if (task.getMaintenanceID() == taskID) {
                return task;
            }
        }
        return null;
    }

    private Branch getMechanicBranch(MainFrame frame) {
        Mechanic mechanic = frame.session().getMechanic();
        if (mechanic == null) {
            return null;
        }
        return mechanic.getBranch();
    }

    private MainFrame getRootFrame() {
        return (MainFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return DATE_FORMAT.format(date);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Maintenance", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Maintenance", JOptionPane.ERROR_MESSAGE);
    }

    private DefaultTableModel createTaskTableModel() {
        return new DefaultTableModel(
                new Object[] {
                        "Task ID", "Date", "Plate", "Vehicle", "Vehicle Status", "Task Status",
                        "Description", "Current KM", "Last Maintenance KM"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
