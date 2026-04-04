package gui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class EditAdminGUI extends JFrame {
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
    private JButton deleteButton;
    private JButton cancelButton;
    private JTextField depttextField;
    private JTextField contacttextField1;
    private JLabel contactLabel1;
    private String selectedImagePath = null;

    public EditAdminGUI(String adminId, String name, String email, String nic, String dob,
                        String dpt, String phone, String no, String street, String city,
                        String profilePic, ViewAdminGUI parent) {
        setContentPane(mainPanel);
        setTitle("Edit Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        //load data into fields
        idtextField.setText(adminId);
        idtextField.setEditable(false); //ID cant edit
        nametextField.setText(name);
        emailtextField.setText(email);
        nictextField.setText(nic);
        dobtextField.setText(dob);
        depttextField.setText(dpt);
        contacttextField1.setText(phone);
        notextField.setText(no);
        streettextField.setText(street);
        citytextField.setText(city);

        // load pp
        loadProfilePic(profilePic);

        //change pp btn
        changePPbtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(); // open file choosing window
            int result = fc.showOpenDialog(null);

            //if the user go to the Jfilechooser and cancel the operation,this won't execute.
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

        //save btn
        saveButton.addActionListener(e -> {
            String query = "UPDATE ADMIN SET Name=?, Email=?, Nic=?, Dob=?, Dpt_name=?, Phone=?, No=?, Street=?, City=? WHERE Admin_id=?";
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nametextField.getText());
                ps.setString(2, emailtextField.getText());
                ps.setString(3, nictextField.getText());
                ps.setString(4, dobtextField.getText());
                ps.setString(5, depttextField.getText());
                ps.setString(6, contacttextField1.getText());
                ps.setString(7, notextField.getText());
                ps.setString(8, streettextField.getText());
                ps.setString(9, citytextField.getText());
                ps.setString(10, idtextField.getText());
                ps.executeUpdate();

                //pp update
                if (selectedImagePath != null) {
                    String picQuery = "UPDATE USER SET Profile_pic=? WHERE User_id=?";
                    PreparedStatement ps2 = con.prepareStatement(picQuery);
                    ps2.setString(1, selectedImagePath);
                    ps2.setString(2, idtextField.getText());
                    ps2.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // parent table refresh
                parent.loadAdmins();
                dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //delete btn
        deleteButton.addActionListener(e -> {
            int confirmMsg = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmMsg == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM USER WHERE User_id=?";
                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, idtextField.getText());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // parent table refresh
                    parent.loadAdmins();
                    dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //cancel btn
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadProfilePic(String path) {
        if (path != null && !path.isEmpty()) {
            // creating absolute path
            File file = new File(System.getProperty("user.dir") + "/JAVA/" + path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                pplabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                pplabel.setText("");
            } else {
                pplabel.setText("No Photo1");
                System.out.println("File not found: >" + file.getAbsolutePath() + "<");
            }
        } else {
            pplabel.setText("No Photo2");
        }
    }
}