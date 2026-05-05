package gui;

import core.Customer;
import core.Employee;
import core.LoyaltyTier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.FileNotFoundException;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(232, 236, 241));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Color.WHITE);
        shell.setPreferredSize(new Dimension(520, 480));
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 211, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel brandBand = new JPanel(new BorderLayout());
        brandBand.setBackground(new Color(28, 54, 86));
        brandBand.setBorder(BorderFactory.createEmptyBorder(30, 38, 28, 38));

        JLabel brand = new JLabel("Kepler");
        brand.setForeground(Color.WHITE);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 34f));

        JLabel product = new JLabel("Car Rental Management");
        product.setForeground(new Color(204, 218, 236));
        product.setFont(product.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.add(brand);
        brandText.add(Box.createRigidArea(new Dimension(0, 4)));
        brandText.add(product);
        brandBand.add(brandText, BorderLayout.WEST);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(34, 42, 36, 42));

        JLabel loginTitle = new JLabel("Sign in");
        loginTitle.setFont(loginTitle.getFont().deriveFont(Font.BOLD, 22f));
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Email or full name");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 12f));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(loginBtn.getFont().deriveFont(Font.BOLD, 13f));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        loginBtn.setBackground(new Color(28, 92, 166));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter an email or full name.",
                        "Login",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            frame.login(username);
        });

        JButton registerBtn = new JButton("Register Customer");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> registerCustomer(frame));

        JPanel separatorRow = new JPanel();
        separatorRow.setLayout(new BoxLayout(separatorRow, BoxLayout.X_AXIS));
        separatorRow.setOpaque(false);
        separatorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        separatorRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        JLabel orLabel = new JLabel("  or  ");
        orLabel.setForeground(new Color(110, 118, 130));
        separatorRow.add(new JSeparator());
        separatorRow.add(orLabel);
        separatorRow.add(new JSeparator());

        JButton guestBtn = new JButton("Continue as Guest");
        guestBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        guestBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        guestBtn.setFocusPainted(false);
        guestBtn.addActionListener(e -> frame.showDashboard(Session.ROLE_GUEST));

        form.add(loginTitle);
        form.add(Box.createRigidArea(new Dimension(0, 22)));
        form.add(usernameLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(usernameField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(loginBtn);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(registerBtn);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(separatorRow);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(guestBtn);

        shell.add(brandBand, BorderLayout.NORTH);
        shell.add(form, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        add(shell, gbc);
    }

    private void registerCustomer(MainFrame frame) {
        JTextField nameField = new JTextField(16);
        JTextField surnameField = new JTextField(16);
        JTextField emailField = new JTextField(22);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Surname"));
        form.add(surnameField);
        form.add(new JLabel("Email"));
        form.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, form, "Register Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, surname and email cannot be empty.",
                    "Register",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isEmailUsed(frame, email)) {
            JOptionPane.showMessageDialog(this,
                    "This email is already used.",
                    "Register",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isFullNameUsed(frame, name, surname)) {
            JOptionPane.showMessageDialog(this,
                    "This full name is already used.",
                    "Register",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Customer customer = new Customer(
                    frame.appState().createNextUserID(),
                    name,
                    surname,
                    email,
                    0,
                    LoyaltyTier.BRONZE);
            frame.appState().addCustomerIfMissing(customer);
            frame.appState().saveAll();
            frame.session().clear();
            frame.session().setUsername(customer.getName() + " " + customer.getSurname());
            frame.session().setCustomer(customer);
            frame.showDashboard(Session.ROLE_CUSTOMER);
        } catch (FileNotFoundException exception) {
            JOptionPane.showMessageDialog(this,
                    "Customer registered but data could not be saved:\n" + exception.getMessage(),
                    "Register",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this,
                    exception.getMessage(),
                    "Register",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isEmailUsed(MainFrame frame, String email) {
        for (Customer customer : frame.appState().getCustomers()) {
            if (customer.getEmail() != null && customer.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        for (Employee employee : frame.appState().getEmployees()) {
            if (employee.getEmail() != null && employee.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFullNameUsed(MainFrame frame, String name, String surname) {
        String fullName = normalize(name + " " + surname);
        for (Customer customer : frame.appState().getCustomers()) {
            if (normalize(customer.getName() + " " + customer.getSurname()).equals(fullName)) {
                return true;
            }
        }
        for (Employee employee : frame.appState().getEmployees()) {
            if (normalize(employee.getName() + " " + employee.getSurname()).equals(fullName)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
