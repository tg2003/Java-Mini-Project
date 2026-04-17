package gui;

import db.DBConnection;
import util.DataValidator;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseCreate extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel contentPanel;
    private JTextField ccodeTextField;
    private JTextField cnameTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JComboBox ccreditscomboBox;

    public CourseCreate() {
        setContentPane(mainPanel);
        setTitle("Add New Course");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        // save button
        saveButton.addActionListener(e -> {
            String code = ccodeTextField.getText().trim();
            String name = cnameTextField.getText().trim();
            String credit = ccreditscomboBox.getSelectedItem().toString();

            // check C_code,Name fields are valid or not
            String codeErr = DataValidator.validateCourseCode(code);
            if (codeErr != null) {
                JOptionPane.showMessageDialog(this, codeErr, "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled! ⚠️");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                //check the entered c_code is already exists?
                String checkQuery = "SELECT C_code FROM COURSE WHERE C_code = ?";
                PreparedStatement checkPs = con.prepareStatement(checkQuery);
                checkPs.setString(1, code);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Course Code already exists! 🚫");
                    return;
                }

                // insert new course
                String insertQuery = "INSERT INTO COURSE (C_code, C_name, Credit) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(insertQuery);
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setString(3, credit);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Course added successfully! ✅");
                    dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        });

        // cancel button logic
        cancelButton.addActionListener(e -> dispose());
    }
}
