package dao;

import db.DBConnection;
import models.Undergraduate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for UNDERGRADUATE table.
 * All queries are parameterised (PreparedStatement) to prevent SQL injection.
 */
public class StudentDAO {

    // ── Authentication ────────────────────────────────────────────────────────
    /**
     * Returns true and loads the Undergraduate if credentials match USER table.
     */
    public Undergraduate login(String userId, String password) {
        String sql = "SELECT u.User_id, u.Password, u.Role, " +
                     "ug.Name, ug.Email, ug.Nic, ug.Dob, ug.Dpt_name, " +
                     "ug.No, ug.Street, ug.City " +
                     "FROM USER u " +
                     "JOIN UNDERGRADUATE ug ON u.User_id = ug.Ug_id " +
                     "WHERE u.User_id = ? AND u.Password = ? AND u.Role = 'Undergraduate'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Profile ────────────────────────────────────────────────────────────────
    public Undergraduate getById(String ugId) {
        String sql = "SELECT u.User_id, u.Password, u.Role, " +
                     "ug.Name, ug.Email, ug.Nic, ug.Dob, ug.Dpt_name, " +
                     "ug.No, ug.Street, ug.City " +
                     "FROM USER u JOIN UNDERGRADUATE ug ON u.User_id = ug.Ug_id " +
                     "WHERE ug.Ug_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Phone numbers ──────────────────────────────────────────────────────────
    public List<String> getPhones(String ugId) {
        List<String> phones = new ArrayList<>();
        String sql = "SELECT Phone FROM UNDERGRADUATE_PHONE WHERE Ug_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) phones.add(rs.getString("Phone"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phones;
    }

    // ── Dashboard summary counts ───────────────────────────────────────────────
    public int getCourseCount(String ugId) {
        String sql = "SELECT COUNT(*) FROM ENROLLS_IN WHERE Ug_id = ?";
        return scalarInt(sql, ugId);
    }

    public int getUnreadNoticeCount() {
        // In this schema all notices are "unread"; count the 3 most recent.
        String sql = "SELECT COUNT(*) FROM NOTICE";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return Math.min(rs.getInt(1), 99);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Undergraduate mapRow(ResultSet rs) throws SQLException {
        return new Undergraduate(
            rs.getString("User_id"),
            rs.getString("Name"),
            rs.getString("Email"),
            rs.getString("Nic"),
            rs.getDate("Dob"),
            rs.getString("Dpt_name"),
            rs.getString("No"),
            rs.getString("Street"),
            rs.getString("City")
        );
    }

    private int scalarInt(String sql, String param) {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
