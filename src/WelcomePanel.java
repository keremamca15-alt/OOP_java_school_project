import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomePanel extends JPanel {
    public WelcomePanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        JPanel page = UiHelper.createPagePanel();
        add(page, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel companyLabel = UiHelper.createTitleLabel("Kerem Auto Rental");
        JLabel appLabel = UiHelper.createSubtitleLabel("Car Rental Agency Management System");
        JLabel instructionLabel = UiHelper.createSubtitleLabel("Select your role to continue");

        headerPanel.add(companyLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(appLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(instructionLabel);

        JPanel buttonPanel = UiHelper.createButtonGrid(3, 1);

        JButton customerButton = UiHelper.createMainButton(MainFrame.ROLE_CUSTOMER);
        JButton agentButton = UiHelper.createMainButton(MainFrame.ROLE_AGENT);
        JButton managerButton = UiHelper.createMainButton(MainFrame.ROLE_MANAGER);

        customerButton.addActionListener(event -> mainFrame.showUserSelectionScreen(MainFrame.ROLE_CUSTOMER));
        agentButton.addActionListener(event -> mainFrame.showUserSelectionScreen(MainFrame.ROLE_AGENT));
        managerButton.addActionListener(event -> mainFrame.showUserSelectionScreen(MainFrame.ROLE_MANAGER));

        buttonPanel.add(customerButton);
        buttonPanel.add(agentButton);
        buttonPanel.add(managerButton);

        page.add(headerPanel, BorderLayout.NORTH);
        page.add(buttonPanel, BorderLayout.CENTER);
    }
}
