package disasterrelief;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

/**
 * Combined panel for Relief Centers and Rescue Teams.
 */
public class CenterTeamPanel extends JPanel {

    private JTextField centerName, centerLoc, centerCap, centerContact;
    private JTextField teamName, teamSpec;
    private JComboBox<String> centerCombo;
    private DefaultTableModel centerModel, teamModel;
    private List<String[]> centerList;

    public CenterTeamPanel() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(MainFrame.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(createCenterSection());
        add(createTeamSection());
        
        loadData();
    }

    private JPanel createCenterSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Form
        JPanel form = MainFrame.cardPanel("\uD83C\uDFE5  Relief Centers");
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,0,4,0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; gbc.gridx = 0;
        int row = 0;

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Center Name"), gbc);
        gbc.gridy = row++; centerName = MainFrame.styledField("Name"); fields.add(centerName, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Location"), gbc);
        gbc.gridy = row++; centerLoc = MainFrame.styledField("City"); fields.add(centerLoc, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Capacity"), gbc);
        gbc.gridy = row++; centerCap = MainFrame.styledField("500"); fields.add(centerCap, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Contact"), gbc);
        gbc.gridy = row++; centerContact = MainFrame.styledField("Phone"); fields.add(centerContact, gbc);
        
        gbc.gridy = row; gbc.insets = new Insets(12,0,0,0);
        JButton addBtn = MainFrame.styledButton("+ Add Center", MainFrame.PRIMARY);
        addBtn.addActionListener(e -> addCenter());
        fields.add(addBtn, gbc);
        form.add(fields, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Location", "Cap"};
        centerModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = IncidentPanel.createStyledTable(centerModel);
        form.add(new JScrollPane(table), BorderLayout.CENTER);

        return form;
    }

    private JPanel createTeamSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Form
        JPanel form = MainFrame.cardPanel("\uD83D\uDEE1  Rescue Teams");
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,0,4,0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; gbc.gridx = 0;
        int row = 0;

        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Team Name"), gbc);
        gbc.gridy = row++; teamName = MainFrame.styledField("Name"); fields.add(teamName, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Base Center"), gbc);
        gbc.gridy = row++; centerCombo = new JComboBox<>();
        centerCombo.setBackground(new Color(35,45,75)); centerCombo.setForeground(MainFrame.TEXT_LIGHT);
        fields.add(centerCombo, gbc);
        gbc.gridy = row++; fields.add(MainFrame.styledLabel("Specialization"), gbc);
        gbc.gridy = row++; teamSpec = MainFrame.styledField("Rescue/Medical"); fields.add(teamSpec, gbc);
        
        gbc.gridy = row; gbc.insets = new Insets(12,0,0,0);
        JButton addBtn = MainFrame.styledButton("+ Add Team", MainFrame.SUCCESS);
        addBtn.addActionListener(e -> addTeam());
        fields.add(addBtn, gbc);
        form.add(fields, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Team Name", "Base", "Spec"};
        teamModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = IncidentPanel.createStyledTable(teamModel);
        form.add(new JScrollPane(table), BorderLayout.CENTER);

        return form;
    }

    private void addCenter() {
        try {
            DatabaseManager.getInstance().addCenter(centerName.getText().trim(), 
                centerLoc.getText().trim(), Integer.parseInt(centerCap.getText().trim()), centerContact.getText().trim());
            centerName.setText(""); centerLoc.setText(""); centerCap.setText(""); centerContact.setText("");
            loadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void addTeam() {
        if (centerList == null || centerList.isEmpty()) return;
        try {
            int cId = Integer.parseInt(centerList.get(centerCombo.getSelectedIndex())[0]);
            DatabaseManager.getInstance().addTeam(cId, teamName.getText().trim(), teamSpec.getText().trim());
            teamName.setText(""); teamSpec.setText("");
            loadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void loadData() {
        centerModel.setRowCount(0); teamModel.setRowCount(0);
        centerList = DatabaseManager.getInstance().getCenterList();
        centerCombo.removeAllItems();
        for (String[] s : centerList) centerCombo.addItem(s[1]);

        try {
            ResultSet rs1 = DatabaseManager.getInstance().getCenters();
            while (rs1.next()) centerModel.addRow(new Object[]{rs1.getInt("center_id"), rs1.getString("name"), rs1.getString("location"), rs1.getInt("capacity")});
            ResultSet rs2 = DatabaseManager.getInstance().getTeams();
            while (rs2.next()) teamModel.addRow(new Object[]{rs2.getInt("team_id"), rs2.getString("team_name"), rs2.getString("center_name"), rs2.getString("specialization")});
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
