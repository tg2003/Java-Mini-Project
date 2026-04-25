package dao;

import db.DBConnection;
import models.Medical;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {
    public List<Medical> getByStudent(String ugId) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, t.Name AS OfficerName " +
                "FROM MEDICAL m LEFT JOIN TECH_OFFICER t ON m.Techoff_id = t.Techoff_id " +
                "WHERE m.Ug_id = ? ORDER BY m.From_date DESC, m.Medical_id DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Medical(
                        rs.getInt("Medical_id"),
                        rs.getString("Ug_id"),
                        rs.getDate("From_date"),
                        rs.getDate("To_date"),
                        rs.getString("Status"),
                        rs.getString("Session_type"),
                        rs.getString("Techoff_id"),
                        rs.getString("OfficerName"),
                        rs.getString("C_code")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int createMedical(String ugId, java.sql.Date fromDate, java.sql.Date toDate,
                             String sessionType, String cCode) {
        String sql = "INSERT INTO MEDICAL (Ug_id, From_date, To_date, Status, Session_type, C_code) " +
                "VALUES (?, ?, ?, 'Pending', ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ugId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);
            ps.setString(4, sessionType);
            if (cCode == null || cCode.isBlank()) {
                ps.setNull(5, java.sql.Types.CHAR);
            } else {
                ps.setString(5, cCode);
            }
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updatePendingMedical(int medicalId, java.sql.Date fromDate, java.sql.Date toDate,
                                        String sessionType, String cCode) {
        String sql = "UPDATE MEDICAL SET From_date=?, To_date=?, Session_type=?, C_code=? " +
                "WHERE Medical_id=? AND Status='Pending'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ps.setString(3, sessionType);
            if (cCode == null || cCode.isBlank()) {
                ps.setNull(4, java.sql.Types.CHAR);
            } else {
                ps.setString(4, cCode);
            }
            ps.setInt(5, medicalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
