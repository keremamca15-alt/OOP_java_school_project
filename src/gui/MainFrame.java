package gui;

import core.BranchManager;
import core.Customer;
import core.Employee;
import core.Mechanic;
import core.RentalAgent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Component;

public class MainFrame extends JFrame {

    private final Session session = new Session();
    private final AppState appState = new AppState();
    private final CardLayout layout = new CardLayout();
    private final JPanel cards = new JPanel(layout);
    private boolean dataLoaded;
    private String loadErrorMessage;

    public MainFrame() {
        super("Car Rental Agency");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 820);
        setLocationRelativeTo(null);

        cards.add(new WelcomePanel(this), "welcome");
        setContentPane(cards);
        loadData();
        showWelcome();
    }

    public Session session() { return session; }
    public AppState appState() { return appState; }

    public void showWelcome() {
        session.clear();
        layout.show(cards, "welcome");
    }

    public void login(String input) {
        if (!ensureDataLoaded()) {
            return;
        }

        Customer customer = appState.findCustomerForLogin(input);
        if (customer != null) {
            session.clear();
            session.setUsername(customer.getName() + " " + customer.getSurname());
            session.setCustomer(customer);
            showDashboard(Session.ROLE_CUSTOMER);
            return;
        }

        Employee employee = appState.findEmployeeForLogin(input);
        if (employee != null) {
            session.clear();
            session.setUsername(employee.getName() + " " + employee.getSurname());
            setEmployeeSession(employee);
            showDashboard(session.getRole());
            return;
        }

        JOptionPane.showMessageDialog(this,
                "No customer or employee found for: " + input,
                "Login failed",
                JOptionPane.WARNING_MESSAGE);
    }

    public void showDashboard(String role) {
        if (!ensureDataLoaded()) {
            return;
        }
        session.setRole(role);
        switch (role) {
            case Session.ROLE_CUSTOMER -> show("customerPanel",     new CustomerPanel(this));
            case Session.ROLE_AGENT    -> show("rentalAgentPanel",  new RentalAgentPanel(this));
            case Session.ROLE_MANAGER  -> show("branchManagerPanel", new BranchManagerPanel(this));
            case Session.ROLE_MECHANIC -> show("mechanicPanel",     new MechanicPanel(this));
            case Session.ROLE_GUEST    -> show("guestPanel",        new GuestPanel(this));
        }
    }

    private void show(String name, JPanel panel) {
        for (Component c : cards.getComponents()) {
            if (name.equals(c.getName())) {
                cards.remove(c);
                break;
            }
        }
        panel.setName(name);
        cards.add(panel, name);
        layout.show(cards, name);
    }

    private void loadData() {
        try {
            appState.loadAll();
            dataLoaded = true;
            loadErrorMessage = null;
        } catch (Exception exception) {
            dataLoaded = false;
            loadErrorMessage = exception.getMessage();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "Data could not be loaded:\n" + loadErrorMessage,
                    "Data load error",
                    JOptionPane.ERROR_MESSAGE));
        }
    }

    private boolean ensureDataLoaded() {
        if (dataLoaded) {
            return true;
        }
        JOptionPane.showMessageDialog(this,
                "Data is not loaded. Dashboards are unavailable.\n" + loadErrorMessage,
                "Data unavailable",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void setEmployeeSession(Employee employee) {
        if (employee instanceof RentalAgent) {
            session.setRole(Session.ROLE_AGENT);
            session.setRentalAgent((RentalAgent) employee);
        } else if (employee instanceof BranchManager) {
            session.setRole(Session.ROLE_MANAGER);
            session.setBranchManager((BranchManager) employee);
        } else if (employee instanceof Mechanic) {
            session.setRole(Session.ROLE_MECHANIC);
            session.setMechanic((Mechanic) employee);
        }
    }
}
