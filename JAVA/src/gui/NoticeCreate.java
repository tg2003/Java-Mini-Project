package gui;

import db.DBConnection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NoticeCreate extends JFrame {
    private JLabel title;
    private JPanel titlePanel;
    private JPanel contentPanel;
    private JTextField titleTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton uploadNewFileButton;
    private JPanel mainPanel;
    private String selectedFilePath =null;

    public NoticeCreate(){
        setContentPane(mainPanel);
        setTitle("Create a new notice");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        //upload new file
        uploadNewFileButton.addActionListener(e->{
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);

            if(result == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                System.out.println("System path: {" +System.getProperty("user.dir")+"} ...My manual path");
                File destFile = new File(System.getProperty("user.dir") + "/JAVA/src/resources/notice/" + f.getName());
                try {
                    java.nio.file.Files.copy(
                            f.toPath(),
                            destFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("File copy error : "+ex.getMessage());
                }
                selectedFilePath=  "resources/notice/" + f.getName();
            }
        });

        //save btn
        saveButton.addActionListener(e->{
            String query = "INSERT INTO notice (Title, Download_link) VALUES (?, ?)";
            try(Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,titleTextField.getText());
                ps.setString(2,selectedFilePath);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Notice created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        });

        // cancel btn
        cancelButton.addActionListener(e -> dispose());


    }
}
