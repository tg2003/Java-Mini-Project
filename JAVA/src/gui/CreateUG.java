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

public class CreateUG extends JFrame {
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
    private JTextField contacttextField1;
    private JTextField contacttextField2;
    private JLabel contactLabel1;
    private JLabel contactLabel2;
    private JTextField pwdtextField;
    private JLabel pwdLabel;
    private String selectedImagePath = null;

    //constructor
    public CreateUG(){
        setContentPane(mainPanel);
        setTitle("Create a new Undergraduate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600,700);
        setLocationRelativeTo(null);

        // auto generate UG ID
        try (Connection con = DBConnection.getConnection()){
            String q = "SELECT User_id FROM USER WHERE Role='Undergraduate' ORDER BY User_id DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            String newid;

            if(rs.next()){
                String lastidInDB = rs.getString("User_id");
                int num =Integer.parseInt(lastidInDB.substring(2));//remove 'UG' in id and save only the numeric part
                newid = String.format("UG%04d",num+1);
            }else {
                newid = "UG0001"; // setting first UG
            }
            idtextField.setText(newid);
        } catch (SQLException e) {
            System.out.println("Connection error: "+e.getMessage());
        }// End of the - auto generate UG ID
        idtextField.setEditable(false); // UG is automatically setted. cant edit

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

        saveButton.addActionListener(e->{
            //taking values from the form
            String id       = idtextField.getText();
            String pwd       = pwdtextField.getText();
            String name     = nametextField.getText();
            String email    = emailtextField.getText();
            String nic      = nictextField.getText();
            String dob      = dobtextField.getText();
            String dpt      = depttextField.getText();
            String no       = notextField.getText();
            String street   = streettextField.getText();
            String city     = citytextField.getText();
            String contact1 = contacttextField1.getText();
            String contact2 = contacttextField2.getText();

            // validate fields (if empty)
            if (name.isEmpty() || pwd.isEmpty() || dob.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all required fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;//exit from lambda func.
            }
            // validate fields (if input data is correct or not)
            String err = null;
            if (!email.isEmpty() && (err = DataValidator.validateEmail(email)) != null ||
                    !nic.isEmpty()   && (err = DataValidator.validateNic(nic)) != null ||
                    !dob.isEmpty()   && (err = DataValidator.validateDob(dob)) != null) {
                JOptionPane.showMessageDialog(null, err, "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = DBConnection.getConnection()){
                //1. add into USER table
                String q1 = "INSERT INTO USER (User_id, Password, Role, Profile_pic) VALUES (?, ?, 'Undergraduate', ?)";
                PreparedStatement ps1 = con.prepareStatement(q1);
                ps1.setString(1,id);
                ps1.setString(2,pwd);
                ps1.setString(3,selectedImagePath);
                ps1.executeUpdate();

                //2. add into UNDERGRADUATE table
                String q2 = "INSERT INTO UNDERGRADUATE (Ug_id, Name, Email, Nic, Dob, Dpt_name, No, Street, City) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps2 = con.prepareStatement(q2);
                ps2.setString(1, id);
                ps2.setString(2, nametextField.getText());
                ps2.setString(3, emailtextField.getText());
                ps2.setString(4, nictextField.getText());
                ps2.setString(5, dobtextField.getText());
                ps2.setString(6, depttextField.getText());
                ps2.setString(7, notextField.getText());
                ps2.setString(8, streettextField.getText());
                ps2.setString(9, citytextField.getText());
                ps2.executeUpdate();

                //3. add into UNDERGRADUATE_PHONE table
                String q3 = "INSERT INTO UNDERGRADUATE_PHONE (Ug_id, Phone) VALUES (?, ?)";
                PreparedStatement ps3 = con.prepareStatement(q3);
                if (!contact1.isEmpty()) {
                    ps3.setString(1, id);
                    ps3.setString(2, contact1); // contact 1
                    ps3.executeUpdate();
                }
                if (!contact2.isEmpty()) {
                    ps3.setString(1, id);
                    ps3.setString(2, contact2); // contact 2
                    ps3.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Undergraduate created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Canot add User", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

    }//end of the constructor

}//end of the cls
