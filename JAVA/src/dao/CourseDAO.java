package dao;

import db.DBConnection;
import models.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for COURSE + ENROLLS_IN + COURSE_MATERIAL tables.
 */
public class CourseDAO {

    /** All courses the student is enrolled in. */
    public List<Course> getEnrolledCourses(String ugId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.C_code, c.C_name, c.Credit, " +
                     "e.Status, e.Sem, e.Batch_yr, e.Level " +
                     "FROM COURSE c " +
                     "JOIN ENROLLS_IN e ON c.C_code = e.C_code " +
                     "WHERE e.Ug_id = ? " +
                     "ORDER BY c.C_code";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Course(
                    rs.getString("C_code"),
                    rs.getString("C_name"),
                    rs.getString("Credit"),
                    rs.getString("Status"),
                    rs.getInt("Sem"),
                    rs.getInt("Batch_yr"),
                    rs.getInt("Level")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Materials for a specific course. */
    public List<String> getMaterials(String cCode) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT Material FROM COURSE_MATERIAL WHERE C_code = ? ORDER BY Material";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("Material"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
