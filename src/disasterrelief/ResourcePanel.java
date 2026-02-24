package disasterrelief;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

/**
 * Resource Inventory management.
 */
public class ResourcePanel extends JPanel {

    private JTextField nameField, typeField, qtyField, unitField;
    private JComboBox<String> centerCombo;
    private DefaultTableModel tableModel;
    private List<String[]> centerList;

    public ResourcePanel() {
        setLayout(new BorderLayout(16, 0));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Form
        JPanel form = MainFrame.cardPanel("\uD83D\uDCE6  Inventory Management");
        form.setPreferredSize(new Dimension(340, 0));
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,0,6,0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; gbc.gridx = 0;
        int row = 0;

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Resource Name"), gbc);
        gbc.gridy = row++; nameField = MainFrame.styledField("Water/Rice"); fields.add(nameField, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Type"), gbc);
        gbc.gridy = row++; typeField = MainFrame.styledField("Food/Medical"); fields.add(typeField, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Quantity"), gbc);
        gbc.gridy = row++; qtyField = MainFrame.styledField("100"); fields.add(qtyField, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Unit"), gbc);
        gbc.gridy = row++; unitField = MainFrame.styledField("Bags/Kits"); fields.add(unitField, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Center"), gbc);
        gbc.gridy = row++; centerCombo = new JComboBox<>();
        centerCombo.setBackground(new Color(35,45,75)); centerCombo.setForeground(MainFrame.TEXT_LIGHT);
        fields.add(centerCombo, gbc);

        gbc.gridy = row; gbc.insets = new Insets(16,0,0,0);
        JButton addBtn = MainFrame.styledButton("+ Add Resource", MainFrame.PRIMARY);
        addBtn.addActionListener(e -> addResource());
        fields.add(addBtn, gbc);
        form.add(fields, BorderLayout.CENTER);
        add(form, BorderLayout.WEST);

        // Table
        JPanel tableCard = MainFrame.cardPanel("\uD83D\uDCCB  Resource Inventory");
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Qty", "Unit", "Center"}, 0);
        JTable table = IncidentPanel.createStyledTable(tableModel);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton refresh = MainFrame.styledButton("\u21BB Refresh", MainFrame.ACCENT);
        refresh.addActionListener(e -> loadData());
        tableCard.add(refresh, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
        loadData();
    }

    private void addResource() {
        if (centerList == null || centerList.isEmpty()) return;
        try {
            int cId = Integer.parseInt(centerList.get(centerCombo.getSelectedIndex())[0]);
            DatabaseManager.getInstance().addResource(cId, nameField.getText().trim(), 
                typeField.getText().trim(), Integer.parseInt(qtyField.getText().trim()), unitField.getText().trim());
            nameField.setText(""); typeField.setText(""); qtyField.setText(""); unitField.setText("");
            loadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        centerList = DatabaseManager.getInstance().getCenterList();
        centerCombo.removeAllItems();
        for (String[] s : centerList) centerCombo.addItem(s[1]);

        try {
            ResultSet rs = DatabaseManager.getInstance().getResources();
            while (rs.next()) tableModel.addRow(new Object[]{
                rs.getInt("resource_id"), rs.getString("name"), rs.getString("type"),
                rs.getInt("quantity"), rs.getString("unit"), rs.getString("center_name")
            });
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
