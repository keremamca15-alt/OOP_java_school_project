package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

public class RoleSelectionPanel extends JPanel {

    public RoleSelectionPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225), 1),
                BorderFactory.createEmptyBorder(40, 48, 40, 48)));
        card.setBackground(Color.WHITE);

        String username = frame.session().getUsername();
        JLabel greeting = new JLabel("Welcome, " + username + "!");
        greeting.setFont(greeting.getFont().deriveFont(Font.BOLD, 22f));
        greeting.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Select your role to continue");
        sub.setFont(sub.getFont().deriveFont(13f));
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 30, 0));

        card.add(greeting);
        card.add(sub);
        card.add(roleButton(frame, Session.ROLE_CUSTOMER, "Browse vehicles, make reservations, view invoices"));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(roleButton(frame, Session.ROLE_AGENT, "Process pickups, returns and damage assessments"));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(roleButton(frame, Session.ROLE_MANAGER, "Manage fleet, employees and branch reports"));
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(roleButton(frame, Session.ROLE_MECHANIC, "View and manage vehicle maintenance queue"));
        card.add(Box.createRigidArea(new Dimension(0, 28)));

        JButton backBtn = new JButton("Back to Login");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> frame.showWelcome());
        card.add(backBtn);

        add(card);
    }

    private JButton roleButton(MainFrame frame, String role, String description) {
        JButton btn = new JButton(
                "<html><b style='font-size:12px'>" + role + "</b>" +
                "<br><span style='color:#666;font-size:10px'>" + description + "</span></html>");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(380, 60));
        btn.setPreferredSize(new Dimension(380, 60));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btn.addActionListener(e -> frame.showDashboard(role));
        return btn;
    }
}
