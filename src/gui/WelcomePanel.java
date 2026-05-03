package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225), 1),
                BorderFactory.createEmptyBorder(40, 48, 40, 48)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(400, 500));

        JLabel title = new JLabel("Car Rental Agency");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 32, 0));

        JLabel usernameLabel = new JLabel("Email or full name");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 12f));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(loginBtn.getFont().deriveFont(Font.BOLD, 13f));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        loginBtn.setBackground(new Color(50, 100, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an email or full name.", "Login", JOptionPane.WARNING_MESSAGE);
                return;
            }
            frame.login(username);
        });

        JPanel orRow = new JPanel();
        orRow.setLayout(new BoxLayout(orRow, BoxLayout.X_AXIS));
        orRow.setOpaque(false);
        orRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        JSeparator sepLeft  = new JSeparator();
        JSeparator sepRight = new JSeparator();
        JLabel orLabel = new JLabel("  or  ");
        orLabel.setForeground(Color.GRAY);
        orLabel.setFont(orLabel.getFont().deriveFont(11f));
        orRow.add(sepLeft);
        orRow.add(orLabel);
        orRow.add(sepRight);

        JButton guestBtn = new JButton("Continue as Guest");
        guestBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        guestBtn.addActionListener(e -> frame.showDashboard(Session.ROLE_GUEST));

        card.add(title);
        card.add(subtitle);
        card.add(usernameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(orRow);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(guestBtn);

        add(card);
    }
}
