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

    public Undergraduate login(String userId, String password) {
        String sql = "SELECT u.User_id, u.Password, u.Role, u.Profile_pic, " +
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
        String sql = "SELECT u.User_id, u.Password, u.Role, u.Profile_pic, " +
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

    // ── Profile Picture ────────────────────────────────────────────────────────
    /**
     * Updates the Profile_pic path in the USER table for the given userId.
     * @param userId  the user's ID (e.g. "TG1701")
     * @param picPath the file path to store (e.g. "resources/userPP/TG1701.png")
     * @return true if the update succeeded
     */
    public boolean updateProfilePic(String userId, String picPath) {
        String sql = "UPDATE USER SET Profile_pic = ? WHERE User_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, picPath);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the stored Profile_pic path for the given userId.
     * Returns null if no picture is set.
     */
    public String getProfilePic(String userId) {
        String sql = "SELECT Profile_pic FROM USER WHERE User_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Profile_pic");
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
        Undergraduate ug = new Undergraduate(
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
        ug.setProfilePic(rs.getString("Profile_pic"));
        return ug;
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

    public boolean updateContactDetails(String ugId, String email,
                                        String houseNo, String street, String city) {
        String sql = "UPDATE UNDERGRADUATE SET Email=?, No=?, Street=?, City=? WHERE Ug_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, houseNo);
            ps.setString(3, street);
            ps.setString(4, city);
            ps.setString(5, ugId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addPhone(String ugId, String phone) {
        String sql = "INSERT IGNORE INTO UNDERGRADUATE_PHONE (Ug_id, Phone) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ps.setString(2, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePhone(String ugId, String phone) {
        String sql = "DELETE FROM UNDERGRADUATE_PHONE WHERE Ug_id=? AND Phone=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ps.setString(2, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
