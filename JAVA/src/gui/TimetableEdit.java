package gui;

import db.DBConnection;
import util.DataValidator;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TimetableEdit extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel contentPanel;
    private JTextField timetableNoTextField;
    private JTextField startTimeTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JTextField endTimetextField;
    private JTextField ccodetextField;
    private JComboBox typecomboBox;
    private JComboBox daycomboBox;

    public TimetableEdit(int id, String day, String start, String end, String code, String type, TimetableView parent) {
        setContentPane(mainPanel);
        setTitle("Edit Timetable");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        //load the results into the form,which is coming from parent(TimetableView)
        timetableNoTextField.setText(String.valueOf(id));
        timetableNoTextField.setEditable(false);
        daycomboBox.setSelectedItem(day);
        startTimeTextField.setText(start);
        endTimetextField.setText(end);
        ccodetextField.setText(code);
        ccodetextField.setEditable(false);
        typecomboBox.setSelectedItem(type);

        //save btn
        saveButton.addActionListener(e->{
            String query = "UPDATE timetable SET Day=?, Start_time=?, End_time=?, C_code=?, Type=? WHERE Timetable_id=?";
            try (Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,daycomboBox.getSelectedItem().toString());
                ps.setString(2,startTimeTextField.getText());
                ps.setString(3,endTimetextField.getText());
                ps.setString(4,ccodetextField.getText());
                ps.setString(5,typecomboBox.getSelectedItem().toString());
                ps.setInt(6,Integer.parseInt(timetableNoTextField.getText()));

                //check start & end time is in correct format...
                String startErr = DataValidator.validateTime(startTimeTextField.getText());
                String endErr = DataValidator.validateTime(endTimetextField.getText());
                if (startErr!=null) {
                    JOptionPane.showMessageDialog(this, "Start Time: " + startErr);
                    return;
                }
                if (endErr != null) {
                    JOptionPane.showMessageDialog(this, "End Time: " + endErr);
                    return;
                }

                int result = ps.executeUpdate();
                if(result>0){
                    JOptionPane.showMessageDialog(this, "Updated Successfully! ✅","Success",JOptionPane.INFORMATION_MESSAGE);
                    parent.loadtimetable();
                    dispose();
                }

            } catch (SQLException ex) {
                System.out.println("Timetable save error");
            }
        });

        //delete btn
        deleteButton.addActionListener(e->{
            int confirmMsg = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this session?","Delete confirmation ",JOptionPane.YES_NO_OPTION);
            if(confirmMsg==JOptionPane.YES_OPTION){
                String q = "DELETE FROM NOTICE WHERE Notice_no=?";
                try (Connection con = DBConnection.getConnection()){
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.setInt(1,Integer.parseInt(timetableNoTextField.getText()));
                    JOptionPane.showMessageDialog(this,"Session Deleted Successfully","Success Message",JOptionPane.INFORMATION_MESSAGE);
                    // parent table refresh
                    parent.loadtimetable();
                    dispose();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        //cancel btn
        cancelButton.addActionListener(e -> dispose());

    }
}
