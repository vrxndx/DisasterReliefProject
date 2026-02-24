package disasterrelief;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with a tabbed pane for every module.
 */
public class MainFrame extends JFrame {

    // ── colour palette ───────────────────────────────────────
    public static final Color BG_DARK      = new Color(26, 26, 46);   // #1a1a2e
    public static final Color BG_PANEL     = new Color(22, 33, 62);   // #16213e
    public static final Color ACCENT       = new Color(15, 52, 96);   // #0f3460
    public static final Color PRIMARY      = new Color(233, 69, 96);  // #e94560
    public static final Color TEXT_LIGHT   = new Color(230, 230, 250);
    public static final Color TEXT_DIM     = new Color(160, 170, 200);
    public static final Color TABLE_ALT    = new Color(30, 40, 70);
    public static final Color SUCCESS      = new Color(46, 204, 113);
    public static final Color WARNING      = new Color(241, 196, 15);
    public static final Color DANGER       = new Color(231, 76, 60);

    public MainFrame() {
        setTitle("Disaster Relief Management System");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        // ── header ──────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 15, 35));
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel logo = new JLabel("\u26A0  Disaster Relief Management System");
        logo.setForeground(PRIMARY);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(logo, BorderLayout.WEST);

        JLabel tagline = new JLabel("Coordinate \u2022 Respond \u2022 Recover  ");
        tagline.setForeground(TEXT_DIM);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        header.add(tagline, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── tabbed pane ─────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_LIGHT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setOpaque(true);

        tabs.addTab("  \uD83D\uDCCA  Dashboard     ", new DashboardPanel());
        tabs.addTab("  \uD83D\uDD25  Incidents     ", new IncidentPanel());
        tabs.addTab("  \uD83D\uDC65  Victims       ", new VictimPanel());
        tabs.addTab("  \uD83D\uDCE9  Relief Requests", new ReliefRequestPanel());
        tabs.addTab("  \uD83C\uDFE5  Centers & Teams", new CenterTeamPanel());
        tabs.addTab("  \uD83D\uDCE6  Resources     ", new ResourcePanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ── utility: create styled buttons used across panels ────
    public static JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField styledField(String placeholder) {
        JTextField field = new JTextField(18);
        field.setBackground(new Color(35, 45, 75));
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setToolTipText(placeholder);
        return field;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(new Color(35, 45, 75));
        combo.setForeground(TEXT_LIGHT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return combo;
    }

    public static JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    public static JPanel cardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 55, 90), 1),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setForeground(PRIMARY);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            card.add(lbl, BorderLayout.NORTH);
        }
        return card;
    }
}
