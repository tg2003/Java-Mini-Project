package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AttendanceReportForm extends JFrame {

    private JComboBox<String> cCourseCode;
    private JComboBox<String> cugId;
    private JComboBox<String> cType;

    private JButton btnCalculate;
    private JPanel ReportPanel;
    private JButton btnBack;

    private JTable table;
    private JScrollPane attScrollPane;

    private String techOffId;

    public AttendanceReportForm(String techOffId) {
        this.techOffId=techOffId;

        setTitle("Attendance Reports");
        setContentPane(ReportPanel);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        table = new JTable();
        attScrollPane.setViewportView(table);


        loadTypes();
        loadCourseCodes();
        loadUgIds();

        btnCalculate.addActionListener(e -> loadReport());

        btnBack.addActionListener(e -> {
            dispose();
            new TechOfficerDashboard(techOffId);
        });

        setVisible(true);
    }


    //load course codes

    private void loadCourseCodes() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT DISTINCT C_code FROM timetable";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cCourseCode.removeAllItems();

            while (rs.next()) {
                cCourseCode.addItem(rs.getString("C_code"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //load ugId

    private void loadUgIds() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT DISTINCT Ug_id FROM attendance";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cugId.removeAllItems();
            cugId.addItem("ALL");

            while (rs.next()) {
                cugId.addItem(rs.getString("Ug_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void loadTypes() {

        cType.removeAllItems();

        cType.addItem("All");
        cType.addItem("Theory");
        cType.addItem("Practical"); // FIXED SPELLING

        cType.setSelectedItem("All");
    }


    //generate reports

    private void loadReport() {

        try {
            String course = (String) cCourseCode.getSelectedItem();
            String ugId = (String) cugId.getSelectedItem();
            String type = (String) cType.getSelectedItem();

            Connection con = DBConnection.getConnection();

            StringBuilder sql = new StringBuilder("""
                SELECT a.Ug_id,
                       COUNT(*) AS total,
                       SUM(CASE WHEN a.Status='Present' OR a.Status='MedicalApproved' THEN 1 ELSE 0 END) AS present,
                       SUM(CASE WHEN a.Status='Present' THEN 1 ELSE 0 END) AS presentOnly
                FROM attendance a
                JOIN timetable t ON a.Timetable_id = t.Timetable_id
                WHERE t.C_code = ?
            """);

            if (!type.equals("All")) {
                sql.append(" AND t.Type = ?");
            }

            if (!ugId.equals("ALL")) {
                sql.append(" AND a.Ug_id = ?");
            }

            sql.append(" GROUP BY a.Ug_id");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;
            pst.setString(index++, course);

            if (!type.equals("All")) {
                pst.setString(index++, type);
            }

            if (!ugId.equals("ALL")) {
                pst.setString(index++, ugId);
            }

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();

            boolean individual = !ugId.equals("ALL");

            if (individual) {
                model.setColumnIdentifiers(new String[]{
                        "Name",
                        "Total Sessions",
                        "Present Sessions",
                        "% Without Medical",
                        "% With Medical"
                });
            } else {
                model.setColumnIdentifiers(new String[]{
                        "UG ID",
                        "Total Sessions",
                        "Present Sessions",
                        "% Without Medical",
                        "% With Medical"
                });
            }

            while (rs.next()) {

                int total = rs.getInt("total");
                int present = rs.getInt("present");
                int presentOnly = rs.getInt("presentOnly");

                double withMedical = (total == 0) ? 0 : (present * 100.0 / total);
                double withoutMedical = (total == 0) ? 0 : (presentOnly * 100.0 / total);

                if (individual) {

                    String name = getStudentName(rs.getString("Ug_id"), con);

                    model.addRow(new Object[]{
                            name,
                            total,
                            present,
                            String.format("%.2f%%", withoutMedical),
                            String.format("%.2f%%", withMedical)
                    });

                } else {

                    model.addRow(new Object[]{
                            rs.getString("Ug_id"),
                            total,
                            present,
                            String.format("%.2f%%", withoutMedical),
                            String.format("%.2f%%", withMedical)
                    });
                }
            }

            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getStudentName(String ugId, Connection con) {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT Name FROM undergraduate WHERE Ug_id=?"
            );
            pst.setString(1, ugId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("Name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}