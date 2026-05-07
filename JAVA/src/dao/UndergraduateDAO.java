package dao;

import db.DBConnection;
import models.Undergraduate;
import java.sql.*;

public class UndergraduateDAO {

    public static Undergraduate getById(String ugId) {

        // JOIN USER table to fetch Profile_pic
        String sql = "SELECT ug.Ug_id, ug.Name, ug.Email, ug.Nic, ug.Dob, " +
                "ug.Dpt_name, ug.No, ug.Street, ug.City, " +
                "u.Profile_pic " +
                "FROM UNDERGRADUATE ug " +
                "JOIN USER u ON u.User_id = ug.Ug_id " +
                "WHERE ug.Ug_id = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Undergraduate ug = new Undergraduate(
                        rs.getString("Ug_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getDate("Dob"),
                        rs.getString("Dpt_name"),
                        rs.getString("No"),
                        rs.getString("Street"),
                        rs.getString("City")
                );
                // set profile pic from DB
                ug.setProfilePic(rs.getString("Profile_pic"));
                return ug;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}