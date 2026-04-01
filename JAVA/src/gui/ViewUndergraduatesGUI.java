package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewUndergraduatesGUI extends JFrame{
    private JPanel mainPanel;
    private JTable table;
    private JScrollPane scrollPanel;
    private JLabel title;

    public ViewUndergraduatesGUI(){
        setContentPane(mainPanel);
        setTitle("View Undergraduates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);
        loadUndergraduates();
    }

    private void loadUndergraduates(){
        String[] columns = {"ID", "Name", "Email", "NIC", "DOB", "Department", "No", "Street", "City"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        String query = "SELECT Ug_id, Name, Email, Nic, Dob, Dpt_name, No, Street, City FROM UNDERGRADUATE";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery(query);

            while(rs.next()){
                Object row[]={
                        rs.getString("Ug_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
                        rs.getString("Dpt_name"),
                        rs.getString("No"),
                        rs.getString("Street"),
                        rs.getString("City")
                };
                model.addRow(row);
            }
            table.getTableHeader().setBackground(Color.decode("#291c0e"));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setPreferredSize(new Dimension(0, 35));
            table.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
