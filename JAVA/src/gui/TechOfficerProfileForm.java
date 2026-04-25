package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class TechOfficerProfileForm extends JFrame {

    private JFormattedTextField TOoffficerProfileFormattedTextField;
    private JTextField tToId;
    private JTextField tName;
    private JTextField tEmail;
    private JTextField tNic;
    private JTextField tDob;
    private JTextField tAddress;
    private JPasswordField fPassword;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnBack;
    private JButton btnChange;
    private JPanel TOProfilePanel;
    private JTextField tPhone;
    private JLabel pplabel;

    private String techOffId;

    // ✅ store RELATIVE path only (IMPORTANT)
    private String imagePath = null;

    public TechOfficerProfileForm(String techOffId) {

        this.techOffId = techOffId;

        setTitle("Tech Officer Profile");
        setContentPane(TOProfilePanel);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pplabel.setPreferredSize(new Dimension(120, 120));

        tToId.setEditable(false);
        tEmail.setEditable(false);
        tNic.setEditable(false);
        tDob.setEditable(false);

        loadProfileData();

        // =========================
        // CHANGE IMAGE (FIXED VERSION)
        // =========================
        btnChange.addActionListener(e -> {

            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();

                try {
                    // ✅ destination inside project
                    String folderPath = "src/resources/userPP/";
                    File folder = new File(folderPath);

                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    File destFile = new File(folder, file.getName());

                    Files.copy(
                            file.toPath(),
                            destFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    // ✅ SAVE RELATIVE PATH ONLY (IMPORTANT FIX)
                    imagePath = "src/resources/userPP/" + file.getName();

                    // ✅ immediate UI update
                    setProfileImage(imagePath);

                    JOptionPane.showMessageDialog(this, "Profile Image Updated!");

                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Image upload failed!");
                }
            }
        });

        btnClear.addActionListener(e -> loadProfileData());
        btnSave.addActionListener(e -> saveProfile());

        btnBack.addActionListener(e -> {
            dispose();
            new TechOfficerDashboard(techOffId);
        });

        setVisible(true);
    }

    // =====================================
    // LOAD PROFILE DATA
    // =====================================
    private void loadProfileData() {
        try {
            Connection con = DBConnection.getConnection();

            String sql1 = "SELECT * FROM tech_officer WHERE Techoff_id=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setString(1, techOffId);

            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                tToId.setText(rs1.getString("Techoff_id"));
                tName.setText(rs1.getString("Name"));
                tEmail.setText(rs1.getString("Email"));
                tNic.setText(rs1.getString("Nic"));
                tDob.setText(rs1.getString("Dob"));
                tAddress.setText(rs1.getString("Street"));
                tPhone.setText(rs1.getString("No"));
            }

            String sql2 = "SELECT password, profile_pic FROM user WHERE user_id=?";
            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setString(1, techOffId);

            ResultSet rs2 = pst2.executeQuery();

            if (rs2.next()) {

                fPassword.setText(rs2.getString("password"));

                // ✅ IMPORTANT: load relative path
                imagePath = rs2.getString("profile_pic");

                if (imagePath != null && !imagePath.isEmpty()) {
                    setProfileImage(imagePath);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // IMAGE DISPLAY + FIX SIZE
    // =====================================
    private void setProfileImage(String path) {
        try {
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("Image not found: " + path);
                return;
            }

            ImageIcon icon = new ImageIcon(path);

            Image img = icon.getImage().getScaledInstance(
                    pplabel.getWidth() > 0 ? pplabel.getWidth() : 120,
                    pplabel.getHeight() > 0 ? pplabel.getHeight() : 120,
                    Image.SCALE_SMOOTH
            );

            pplabel.setIcon(new ImageIcon(img));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // SAVE PROFILE
    // =====================================
    private void saveProfile() {
        try {
            Connection con = DBConnection.getConnection();

            String name = tName.getText();
            String address = tAddress.getText();
            String phone = tPhone.getText();
            String password = new String(fPassword.getPassword());

            String sql1 = """
                UPDATE tech_officer 
                SET Name=?, Street=?, No=? 
                WHERE Techoff_id=?
            """;

            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setString(1, name);
            pst1.setString(2, address);
            pst1.setString(3, phone);
            pst1.setString(4, techOffId);
            pst1.executeUpdate();

            String sql2 = """
                UPDATE user 
                SET password=?, profile_pic=? 
                WHERE user_id=?
            """;

            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setString(1, password);
            pst2.setString(2, imagePath); // ✅ relative path saved
            pst2.setString(3, techOffId);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile!");
        }
    }
}