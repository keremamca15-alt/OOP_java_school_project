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

public class CustomerPanel extends JPanel {

    public CustomerPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search Vehicles",   placeholder("Vehicle search form and results will appear here."));
        tabs.addTab("My Reservations",   placeholder("Your active and past reservations will appear here."));
        tabs.addTab("My Invoices",        placeholder("Your invoices and payment history will appear here."));
        tabs.addTab("Loyalty & Profile",  buildLoyaltyTab(frame));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 70, 160));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        String username = frame.session().getUsername();
        JLabel user = new JLabel("Logged in as: " + username);
        user.setForeground(new Color(200, 220, 255));

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

    private JPanel buildLoyaltyTab(MainFrame frame) {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel info = new JPanel();
        info.setLayout(new javax.swing.BoxLayout(info, javax.swing.BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createTitledBorder("Loyalty Status"));

        info.add(new JLabel("Username: " + frame.session().getUsername()));
        info.add(new JLabel(" "));
        info.add(new JLabel("Loyalty Tier:   [will display tier]"));
        info.add(new JLabel("Loyalty Points: [will display points]"));
        info.add(new JLabel(" "));
        info.add(new JLabel("Tier benefits and point redemption options will appear here."));

        tab.add(info);
        return tab;
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
