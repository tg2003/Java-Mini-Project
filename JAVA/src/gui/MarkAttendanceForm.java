package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MarkAttendanceForm extends JFrame {

    private JTextField tdate;
    private JButton btnSubmit;
    private JComboBox<String> cCourseCode;
    private JButton btnBack;
    private JPanel AttendancePanel;
    private JButton btnLoad;
    private JComboBox<String> cType;
    private JTable table1;
    private JComboBox<Integer> cWeekNo;
    private JScrollPane attScrollPane;

    private String techOffId;

    public MarkAttendanceForm(String techOffId) {
        this.techOffId = techOffId;

        setTitle("Mark Attendance");
        setContentPane(AttendancePanel);
        setMinimumSize(new Dimension(800, 600));

        loadCourseCodes();
        loadTypes();
        loadWeekNumbers();

        btnLoad.addActionListener(e -> loadStudents());
        btnSubmit.addActionListener(e -> saveAttendance());

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

    // load types
    private void loadTypes() {
        cType.removeAllItems();
        cType.addItem("All");
        cType.addItem("Theory");
        cType.addItem("Practical");
    }

    //load week numbers
    private void loadWeekNumbers() {
        cWeekNo.removeAllItems();
        for (int i = 1; i <= 15; i++) {
            cWeekNo.addItem(i);
        }
    }


    private void loadStudents() {
        try {
            String course = (String) cCourseCode.getSelectedItem();
            String type = (String) cType.getSelectedItem();

            Connection con = DBConnection.getConnection();

            StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT u.Ug_id, u.Name
                FROM undergraduate u
                JOIN enrolls_in e ON u.Ug_id = e.Ug_id
                JOIN timetable t ON e.C_code = t.C_code
                WHERE t.C_code = ?
            """);

            if (!type.equals("All")) {
                sql.append(" AND t.Type = ?");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(1, course);

            if (!type.equals("All")) {
                pst.setString(2, type);
            }

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"UG ID", "Name", "Status"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Ug_id"),
                        rs.getString("Name"),
                        "Present"
                });
            }

            table1.setModel(model);

            JComboBox<String> combo = new JComboBox<>(new String[]{
                    "Present", "Absent", "MedicalApproved", "MedicalDeclined"
            });

            table1.getColumnModel().getColumn(2)
                    .setCellEditor(new DefaultCellEditor(combo));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int getTimetableId(String course, String type, Connection con) throws Exception {

        String sql;

        if (type.equals("All")) {
            sql = "SELECT Timetable_id FROM timetable WHERE C_code=? LIMIT 1";
        } else {
            sql = "SELECT Timetable_id FROM timetable WHERE C_code=? AND Type=?";
        }

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, course);

        if (!type.equals("All")) {
            pst.setString(2, type);
        }

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("Timetable_id");
        }

        throw new Exception("No timetable found!");
    }


    private void saveAttendance() {
        try {
            String course = (String) cCourseCode.getSelectedItem();
            String type = (String) cType.getSelectedItem();
            int weekNo = (int) cWeekNo.getSelectedItem();

            Connection con = DBConnection.getConnection();

            int timetableId = getTimetableId(course, type, con);

            for (int i = 0; i < table1.getRowCount(); i++) {

                String ugId = table1.getValueAt(i, 0).toString();
                String status = table1.getValueAt(i, 2).toString();

                String check = "SELECT * FROM attendance WHERE Ug_id=? AND Timetable_id=? AND Week_no=?";
                PreparedStatement checkPst = con.prepareStatement(check);

                checkPst.setString(1, ugId);
                checkPst.setInt(2, timetableId);
                checkPst.setInt(3, weekNo);

                ResultSet rs = checkPst.executeQuery();

                if (rs.next()) {

                    String update = "UPDATE attendance SET Status=?, Techoff_id=? WHERE Ug_id=? AND Timetable_id=? AND Week_no=?";
                    PreparedStatement pst = con.prepareStatement(update);

                    pst.setString(1, status);
                    pst.setString(2, techOffId);
                    pst.setString(3, ugId);
                    pst.setInt(4, timetableId);
                    pst.setInt(5, weekNo);

                    pst.executeUpdate();

                } else {

                    String insert = "INSERT INTO attendance (Week_no, Ug_id, Timetable_id, Techoff_id, Status) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(insert);

                    pst.setInt(1, weekNo);
                    pst.setString(2, ugId);
                    pst.setInt(3, timetableId);
                    pst.setString(4, techOffId);
                    pst.setString(5, status);

                    pst.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Attendance Saved Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}