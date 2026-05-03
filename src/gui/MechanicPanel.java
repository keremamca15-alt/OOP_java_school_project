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

public class MechanicPanel extends JPanel {

    public MechanicPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Maintenance Queue",  placeholder("Pending maintenance tasks assigned to you will appear here."));
        tabs.addTab("Schedule Task",       placeholder("Create and schedule a new maintenance task for a vehicle."));
        tabs.addTab("Completed Tasks",     placeholder("History of completed maintenance tasks will appear here."));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(160, 80, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Mechanic Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String username = frame.session().getUsername();
        JLabel user = new JLabel("Mechanic: " + username);
        user.setForeground(new Color(255, 220, 180));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(user);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> frame.showWelcome());
        right.add(logoutBtn);

        JButton switchRoleBtn = new JButton("Switch User");
        switchRoleBtn.addActionListener(e -> frame.showWelcome());
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
