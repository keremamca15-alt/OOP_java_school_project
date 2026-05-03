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

public class GuestPanel extends JPanel {

    public GuestPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(buildHeader(frame), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Vehicles",  placeholder("Available vehicles and pricing will be shown here (read-only)."));
        tabs.addTab("Our Branches",     placeholder("Branch locations and contact information will appear here."));
        tabs.addTab("About",            buildAboutTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame frame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Car Rental Agency — Guest View");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.WHITE);

        JLabel note = new JLabel("Browsing as guest (read-only)");
        note.setForeground(new Color(190, 190, 190));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(note);

        JButton loginBtn = new JButton("Login / Sign In");
        loginBtn.addActionListener(e -> frame.showWelcome());
        right.add(loginBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildAboutTab() {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel info = new JPanel();
        info.setLayout(new javax.swing.BoxLayout(info, javax.swing.BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createTitledBorder("About Us"));

        info.add(new JLabel("Welcome to Car Rental Agency!"));
        info.add(new JLabel(" "));
        info.add(new JLabel("We offer Economy, SUV, Luxury and Van vehicle options"));
        info.add(new JLabel("across multiple branch locations."));
        info.add(new JLabel(" "));
        info.add(new JLabel("Create an account or log in to make reservations,"));
        info.add(new JLabel("earn loyalty points and access exclusive member discounts."));

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
