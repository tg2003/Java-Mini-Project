package dao;

import db.DBConnection;
import models.Marks;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {
    public List<Marks> getByStudent(String ugId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, c.C_name FROM MARKS m " +
                     "JOIN COURSE c ON m.C_code = c.C_code " +
                     "WHERE m.Ug_id = ? ORDER BY c.C_code";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Marks(
                    rs.getInt("Mark_id"), rs.getString("Ug_id"),
                    rs.getString("C_code"), rs.getString("C_name"),
                    rs.getObject("Quiz01")      != null ? rs.getDouble("Quiz01")      : null,
                    rs.getObject("Quiz02")      != null ? rs.getDouble("Quiz02")      : null,
                    rs.getObject("Quiz03")      != null ? rs.getDouble("Quiz03")      : null,
                    rs.getObject("Assignment1") != null ? rs.getDouble("Assignment1") : null,
                    rs.getObject("Assignment2") != null ? rs.getDouble("Assignment2") : null,
                    rs.getObject("Project")     != null ? rs.getDouble("Project")     : null,
                    rs.getObject("Mid_T")       != null ? rs.getDouble("Mid_T")       : null,
                    rs.getObject("Mid_P")       != null ? rs.getDouble("Mid_P")       : null,
                    rs.getObject("Final_T")     != null ? rs.getDouble("Final_T")     : null,
                    rs.getObject("Final_P")     != null ? rs.getDouble("Final_P")     : null,
                    rs.getString("Attempt_type")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
