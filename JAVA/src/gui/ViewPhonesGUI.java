package gui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;
public class ViewPhonesGUI extends JFrame {
    private JPanel mainPanel;
    private JLabel phone1;
    private JLabel phone2;

    public ViewPhonesGUI(String id, String tableName, String idColumn) {
        setContentPane(mainPanel);
        setTitle("Phone Numbers");
        setSize(100,80 );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT Phone FROM " + tableName + " WHERE " + idColumn + " = ?"
            );
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) phone1.setText(rs.getString("Phone"));
            if (rs.next()) phone2.setText(rs.getString("Phone"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
