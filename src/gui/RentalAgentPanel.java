package gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

public class RentalAgentPanel extends JPanel {

    public RentalAgentPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("New Reservation",    placeholder("Reservation creation form will appear here."));
        tabs.addTab("Process Pickup",     placeholder("Select a confirmed reservation to process vehicle pickup."));
        tabs.addTab("Process Return",     placeholder("Select an active contract to process vehicle return and generate invoice."));
        tabs.addTab("Damage Assessment",  placeholder("Assess vehicle damage and attach to a return invoice."));
        tabs.addTab("All Reservations",   placeholder("Full reservation list with filter and status management."));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 110, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Rental Agent Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String username = frame.session().getUsername();
        JLabel user = new JLabel("Agent: " + username);
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

        JButton switchRoleBtn = new JButton("Switch Role");
        switchRoleBtn.addActionListener(e -> frame.showRoleSelection());
        right.add(switchRoleBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
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
