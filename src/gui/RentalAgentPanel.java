package gui;

import core.Addon;
import core.Branch;
import core.ContractStatus;
import core.Customer;
import core.DamageAssessment;
import core.Invoice;
import core.InvalidReservationException;
import core.Payment;
import core.PaymentPurpose;
import core.RentalAgent;
import core.RentalContract;
import core.Reservation;
import core.ReservationStatus;
import core.Vehicle;
import core.VehicleNotAvailableException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RentalAgentPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final DefaultTableModel pickupModel = createReservationTableModel();
    private final DefaultTableModel activeContractModel = createContractTableModel();
    private final DefaultTableModel allReservationModel = createReservationTableModel();
    private final DefaultTableModel invoiceModel = createInvoiceTableModel();
    private final DefaultTableModel damageAssessmentModel = createDamageAssessmentTableModel();
    private final DefaultTableModel invoicePaymentModel = createInvoicePaymentTableModel();

    public RentalAgentPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        refreshTables(frame);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Process Pickup", buildPickupTab(frame));
        tabs.addTab("Active Contracts", buildActiveContractsTab());
        tabs.addTab("Process Return", buildReturnTab(frame));
        tabs.addTab("All Reservations", buildAllReservationsTab());
        tabs.addTab("Invoices", buildInvoicesTab(frame));
        tabs.addTab("Damage Assessments", buildDamageAssessmentsTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 110, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        RentalAgent agent = frame.session().getRentalAgent();
        Branch branch = getAgentBranch(frame);

        JLabel title = new JLabel("Rental Agent Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String branchText = "No branch";
        if (branch != null) {
            branchText = branch.getName();
        }
        JLabel user = new JLabel("Agent: " + frame.session().getUsername() + " | Branch: " + branchText);
        user.setForeground(new Color(190, 240, 210));

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

        if (agent == null || branch == null) {
            JOptionPane.showMessageDialog(this,
                    "This rental agent is not assigned to a branch.",
                    "Agent branch",
                    JOptionPane.WARNING_MESSAGE);
        }
        return header;
    }

    private JPanel buildPickupTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(pickupModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JButton confirmButton = new JButton("Confirm Selected Reservation");
        confirmButton.addActionListener(e -> confirmSelectedReservation(frame, table));

        JButton pickupButton = new JButton("Process Pickup");
        pickupButton.addActionListener(e -> processSelectedPickup(frame, table));

        JButton cancelButton = new JButton("Cancel Selected Reservation");
        cancelButton.addActionListener(e -> cancelSelectedReservation(frame, table, pickupModel));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTables(frame));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(confirmButton);
        actions.add(pickupButton);
        actions.add(cancelButton);
        actions.add(refreshButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActiveContractsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JTable table = new JTable(activeContractModel);
        table.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReturnTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable contractTable = new JTable(activeContractModel);
        contractTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contractTable.setAutoCreateRowSorter(true);

        JTextField finalMileageField = new JTextField(10);
        JTextField damageCostField = new JTextField(10);
        JTextArea damageDescriptionArea = new JTextArea(3, 24);
        JTextArea mileageInfoArea = new JTextArea(6, 24);
        JTextArea invoicePreviewArea = new JTextArea(9, 32);
        mileageInfoArea.setEditable(false);
        invoicePreviewArea.setEditable(false);

        JButton returnButton = new JButton("Process Return");
        returnButton.addActionListener(e -> processSelectedReturn(
                frame,
                contractTable,
                finalMileageField,
                damageCostField,
                damageDescriptionArea,
                invoicePreviewArea));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshTables(frame);
            updateMileageInfo(contractTable, finalMileageField, mileageInfoArea);
        });

        contractTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMileageInfo(contractTable, finalMileageField, mileageInfoArea);
            }
        });
        finalMileageField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMileageInfo(contractTable, finalMileageField, mileageInfoArea);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMileageInfo(contractTable, finalMileageField, mileageInfoArea);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMileageInfo(contractTable, finalMileageField, mileageInfoArea);
            }
        });

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Return Details"));
        form.add(new JLabel("Mileage Info:"));
        form.add(new JScrollPane(mileageInfoArea));
        form.add(new JLabel("Final Mileage:"));
        form.add(finalMileageField);
        form.add(new JLabel("Damage Cost:"));
        form.add(damageCostField);
        form.add(new JLabel("Damage Description:"));
        form.add(new JScrollPane(damageDescriptionArea));
        form.add(returnButton);
        form.add(refreshButton);

        JPanel side = new JPanel(new BorderLayout(8, 8));
        side.add(form, BorderLayout.NORTH);
        side.add(new JScrollPane(invoicePreviewArea), BorderLayout.CENTER);

        panel.add(new JScrollPane(contractTable), BorderLayout.CENTER);
        panel.add(side, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildAllReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JTable table = new JTable(allReservationModel);
        table.setAutoCreateRowSorter(true);

        JButton cancelButton = new JButton("Cancel Selected Reservation");
        cancelButton.addActionListener(e -> cancelSelectedReservation(getRootFrame(), table, allReservationModel));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(cancelButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildInvoicesTab(MainFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTable table = new JTable(invoiceModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTextArea detailArea = new JTextArea(12, 36);
        detailArea.setEditable(false);
        JTable paymentTable = new JTable(invoicePaymentModel);
        paymentTable.setAutoCreateRowSorter(true);
        paymentTable.setRowHeight(28);
        paymentTable.setFillsViewportHeight(true);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInvoiceDetail(table, detailArea);
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshTables(frame);
            updateInvoiceDetail(table, detailArea);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.add(refreshButton);

        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Invoice Detail"));
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setPreferredSize(new java.awt.Dimension(420, 260));
        JScrollPane paymentScroll = new JScrollPane(paymentTable);
        paymentScroll.setPreferredSize(new java.awt.Dimension(420, 220));
        paymentScroll.setMinimumSize(new java.awt.Dimension(360, 180));
        detailPanel.add(detailScroll, BorderLayout.NORTH);
        detailPanel.add(paymentScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(table),
                detailPanel);
        splitPane.setResizeWeight(0.62);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDamageAssessmentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JTable table = new JTable(damageAssessmentModel);
        table.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void confirmSelectedReservation(MainFrame frame, JTable table) {
        Reservation reservation = getSelectedReservation(table, pickupModel);
        if (reservation == null) {
            showWarning("Please select a reservation.");
            return;
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            showWarning("Only pending reservations can be confirmed.");
            return;
        }

        reservation.confirmReservation();
        if (saveAndRefresh(frame)) {
            JOptionPane.showMessageDialog(this,
                    "Reservation " + reservation.getReservationID() + " confirmed.",
                    "Confirm Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processSelectedPickup(MainFrame frame, JTable table) {
        RentalAgent agent = frame.session().getRentalAgent();
        if (agent == null) {
            showWarning("No rental agent is logged in.");
            return;
        }

        Reservation reservation = getSelectedReservation(table, pickupModel);
        if (reservation == null) {
            showWarning("Please select a reservation.");
            return;
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            showWarning("Only confirmed reservations can be picked up.");
            return;
        }

        try {
            RentalContract existingContract = reservation.getRentalContract();
            agent.processPickup(reservation);
            RentalContract contract = reservation.getRentalContract();
            if (contract != null) {
                if (existingContract == null) {
                    contract.setContractID(frame.appState().createNextRentalContractID());
                }
                for (Addon addon : reservation.getAddons()) {
                    contract.addAddon(addon);
                }
                createDepositPaymentIfNeeded(frame, reservation, contract);
                frame.appState().addRentalContractIfMissing(contract);
            }
            if (saveAndRefresh(frame)) {
                JOptionPane.showMessageDialog(this,
                        "Pickup processed for reservation " + reservation.getReservationID() + ".",
                        "Process Pickup",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (InvalidReservationException | VehicleNotAvailableException exception) {
            showWarning(exception.getMessage());
        }
    }

    private void processSelectedReturn(MainFrame frame, JTable table, JTextField finalMileageField,
            JTextField damageCostField, JTextArea damageDescriptionArea, JTextArea invoicePreviewArea) {
        RentalAgent agent = frame.session().getRentalAgent();
        if (agent == null) {
            showWarning("No rental agent is logged in.");
            return;
        }

        RentalContract contract = getSelectedContract(table);
        if (contract == null) {
            showWarning("Please select an active contract.");
            return;
        }
        Reservation reservation = contract.getReservation();
        if (reservation == null) {
            showWarning("Selected contract has no reservation.");
            return;
        }

        try {
            int finalMileage = parseFinalMileage(finalMileageField.getText());
            double damageCost = parseDamageCost(damageCostField.getText());
            DamageAssessment assessment = null;
            if (damageCost > 0) {
                String description = damageDescriptionArea.getText().trim();
                if (description.isEmpty()) {
                    description = "Damage reported at return";
                }
                assessment = agent.assessDamage(reservation.getVehicle(), description, damageCost);
                assessment.setAssessmentID(frame.appState().createNextDamageAssessmentID());
                frame.appState().addDamageAssessmentIfMissing(assessment);
            }

            Invoice invoice = agent.processReturn(reservation, assessment, finalMileage);
            invoice.applyDiscount(reservation.getCustomer());
            invoice.calculateTotal();
            invoice.setInvoiceID(frame.appState().createNextInvoiceID());
            frame.appState().addInvoiceIfMissing(invoice);
            addReservationPrepaymentToInvoice(reservation, invoice);
            createReturnPayments(frame, reservation, contract, invoice);
            awardLoyaltyPoints(reservation, invoice);

            if (saveAndRefresh(frame)) {
                invoicePreviewArea.setText(formatInvoicePreview(invoice));
                JOptionPane.showMessageDialog(this,
                        "Return processed for contract " + contract.getContractID() + ".",
                        "Process Return",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException exception) {
            showWarning("Final mileage and damage cost must be numeric.");
        } catch (IllegalArgumentException | InvalidReservationException exception) {
            showWarning(exception.getMessage());
        }
    }

    private void cancelSelectedReservation(MainFrame frame, JTable table, DefaultTableModel model) {
        Reservation reservation = getSelectedReservation(table, model);
        if (reservation == null) {
            showWarning("Please select a reservation.");
            return;
        }
        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            showWarning("Only pending or confirmed reservations can be cancelled.");
            return;
        }
        RentalContract contract = reservation.getRentalContract();
        if (contract != null && contract.getStatus() == ContractStatus.ACTIVE) {
            showWarning("Active contracts cannot be cancelled from reservation screen.");
            return;
        }

        reservation.cancelReservation();
        if (saveAndRefresh(frame)) {
            JOptionPane.showMessageDialog(this,
                    "Reservation " + reservation.getReservationID() + " cancelled.",
                    "Cancel Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void awardLoyaltyPoints(Reservation reservation, Invoice invoice) {
        Customer customer = reservation.getCustomer();
        if (customer == null) {
            return;
        }
        double paidAmount = invoice.calculatePaidAmount();
        if (paidAmount > 0) {
            customer.earnPoints(paidAmount);
        }
    }

    private void createDepositPaymentIfNeeded(MainFrame frame, Reservation reservation, RentalContract contract) {
        if (contract.getPickupPayment() != null || contract.getDepositAmount() <= 0) {
            return;
        }
        Payment depositPayment = new Payment(
                frame.appState().createNextPaymentID(),
                contract.getDepositAmount(),
                new Date(),
                PaymentPurpose.DEPOSIT,
                getCustomerID(reservation));
        depositPayment.setCustomer(reservation.getCustomer());
        depositPayment.setRentalContract(contract);
        frame.appState().addPaymentIfMissing(depositPayment);
    }

    private void createReturnPayments(MainFrame frame, Reservation reservation, RentalContract contract, Invoice invoice) {
        double additionalChargeAmount = contract.calculateAdditionalChargeAfterDeposit();
        if (additionalChargeAmount > 0) {
            Payment additionalChargePayment = new Payment(
                    frame.appState().createNextPaymentID(),
                    additionalChargeAmount,
                    new Date(),
                    PaymentPurpose.ADDITIONAL_CHARGE,
                    getCustomerID(reservation));
            additionalChargePayment.setCustomer(reservation.getCustomer());
            additionalChargePayment.setRentalContract(contract);
            additionalChargePayment.setInvoice(invoice);
            frame.appState().addPaymentIfMissing(additionalChargePayment);
        }

        double refundAmount = contract.calculateDepositRefund();
        if (refundAmount > 0) {
            Payment refundPayment = new Payment(
                    frame.appState().createNextPaymentID(),
                    refundAmount,
                    new Date(),
                    PaymentPurpose.REFUND,
                    getCustomerID(reservation));
            refundPayment.setCustomer(reservation.getCustomer());
            refundPayment.setRentalContract(contract);
            refundPayment.setInvoice(invoice);
            frame.appState().addPaymentIfMissing(refundPayment);
        }
    }

    private void addReservationPrepaymentToInvoice(Reservation reservation, Invoice invoice) {
        Payment prepayment = reservation.getPrepayment();
        if (prepayment != null && prepayment.processPayment()) {
            invoice.addPayment(prepayment);
        }
    }

    private void updateMileageInfo(JTable table, JTextField finalMileageField, JTextArea mileageInfoArea) {
        RentalContract contract = getSelectedContract(table);
        if (contract == null) {
            mileageInfoArea.setText("Select an active contract.");
            return;
        }

        Reservation reservation = contract.getReservation();
        if (reservation == null || reservation.getVehicle() == null) {
            mileageInfoArea.setText("Contract vehicle information is incomplete.");
            return;
        }

        Vehicle vehicle = reservation.getVehicle();
        int days = reservation.calculateDuration();
        int initialMileage = contract.getInitialMileage();
        String finalMileageText = finalMileageField.getText().trim();

        StringBuilder info = new StringBuilder();
        info.append("Initial mileage: ").append(initialMileage);
        info.append("\nMileage policy: ").append(vehicle.getMileagePolicy());
        info.append("\nRental days: ").append(days);

        if (vehicle.getMileagePolicy() != null && vehicle.getMileagePolicy().getDailyKmLimit() != Integer.MAX_VALUE) {
            info.append("\nIncluded km: ").append(vehicle.getMileagePolicy().getDailyKmLimit() * days);
        } else {
            info.append("\nIncluded km: Unlimited");
        }

        if (finalMileageText.isEmpty()) {
            info.append("\nFinal mileage: -");
            info.append("\nUsed km: -");
            info.append("\nExtra km: -");
            info.append("\nExtra km fee: -");
            mileageInfoArea.setText(info.toString());
            return;
        }

        try {
            int finalMileage = Integer.parseInt(finalMileageText);
            int usedKm = finalMileage - initialMileage;
            if (usedKm < 0) {
                info.append("\nFinal mileage: ").append(finalMileage);
                info.append("\nUsed km: invalid");
                info.append("\nExtra km: invalid");
                info.append("\nExtra km fee: invalid");
                mileageInfoArea.setText(info.toString());
                return;
            }

            int extraKm = calculateExtraKm(vehicle, days, usedKm);
            double extraKmFee = 0.0;
            if (vehicle.getMileagePolicy() != null) {
                extraKmFee = vehicle.getMileagePolicy().calculateExtraCharge(days, usedKm);
            }

            info.append("\nFinal mileage: ").append(finalMileage);
            info.append("\nUsed km: ").append(usedKm);
            info.append("\nExtra km: ").append(extraKm);
            info.append("\nExtra km fee: ").append(extraKmFee);
            mileageInfoArea.setText(info.toString());
        } catch (NumberFormatException exception) {
            info.append("\nFinal mileage: invalid");
            info.append("\nUsed km: invalid");
            info.append("\nExtra km: invalid");
            info.append("\nExtra km fee: invalid");
            mileageInfoArea.setText(info.toString());
        }
    }

    private int calculateExtraKm(Vehicle vehicle, int days, int usedKm) {
        if (vehicle.getMileagePolicy() == null || vehicle.getMileagePolicy().getDailyKmLimit() == Integer.MAX_VALUE) {
            return 0;
        }
        int includedKm = vehicle.getMileagePolicy().getDailyKmLimit() * days;
        int extraKm = usedKm - includedKm;
        if (extraKm < 0) {
            return 0;
        }
        return extraKm;
    }

    private boolean saveAndRefresh(MainFrame frame) {
        try {
            frame.appState().saveAll();
            refreshTables(frame);
            return true;
        } catch (FileNotFoundException exception) {
            JOptionPane.showMessageDialog(this,
                    "Data could not be saved:\n" + exception.getMessage(),
                    "Save Data",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void refreshTables(MainFrame frame) {
        fillPickupTable(frame);
        fillAllReservationTable(frame);
        fillActiveContractTable(frame);
        fillInvoiceTable(frame);
        fillDamageAssessmentTable(frame);
    }

    private void fillPickupTable(MainFrame frame) {
        pickupModel.setRowCount(0);
        Branch branch = getAgentBranch(frame);
        if (branch == null) {
            return;
        }
        for (Reservation reservation : frame.appState().getReservations()) {
            if (isSameBranch(reservation, branch)
                    && (reservation.getStatus() == ReservationStatus.PENDING
                    || reservation.getStatus() == ReservationStatus.CONFIRMED)) {
                addReservationRow(pickupModel, reservation);
            }
        }
    }

    private void fillAllReservationTable(MainFrame frame) {
        allReservationModel.setRowCount(0);
        Branch branch = getAgentBranch(frame);
        if (branch == null) {
            return;
        }
        for (Reservation reservation : frame.appState().getReservations()) {
            if (isSameBranch(reservation, branch)) {
                addReservationRow(allReservationModel, reservation);
            }
        }
    }

    private void fillActiveContractTable(MainFrame frame) {
        activeContractModel.setRowCount(0);
        Branch branch = getAgentBranch(frame);
        if (branch == null) {
            return;
        }
        for (RentalContract contract : frame.appState().getRentalContracts()) {
            Reservation reservation = contract.getReservation();
            if (contract.getStatus() == ContractStatus.ACTIVE && reservation != null
                    && isSameBranch(reservation, branch)) {
                activeContractModel.addRow(new Object[]{
                        contract.getContractID(),
                        reservation.getReservationID(),
                        getCustomerName(reservation),
                        getVehiclePlate(reservation),
                        formatDate(contract.getPickupDate()),
                        formatDate(contract.getExpectedReturnDate()),
                        contract.getStatus()
                });
            }
        }
    }

    private void fillInvoiceTable(MainFrame frame) {
        invoiceModel.setRowCount(0);
        Branch branch = getAgentBranch(frame);
        if (branch == null) {
            return;
        }
        for (Invoice invoice : frame.appState().getInvoices()) {
            RentalContract contract = invoice.getRentalContract();
            Reservation reservation = null;
            if (contract != null) {
                reservation = contract.getReservation();
            }
            if (reservation != null && isSameBranch(reservation, branch)) {
                invoiceModel.addRow(new Object[]{
                        invoice.getInvoiceID(),
                        contract.getContractID(),
                        reservation.getReservationID(),
                        getCustomerName(reservation),
                        getVehiclePlate(reservation),
                        invoice.getDiscountAmount(),
                        invoice.calculateTotal(),
                        invoice.calculatePaidAmount(),
                        getRemainingAfterDeposit(contract, invoice),
                        contract.calculateDepositRefund(),
                        contract.calculateAdditionalChargeAfterDeposit()
                });
            }
        }
    }

    private void fillDamageAssessmentTable(MainFrame frame) {
        damageAssessmentModel.setRowCount(0);
        Branch branch = getAgentBranch(frame);
        if (branch == null) {
            return;
        }
        for (DamageAssessment assessment : frame.appState().getDamageAssessments()) {
            Vehicle vehicle = assessment.getVehicle();
            if (vehicle == null || vehicle.getBranch() != branch) {
                continue;
            }
            Reservation reservation = getReservationForAssessment(assessment);
            damageAssessmentModel.addRow(new Object[]{
                    assessment.getAssessmentID(),
                    formatDate(assessment.getAssessmentDate()),
                    vehicle.getPlateNumber(),
                    getCustomerName(reservation),
                    getReservationID(reservation),
                    getInvoiceID(assessment.getInvoice()),
                    assessment.getDescription(),
                    assessment.getDamageCost()
            });
        }
    }

    private void updateInvoiceDetail(JTable table, JTextArea detailArea) {
        Invoice invoice = getSelectedInvoice(table);
        invoicePaymentModel.setRowCount(0);
        if (invoice == null) {
            detailArea.setText("Select an invoice.");
            return;
        }

        RentalContract contract = invoice.getRentalContract();
        double depositHeld = 0.0;
        double depositUsed = 0.0;
        double depositRefund = 0.0;
        double additionalAfterDeposit = 0.0;
        double remainingAfterDeposit = invoice.calculateRemainingAmount();
        if (contract != null) {
            depositHeld = contract.getDepositAmount();
            depositUsed = contract.calculateDepositUsedForExtras();
            depositRefund = contract.calculateDepositRefund();
            additionalAfterDeposit = contract.calculateAdditionalChargeAfterDeposit();
            remainingAfterDeposit = contract.calculateRemainingAmountAfterDeposit();
        }

        detailArea.setText("Invoice ID: " + invoice.getInvoiceID()
                + "\nBase amount: " + invoice.getBaseAmount()
                + "\nAddon fee: " + invoice.getAddonFee()
                + "\nDiscount: " + invoice.getDiscountAmount()
                + "\nDamage fee: " + invoice.getDamageFee()
                + "\nExtra km/additional: " + invoice.getAdditionalCharges()
                + "\nTotal: " + invoice.calculateTotal()
                + "\nPaid: " + invoice.calculatePaidAmount()
                + "\nRemaining before deposit: " + invoice.calculateRemainingAmount()
                + "\nDeposit held: " + depositHeld
                + "\nDeposit used: " + depositUsed
                + "\nDeposit refund: " + depositRefund
                + "\nAdditional after deposit: " + additionalAfterDeposit
                + "\nRemaining after deposit: " + remainingAfterDeposit);

        for (Payment payment : invoice.getPayments()) {
            addPaymentRow(payment);
        }
        if (contract != null && contract.getPickupPayment() != null
                && !invoice.getPayments().contains(contract.getPickupPayment())) {
            addPaymentRow(contract.getPickupPayment());
        }
    }

    private void addPaymentRow(Payment payment) {
        invoicePaymentModel.addRow(new Object[]{
                payment.getPaymentID(),
                formatDate(payment.getPaymentDate()),
                payment.getPaymentPurpose(),
                payment.getAmount()
        });
    }

    private void addReservationRow(DefaultTableModel model, Reservation reservation) {
        model.addRow(new Object[]{
                reservation.getReservationID(),
                getCustomerName(reservation),
                getVehiclePlate(reservation),
                getBranchName(reservation),
                formatDate(reservation.getStartDate()),
                formatDate(reservation.getEndDate()),
                reservation.getStatus(),
                getContractID(reservation),
                reservation.getPrePaymentAmount(),
                reservation.getDepositAmount()
        });
    }

    private Invoice getSelectedInvoice(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int invoiceID = (int) invoiceModel.getValueAt(modelRow, 0);
        return findInvoice(invoiceID);
    }

    private Invoice findInvoice(int invoiceID) {
        for (Invoice invoice : getRootFrame().appState().getInvoices()) {
            if (invoice.getInvoiceID() == invoiceID) {
                return invoice;
            }
        }
        return null;
    }

    private Reservation getReservationForAssessment(DamageAssessment assessment) {
        if (assessment.getInvoice() != null
                && assessment.getInvoice().getRentalContract() != null) {
            return assessment.getInvoice().getRentalContract().getReservation();
        }
        return null;
    }

    private int getReservationID(Reservation reservation) {
        if (reservation == null) {
            return 0;
        }
        return reservation.getReservationID();
    }

    private int getInvoiceID(Invoice invoice) {
        if (invoice == null) {
            return 0;
        }
        return invoice.getInvoiceID();
    }

    private RentalContract getSelectedContract(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int contractID = (int) activeContractModel.getValueAt(modelRow, 0);
        return findContract(contractID);
    }

    private RentalContract findContract(int contractID) {
        for (RentalContract contract : getRootFrame().appState().getRentalContracts()) {
            if (contract.getContractID() == contractID) {
                return contract;
            }
        }
        return null;
    }

    private Reservation getSelectedReservation(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int reservationID = (int) model.getValueAt(modelRow, 0);
        return findReservation(reservationID);
    }

    private Reservation findReservation(int reservationID) {
        for (Reservation reservation : getRootFrame().appState().getReservations()) {
            if (reservation.getReservationID() == reservationID) {
                return reservation;
            }
        }
        return null;
    }

    private MainFrame getRootFrame() {
        return (MainFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    private Branch getAgentBranch(MainFrame frame) {
        RentalAgent agent = frame.session().getRentalAgent();
        if (agent == null) {
            return null;
        }
        return agent.getBranch();
    }

    private boolean isSameBranch(Reservation reservation, Branch branch) {
        if (reservation.getVehicle() == null || reservation.getVehicle().getBranch() == null || branch == null) {
            return false;
        }
        return reservation.getVehicle().getBranch() == branch;
    }

    private String getCustomerName(Reservation reservation) {
        if (reservation == null) {
            return "-";
        }
        Customer customer = reservation.getCustomer();
        if (customer == null) {
            return "-";
        }
        return customer.getName() + " " + customer.getSurname();
    }

    private int getCustomerID(Reservation reservation) {
        Customer customer = reservation.getCustomer();
        if (customer == null) {
            return 0;
        }
        return customer.getUserID();
    }

    private String getVehiclePlate(Reservation reservation) {
        Vehicle vehicle = reservation.getVehicle();
        if (vehicle == null) {
            return "-";
        }
        return vehicle.getPlateNumber();
    }

    private String getBranchName(Reservation reservation) {
        Vehicle vehicle = reservation.getVehicle();
        if (vehicle == null || vehicle.getBranch() == null) {
            return "-";
        }
        return vehicle.getBranch().getName();
    }

    private int getContractID(Reservation reservation) {
        if (reservation.getRentalContract() == null) {
            return 0;
        }
        return reservation.getRentalContract().getContractID();
    }

    private int parseFinalMileage(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Final mileage is required.");
        }
        int finalMileage = Integer.parseInt(value.trim());
        if (finalMileage < 0) {
            throw new IllegalArgumentException("Final mileage cannot be negative.");
        }
        return finalMileage;
    }

    private double parseDamageCost(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        double damageCost = Double.parseDouble(value.trim());
        if (damageCost < 0) {
            throw new IllegalArgumentException("Damage cost cannot be negative.");
        }
        return damageCost;
    }

    private String formatInvoicePreview(Invoice invoice) {
        RentalContract contract = invoice.getRentalContract();
        double depositHeld = 0.0;
        double depositUsed = 0.0;
        double depositRefund = 0.0;
        double extraChargeAfterDeposit = 0.0;
        if (contract != null) {
            depositHeld = contract.getDepositAmount();
            depositUsed = contract.calculateDepositUsedForExtras();
            depositRefund = contract.calculateDepositRefund();
            extraChargeAfterDeposit = contract.calculateAdditionalChargeAfterDeposit();
        }

        return "Invoice ID: " + invoice.getInvoiceID()
                + "\nBase amount: " + invoice.getBaseAmount()
                + "\nAddon fee: " + invoice.getAddonFee()
                + "\nDiscount: " + invoice.getDiscountAmount()
                + "\nDamage fee: " + invoice.getDamageFee()
                + "\nAdditional charges: " + invoice.getAdditionalCharges()
                + "\nReturn extra cost: " + invoice.calculateReturnExtraCost()
                + "\nDeposit held: " + depositHeld
                + "\nDeposit used for extras: " + depositUsed
                + "\nDeposit refund: " + depositRefund
                + "\nAdditional charge after deposit: " + extraChargeAfterDeposit
                + "\nTotal amount: " + invoice.getTotalAmount()
                + "\nRemaining before deposit: " + invoice.calculateRemainingAmount()
                + "\nRemaining after deposit: " + getRemainingAfterDeposit(contract, invoice)
                + "\nInvoice and deposit are settled separately.";
    }

    private double getRemainingAfterDeposit(RentalContract contract, Invoice invoice) {
        if (contract == null) {
            return invoice.calculateRemainingAmount();
        }
        return contract.calculateRemainingAmountAfterDeposit();
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    private DefaultTableModel createReservationTableModel() {
        return new DefaultTableModel(
                new Object[]{"Reservation ID", "Customer", "Vehicle", "Branch", "Start Date", "End Date", "Status",
                        "Contract ID", "Prepayment", "Deposit"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createContractTableModel() {
        return new DefaultTableModel(
                new Object[]{"Contract ID", "Reservation ID", "Customer", "Vehicle", "Pickup Date", "Expected Return", "Status"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createInvoiceTableModel() {
        return new DefaultTableModel(
                new Object[]{"Invoice ID", "Contract ID", "Reservation ID", "Customer", "Vehicle", "Discount",
                        "Total", "Paid", "Remaining", "Deposit Refund", "Additional After Deposit"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createInvoicePaymentTableModel() {
        return new DefaultTableModel(
                new Object[]{"Payment ID", "Date", "Purpose", "Amount"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createDamageAssessmentTableModel() {
        return new DefaultTableModel(
                new Object[]{"Assessment ID", "Date", "Vehicle", "Customer", "Reservation ID", "Invoice ID",
                        "Description", "Cost"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Rental Agent",
                JOptionPane.WARNING_MESSAGE);
    }

    private static JPanel placeholder(String message) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.ITALIC, 13f));
        label.setForeground(Color.GRAY);
        p.add(label);
        return p;
    }
}
