package gui;

import db.DBConnection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NoticeEdit extends JFrame {
    private JPanel titlePanel;
    private JLabel title;
    private JPanel contentPanel;
    private JTextField noticenoTextField;
    private JTextField titleTextField;
    private JTextField datatimeTextField;
    private JTextField linkTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JPanel mainPanel;
    private JButton uploadNewFileButton;
    private String selectedFilePath = null;

    public NoticeEdit(String noticeNo, String title, String dateTime, String downloadLink, NoticeView parent){
        setContentPane(mainPanel);
        setTitle("Edit Undergraduate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        //load data into fileds
        noticenoTextField.setText(noticeNo);
        noticenoTextField.setEditable(false);
        titleTextField.setText(title);
        datatimeTextField.setText(dateTime);
        linkTextField.setText(downloadLink);

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
                linkTextField.setText(selectedFilePath);
            }
        });

        //save btn
        saveButton.addActionListener(e->{
            String q = "UPDATE notice SET Title = ?, Date_time = ?, Download_link = ? WHERE Notice_no = ?";
            try(Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1,titleTextField.getText());
                ps.setString(2,datatimeTextField.getText());
                ps.setString(3,linkTextField.getText());
                ps.setInt(4,Integer.parseInt(noticenoTextField.getText()));

                int result = ps.executeUpdate();
                if(result>0){
                    JOptionPane.showMessageDialog(this, "Updated Successfully! ✅","Success",JOptionPane.INFORMATION_MESSAGE);
                    parent.loadNotices();
                    dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //delete button
        deleteButton.addActionListener(e->{
            int confirmMsg = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this note?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmMsg==JOptionPane.YES_OPTION){
                String q = "DELETE FROM NOTICE WHERE Notice_no=?";

                try (Connection con = DBConnection.getConnection();){
                    PreparedStatement ps = con.prepareStatement(q);
                    ps.setString(1,noticenoTextField.getText());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Note Deleted Successfully","Success Message",JOptionPane.INFORMATION_MESSAGE);
                    // parent table refresh
                    parent.loadNotices();
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        //cancel button
        cancelButton.addActionListener(e -> dispose());
    }
}
