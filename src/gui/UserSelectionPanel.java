import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserSelectionPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SessionManager sessionManager;
    private final JLabel roleLabel;
    private final JComboBox<String> userComboBox;
    private final Map<String, String[]> sampleUsers;

    public UserSelectionPanel(MainFrame mainFrame, SessionManager sessionManager) {
        this.mainFrame = mainFrame;
        this.sessionManager = sessionManager;
        this.sampleUsers = createSampleUsers();

        setLayout(new BorderLayout());

        JPanel page = UiHelper.createPagePanel();
        add(page, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        roleLabel = UiHelper.createTitleLabel("");
        JLabel instructionLabel = UiHelper.createSubtitleLabel("Choose a sample user for this demo session");
        userComboBox = new JComboBox<>();
        userComboBox.setMaximumSize(userComboBox.getPreferredSize());

        centerPanel.add(roleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(instructionLabel);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(userComboBox);

        JPanel buttonPanel = UiHelper.createButtonGrid(1, 2);
        JButton backButton = UiHelper.createMainButton("Back");
        JButton loginButton = UiHelper.createMainButton("Login");

        backButton.addActionListener(event -> mainFrame.showWelcomeScreen());
        loginButton.addActionListener(event -> {
            String selectedUser = (String) userComboBox.getSelectedItem();
            if (selectedUser != null) {
                mainFrame.loginSelectedUser(selectedUser);
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        page.add(centerPanel, BorderLayout.CENTER);
        page.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshUsers() {
        String role = sessionManager.getSelectedRole();
        roleLabel.setText(role + " Login");
        userComboBox.setModel(new DefaultComboBoxModel<>(sampleUsers.get(role)));
    }

    private Map<String, String[]> createSampleUsers() {
        // These mocked lists replace real username/password authentication for the prototype.
        Map<String, String[]> users = new LinkedHashMap<>();
        users.put(MainFrame.ROLE_CUSTOMER, new String[]{"Kerem Yilmaz", "Ayse Demir"});
        users.put(MainFrame.ROLE_AGENT, new String[]{"Ali Kaya", "Ece Polat"});
        users.put(MainFrame.ROLE_MANAGER, new String[]{"Zeynep Acar", "Mert Can"});
        return users;
    }
}
