package gui;

import core.Addon;
import core.Branch;
import core.Customer;
import core.InvalidReservationException;
import core.Reservation;
import core.ReservationStatus;
import core.Vehicle;
import core.VehicleNotAvailableException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CustomerPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        DATE_FORMAT.setLenient(false);
    }

    private final ArrayList<Vehicle> currentSearchResults = new ArrayList<>();
    private Vehicle selectedVehicle;
    private DefaultTableModel reservationModel;
    private JTabbedPane tabs;

    public CustomerPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        reservationModel = createReservationTableModel();
        fillReservationTable(reservationModel, frame.session().getCustomer());

        tabs.addTab("Search Vehicles",   buildSearchTab(frame, reservationModel));
        tabs.addTab("My Reservations",   buildReservationsTab(frame, reservationModel));
        tabs.addTab("My Invoices",        placeholder("Your invoices and payment history will appear here."));
        tabs.addTab("Loyalty & Profile",  buildLoyaltyTab(frame));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 70, 160));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String username = frame.session().getUsername();
        JLabel user = new JLabel("Logged in as: " + username);
        user.setForeground(new Color(200, 220, 255));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(user);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> frame.showWelcome());
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSearchTab(MainFrame frame, DefaultTableModel reservationModel) {
        JPanel tab = new JPanel(new BorderLayout(12, 12));
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<Branch> branchCombo = new JComboBox<>();
        for (Branch branch : frame.appState().getBranches()) {
            branchCombo.addItem(branch);
        }

        JTextField startDateField = new JTextField(formatDate(daysFromToday(1)), 10);
        JTextField endDateField = new JTextField(formatDate(daysFromToday(4)), 10);
        JLabel statusLabel = new JLabel("Search vehicles, select one row, then make a reservation.");
        JLabel detailLabel = new JLabel("Selected vehicle: -");
        JLabel durationLabel = new JLabel("Duration: -");
        JLabel rentalCostLabel = new JLabel("Vehicle rental cost: -");
        JLabel addonCostLabel = new JLabel("Addon total: -");
        JLabel estimatedTotalLabel = new JLabel("Estimated total: -");

        DefaultTableModel vehicleModel = createVehicleTableModel();
        JTable vehicleTable = new JTable(vehicleModel);
        vehicleTable.setAutoCreateRowSorter(true);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListModel<Addon> availableAddonModel = new DefaultListModel<>();
        for (Addon addon : frame.appState().getAddons()) {
            availableAddonModel.addElement(addon);
        }
        DefaultListModel<Addon> selectedAddonModel = new DefaultListModel<>();
        JList<Addon> availableAddonList = new JList<>(availableAddonModel);
        availableAddonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JList<Addon> selectedAddonList = new JList<>(selectedAddonModel);
        selectedAddonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            selectedVehicle = getSelectedVehicle(vehicleTable);
            updatePreview(
                    selectedVehicle,
                    getSelectedAddons(selectedAddonModel),
                    startDateField,
                    endDateField,
                    detailLabel,
                    durationLabel,
                    rentalCostLabel,
                    addonCostLabel,
                    estimatedTotalLabel);
        });

        JButton searchButton = new JButton("Search Vehicles");
        searchButton.addActionListener(e -> {
            Branch branch = (Branch) branchCombo.getSelectedItem();
            if (branch == null) {
                JOptionPane.showMessageDialog(this, "Please select a branch.", "Search", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Date startDate = parseDate(startDateField.getText());
                Date endDate = parseDate(endDateField.getText());
                currentSearchResults.clear();
                currentSearchResults.addAll(branch.findAvailableVehicles(startDate, endDate));
                fillVehicleTable(vehicleModel, currentSearchResults);
                selectedVehicle = null;
                statusLabel.setText(currentSearchResults.size() + " available vehicles found.");
                updatePreview(null, getSelectedAddons(selectedAddonModel), startDateField, endDateField,
                        detailLabel, durationLabel, rentalCostLabel, addonCostLabel, estimatedTotalLabel);
            } catch (ParseException exception) {
                JOptionPane.showMessageDialog(this,
                        "Date format must be yyyy-MM-dd.",
                        "Invalid date",
                        JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(this,
                        exception.getMessage(),
                        "Search failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton reserveButton = new JButton("Create Reservation");
        reserveButton.setFont(reserveButton.getFont().deriveFont(Font.BOLD, 13f));
        reserveButton.setPreferredSize(new Dimension(220, 36));
        reserveButton.addActionListener(e -> {
            Customer customer = frame.session().getCustomer();
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "No customer is logged in.", "Reservation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = vehicleTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a vehicle.", "Reservation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Vehicle vehicle = getSelectedVehicle(vehicleTable);
            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Selected vehicle could not be found.", "Reservation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Date startDate = parseDate(startDateField.getText());
                Date endDate = parseDate(endDateField.getText());
                Reservation reservation = customer.makeReservation(
                        frame.appState().createNextReservationID(),
                        vehicle,
                        startDate,
                        endDate);
                for (Addon addon : getSelectedAddons(selectedAddonModel)) {
                    reservation.addAddon(addon);
                }
                reservation.setPrePaymentAmount(0.0);
                reservation.setDepositAmount(0.0);
                frame.appState().getReservations().add(reservation);
                fillReservationTable(reservationModel, customer);
                if (saveData(frame, false)) {
                    statusLabel.setText("Reservation created and saved with ID " + reservation.getReservationID() + ".");
                } else {
                    statusLabel.setText("Reservation created in memory, but save failed.");
                }
                tabs.setSelectedIndex(1);
            } catch (ParseException exception) {
                JOptionPane.showMessageDialog(this,
                        "Date format must be yyyy-MM-dd.",
                        "Invalid date",
                        JOptionPane.WARNING_MESSAGE);
            } catch (InvalidReservationException | VehicleNotAvailableException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this,
                        exception.getMessage(),
                        "Reservation failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton addAddonButton = new JButton("Add Selected Addon");
        addAddonButton.addActionListener(e -> {
            Addon addon = availableAddonList.getSelectedValue();
            if (addon == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select an addon to add.",
                        "Add addon",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!containsAddon(selectedAddonModel, addon)) {
                selectedAddonModel.addElement(addon);
                updatePreview(selectedVehicle, getSelectedAddons(selectedAddonModel), startDateField, endDateField,
                        detailLabel, durationLabel, rentalCostLabel, addonCostLabel, estimatedTotalLabel);
            }
        });

        JButton removeAddonButton = new JButton("Remove Addon");
        removeAddonButton.addActionListener(e -> {
            int selectedIndex = selectedAddonList.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select an addon to remove.",
                        "Remove addon",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedAddonModel.remove(selectedIndex);
            updatePreview(selectedVehicle, getSelectedAddons(selectedAddonModel), startDateField, endDateField,
                    detailLabel, durationLabel, rentalCostLabel, addonCostLabel, estimatedTotalLabel);
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controls.add(new JLabel("Branch:"));
        controls.add(branchCombo);
        controls.add(new JLabel("Start Date:"));
        controls.add(startDateField);
        controls.add(new JLabel("End Date:"));
        controls.add(endDateField);
        controls.add(searchButton);

        JPanel preview = new JPanel(new GridLayout(0, 1, 4, 4));
        preview.setBorder(BorderFactory.createTitledBorder("Reservation Preview"));
        preview.add(detailLabel);
        preview.add(durationLabel);
        preview.add(rentalCostLabel);
        preview.add(addonCostLabel);
        preview.add(estimatedTotalLabel);
        preview.add(reserveButton);

        JPanel availableAddonPanel = new JPanel(new BorderLayout());
        availableAddonPanel.setBorder(BorderFactory.createTitledBorder("Available Addons"));
        availableAddonPanel.add(new JScrollPane(availableAddonList), BorderLayout.CENTER);
        availableAddonPanel.add(addAddonButton, BorderLayout.SOUTH);

        JPanel selectedAddonPanel = new JPanel(new BorderLayout());
        selectedAddonPanel.setBorder(BorderFactory.createTitledBorder("Selected Addons"));
        selectedAddonPanel.add(new JScrollPane(selectedAddonList), BorderLayout.CENTER);
        selectedAddonPanel.add(removeAddonButton, BorderLayout.SOUTH);

        JPanel addonPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        addonPanel.add(availableAddonPanel);
        addonPanel.add(selectedAddonPanel);

        JPanel sidePanel = new JPanel(new BorderLayout(8, 8));
        sidePanel.add(addonPanel, BorderLayout.CENTER);
        sidePanel.add(preview, BorderLayout.SOUTH);

        tab.add(controls, BorderLayout.NORTH);
        tab.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);
        tab.add(sidePanel, BorderLayout.EAST);
        tab.add(statusLabel, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildReservationsTab(MainFrame frame, DefaultTableModel reservationModel) {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JTable reservationsTable = new JTable(reservationModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton cancelButton = new JButton("Cancel Selected Reservation");
        cancelButton.addActionListener(e -> cancelSelectedReservation(frame, reservationsTable));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(cancelButton);

        tab.add(actions, BorderLayout.NORTH);
        tab.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildLoyaltyTab(MainFrame frame) {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel info = new JPanel();
        info.setLayout(new javax.swing.BoxLayout(info, javax.swing.BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createTitledBorder("Loyalty Status"));

        Customer customer = frame.session().getCustomer();
        info.add(new JLabel("Username: " + frame.session().getUsername()));
        info.add(new JLabel(" "));
        if (customer != null) {
            info.add(new JLabel("Loyalty Tier:   " + customer.getLoyaltyTier()));
            info.add(new JLabel("Loyalty Points: " + customer.getLoyaltyPoints()));
        } else {
            info.add(new JLabel("Loyalty Tier:   -"));
            info.add(new JLabel("Loyalty Points: -"));
        }
        info.add(new JLabel(" "));
        info.add(new JLabel("Tier benefits and point redemption options will appear here."));

        tab.add(info);
        return tab;
    }

    private static JPanel placeholder(String message) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.ITALIC, 13f));
        label.setForeground(Color.GRAY);
        p.add(label);
        return p;
    }

    private DefaultTableModel createVehicleTableModel() {
        return new DefaultTableModel(
                new Object[]{"Plate", "Type", "Brand", "Model", "Year", "Daily Rate", "Insurance", "Mileage Policy", "Status"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createReservationTableModel() {
        return new DefaultTableModel(
                new Object[]{"ID", "Vehicle", "Start Date", "End Date", "Status", "Addons", "Estimated Total"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void fillVehicleTable(DefaultTableModel model, ArrayList<Vehicle> vehicles) {
        model.setRowCount(0);
        for (Vehicle vehicle : vehicles) {
            model.addRow(new Object[]{
                    vehicle.getPlateNumber(),
                    vehicle.getClass().getSimpleName(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getDailyRate(),
                    vehicle.getInsuranceOption(),
                    vehicle.getMileagePolicy(),
                    vehicle.getStatus()
            });
        }
    }

    private void fillReservationTable(DefaultTableModel model, Customer customer) {
        model.setRowCount(0);
        if (customer == null) {
            return;
        }
        for (Reservation reservation : customer.getReservations()) {
            Vehicle vehicle = reservation.getVehicle();
            String plate = "-";
            if (vehicle != null) {
                plate = vehicle.getPlateNumber();
            }
            model.addRow(new Object[]{
                    reservation.getReservationID(),
                    plate,
                    formatDate(reservation.getStartDate()),
                    formatDate(reservation.getEndDate()),
                    reservation.getStatus(),
                    formatAddonNames(reservation.getAddons()),
                    calculateEstimatedTotal(reservation)
            });
        }
    }

    private Vehicle getSelectedVehicle(JTable vehicleTable) {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = vehicleTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= currentSearchResults.size()) {
            return null;
        }
        return currentSearchResults.get(modelRow);
    }

    private void updatePreview(Vehicle vehicle, java.util.List<Addon> addons, JTextField startDateField,
            JTextField endDateField, JLabel detailLabel, JLabel durationLabel, JLabel rentalCostLabel,
            JLabel addonCostLabel, JLabel estimatedTotalLabel) {
        if (vehicle == null) {
            detailLabel.setText("Selected vehicle: -");
            durationLabel.setText("Duration: -");
            rentalCostLabel.setText("Vehicle rental cost: -");
            addonCostLabel.setText("Addon total: -");
            estimatedTotalLabel.setText("Estimated total: -");
            return;
        }
        try {
            Date startDate = parseDate(startDateField.getText());
            Date endDate = parseDate(endDateField.getText());
            int days = calculateDays(startDate, endDate);
            double rentalCost = vehicle.calculateRentalCost(days);
            double addonCost = calculateAddonCost(addons, days);
            detailLabel.setText("Selected vehicle: " + vehicle.getPlateNumber() + " - "
                    + vehicle.getBrand() + " " + vehicle.getModel());
            durationLabel.setText("Duration: " + days + " day(s)");
            rentalCostLabel.setText("Vehicle rental cost: " + rentalCost);
            addonCostLabel.setText("Addon total: " + addonCost);
            estimatedTotalLabel.setText("Estimated total: " + (rentalCost + addonCost));
        } catch (ParseException | IllegalArgumentException | IllegalStateException exception) {
            detailLabel.setText("Selected vehicle: " + vehicle.getPlateNumber());
            durationLabel.setText("Duration: invalid date range");
            rentalCostLabel.setText("Vehicle rental cost: -");
            addonCostLabel.setText("Addon total: -");
            estimatedTotalLabel.setText("Estimated total: -");
        }
    }

    private int calculateDays(Date startDate, Date endDate) {
        if (!endDate.after(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
        long difference = endDate.getTime() - startDate.getTime();
        long dayInMillis = 24L * 60 * 60 * 1000;
        return (int) (difference / dayInMillis);
    }

    private double calculateAddonCost(java.util.List<Addon> addons, int days) {
        double total = 0.0;
        for (Addon addon : addons) {
            total += addon.calculateCost(days);
        }
        return total;
    }

    private ArrayList<Addon> getSelectedAddons(DefaultListModel<Addon> selectedAddonModel) {
        ArrayList<Addon> addons = new ArrayList<>();
        for (int i = 0; i < selectedAddonModel.size(); i++) {
            addons.add(selectedAddonModel.get(i));
        }
        return addons;
    }

    private boolean containsAddon(DefaultListModel<Addon> selectedAddonModel, Addon addon) {
        for (int i = 0; i < selectedAddonModel.size(); i++) {
            if (selectedAddonModel.get(i) == addon) {
                return true;
            }
        }
        return false;
    }

    private String formatAddonNames(ArrayList<Addon> addons) {
        String result = "";
        for (Addon addon : addons) {
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += addon.getName();
        }
        return result;
    }

    private double calculateEstimatedTotal(Reservation reservation) {
        if (reservation.getVehicle() == null) {
            return 0.0;
        }
        int days = reservation.calculateDuration();
        return reservation.getVehicle().calculateRentalCost(days)
                + calculateAddonCost(reservation.getAddons(), days);
    }

    private void cancelSelectedReservation(MainFrame frame, JTable reservationsTable) {
        Customer customer = frame.session().getCustomer();
        if (customer == null) {
            JOptionPane.showMessageDialog(this,
                    "No customer is logged in.",
                    "Cancel Reservation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation.",
                    "Cancel Reservation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = reservationsTable.convertRowIndexToModel(selectedRow);
        int reservationID = (int) reservationModel.getValueAt(modelRow, 0);
        Reservation reservation = findReservation(customer, reservationID);
        if (reservation == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected reservation could not be found.",
                    "Cancel Reservation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this,
                    "Completed reservations cannot be cancelled.",
                    "Cancel Reservation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this,
                    "Reservation is already cancelled.",
                    "Cancel Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        reservation.cancelReservation();
        fillReservationTable(reservationModel, customer);
        if (saveData(frame, false)) {
            JOptionPane.showMessageDialog(this,
                    "Reservation " + reservationID + " cancelled and saved.",
                    "Cancel Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Reservation findReservation(Customer customer, int reservationID) {
        for (Reservation reservation : customer.getReservations()) {
            if (reservation.getReservationID() == reservationID) {
                return reservation;
            }
        }
        return null;
    }

    private boolean saveData(MainFrame frame, boolean showMessage) {
        try {
            frame.appState().saveAll();
            fillReservationTable(reservationModel, frame.session().getCustomer());
            tabs.setSelectedIndex(1);
            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        "Data saved successfully.\nMy Reservations count: " + reservationModel.getRowCount(),
                        "Save Data",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (FileNotFoundException exception) {
            JOptionPane.showMessageDialog(this,
                    "Data could not be saved:\n" + exception.getMessage(),
                    "Save Data",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Date parseDate(String value) throws ParseException {
        return DATE_FORMAT.parse(value.trim());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    private Date daysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}
