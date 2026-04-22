import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                System.out.println("Using default Swing look and feel.");
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
