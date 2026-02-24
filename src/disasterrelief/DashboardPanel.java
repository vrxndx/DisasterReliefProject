package disasterrelief;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard — shows high-level statistics in styled cards.
 */
public class DashboardPanel extends JPanel {

    private JLabel incidentCount, victimCount, requestCount, pendingCount,
                   centerCount, teamCount, resourceCount;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ── title row ───────────────────────────────────────
        JLabel title = new JLabel("\uD83D\uDCCA  Dashboard Overview");
        title.setForeground(MainFrame.TEXT_LIGHT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // ── stats grid ──────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 4, 18, 18));
        grid.setOpaque(false);

        incidentCount  = addStatCard(grid, "\uD83D\uDD25", "Total Incidents",   "0", new Color(231, 76, 60));
        victimCount    = addStatCard(grid, "\uD83D\uDC65", "Registered Victims","0", new Color(52, 152, 219));
        requestCount   = addStatCard(grid, "\uD83D\uDCE9", "Relief Requests",   "0", new Color(46, 204, 113));
        pendingCount   = addStatCard(grid, "\u23F3",       "Pending Requests",  "0", new Color(241, 196, 15));
        centerCount    = addStatCard(grid, "\uD83C\uDFE5", "Relief Centers",    "0", new Color(155, 89, 182));
        teamCount      = addStatCard(grid, "\uD83D\uDEE1", "Active Teams",      "0", new Color(26, 188, 156));
        resourceCount  = addStatCard(grid, "\uD83D\uDCE6", "Resource Items",    "0", new Color(230, 126, 34));
        addInfoCard(grid);

        add(grid, BorderLayout.CENTER);

        // ── refresh button ──────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton refresh = MainFrame.styledButton("\u21BB  Refresh", MainFrame.ACCENT);
        refresh.addActionListener(e -> loadStats());
        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);

        loadStats();
    }

    private JLabel addStatCard(JPanel parent, String icon, String label, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent.darker(), 1),
            BorderFactory.createEmptyBorder(24, 20, 24, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(accent);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setForeground(MainFrame.TEXT_DIM);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(nameLabel);

        parent.add(card);
        return valueLabel;
    }

    private void addInfoCard(JPanel parent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(20, 28, 55));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.PRIMARY.darker(), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLbl = new JLabel("\u2139  Quick Info");
        titleLbl.setForeground(MainFrame.PRIMARY);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        card.add(titleLbl, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
            "Use the tabs on the left to manage\n" +
            "incidents, victims, requests, relief\n" +
            "centers, teams, and resources.\n\n" +
            "All data is stored in MySQL."
        );
        info.setEditable(false);
        info.setOpaque(false);
        info.setForeground(MainFrame.TEXT_DIM);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(info, BorderLayout.CENTER);

        parent.add(card);
    }

    private void loadStats() {
        DatabaseManager db = DatabaseManager.getInstance();
        incidentCount.setText(String.valueOf(db.getCount("incidents")));
        victimCount.setText(String.valueOf(db.getCount("victims")));
        requestCount.setText(String.valueOf(db.getCount("relief_requests")));
        pendingCount.setText(String.valueOf(db.getPendingRequestCount()));
        centerCount.setText(String.valueOf(db.getCount("centers")));
        teamCount.setText(String.valueOf(db.getCount("teams")));
        resourceCount.setText(String.valueOf(db.getCount("resources")));
    }
}
