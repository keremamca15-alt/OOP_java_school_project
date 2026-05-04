package gui;

import core.Branch;
import core.BranchManager;
import core.BranchReport;
import core.ContractStatus;
import core.Customer;
import core.Employee;
import core.Invoice;
import core.Mechanic;
import core.RentalAgent;
import core.RentalContract;
import core.Reservation;
import core.ReservationStatus;
import core.Vehicle;
import core.VehicleStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BranchManagerPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final DefaultTableModel fleetModel = createFleetTableModel();
    private final DefaultTableModel employeeModel = createEmployeeTableModel();
    private final DefaultTableModel reservationModel = createReservationTableModel();
    private final DefaultTableModel reportModel = createReportTableModel();
    private final JTextArea summaryArea = new JTextArea(7, 40);

    public BranchManagerPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        refreshTables(frame);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Fleet Overview", buildFleetTab(frame));
        tabs.addTab("Employees", buildEmployeesTab(frame));
        tabs.addTab("All Reservations", buildReservationsTab());
        tabs.addTab("Branch Reports", buildReportsTab(frame));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(100, 40, 140));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        BranchManager manager = frame.session().getBranchManager();
        Branch branch = getManagedBranch(frame);

        JLabel title = new JLabel("Branch Manager Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String branchText = branch == null ? "No branch" : branch.getName();
        String managerText = frame.session().getUsername();
        if (manager != null) {
            managerText = manager.getName() + " " + manager.getSurname() + " | " + manager.getEmail();
        }
        JLabel user = new JLabel("Manager: " + managerText + " | Branch: " + branchText);
        user.setForeground(new Color(220, 190, 250));

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

        if (manager == null || branch == null) {
            JOptionPane.showMessageDialog(this,
                    "This branch manager is not assigned to a branch.",
                    "Manager branch",
                    JOptionPane.WARNING_MESSAGE);
        }
        return header;
    }

    private JPanel buildFleetTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        summaryArea.setEditable(false);
        summaryArea.setFont(summaryArea.getFont().deriveFont(Font.PLAIN, 13f));

        JTable table = new JTable(fleetModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton assignButton = new JButton("Assign Selected Vehicle");
        assignButton.addActionListener(e -> assignSelectedVehicle(frame, table));

        JButton removeButton = new JButton("Remove Selected Vehicle");
        removeButton.addActionListener(e -> removeSelectedVehicle(frame, table));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTables(frame));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        actions.add(assignButton);
        actions.add(removeButton);
        actions.add(refreshButton);
        top.add(actions, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildEmployeesTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(employeeModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton assignButton = new JButton("Assign Selected Employee");
        assignButton.addActionListener(e -> assignSelectedEmployee(frame, table));

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> addEmployee(frame));

        JButton removeButton = new JButton("Remove Selected Employee");
        removeButton.addActionListener(e -> removeSelectedEmployee(frame, table));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTables(frame));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(addButton);
        actions.add(assignButton);
        actions.add(removeButton);
        actions.add(refreshButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(reservationModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReportsTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(reportModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton generateButton = new JButton("Generate Report");
        generateButton.setEnabled(getManagedBranch(frame) != null && frame.session().getBranchManager() != null);
        generateButton.addActionListener(e -> generateReport(frame));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTables(frame));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(generateButton);
        actions.add(refreshButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void assignSelectedEmployee(MainFrame frame, JTable table) {
        BranchManager manager = frame.session().getBranchManager();
        Branch branch = getManagedBranch(frame);
        Employee employee = getSelectedEmployee(frame, table);
        if (manager == null || branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }
        if (employee == null) {
            showWarning("Please select an employee.");
            return;
        }
        if (employee instanceof BranchManager) {
            showWarning("Branch managers cannot be assigned from this screen.");
            return;
        }

        try {
            manager.addEmployee(employee);
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this, "Employee assigned to " + branch.getName() + ".",
                    "Branch manager", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Employee assigned but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void addEmployee(MainFrame frame) {
        BranchManager manager = frame.session().getBranchManager();
        Branch branch = getManagedBranch(frame);
        if (manager == null || branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }

        JComboBox<String> roleBox = new JComboBox<>(new String[] { "RENTAL_AGENT", "MECHANIC" });
        JTextField nameField = new JTextField(16);
        JTextField surnameField = new JTextField(16);
        JTextField emailField = new JTextField(20);
        JTextField salaryField = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Role"));
        form.add(roleBox);
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Surname"));
        form.add(surnameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Salary"));
        form.add(salaryField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            showWarning("Name, surname and email cannot be empty.");
            return;
        }
        if (isEmailUsed(frame, email)) {
            showWarning("This email is already used by another user.");
            return;
        }
        if (isEmployeeNameUsed(frame, name, surname)) {
            showWarning("An employee with this name and surname already exists.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            Employee employee;
            if ("MECHANIC".equals(roleBox.getSelectedItem())) {
                employee = new Mechanic();
            } else {
                employee = new RentalAgent();
            }
            employee.setEmployeeID(frame.appState().createNextEmployeeID());
            employee.setUserID(frame.appState().createNextUserID());
            employee.setName(name);
            employee.setSurname(surname);
            employee.setEmail(email);
            employee.setSalary(salary);
            manager.addEmployee(employee);
            frame.appState().addEmployeeIfMissing(employee);
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this,
                    "Employee added to " + branch.getName() + ".",
                    "Branch manager",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            showWarning("Salary must be a valid number.");
        } catch (FileNotFoundException ex) {
            showError("Employee added but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void removeSelectedEmployee(MainFrame frame, JTable table) {
        BranchManager manager = frame.session().getBranchManager();
        Branch branch = getManagedBranch(frame);
        Employee employee = getSelectedEmployee(frame, table);
        if (manager == null || branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }
        if (employee == null) {
            showWarning("Please select an employee.");
            return;
        }
        if (employee.getBranch() != branch) {
            showWarning("Only employees assigned to your branch can be removed.");
            return;
        }
        if (employee instanceof BranchManager) {
            showWarning("Branch managers cannot be removed from this screen.");
            return;
        }

        try {
            manager.removeEmployee(employee.getEmployeeID());
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this, "Employee moved to unassigned list.",
                    "Branch manager", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Employee removed but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void assignSelectedVehicle(MainFrame frame, JTable table) {
        Branch branch = getManagedBranch(frame);
        Vehicle vehicle = getSelectedVehicle(frame, table);
        if (branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }
        if (vehicle == null) {
            showWarning("Please select a vehicle.");
            return;
        }

        try {
            branch.addVehicle(vehicle);
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this, "Vehicle assigned to " + branch.getName() + ".",
                    "Branch manager", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Vehicle assigned but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void removeSelectedVehicle(MainFrame frame, JTable table) {
        Branch branch = getManagedBranch(frame);
        Vehicle vehicle = getSelectedVehicle(frame, table);
        if (branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }
        if (vehicle == null) {
            showWarning("Please select a vehicle.");
            return;
        }
        if (vehicle.getBranch() != branch) {
            showWarning("Only vehicles assigned to your branch can be removed.");
            return;
        }
        if (hasActiveOrFutureWork(vehicle)) {
            showWarning("Vehicle cannot be removed because it has active/future reservation or active contract.");
            return;
        }

        try {
            branch.removeVehicle(vehicle.getVehicleID());
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this, "Vehicle moved to unassigned list.",
                    "Branch manager", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Vehicle removed but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void generateReport(MainFrame frame) {
        BranchManager manager = frame.session().getBranchManager();
        Branch branch = getManagedBranch(frame);
        if (manager == null || branch == null) {
            showWarning("This manager is not assigned to a branch.");
            return;
        }

        try {
            BranchReport report = manager.generateReport(branch);
            frame.appState().addBranchReportIfMissing(report);
            frame.appState().saveAll();
            refreshTables(frame);
            JOptionPane.showMessageDialog(this,
                    "Report generated. Total revenue: " + formatMoney(report.getTotalRevenue()),
                    "Branch report",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            showError("Report generated but data could not be saved: " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void refreshTables(MainFrame frame) {
        Branch branch = getManagedBranch(frame);
        fillSummary(branch);
        fillFleetTable(frame, branch);
        fillEmployeeTable(frame, branch);
        fillReservationTable(branch);
        fillReportTable(branch);
    }

    private void fillSummary(Branch branch) {
        if (branch == null) {
            summaryArea.setText("No managed branch.");
            return;
        }

        int available = 0;
        int rented = 0;
        int maintenance = 0;
        int outOfService = 0;
        int reservations = 0;
        double paidRevenue = 0.0;
        ArrayList<Invoice> countedInvoices = new ArrayList<>();

        for (Vehicle vehicle : branch.getVehicles()) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                available++;
            } else if (vehicle.getStatus() == VehicleStatus.RENTED) {
                rented++;
            } else if (vehicle.getStatus() == VehicleStatus.IN_MAINTENANCE) {
                maintenance++;
            } else if (vehicle.getStatus() == VehicleStatus.OUT_OF_SERVICE) {
                outOfService++;
            }

            reservations += vehicle.getReservations().size();
            for (Reservation reservation : vehicle.getReservations()) {
                Invoice invoice = getInvoice(reservation);
                if (invoice != null && !countedInvoices.contains(invoice)) {
                    countedInvoices.add(invoice);
                    paidRevenue += invoice.calculatePaidAmount();
                }
            }
        }

        summaryArea.setText(
                "Branch: " + branch.getName() + "\n"
                        + "Address: " + branch.getAddress() + "\n"
                        + "Vehicles: " + branch.getVehicles().size()
                        + " | Available: " + available
                        + " | Rented: " + rented
                        + " | Maintenance: " + maintenance
                        + " | Out of service: " + outOfService + "\n"
                        + "Employees: " + branch.getEmployees().size() + "\n"
                        + "Reservations: " + reservations + "\n"
                        + "Paid revenue: " + formatMoney(paidRevenue));
    }

    private void fillFleetTable(MainFrame frame, Branch branch) {
        fleetModel.setRowCount(0);
        if (branch != null) {
            for (Vehicle vehicle : branch.getVehicles()) {
                addVehicleRow(vehicle);
            }
        }
        for (Vehicle vehicle : frame.appState().getVehicles()) {
            if (vehicle.getBranch() == null) {
                addVehicleRow(vehicle);
            }
        }
    }

    private void addVehicleRow(Vehicle vehicle) {
        fleetModel.addRow(new Object[] {
                vehicle.getVehicleID(),
                vehicle.getPlateNumber(),
                vehicle.getClass().getSimpleName(),
                vehicle.getBrand() + " " + vehicle.getModel(),
                vehicle.getYear(),
                formatMoney(vehicle.getDailyRate()),
                vehicle.getStatus(),
                formatBranch(vehicle.getBranch()),
                vehicle.getCurrentMileage(),
                vehicle.getLastMaintenanceMileage(),
                vehicle.getMaintenanceInterval()
        });
    }

    private void fillEmployeeTable(MainFrame frame, Branch branch) {
        employeeModel.setRowCount(0);
        if (branch != null) {
            for (Employee employee : branch.getEmployees()) {
                addEmployeeRow(employee);
            }
        }
        for (Employee employee : frame.appState().getEmployees()) {
            if (employee.getBranch() == null) {
                addEmployeeRow(employee);
            }
        }
    }

    private void addEmployeeRow(Employee employee) {
        employeeModel.addRow(new Object[] {
                employee.getEmployeeID(),
                employee.getClass().getSimpleName(),
                employee.getName() + " " + employee.getSurname(),
                employee.getEmail(),
                formatMoney(employee.getSalary()),
                formatBranch(employee.getBranch())
        });
    }

    private void fillReservationTable(Branch branch) {
        reservationModel.setRowCount(0);
        if (branch == null) {
            return;
        }

        for (Vehicle vehicle : branch.getVehicles()) {
            for (Reservation reservation : vehicle.getReservations()) {
                Customer customer = reservation.getCustomer();
                RentalContract contract = reservation.getRentalContract();
                Invoice invoice = getInvoice(reservation);
                reservationModel.addRow(new Object[] {
                        reservation.getReservationID(),
                        formatCustomer(customer),
                        vehicle.getPlateNumber(),
                        formatDate(reservation.getStartDate()),
                        formatDate(reservation.getEndDate()),
                        reservation.getStatus(),
                        contract == null ? "-" : contract.getContractID(),
                        invoice == null ? "-" : invoice.getInvoiceID(),
                        invoice == null ? formatMoney(0.0) : formatMoney(invoice.calculatePaidAmount())
                });
            }
        }
    }

    private void fillReportTable(Branch branch) {
        reportModel.setRowCount(0);
        if (branch == null) {
            return;
        }

        for (BranchReport report : branch.getBranchReports()) {
            reportModel.addRow(new Object[] {
                    report.getReportID(),
                    formatDate(report.getGeneratedDate()),
                    report.getTotalVehicles(),
                    report.getTotalReservations(),
                    report.getAvailableVehicles(),
                    report.getRentedVehicles(),
                    formatMoney(report.getTotalRevenue()),
                    report.getGeneratedBy() == null ? "-" : report.getGeneratedBy().getName() + " "
                            + report.getGeneratedBy().getSurname()
            });
        }
    }

    private boolean hasActiveOrFutureWork(Vehicle vehicle) {
        if (vehicle.getStatus() == VehicleStatus.RENTED) {
            return true;
        }
        Date today = startOfDay(new Date());
        for (Reservation reservation : vehicle.getReservations()) {
            RentalContract contract = reservation.getRentalContract();
            if (contract != null && contract.getStatus() == ContractStatus.ACTIVE) {
                return true;
            }
            if ((reservation.getStatus() == ReservationStatus.PENDING
                    || reservation.getStatus() == ReservationStatus.CONFIRMED)
                    && reservation.getEndDate() != null
                    && !startOfDay(reservation.getEndDate()).before(today)) {
                return true;
            }
        }
        return false;
    }

    private Employee getSelectedEmployee(MainFrame frame, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int employeeID = (Integer) employeeModel.getValueAt(modelRow, 0);
        for (Employee employee : frame.appState().getEmployees()) {
            if (employee.getEmployeeID() == employeeID) {
                return employee;
            }
        }
        return null;
    }

    private Vehicle getSelectedVehicle(MainFrame frame, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int vehicleID = (Integer) fleetModel.getValueAt(modelRow, 0);
        for (Vehicle vehicle : frame.appState().getVehicles()) {
            if (vehicle.getVehicleID() == vehicleID) {
                return vehicle;
            }
        }
        return null;
    }

    private boolean isEmailUsed(MainFrame frame, String email) {
        for (Customer customer : frame.appState().getCustomers()) {
            if (customer.getEmail() != null && customer.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        for (Employee employee : frame.appState().getEmployees()) {
            if (employee.getEmail() != null && employee.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmployeeNameUsed(MainFrame frame, String name, String surname) {
        String normalizedName = normalizeName(name);
        String normalizedSurname = normalizeName(surname);
        for (Employee employee : frame.appState().getEmployees()) {
            if (normalizeName(employee.getName()).equals(normalizedName)
                    && normalizeName(employee.getSurname()).equals(normalizedSurname)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeName(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private Branch getManagedBranch(MainFrame frame) {
        BranchManager manager = frame.session().getBranchManager();
        if (manager == null) {
            return null;
        }
        return manager.getManagedBranch();
    }

    private Invoice getInvoice(Reservation reservation) {
        if (reservation == null || reservation.getRentalContract() == null) {
            return null;
        }
        return reservation.getRentalContract().getInvoice();
    }

    private String formatCustomer(Customer customer) {
        if (customer == null) {
            return "-";
        }
        return customer.getName() + " " + customer.getSurname();
    }

    private String formatBranch(Branch branch) {
        if (branch == null) {
            return "Unassigned";
        }
        return branch.getName();
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "-";
        }
        return DATE_FORMAT.format(date);
    }

    private Date startOfDay(Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Branch manager", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Branch manager", JOptionPane.ERROR_MESSAGE);
    }

    private DefaultTableModel createFleetTableModel() {
        return new DefaultTableModel(
                new Object[] {
                        "ID", "Plate", "Type", "Brand / Model", "Year", "Daily Rate", "Status",
                        "Branch", "Current KM", "Last Maintenance KM", "Maintenance Interval"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createEmployeeTableModel() {
        return new DefaultTableModel(
                new Object[] { "Employee ID", "Role", "Name", "Email", "Salary", "Branch" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createReservationTableModel() {
        return new DefaultTableModel(
                new Object[] {
                        "Reservation ID", "Customer", "Vehicle", "Start Date", "End Date", "Status",
                        "Contract ID", "Invoice ID", "Paid Amount"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createReportTableModel() {
        return new DefaultTableModel(
                new Object[] {
                        "Report ID", "Date", "Total Vehicles", "Total Reservations", "Available",
                        "Rented", "Total Revenue", "Generated By"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
