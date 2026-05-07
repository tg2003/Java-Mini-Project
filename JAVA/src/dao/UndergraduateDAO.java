package dao;

import db.DBConnection;
import models.Undergraduate;
import java.sql.*;

public class UndergraduateDAO {

    public static Undergraduate getById(String ugId) {

        // ── FIX: JOIN USER table to fetch Profile_pic ──────────────────────
        // Old query only read UNDERGRADUATE table so Profile_pic was ALWAYS
        // null after login — even though the path was saved in the DB.
        // ───────────────────────────────────────────────────────────────────
        String sql = "SELECT ug.Ug_id, ug.Name, ug.Email, ug.Nic, ug.Dob, " +
                "ug.Dpt_name, ug.No, ug.Street, ug.City, " +
                "u.Profile_pic " +                          // ← added
                "FROM UNDERGRADUATE ug " +
                "JOIN USER u ON u.User_id = ug.Ug_id " +   // ← added
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
                // ── FIX: set profile pic from DB ───────────────────────────
                ug.setProfilePic(rs.getString("Profile_pic"));  // ← added
                // ──────────────────────────────────────────────────────────
                return ug;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}