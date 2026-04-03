package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTOGUI extends JFrame{
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JLabel title;
    private JScrollPane scrollPanel;
    private JTable table;

    public ViewTOGUI() {
        setContentPane(mainPanel);
        setTitle("View Technical Officers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        loadTechOfficers();
    }

    private void loadTechOfficers() {
        String[] columns = {"ID", "Name", "Email", "NIC", "DOB", "Phone", "No", "Street", "City"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String query = "SELECT * FROM TECH_OFFICER";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("Techoff_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
