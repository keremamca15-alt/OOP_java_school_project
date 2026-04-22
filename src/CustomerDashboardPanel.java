import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomerDashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SessionManager sessionManager;
    private final JLabel headerLabel;

    public CustomerDashboardPanel(MainFrame mainFrame, SessionManager sessionManager) {
        this.mainFrame = mainFrame;
        this.sessionManager = sessionManager;

        setLayout(new BorderLayout());

        JPanel page = UiHelper.createPagePanel();
        add(page, BorderLayout.CENTER);

        headerLabel = UiHelper.createTitleLabel("");
        page.add(headerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = UiHelper.createButtonGrid(5, 1);
        addDashboardButton(buttonPanel, "Search Vehicles");
        addDashboardButton(buttonPanel, "Make Reservation");
        addDashboardButton(buttonPanel, "View My Reservations");
        addDashboardButton(buttonPanel, "View Loyalty Info");

        JButton logoutButton = UiHelper.createMainButton("Logout");
        logoutButton.addActionListener(event -> mainFrame.showWelcomeScreen());
        buttonPanel.add(logoutButton);

        page.add(buttonPanel, BorderLayout.CENTER);
    }

    public void refreshHeader() {
        headerLabel.setText("Customer Dashboard - " + sessionManager.getSelectedUser());
    }

    private void addDashboardButton(JPanel panel, String buttonText) {
        JButton button = UiHelper.createMainButton(buttonText);
        button.addActionListener(event -> UiHelper.showPlaceholder(this, buttonText));
        panel.add(button);
    }
}
