import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_AGENT = "Rental Agent";
    public static final String ROLE_MANAGER = "Branch Manager";

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final SessionManager sessionManager;

    private final WelcomePanel welcomePanel;
    private final UserSelectionPanel userSelectionPanel;
    private final CustomerDashboardPanel customerDashboardPanel;
    private final RentalAgentDashboardPanel rentalAgentDashboardPanel;
    private final BranchManagerDashboardPanel branchManagerDashboardPanel;

    public MainFrame() {
        super("Car Rental Agency Management System");

        // SessionManager stores the current demo role/user instead of real authentication data.
        sessionManager = new SessionManager();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        welcomePanel = new WelcomePanel(this);
        userSelectionPanel = new UserSelectionPanel(this, sessionManager);
        customerDashboardPanel = new CustomerDashboardPanel(this, sessionManager);
        rentalAgentDashboardPanel = new RentalAgentDashboardPanel(this, sessionManager);
        branchManagerDashboardPanel = new BranchManagerDashboardPanel(this, sessionManager);

        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(userSelectionPanel, "userSelection");
        contentPanel.add(customerDashboardPanel, "customerDashboard");
        contentPanel.add(rentalAgentDashboardPanel, "agentDashboard");
        contentPanel.add(branchManagerDashboardPanel, "managerDashboard");

        add(contentPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
    }

    public void showWelcomeScreen() {
        // Returning to the welcome screen clears the pseudo-login session.
        sessionManager.logout();
        cardLayout.show(contentPanel, "welcome");
    }

    public void showUserSelectionScreen(String role) {
        sessionManager.selectRole(role);
        userSelectionPanel.refreshUsers();
        cardLayout.show(contentPanel, "userSelection");
    }

    public void loginSelectedUser(String user) {
        sessionManager.loginAs(user);

        // The selected role decides which dashboard card is shown after login.
        if (ROLE_CUSTOMER.equals(sessionManager.getSelectedRole())) {
            customerDashboardPanel.refreshHeader();
            cardLayout.show(contentPanel, "customerDashboard");
        } else if (ROLE_AGENT.equals(sessionManager.getSelectedRole())) {
            rentalAgentDashboardPanel.refreshHeader();
            cardLayout.show(contentPanel, "agentDashboard");
        } else if (ROLE_MANAGER.equals(sessionManager.getSelectedRole())) {
            branchManagerDashboardPanel.refreshHeader();
            cardLayout.show(contentPanel, "managerDashboard");
        }
    }
}
