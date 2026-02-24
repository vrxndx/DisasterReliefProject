package disasterrelief;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Incident management — form on the left, table on the right.
 */
public class IncidentPanel extends JPanel {

    private JTextField titleField, locationField, descField;
    private JComboBox<String> typeCombo, severityCombo;
    private JTextField dateField;
    private DefaultTableModel tableModel;

    public IncidentPanel() {
        setLayout(new BorderLayout(16, 0));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── left form panel ─────────────────────────────────
        JPanel form = MainFrame.cardPanel("\uD83D\uDD25  Report New Incident");
        form.setPreferredSize(new Dimension(340, 0));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 0, 6, 0);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx   = 0;

        int row = 0;
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Title"), gbc);
        gbc.gridy = row++; titleField = MainFrame.styledField("Incident title"); fields.add(titleField, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Type"), gbc);
        gbc.gridy = row++; typeCombo = MainFrame.styledCombo(new String[]{
            "Flood","Earthquake","Fire","Cyclone","Landslide","Accident","Other"
        }); fields.add(typeCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridy = row++; dateField = MainFrame.styledField("2025-01-15"); fields.add(dateField, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Location"), gbc);
        gbc.gridy = row++; locationField = MainFrame.styledField("City, Area"); fields.add(locationField, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Severity"), gbc);
        gbc.gridy = row++; severityCombo = MainFrame.styledCombo(new String[]{
            "Low","Medium","High","Critical"
        }); fields.add(severityCombo, gbc);

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Description"), gbc);
        gbc.gridy = row++; descField = MainFrame.styledField("Details..."); fields.add(descField, gbc);

        gbc.gridy = row;
        gbc.insets = new Insets(16, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton addBtn = MainFrame.styledButton("+ Add Incident", MainFrame.PRIMARY);
        addBtn.addActionListener(e -> addIncident());
        btnRow.add(addBtn);
        fields.add(btnRow, gbc);

        form.add(fields, BorderLayout.CENTER);
        add(form, BorderLayout.WEST);

        // ── right table panel ───────────────────────────────
        JPanel tableCard = MainFrame.cardPanel("\uD83D\uDCCB  Incident Records");
        String[] cols = {"ID","Title","Type","Date","Location","Severity","Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(MainFrame.BG_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottomBar.setOpaque(false);
        JButton delBtn = MainFrame.styledButton("Delete Selected", MainFrame.DANGER);
        delBtn.addActionListener(e -> deleteSelected(table));
        JButton refBtn = MainFrame.styledButton("\u21BB Refresh", MainFrame.ACCENT);
        refBtn.addActionListener(e -> loadData());
        bottomBar.add(delBtn);
        bottomBar.add(refBtn);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
        loadData();
    }

    private void addIncident() {
        try {
            DatabaseManager.getInstance().addIncident(
                titleField.getText().trim(),
                (String) typeCombo.getSelectedItem(),
                dateField.getText().trim(),
                locationField.getText().trim(),
                descField.getText().trim(),
                (String) severityCombo.getSelectedItem()
            );
            titleField.setText(""); dateField.setText(""); locationField.setText(""); descField.setText("");
            loadData();
            JOptionPane.showMessageDialog(this, "Incident added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try { DatabaseManager.getInstance().deleteIncident(id); loadData(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseManager.getInstance().getIncidents();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("incident_id"), rs.getString("title"), rs.getString("type"),
                    rs.getString("date"), rs.getString("location"),
                    rs.getString("severity_level"), rs.getString("description")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── shared table styling ────────────────────────────────
    static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(MainFrame.BG_PANEL);
        table.setForeground(MainFrame.TEXT_LIGHT);
        table.setGridColor(new Color(40, 55, 90));
        table.setSelectionBackground(MainFrame.ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(15, 30, 60));
        header.setForeground(MainFrame.TEXT_LIGHT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MainFrame.PRIMARY));

        // alternating row colours
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                if (!isSel) {
                    comp.setBackground(r % 2 == 0 ? MainFrame.BG_PANEL : MainFrame.TABLE_ALT);
                    comp.setForeground(MainFrame.TEXT_LIGHT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return comp;
            }
        });

        return table;
    }
}
