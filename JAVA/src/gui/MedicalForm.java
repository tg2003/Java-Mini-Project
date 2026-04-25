package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MedicalForm extends JFrame {

    private JButton btnApprove;
    private JButton btnReject;
    private JPanel MedicalPanel;
    private JButton btnBack;

    private JScrollPane medScrollPane;        // Pending
    private JScrollPane allMedicalScrollPane; // All records

    private JButton btnView;
    private JButton btnLoad;
    private JButton btnDelete;

    private JTable pendingTable;
    private JTable allTable;

    private DefaultTableModel pendingModel;
    private DefaultTableModel allModel;

    private String techOffId;

    public MedicalForm(String techOffId) {

        this.techOffId = techOffId;

        setTitle("Verify Medicals");
        setContentPane(MedicalPanel);
        setMinimumSize(new Dimension(800,600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // =============================
        // PENDING TABLE
        // =============================
        pendingModel = new DefaultTableModel();
        pendingModel.setColumnIdentifiers(new String[]{
                "Medical ID", "UG ID", "Course", "Date", "Session Type", "Status"
        });

        pendingTable = new JTable(pendingModel);
        pendingTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        medScrollPane.setViewportView(pendingTable);

        // =============================
        // ALL MEDICAL TABLE
        // =============================
        allModel = new DefaultTableModel();
        allModel.setColumnIdentifiers(new String[]{
                "Medical ID", "UG ID", "Course", "Date", "Session Type", "Status"
        });

        allTable = new JTable(allModel);
        allMedicalScrollPane.setViewportView(allTable);

        // =============================
        // DEFAULT LOAD (Pending)
        // =============================
        loadPendingMedicals();

        // =============================
        // BUTTONS
        // =============================
        btnApprove.addActionListener(e -> updateMedicalStatus("MedicalApproved"));
        btnReject.addActionListener(e -> updateMedicalStatus("MedicalDeclined"));

        btnLoad.addActionListener(e -> loadPendingMedicals());
        btnView.addActionListener(e -> loadAllMedicals());

        btnBack.addActionListener(e -> {
            dispose();
            new TechOfficerDashboard(techOffId);
        });

        setVisible(true);
    }

    // =====================================
    // LOAD PENDING MEDICALS
    private void loadPendingMedicals() {
        try {
            Connection con = DBConnection.getConnection();

            String query = """
                SELECT medical_id, ug_id, c_code, from_date, session_type, status
                FROM medical
                WHERE status='Pending'
            """;

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            pendingModel.setRowCount(0);

            while (rs.next()) {
                pendingModel.addRow(new Object[]{
                        rs.getInt("medical_id"),
                        rs.getString("ug_id"),
                        rs.getString("c_code"),
                        rs.getString("from_date"),
                        rs.getString("session_type"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // LOAD ALL MEDICALS
    private void loadAllMedicals() {
        try {
            Connection con = DBConnection.getConnection();

            String query = """
                SELECT medical_id, ug_id, c_code, from_date, session_type, status
                FROM medical
            """;

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            allModel.setRowCount(0);

            while (rs.next()) {
                allModel.addRow(new Object[]{
                        rs.getInt("medical_id"),
                        rs.getString("ug_id"),
                        rs.getString("c_code"),
                        rs.getString("from_date"),
                        rs.getString("session_type"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // UPDATE STATUS (ONLY FROM PENDING TABLE)
    private void updateMedicalStatus(String newStatus) {

        int[] selectedRows = pendingTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one row!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            for (int row : selectedRows) {

                int medicalId = Integer.parseInt(pendingModel.getValueAt(row, 0).toString());

                String query = "UPDATE medical SET status=?, techoff_id=? WHERE medical_id=?";
                PreparedStatement pst = con.prepareStatement(query);

                pst.setString(1, newStatus); // ENUM-safe
                pst.setString(2, techOffId);
                pst.setInt(3, medicalId);

                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Updated successfully!");

            // Refresh both tables
            loadPendingMedicals();
            loadAllMedicals();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}