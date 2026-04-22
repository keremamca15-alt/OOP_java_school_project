import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class UiHelper {
    private UiHelper() {
    }

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 45, 30, 45));
        panel.setBackground(new Color(245, 247, 250));
        return panel;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setForeground(new Color(35, 45, 60));
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setForeground(new Color(85, 96, 110));
        return label;
    }

    public static JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(220, 45));
        return button;
    }

    public static JPanel createButtonGrid(int rows, int columns) {
        JPanel panel = new JPanel(new GridLayout(rows, columns, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 90, 10, 90));
        return panel;
    }

    public static void showPlaceholder(Component parent, String featureName) {
        // Dashboard buttons use placeholders until real business logic is added.
        JOptionPane.showMessageDialog(
                parent,
                featureName + " screen will be implemented later.",
                "Demo Feature",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
