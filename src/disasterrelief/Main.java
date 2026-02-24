package disasterrelief;

import javax.swing.*;

/**
 * Entry point — sets the look-and-feel and launches the main window.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Use Nimbus look-and-feel for a modern appearance
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Override Nimbus defaults for our dark theme
            UIManager.put("control",                new java.awt.Color(30, 30, 52));
            UIManager.put("nimbusBase",             new java.awt.Color(18, 18, 40));
            UIManager.put("nimbusFocus",            new java.awt.Color(233, 69, 96));
            UIManager.put("nimbusLightBackground",  new java.awt.Color(26, 26, 46));
            UIManager.put("text",                   new java.awt.Color(230, 230, 250));
            UIManager.put("nimbusSelectionBackground", new java.awt.Color(15, 52, 96));
        } catch (Exception e) {
            // fallback to system L&F
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            // Ensure database and tables exist
            DatabaseManager.getInstance().initializeDatabase();
            
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
