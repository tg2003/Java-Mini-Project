package gui;

import db.DBConnection;
import util.DataValidator;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimetableAddNew extends JFrame {
    private JPanel titlePanel;
    private JPanel contentPanel;
    private JTextField timetableNoTextField;
    private JTextField startTimeTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField endTimetextField;
    private JComboBox typecomboBox;
    private JComboBox daycomboBox;
    private JPanel mainPanel;
    private JComboBox coursecomboBox;

    public TimetableAddNew(){
        setContentPane(mainPanel);
        setTitle("Add New Timetable Session");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        loadCourseCodes();

        saveButton.addActionListener(e->{
            //getting data
            timetableNoTextField.getText();
            timetableNoTextField.setEditable(false);
            String day = daycomboBox.getSelectedItem().toString();
            String start = startTimeTextField.getText();
            String end = endTimetextField.getText();
            String code = coursecomboBox.getSelectedItem().toString();
            String type = typecomboBox.getSelectedItem().toString();

            //check start & end time is in correct format...
            String startErr = DataValidator.validateTime(start);
            String endErr = DataValidator.validateTime(end);
            if (startErr!=null) {
                JOptionPane.showMessageDialog(this, "Start Time: " + startErr);
                return;
            }
            if (endErr != null) {
                JOptionPane.showMessageDialog(this, "End Time: " + endErr);
                return;
            }

            String query = "INSERT INTO timetable (Day, Start_time, End_time, C_code, Type) VALUES (?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,day);
                ps.setString(2, start);
                ps.setString(3, end);
                ps.setString(4, code);
                ps.setString(5, type);
                int result = ps.executeUpdate();
                if (result>0){
                    JOptionPane.showMessageDialog(this, "New Session Added Successfully! ✅");
                    dispose();
                }

            }catch (SQLException ex) {
                System.out.println("Connection error: " + ex.getMessage());
            }
        });

        //cancel
        cancelButton.addActionListener(e->{dispose();});
    }


    //taking course codes into JComboBox
    public void loadCourseCodes() {
        String q = "SELECT C_code FROM COURSE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {

            coursecomboBox.removeAllItems(); // important

            while (rs.next()) {
                coursecomboBox.addItem(rs.getString("C_code"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
