package dao;

import db.DBConnection;
import models.Undergraduate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

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

    public int getCourseCount(String ugId) {
        String sql = "SELECT COUNT(*) FROM ENROLLS_IN WHERE Ug_id = ?";
        return scalarInt(sql, ugId);
    }

    public int getUnreadNoticeCount() {
        String sql = "SELECT COUNT(*) FROM NOTICE";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Math.min(rs.getInt(1), 99);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateProfile(Undergraduate student, List<String> phones) {
        String updateStudent = "UPDATE UNDERGRADUATE SET Name=?, Email=?, Nic=?, Dob=?, No=?, Street=?, City=? WHERE Ug_id=?";
        String deletePhones = "DELETE FROM UNDERGRADUATE_PHONE WHERE Ug_id=?";
        String insertPhone = "INSERT INTO UNDERGRADUATE_PHONE (Ug_id, Phone) VALUES (?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(updateStudent)) {
                ps.setString(1, student.getName());
                ps.setString(2, student.getEmail());
                ps.setString(3, student.getNic());
                ps.setDate(4, student.getDob());
                ps.setString(5, student.getHouseNo());
                ps.setString(6, student.getStreet());
                ps.setString(7, student.getCity());
                ps.setString(8, student.getUgId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(deletePhones)) {
                ps.setString(1, student.getUgId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(insertPhone)) {
                for (String phone : phones) {
                    if (phone == null || phone.isBlank()) continue;
                    ps.setString(1, student.getUgId());
                    ps.setString(2, phone.trim());
                    ps.executeUpdate();
                }
            }

            con.commit();
            con.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
            return false;
        }
    }

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
