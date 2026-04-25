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


    private JScrollPane medScrollPane;

    private JTable table;
    private DefaultTableModel model;

    private String techOffId;

    public MedicalForm(String techOffId) {

        setTitle("Verify Medicals");
        setContentPane(MedicalPanel);
        setMinimumSize(new Dimension(800,600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        System.out.println("MedicalForm opened");


        // TABLE INSIDE EXISTING SCROLLPANE

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Medical ID", "UG ID", "Course", "Date", "Session Type", "Status"
        });

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        medScrollPane.setViewportView(table);


        // LOAD DATA

        loadPendingMedicals();


        // BUTTONS

        btnApprove.addActionListener(e -> updateMedicalStatus("MedicalApproved"));
        btnReject.addActionListener(e -> updateMedicalStatus("MedicalDeclined"));

        btnBack.addActionListener(e -> {
            dispose();
            new TechOfficerDashboard(techOffId);
        });

        setVisible(true);
    }

    private void loadPendingMedicals() {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT medical_id, ug_id, c_code, from_date, session_type, status FROM medical WHERE status='Pending'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("medical_id"),
                        rs.getString("ug_id"),
                        rs.getString("c_code"),
                        rs.getString("from_date"),
                        rs.getString("session_type"),
                        rs.getString("status")
                });
            }

            System.out.println("Medical data loaded");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMedicalStatus(String newStatus) {

        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one row!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            for (int row : selectedRows) {

                int medicalId = Integer.parseInt(model.getValueAt(row, 0).toString());

                String query = "UPDATE medical SET status=?, techOff_id=? WHERE medical_id=?";
                PreparedStatement pst = con.prepareStatement(query);

                pst.setString(1, newStatus); // MUST match ENUM exactly
                pst.setString(2,techOffId);
                pst.setInt(3, medicalId);

                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Updated successfully!");

            loadPendingMedicals();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}