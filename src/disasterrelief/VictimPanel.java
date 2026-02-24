package disasterrelief;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

/**
 * Victim registration — form + table with incident linking.
 */
public class VictimPanel extends JPanel {

    private JTextField nameField, contactField, addressField;
    private JComboBox<String> statusCombo, incidentCombo;
    private DefaultTableModel tableModel;
    private List<String[]> incidentList;

    public VictimPanel() {
        setLayout(new BorderLayout(16, 0));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── form ────────────────────────────────────────────
        JPanel form = MainFrame.cardPanel("\uD83D\uDC65  Register Victim");
        form.setPreferredSize(new Dimension(340, 0));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; gbc.gridx = 0;

        int row = 0;
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Full Name"), gbc);
        gbc.gridy = row++; nameField = MainFrame.styledField("Name"); fields.add(nameField, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Linked Incident"), gbc);
        gbc.gridy = row++; incidentCombo = new JComboBox<>(); refreshIncidentCombo();
        incidentCombo.setBackground(new Color(35,45,75));
        incidentCombo.setForeground(MainFrame.TEXT_LIGHT);
        fields.add(incidentCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Status"), gbc);
        gbc.gridy = row++; statusCombo = MainFrame.styledCombo(new String[]{
            "Affected","Injured","Safe","Missing","Deceased"
        }); fields.add(statusCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Contact Info"), gbc);
        gbc.gridy = row++; contactField = MainFrame.styledField("Phone / Email"); fields.add(contactField, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Address"), gbc);
        gbc.gridy = row++; addressField = MainFrame.styledField("Address"); fields.add(addressField, gbc);

        gbc.gridy = row; gbc.insets = new Insets(16,0,0,0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton addBtn = MainFrame.styledButton("+ Add Victim", MainFrame.PRIMARY);
        addBtn.addActionListener(e -> addVictim());
        btnRow.add(addBtn);
        fields.add(btnRow, gbc);

        form.add(fields, BorderLayout.CENTER);
        add(form, BorderLayout.WEST);

        // ── table ───────────────────────────────────────────
        JPanel tableCard = MainFrame.cardPanel("\uD83D\uDCCB  Victim Records");
        String[] cols = {"ID","Name","Incident","Status","Contact","Address"};
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
        JButton del = MainFrame.styledButton("Delete Selected", MainFrame.DANGER);
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            try { DatabaseManager.getInstance().deleteVictim(
                Integer.parseInt(tableModel.getValueAt(r, 0).toString())); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
        JButton ref = MainFrame.styledButton("\u21BB Refresh", MainFrame.ACCENT);
        ref.addActionListener(e -> { loadData(); refreshIncidentCombo(); });
        bot.add(del); bot.add(ref);
        tableCard.add(bot, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
        loadData();
    }

    private void refreshIncidentCombo() {
        incidentList = DatabaseManager.getInstance().getIncidentList();
        if (incidentCombo != null) {
            incidentCombo.removeAllItems();
            for (String[] s : incidentList) incidentCombo.addItem(s[0] + " - " + s[1]);
        }
    }

    private void addVictim() {
        if (incidentList.isEmpty()) { JOptionPane.showMessageDialog(this, "Add an incident first."); return; }
        try {
            int idx = incidentCombo.getSelectedIndex();
            int incId = Integer.parseInt(incidentList.get(idx)[0]);
            DatabaseManager.getInstance().addVictim(incId,
                nameField.getText().trim(), (String) statusCombo.getSelectedItem(),
                contactField.getText().trim(), addressField.getText().trim());
            nameField.setText(""); contactField.setText(""); addressField.setText("");
            loadData();
            JOptionPane.showMessageDialog(this, "Victim registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getInstance().getVictims();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("victim_id"), rs.getString("name"),
                    rs.getString("incident_title"), rs.getString("status"),
                    rs.getString("contact_info"), rs.getString("address")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
