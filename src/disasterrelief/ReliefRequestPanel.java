package disasterrelief;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

/**
 * Relief Request management — create requests + update status.
 */
public class ReliefRequestPanel extends JPanel {

    private JComboBox<String> victimCombo, priorityCombo;
    private JTextArea needsArea;
    private DefaultTableModel tableModel;
    private List<String[]> victimList;

    public ReliefRequestPanel() {
        setLayout(new BorderLayout(16, 0));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── form ────────────────────────────────────────────
        JPanel form = MainFrame.cardPanel("\uD83D\uDCE9  Submit Relief Request");
        form.setPreferredSize(new Dimension(340, 0));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,0,6,0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; gbc.gridx = 0;
        int row = 0;

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Victim"), gbc);
        gbc.gridy = row++; victimCombo = new JComboBox<>(); refreshVictimCombo();
        victimCombo.setBackground(new Color(35,45,75));
        victimCombo.setForeground(MainFrame.TEXT_LIGHT);
        fields.add(victimCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Priority"), gbc);
        gbc.gridy = row++; priorityCombo = MainFrame.styledCombo(new String[]{
            "Normal","Urgent","Critical"
        }); fields.add(priorityCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Needs Description"), gbc);
        gbc.gridy = row++;
        needsArea = new JTextArea(4, 18);
        needsArea.setBackground(new Color(35,45,75));
        needsArea.setForeground(MainFrame.TEXT_LIGHT);
        needsArea.setCaretColor(MainFrame.TEXT_LIGHT);
        needsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        needsArea.setLineWrap(true);
        needsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.ACCENT, 1),
            BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        fields.add(new JScrollPane(needsArea), gbc);

        gbc.gridy = row; gbc.insets = new Insets(16,0,0,0);
        JButton addBtn = MainFrame.styledButton("+ Submit Request", MainFrame.PRIMARY);
        addBtn.addActionListener(e -> addRequest());
        fields.add(addBtn, gbc);

        form.add(fields, BorderLayout.CENTER);
        add(form, BorderLayout.WEST);

        // ── table ───────────────────────────────────────────
        JPanel tableCard = MainFrame.cardPanel("\uD83D\uDCCB  Relief Requests");
        String[] cols = {"ID","Victim","Needs","Priority","Status","Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = IncidentPanel.createStyledTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(MainFrame.BG_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bot.setOpaque(false);

        // status update buttons
        JButton markProgress = MainFrame.styledButton("Mark In-Progress", MainFrame.WARNING);
        markProgress.addActionListener(e -> updateStatus(table, "In Progress"));
        JButton markDone = MainFrame.styledButton("Mark Completed", MainFrame.SUCCESS);
        markDone.addActionListener(e -> updateStatus(table, "Completed"));
        JButton del = MainFrame.styledButton("Delete", MainFrame.DANGER);
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            try { DatabaseManager.getInstance().deleteRequest(
                Integer.parseInt(tableModel.getValueAt(r, 0).toString())); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
        JButton ref = MainFrame.styledButton("\u21BB Refresh", MainFrame.ACCENT);
        ref.addActionListener(e -> { loadData(); refreshVictimCombo(); });
        bot.add(markProgress); bot.add(markDone); bot.add(del); bot.add(ref);
        tableCard.add(bot, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
        loadData();
    }

    private void refreshVictimCombo() {
        victimList = DatabaseManager.getInstance().getVictimList();
        if (victimCombo != null) {
            victimCombo.removeAllItems();
            for (String[] s : victimList) victimCombo.addItem(s[0] + " - " + s[1]);
        }
    }

    private void addRequest() {
        if (victimList == null || victimList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Register a victim first."); return;
        }
        try {
            int idx = victimCombo.getSelectedIndex();
            int vId = Integer.parseInt(victimList.get(idx)[0]);
            DatabaseManager.getInstance().addReliefRequest(vId,
                needsArea.getText().trim(), (String) priorityCombo.getSelectedItem());
            needsArea.setText("");
            loadData();
            JOptionPane.showMessageDialog(this, "Request submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(JTable table, String status) {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a request first."); return; }
        int id = Integer.parseInt(tableModel.getValueAt(r, 0).toString());
        try { DatabaseManager.getInstance().updateRequestStatus(id, status); loadData(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getInstance().getReliefRequests();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("req_id"), rs.getString("victim_name"),
                    rs.getString("needs_description"), rs.getString("priority"),
                    rs.getString("status"), rs.getString("date_requested")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
