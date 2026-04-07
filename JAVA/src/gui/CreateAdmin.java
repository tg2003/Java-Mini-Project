package gui;

import db.DBConnection;
import util.DataValidator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAdmin extends JFrame {
    private JPanel mainPanel;
    private JLabel pplabel;
    private JButton changePPbtn;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel nicLabel;
    private JLabel dobLabel;
    private JLabel deptLabel;
    private JLabel noLabel;
    private JLabel streetLabel;
    private JLabel cityLabel;
    private JTextField idtextField;
    private JTextField nametextField;
    private JTextField emailtextField;
    private JTextField nictextField;
    private JTextField dobtextField;
    private JTextField notextField;
    private JTextField streettextField;
    private JTextField citytextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField depttextField;
    private JTextField phonetextField;
    private JLabel contactLabel1;
    private JTextField pwdtextField;
    private JLabel pwdLabel;
    private String selectedImagePath = null;

    public CreateAdmin() {
        setContentPane(mainPanel);
        setTitle("Create a new Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        // auto generate Admin ID
        try (Connection con = DBConnection.getConnection()) {
            String q = "SELECT User_id FROM USER WHERE Role='Admin' ORDER BY User_id DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            String newId;

            if (rs.next()) {
                String lastId = rs.getString("User_id");
                int num = Integer.parseInt(lastId.substring(2)); // remove 'AD' and save only the numeric part
                newId = String.format("AD%04d", num + 1);
            } else {
                newId = "AD0001"; // setting first Admin
            }
            idtextField.setText(newId);
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        } // End of the - auto generate Admin ID

        idtextField.setEditable(false); // Admin ID is automatically set. cant edit

        // change pp btn
        changePPbtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(); // open file choosing window
            int result = fc.showOpenDialog(null);

            // if the user go to the Jfilechooser and cancel the operation,this won't execute.
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                File destFile = new File(System.getProperty("user.dir") + "/JAVA/src/resources/userPP/" + file.getName());
                try {
                    java.nio.file.Files.copy(
                            file.toPath(),
                            destFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                selectedImagePath = "src/resources/userPP/" + file.getName();
                ImageIcon icon = new ImageIcon(new ImageIcon(selectedImagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                pplabel.setIcon(icon);
            }
        });

        saveButton.addActionListener(e -> {
            // taking values from the form
            String id      = idtextField.getText();
            String pwd     = pwdtextField.getText();
            String name    = nametextField.getText();
            String email   = emailtextField.getText();
            String nic     = nictextField.getText();
            String dob     = dobtextField.getText();
            String dpt     = depttextField.getText();
            String phone   = phonetextField.getText(); // single phone — ADMIN table එකේම
            String no      = notextField.getText();
            String street  = streettextField.getText();
            String city    = citytextField.getText();

            // validate fields (if empty)
            if (name.isEmpty() || pwd.isEmpty() || dob.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all required fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return; // exit from lambda func.
            }

            // validate fields (if input data is correct or not)
            String err = null;
            if (!email.isEmpty() && (err = DataValidator.validateEmail(email)) != null ||
                    !nic.isEmpty()   && (err = DataValidator.validateNic(nic)) != null ||
                    !dob.isEmpty()   && (err = DataValidator.validateDob(dob)) != null) {
                JOptionPane.showMessageDialog(null, err, "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                // 1. add into USER table
                String q1 = "INSERT INTO USER (User_id, Password, Role, Profile_pic) VALUES (?, ?, 'Admin', ?)";
                PreparedStatement ps1 = con.prepareStatement(q1);
                ps1.setString(1, id);
                ps1.setString(2, pwd);
                ps1.setString(3, selectedImagePath);
                ps1.executeUpdate();

                // 2. add into ADMIN table — phone directly here, no separate phone table
                String q2 = "INSERT INTO ADMIN (Admin_id, Name, Email, Nic, Dob, Dpt_name, Phone, No, Street, City) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps2 = con.prepareStatement(q2);
                ps2.setString(1, id);
                ps2.setString(2, name);
                ps2.setString(3, email);
                ps2.setString(4, nic);
                ps2.setString(5, dob);
                ps2.setString(6, dpt);
                ps2.setString(7, phone);
                ps2.setString(8, no);
                ps2.setString(9, street);
                ps2.setString(10, city);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(null, "Admin created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Cannot add Admin!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // cancel btn
        cancelButton.addActionListener(e -> dispose());

    } // end of the constructor
}
