package gui;

import core.Branch;
import core.BranchNotFoundException;
import core.Customer;
import core.Vehicle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GuestPanel extends JPanel {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        DATE_FORMAT.setLenient(false);
    }

    public GuestPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Vehicles",  buildBrowseTab(frame));
        tabs.addTab("Our Branches",     buildBranchesTab(frame));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Kepler - Guest");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton loginBtn = new JButton("Login / Sign In");
        loginBtn.addActionListener(e -> frame.showWelcome());
        right.add(loginBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBrowseTab(MainFrame frame) {
        JPanel tab = new JPanel(new BorderLayout(12, 12));
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<Branch> branchCombo = new JComboBox<>();
        for (Branch branch : frame.appState().getBranches()) {
            branchCombo.addItem(branch);
        }

        JTextField startDateField = new JTextField(formatDate(daysFromToday(1)), 10);
        JTextField endDateField = new JTextField(formatDate(daysFromToday(4)), 10);
        JLabel statusLabel = new JLabel("Enter a date range and search available vehicles.");

        DefaultTableModel model = createVehicleTableModel();
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

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
                Customer guestSearch = new Customer();
                ArrayList<Vehicle> vehicles = guestSearch.searchAvailableVehicles(branch, startDate, endDate);
                fillVehicleTable(model, vehicles);
                statusLabel.setText(vehicles.size() + " available vehicles found.");
            } catch (ParseException exception) {
                JOptionPane.showMessageDialog(this,
                        "Date format must be yyyy-MM-dd.",
                        "Invalid date",
                        JOptionPane.WARNING_MESSAGE);
            } catch (BranchNotFoundException | IllegalArgumentException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(this,
                        exception.getMessage(),
                        "Search failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton reserveButton = new JButton("Reserve Selected Vehicle");
        reserveButton.addActionListener(e -> {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a vehicle first.",
                        "Reservation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Please login or register to make a reservation.",
                    "Login required",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.showWelcome();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controls.add(new JLabel("Branch:"));
        controls.add(branchCombo);
        controls.add(new JLabel("Start Date:"));
        controls.add(startDateField);
        controls.add(new JLabel("End Date:"));
        controls.add(endDateField);
        controls.add(searchButton);
        controls.add(reserveButton);

        tab.add(controls, BorderLayout.NORTH);
        tab.add(new JScrollPane(table), BorderLayout.CENTER);
        tab.add(statusLabel, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildBranchesTab(MainFrame frame) {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Branch branch : frame.appState().getBranches()) {
            model.addRow(new Object[]{branch.getBranchID(), branch.getName(), branch.getAddress()});
        }

        tab.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return tab;
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

    private Date parseDate(String value) throws ParseException {
        return DATE_FORMAT.parse(value.trim());
    }

    private String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    private Date daysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}
