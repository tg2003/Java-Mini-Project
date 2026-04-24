package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class MarkAttendanceForm extends JFrame {

    private JTextField tdate;
    private JButton btnSubmit;
    private JComboBox<Integer> cTimetable;
    private JButton btnBack;
    private JPanel AttendancePanel;
    private JButton btnLoad;
    private JComboBox<Integer> comboBox1;
    private JTable table1;

   //add date picker
    private JSpinner dateSpinner;
    private String techOffId;

    public MarkAttendanceForm(String techOffId) {
        this.techOffId = techOffId;

        setTitle("Mark Attendance");
        setContentPane(AttendancePanel);
        setMinimumSize(new Dimension(800, 600));


        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        tdate.setLayout(new BorderLayout());
        tdate.removeAll();
        tdate.add(dateSpinner, BorderLayout.CENTER);

        loadTimetableIds();
        loadWeekNumbers();

        //load button
        btnLoad.addActionListener(e -> loadStudents());

        //submit button
        btnSubmit.addActionListener(e -> saveAttendance());

        //back button
        btnBack.addActionListener(e -> {
            dispose();
            new TechOfficerDashboard(techOffId);
        });

        setVisible(true);
    }

   //load timetable ids
    private void loadTimetableIds() {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT Timetable_id FROM timetable ORDER BY Timetable_id ASC";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            cTimetable.removeAllItems(); // IMPORTANT

            System.out.println("Loading timetable IDs...");

            while (rs.next()) {
                int id = rs.getInt("Timetable_id");
                System.out.println("Adding ID: " + id);

                cTimetable.addItem(id);
            }

            System.out.println("Timetable loaded successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load week numbers
    private void loadWeekNumbers() {
        for (int i = 1; i <= 15; i++) {
            comboBox1.addItem(i);
        }
    }

    //Load students into table
    private void loadStudents() {
        try {
            int timetableId = (int) cTimetable.getSelectedItem();

            Connection con = DBConnection.getConnection();

            String query = """
                SELECT u.Ug_id, u.Name
                FROM UNDERGRADUATE u
                JOIN ENROLLS_IN e ON u.Ug_id = e.Ug_id
                JOIN TIMETABLE t ON e.C_code = t.C_code
                WHERE t.Timetable_id = ?
            """;

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, timetableId);

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

            // ComboBox inside table
            JComboBox<String> combo = new JComboBox<>(new String[]{
                    "Present", "Absent", "MedicalApproved", "MedicalDeclined"
            });

            table1.getColumnModel().getColumn(2)
                    .setCellEditor(new DefaultCellEditor(combo));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Save attendance
    private void saveAttendance() {
        try {
            int timetableId = (int) cTimetable.getSelectedItem();
            int weekNo = (int) comboBox1.getSelectedItem();

            // Get date from spinner
            Date selectedDate = (Date) dateSpinner.getValue();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            Connection con = DBConnection.getConnection();

            for (int i = 0; i < table1.getRowCount(); i++) {

                String ugId = table1.getValueAt(i, 0).toString();
                String status = table1.getValueAt(i, 2).toString();

                // check existing record
                String check = "SELECT * FROM ATTENDANCE WHERE Ug_id=? AND Timetable_id=? AND Week_no=?";
                PreparedStatement checkPst = con.prepareStatement(check);
                checkPst.setString(1, ugId);
                checkPst.setInt(2, timetableId);
                checkPst.setInt(3, weekNo);

                ResultSet rs = checkPst.executeQuery();

                if (rs.next()) {
                    // UPDATE
                    String update = "UPDATE ATTENDANCE SET Status=?, Date=?, Techoff_id=? WHERE Ug_id=? AND Timetable_id=? AND Week_no=?";
                    PreparedStatement pst = con.prepareStatement(update);

                    pst.setString(1, status);
                    pst.setDate(2, sqlDate);
                    pst.setString(3,techOffId);
                    pst.setString(4, ugId);
                    pst.setInt(5, timetableId);
                    pst.setInt(6, weekNo);

                    pst.executeUpdate();

                } else {
                    // INSERT
                    String insert = "INSERT INTO ATTENDANCE VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(insert);

                    pst.setInt(1, weekNo);
                    pst.setString(2, ugId);
                    pst.setInt(3, timetableId);
                    pst.setDate(4, sqlDate);
                    pst.setString(5,techOffId);
                    pst.setString(6, status);

                    pst.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Attendance Saved Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
