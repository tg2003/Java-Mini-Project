package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAdminGUI extends JFrame{
    private JPanel mainPanel;
    private JLabel title;
    private JScrollPane scrollPanel;
    private JTable table;

    public ViewAdminGUI(){
        setContentPane(mainPanel);
        setTitle("View Admins");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);
        loadAdmins();
    }

    private void loadAdmins(){
        String[] columns = {"ID", "Name", "Email", "NIC", "DOB", "Department", "Phone", "No", "Street", "City"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String query = "SELECT a.Admin_id, a.Name, a.Email, a.Nic, a.Dob, a.Dpt_name, a.Phone, a.No, a.Street, a.City " +
                "FROM ADMIN a JOIN USER u ON a.Admin_id = u.User_id";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Object row[]={
                        rs.getString("Admin_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
                        rs.getString("Dpt_name"),
                        rs.getString("Phone"),
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
            throw new RuntimeException(e);
        }
    }

}
