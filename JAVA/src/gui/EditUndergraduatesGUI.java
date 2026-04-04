package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditUndergraduatesGUI extends JFrame{
    private JPanel mainPanel;
    private JLabel pplabel;
    private JButton changePPbtn;
    private JTextField idtextField;
    private JTextField nametextField;
    private JTextField nictextField;
    private JTextField dobtextField;
    private JTextField depttextField;
    private JTextField notextField;
    private JTextField streettextField;
    private JTextField citytextField;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton cancelButton;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel deptLabel;
    private JLabel noLabel;
    private JLabel cityLabel;
    private JLabel streetLabel;
    private JLabel nicLabel;
    private JLabel dobLabel;
    private JTextField emailtextField;
    private JTextField contacttextField1;
    private JTextField contacttextField2;
    private JLabel contactLabel1;
    private JLabel contactLabel2;
    private JTextField pwdtextField;
    private JLabel pwdLabel;
    private String selectedImagePath = null;

    public EditUndergraduatesGUI(String ugId, String name, String email, String nic, String dob, String dpt, String no,
                                 String street, String city, String profilePic, ViewUndergraduatesGUI parent){
        setContentPane(mainPanel);
        setTitle("Edit Undergraduate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        //load data into fileds
        idtextField.setText(ugId);
        idtextField.setEditable(false); //ID cant edit
        nametextField.setText(name);
        emailtextField.setText(email);
        nictextField.setText(nic);
        dobtextField.setText(dob);
        depttextField.setText(dpt);
        notextField.setText(no);
        streettextField.setText(street);
        citytextField.setText(city);

        // load pp
        loadProfilePic(profilePic);

        // load contact no
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT Phone FROM UNDERGRADUATE_PHONE WHERE Ug_id = ?"
            );
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) contacttextField1.setText(rs.getString("Phone"));
            if (rs.next()) contacttextField2.setText(rs.getString("Phone"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //change pp btn
        changePPbtn.addActionListener(e->{
            JFileChooser fc = new JFileChooser(); // open file choosing window
            int result= fc.showOpenDialog(null);

            //if the user go to the Jfilechooser and cancel the operation,this won't execute.
            if (result==JFileChooser.APPROVE_OPTION){
                System.out.println("Approved opt"); // debug - chk whether we are inside the if{}
                File file = fc.getSelectedFile();

                System.out.println("directory path: {" +System.getProperty("user.dir")+"} ...My manual path");//Debug - the current path
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
                ImageIcon icon = new ImageIcon(new ImageIcon(selectedImagePath).getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH));
                pplabel.setIcon(icon);
            }
        });

        //save btn
        saveButton.addActionListener(e -> {
            String query = "UPDATE UNDERGRADUATE SET Name=?, Email=?, Nic=?, Dob=?, Dpt_name=?, No=?, Street=?, City=? WHERE Ug_id=?";
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nametextField.getText());
                ps.setString(2, emailtextField.getText());
                ps.setString(3, nictextField.getText());
                ps.setString(4, dobtextField.getText());
                ps.setString(5, depttextField.getText());
                ps.setString(6, notextField.getText());
                ps.setString(7, streettextField.getText());
                ps.setString(8, citytextField.getText());
                ps.setString(9, idtextField.getText());
                ps.executeUpdate();
                // password update - if a new pwd has set
                if (!pwdtextField.getText().isEmpty()) {
                    String pwdQuery = "UPDATE USER SET Password=? WHERE User_id=?";
                    PreparedStatement psPwd = con.prepareStatement(pwdQuery);
                    psPwd.setString(1, pwdtextField.getText());
                    psPwd.setString(2, idtextField.getText());
                    psPwd.executeUpdate();
                }
                //Delete phone numbers and reinsert data
                String deletePhone = "DELETE FROM UNDERGRADUATE_PHONE WHERE Ug_id = ?";
                PreparedStatement psDel = con.prepareStatement(deletePhone);
                psDel.setString(1, idtextField.getText());
                psDel.executeUpdate();

                // save phone number update on click 'save'
                if (!contacttextField1.getText().isEmpty()) {
                    PreparedStatement psP1 = con.prepareStatement(
                            "INSERT INTO UNDERGRADUATE_PHONE (Ug_id, Phone) VALUES (?, ?)");
                    psP1.setString(1, idtextField.getText());
                    psP1.setString(2, contacttextField1.getText());
                    psP1.executeUpdate();
                }

                if (!contacttextField2.getText().isEmpty()) {
                    PreparedStatement psP2 = con.prepareStatement(
                            "INSERT INTO UNDERGRADUATE_PHONE (Ug_id, Phone) VALUES (?, ?)");
                    psP2.setString(1, idtextField.getText());
                    psP2.setString(2, contacttextField2.getText());
                    psP2.executeUpdate();
                }


                //pp update
                if(selectedImagePath!=null){
                    String picQuery = "UPDATE USER SET Profile_pic=? WHERE User_id=?";
                    PreparedStatement ps2 = con.prepareStatement(picQuery);
                    ps2.setString(1,selectedImagePath);
                    ps2.setString(2,idtextField.getText());
                    ps2.executeUpdate();
                }
                JOptionPane.showMessageDialog(null, "Updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // parent table refresh
                parent.loadUndergraduates();
                dispose();


            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //delete btn
        deleteButton.addActionListener(e->{
            int confirmMsg = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmMsg==JOptionPane.YES_OPTION){
                String query = "DELETE FROM USER WHERE User_id=?";
                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, idtextField.getText());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // parent table refresh
                    parent.loadUndergraduates();
                    dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

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
                System.out.println("File not found: >" + file.getAbsolutePath()+"<"); // debug
            }
        } else {
            pplabel.setText("No Photo2");
        }
    }

}
