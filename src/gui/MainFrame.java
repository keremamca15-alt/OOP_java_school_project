package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Component;

public class MainFrame extends JFrame {

    private final Session session = new Session();
    private final CardLayout layout = new CardLayout();
    private final JPanel cards = new JPanel(layout);

    public MainFrame() {
        super("Car Rental Agency");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        cards.add(new WelcomePanel(this), "welcome");
        setContentPane(cards);
        showWelcome();
    }

    public Session session() { return session; }

    public void showWelcome() {
        session.clear();
        layout.show(cards, "welcome");
    }

    public void showRoleSelection() {
        show("roleSelection", new RoleSelectionPanel(this));
    }

    public void showDashboard(String role) {
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
}
