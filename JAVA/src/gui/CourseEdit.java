package gui;

import db.DBConnection;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class CourseEdit extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel contentPanel;
    private JTextField ccodeTextField;
    private JTextField cnameTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JComboBox ccreditscomboBox;

    public CourseEdit(String code, String name, String credit, CourseView parent) {
        setContentPane(mainPanel);
        setTitle("Edit Course Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // set data on fields
        ccodeTextField.setText(code);
        ccodeTextField.setEditable(false);
        cnameTextField.setText(name);
        ccreditscomboBox.setSelectedItem(credit);

        // save btn
        saveButton.addActionListener(e -> {
            String updatedName = cnameTextField.getText();
            String updatedCredit = ccreditscomboBox.getSelectedItem().toString();

            try (Connection con = DBConnection.getConnection()) {
                String query = "UPDATE COURSE SET C_name = ?, Credit = ? WHERE C_code = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, updatedName);
                ps.setString(2, updatedCredit);
                ps.setString(3, code);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Course updated successfully! ✅");
                    parent.loadCourses(); // refresh parent(courseView class) table
                    dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // delete btn
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getConnection()) {
                    String query = "DELETE FROM COURSE WHERE C_code = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, code);

                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Course deleted! 🗑️");
                        parent.loadCourses(); // refresh parent table
                        dispose();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // cancel button
        cancelButton.addActionListener(e -> dispose());
    }
}
