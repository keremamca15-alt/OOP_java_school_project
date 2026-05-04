package gui;

import core.Addon;
import core.Branch;
import core.Customer;
import core.Invoice;
import core.InvalidReservationException;
import core.Payment;
import core.PaymentPurpose;
import core.RentalAgent;
import core.Reservation;
import core.ReservationStatus;
import core.RentalContract;
import core.Vehicle;
import core.VehicleNotAvailableException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
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
import java.awt.Component;
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
    private DefaultTableModel invoiceModel;
    private DefaultTableModel paymentModel;
    private JTabbedPane tabs;
    private Customer activeCustomer;

    public CustomerPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        activeCustomer = frame.session().getCustomer();
        add(buildHeader(frame), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        reservationModel = createReservationTableModel();
        invoiceModel = createInvoiceTableModel();
        paymentModel = createPaymentTableModel();
        fillReservationTable(reservationModel, frame.session().getCustomer());
        fillInvoiceTable(invoiceModel, frame);
        fillPaymentTable(paymentModel, frame.session().getCustomer());

        tabs.addTab("Search Vehicles",   buildSearchTab(frame, reservationModel));
        tabs.addTab("My Reservations",   buildReservationsTab(frame, reservationModel));
        tabs.addTab("My Invoices",        buildInvoicesTab(frame));
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
        JLabel discountLabel = new JLabel("Loyalty discount: -");
        JLabel addonCostLabel = new JLabel("Addon total: -");
        JLabel estimatedTotalLabel = new JLabel("Estimated total: -");
        JLabel prepaymentLabel = new JLabel("Prepayment due now: -");
        JLabel depositLabel = new JLabel("Deposit at pickup: -");

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
        availableAddonList.setVisibleRowCount(5);
        availableAddonList.setFixedCellHeight(28);
        availableAddonList.setPrototypeCellValue(new Addon(0, "Child Seat", "", 999.99));
        availableAddonList.setCellRenderer(new AddonListRenderer());
        JList<Addon> selectedAddonList = new JList<>(selectedAddonModel);
        selectedAddonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedAddonList.setVisibleRowCount(4);
        selectedAddonList.setFixedCellHeight(28);
        selectedAddonList.setPrototypeCellValue(new Addon(0, "Child Seat", "", 999.99));
        selectedAddonList.setCellRenderer(new AddonListRenderer());

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
                    discountLabel,
                    addonCostLabel,
                    estimatedTotalLabel,
                    prepaymentLabel,
                    depositLabel);
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
                        detailLabel, durationLabel, rentalCostLabel, discountLabel, addonCostLabel, estimatedTotalLabel,
                        prepaymentLabel, depositLabel);
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
                int days = reservation.calculateDuration();
                double prepaymentAmount = calculateDiscountedRentalCost(customer, vehicle.calculateRentalCost(days))
                        + calculateAddonCost(reservation.getAddons(), days);
                reservation.setPrePaymentAmount(prepaymentAmount);
                reservation.setDepositAmount(RentalAgent.getDefaultDepositAmount());

                Payment prepayment = new Payment(
                        frame.appState().createNextPaymentID(),
                        prepaymentAmount,
                        new Date(),
                        PaymentPurpose.PREPAYMENT,
                        customer.getUserID());
                prepayment.setCustomer(customer);
                prepayment.setReservation(reservation);
                frame.appState().addPaymentIfMissing(prepayment);

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
        addAddonButton.setPreferredSize(new Dimension(220, 34));
        addAddonButton.setMinimumSize(new Dimension(220, 34));
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
                        detailLabel, durationLabel, rentalCostLabel, discountLabel, addonCostLabel, estimatedTotalLabel,
                        prepaymentLabel, depositLabel);
            }
        });

        JButton removeAddonButton = new JButton("Remove Addon");
        removeAddonButton.setPreferredSize(new Dimension(220, 34));
        removeAddonButton.setMinimumSize(new Dimension(220, 34));
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
                    detailLabel, durationLabel, rentalCostLabel, discountLabel, addonCostLabel, estimatedTotalLabel,
                    prepaymentLabel, depositLabel);
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
        preview.setPreferredSize(new Dimension(330, 230));
        preview.add(detailLabel);
        preview.add(durationLabel);
        preview.add(rentalCostLabel);
        preview.add(discountLabel);
        preview.add(addonCostLabel);
        preview.add(estimatedTotalLabel);
        preview.add(prepaymentLabel);
        preview.add(depositLabel);
        preview.add(reserveButton);

        JPanel availableAddonPanel = new JPanel(new BorderLayout());
        availableAddonPanel.setBorder(BorderFactory.createTitledBorder("Available Addons"));
        JScrollPane availableAddonScroll = new JScrollPane(availableAddonList);
        availableAddonScroll.setPreferredSize(new Dimension(310, 150));
        availableAddonScroll.setMinimumSize(new Dimension(280, 120));
        availableAddonPanel.add(availableAddonScroll, BorderLayout.CENTER);
        availableAddonPanel.add(addAddonButton, BorderLayout.SOUTH);

        JPanel selectedAddonPanel = new JPanel(new BorderLayout());
        selectedAddonPanel.setBorder(BorderFactory.createTitledBorder("Selected Addons"));
        JScrollPane selectedAddonScroll = new JScrollPane(selectedAddonList);
        selectedAddonScroll.setPreferredSize(new Dimension(310, 120));
        selectedAddonScroll.setMinimumSize(new Dimension(280, 100));
        selectedAddonPanel.add(selectedAddonScroll, BorderLayout.CENTER);
        selectedAddonPanel.add(removeAddonButton, BorderLayout.SOUTH);

        JPanel addonPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        addonPanel.setPreferredSize(new Dimension(330, 340));
        addonPanel.setMinimumSize(new Dimension(300, 280));
        addonPanel.add(availableAddonPanel);
        addonPanel.add(selectedAddonPanel);

        JPanel sidePanel = new JPanel(new BorderLayout(8, 8));
        sidePanel.setPreferredSize(new Dimension(360, 560));
        sidePanel.setMinimumSize(new Dimension(330, 480));
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

    private JPanel buildInvoicesTab(MainFrame frame) {
        JPanel tab = new JPanel(new GridLayout(2, 1, 8, 8));
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable invoiceTable = new JTable(invoiceModel);
        invoiceTable.setAutoCreateRowSorter(true);
        JTable paymentTable = new JTable(paymentModel);
        paymentTable.setAutoCreateRowSorter(true);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCustomerTables(frame));

        JPanel invoicePanel = new JPanel(new BorderLayout(8, 8));
        invoicePanel.setBorder(BorderFactory.createTitledBorder("Invoices"));
        invoicePanel.add(refreshButton, BorderLayout.NORTH);
        invoicePanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment History"));
        paymentPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        tab.add(invoicePanel);
        tab.add(paymentPanel);
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

    private static class AddonListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Addon) {
                Addon addon = (Addon) value;
                label.setText(addon.getName() + " - " + addon.getDailyPrice() + "/day");
            }
            label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return label;
        }
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
                new Object[]{"ID", "Vehicle", "Start Date", "End Date", "Status", "Addons", "Estimated Total",
                        "Prepayment", "Deposit"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createInvoiceTableModel() {
        return new DefaultTableModel(
                new Object[]{"Invoice ID", "Reservation ID", "Vehicle", "Discount", "Total", "Paid", "Remaining",
                        "Deposit Refund"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createPaymentTableModel() {
        return new DefaultTableModel(
                new Object[]{"Payment ID", "Date", "Purpose", "Amount", "Reservation ID", "Contract ID", "Invoice ID"},
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
                    calculateEstimatedTotal(reservation),
                    reservation.getPrePaymentAmount(),
                    reservation.getDepositAmount()
            });
        }
    }

    private void fillInvoiceTable(DefaultTableModel model, MainFrame frame) {
        model.setRowCount(0);
        Customer customer = frame.session().getCustomer();
        if (customer == null) {
            return;
        }
        for (Invoice invoice : frame.appState().getInvoices()) {
            RentalContract contract = invoice.getRentalContract();
            Reservation reservation = getReservation(invoice);
            if (reservation == null || reservation.getCustomer() != customer) {
                continue;
            }
            model.addRow(new Object[]{
                    invoice.getInvoiceID(),
                    reservation.getReservationID(),
                    getVehiclePlate(reservation),
                    invoice.getDiscountAmount(),
                    invoice.calculateTotal(),
                    invoice.calculatePaidAmount(),
                    calculateCustomerRemaining(invoice, contract),
                    calculateDepositRefund(contract)
            });
        }
    }

    private void fillPaymentTable(DefaultTableModel model, Customer customer) {
        model.setRowCount(0);
        if (customer == null) {
            return;
        }
        for (Payment payment : customer.getPayments()) {
            model.addRow(new Object[]{
                    payment.getPaymentID(),
                    formatDate(payment.getPaymentDate()),
                    payment.getPaymentPurpose(),
                    payment.getAmount(),
                    getPaymentReservationID(payment),
                    getPaymentContractID(payment),
                    getPaymentInvoiceID(payment)
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
            JLabel discountLabel, JLabel addonCostLabel, JLabel estimatedTotalLabel, JLabel prepaymentLabel,
            JLabel depositLabel) {
        if (vehicle == null) {
            detailLabel.setText("Selected vehicle: -");
            durationLabel.setText("Duration: -");
            rentalCostLabel.setText("Vehicle rental cost: -");
            discountLabel.setText("Loyalty discount: -");
            addonCostLabel.setText("Addon total: -");
            estimatedTotalLabel.setText("Estimated total: -");
            prepaymentLabel.setText("Prepayment due now: -");
            depositLabel.setText("Deposit at pickup: -");
            return;
        }
        try {
            Date startDate = parseDate(startDateField.getText());
            Date endDate = parseDate(endDateField.getText());
            int days = calculateDays(startDate, endDate);
            double rentalCost = vehicle.calculateRentalCost(days);
            double discount = calculateDiscountAmount(getActiveCustomer(), rentalCost);
            double discountedRentalCost = rentalCost - discount;
            double addonCost = calculateAddonCost(addons, days);
            detailLabel.setText("Selected vehicle: " + vehicle.getPlateNumber() + " - "
                    + vehicle.getBrand() + " " + vehicle.getModel());
            durationLabel.setText("Duration: " + days + " day(s)");
            rentalCostLabel.setText("Vehicle rental cost: " + rentalCost);
            discountLabel.setText("Loyalty discount: " + discount);
            addonCostLabel.setText("Addon total: " + addonCost);
            estimatedTotalLabel.setText("Estimated total: " + (discountedRentalCost + addonCost));
            prepaymentLabel.setText("Prepayment due now: " + (discountedRentalCost + addonCost));
            depositLabel.setText("Deposit at pickup: " + RentalAgent.getDefaultDepositAmount());
        } catch (ParseException | IllegalArgumentException | IllegalStateException exception) {
            detailLabel.setText("Selected vehicle: " + vehicle.getPlateNumber());
            durationLabel.setText("Duration: invalid date range");
            rentalCostLabel.setText("Vehicle rental cost: -");
            discountLabel.setText("Loyalty discount: -");
            addonCostLabel.setText("Addon total: -");
            estimatedTotalLabel.setText("Estimated total: -");
            prepaymentLabel.setText("Prepayment due now: -");
            depositLabel.setText("Deposit at pickup: -");
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

    private Customer getActiveCustomer() {
        return activeCustomer;
    }

    private double calculateDiscountedRentalCost(Customer customer, double rentalCost) {
        return rentalCost - calculateDiscountAmount(customer, rentalCost);
    }

    private double calculateDiscountAmount(Customer customer, double rentalCost) {
        if (customer == null || customer.getLoyaltyTier() == null || rentalCost <= 0) {
            return 0.0;
        }
        return rentalCost * customer.getLoyaltyTier().getDiscountRate();
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

    private Reservation getReservation(Invoice invoice) {
        if (invoice == null || invoice.getRentalContract() == null) {
            return null;
        }
        return invoice.getRentalContract().getReservation();
    }

    private String getVehiclePlate(Reservation reservation) {
        if (reservation == null || reservation.getVehicle() == null) {
            return "-";
        }
        return reservation.getVehicle().getPlateNumber();
    }

    private double calculateCustomerRemaining(Invoice invoice, RentalContract contract) {
        if (contract == null) {
            return invoice.calculateRemainingAmount();
        }
        return contract.calculateRemainingAmountAfterDeposit();
    }

    private double calculateDepositRefund(RentalContract contract) {
        if (contract == null) {
            return 0.0;
        }
        return contract.calculateDepositRefund();
    }

    private int getPaymentReservationID(Payment payment) {
        if (payment.getReservation() != null) {
            return payment.getReservation().getReservationID();
        }
        if (payment.getRentalContract() != null && payment.getRentalContract().getReservation() != null) {
            return payment.getRentalContract().getReservation().getReservationID();
        }
        if (payment.getInvoice() != null && payment.getInvoice().getRentalContract() != null
                && payment.getInvoice().getRentalContract().getReservation() != null) {
            return payment.getInvoice().getRentalContract().getReservation().getReservationID();
        }
        return 0;
    }

    private int getPaymentContractID(Payment payment) {
        if (payment.getRentalContract() != null) {
            return payment.getRentalContract().getContractID();
        }
        if (payment.getInvoice() != null && payment.getInvoice().getRentalContract() != null) {
            return payment.getInvoice().getRentalContract().getContractID();
        }
        return 0;
    }

    private int getPaymentInvoiceID(Payment payment) {
        if (payment.getInvoice() == null) {
            return 0;
        }
        return payment.getInvoice().getInvoiceID();
    }

    private double calculateEstimatedTotal(Reservation reservation) {
        if (reservation.getVehicle() == null) {
            return 0.0;
        }
        int days = reservation.calculateDuration();
        return calculateDiscountedRentalCost(reservation.getCustomer(), reservation.getVehicle().calculateRentalCost(days))
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
            refreshCustomerTables(frame);
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

    private void refreshCustomerTables(MainFrame frame) {
        Customer customer = frame.session().getCustomer();
        fillReservationTable(reservationModel, customer);
        fillInvoiceTable(invoiceModel, frame);
        fillPaymentTable(paymentModel, customer);
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
