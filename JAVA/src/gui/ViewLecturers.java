package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewLecturers extends JFrame{
    private JPanel mainPanel;
    private JLabel title;
    private JScrollPane scrollPanel;
    private JTable table;
    private JPanel titlePanel;

    public ViewLecturers(){
        setContentPane(mainPanel);
        setTitle("View Lecturers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);
        loadLecturers();
    }

    private void loadLecturers(){
        String columns[]= {"ID", "Name", "Email", "NIC", "DOB", "Education", "Department", "No", "Street", "City"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        String q = "SELECT * FROM LECTURER";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Object row[]={
                        rs.getString("Lec_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
                        rs.getString("Education_lvl"),
                        rs.getString("Dpt_name"),
                        rs.getString("No"),
                        rs.getString("Street"),
                        rs.getString("City")
                };
                model.addRow(row);
            }
            table.setModel(model);
            table.getTableHeader().setBackground(Color.decode("#291c0e"));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
