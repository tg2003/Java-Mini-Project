package dao;

import db.DBConnection;
import models.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {
    public List<Medical> getByStudent(String ugId) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, t.Name AS OfficerName " +
                     "FROM MEDICAL m LEFT JOIN TECH_OFFICER t ON m.Techoff_id = t.Techoff_id " +
                     "WHERE m.Ug_id = ? ORDER BY m.From_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Medical(
                    rs.getInt("Medical_id"), rs.getString("Ug_id"),
                    rs.getDate("From_date"), rs.getDate("To_date"),
                    rs.getString("Status"), rs.getString("Session_type"),
                    rs.getString("Techoff_id"), rs.getString("OfficerName"),
                    rs.getString("C_code")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
