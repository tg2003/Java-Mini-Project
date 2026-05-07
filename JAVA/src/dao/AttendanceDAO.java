package dao;

import db.DBConnection;
import models.Attendance;
import models.AttendanceCourseDetails;
import models.AttendanceCourseSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    /** Full attendance history for a student*/
    public List<Attendance> getByStudent(String ugId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.Week_no, a.Ug_id, a.Timetable_id, a.Date, " +
                "a.Techoff_id, a.Status, " +
                "t.Day, t.Start_time, t.End_time, t.C_code, t.Type, " +
                "c.C_name " +
                "FROM ATTENDANCE a " +
                "JOIN TIMETABLE t ON a.Timetable_id = t.Timetable_id " +
                "JOIN COURSE c ON t.C_code = c.C_code " +
                "WHERE a.Ug_id = ? " +
                "ORDER BY a.Date DESC, a.Timetable_id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("Week_no"),
                        rs.getString("Ug_id"),
                        rs.getInt("Timetable_id"),
                        rs.getDate("Date"),
                        rs.getString("Status"),
                        rs.getString("Day"),
                        rs.getString("Start_time"),
                        rs.getString("End_time"),
                        rs.getString("C_code"),
                        rs.getString("C_name"),
                        rs.getString("Type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Attendance> getAbsentSessionsInRange(String ugId, Date fromDate, Date toDate) {
        List<Attendance> list = new ArrayList<>();
        String sql =
                "SELECT a.Week_no, a.Ug_id, a.Timetable_id, a.Date, " +
                        "a.Status, t.Day, t.Start_time, t.End_time, t.C_code, t.Type, c.C_name " +
                        "FROM ATTENDANCE a " +
                        "JOIN TIMETABLE t ON a.Timetable_id = t.Timetable_id " +
                        "JOIN COURSE c ON t.C_code = c.C_code " +
                        "WHERE a.Ug_id = ? " +
                        "AND a.Status = 'Absent' " +
                        "AND a.Date BETWEEN ? AND ? " +
                        // exclude sessions that already have a MEDICAL record submitted
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM MEDICAL m " +
                        "  WHERE m.Ug_id = a.Ug_id " +
                        "  AND m.C_code = t.C_code " +
                        "  AND a.Date BETWEEN m.From_date AND m.To_date" +
                        ") " +
                        "ORDER BY a.Date ASC, t.Start_time";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("Week_no"),
                        rs.getString("Ug_id"),
                        rs.getInt("Timetable_id"),
                        rs.getDate("Date"),
                        rs.getString("Status"),
                        rs.getString("Day"),
                        rs.getString("Start_time"),
                        rs.getString("End_time"),
                        rs.getString("C_code"),
                        rs.getString("C_name"),
                        rs.getString("Type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Attendance percentage for a student across all sessions. */
    public double getAttendancePercentage(String ugId) {
        String sql = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN Status IN ('Present','MedicalApproved') THEN 1 ELSE 0 END) AS attended " +
                "FROM ATTENDANCE WHERE Ug_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total    = rs.getInt("total");
                int attended = rs.getInt("attended");
                if (total == 0) return 0.0;
                return (attended * 100.0) / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<AttendanceCourseSummary> getCourseSummaries(String ugId) {
        List<AttendanceCourseSummary> list = new ArrayList<>();
        String sql = "SELECT c.C_code, c.C_name, " +
                "COALESCE(SUM(CASE WHEN a.Ug_id IS NOT NULL " +
                "THEN TIME_TO_SEC(TIMEDIFF(t.End_time, t.Start_time)) / 3600 ELSE 0 END), 0) AS total_hours, " +
                "COALESCE(SUM(CASE WHEN a.Status IN ('Present','MedicalApproved') " +
                "THEN TIME_TO_SEC(TIMEDIFF(t.End_time, t.Start_time)) / 3600 ELSE 0 END), 0) AS attended_hours " +
                "FROM ENROLLS_IN e " +
                "JOIN COURSE c ON c.C_code = e.C_code " +
                "LEFT JOIN TIMETABLE t ON t.C_code = e.C_code " +
                "LEFT JOIN ATTENDANCE a ON a.Timetable_id = t.Timetable_id AND a.Ug_id = e.Ug_id " +
                "WHERE e.Ug_id = ? " +
                "GROUP BY c.C_code, c.C_name " +
                "ORDER BY c.C_code";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double totalHours    = rs.getDouble("total_hours");
                double attendedHours = rs.getDouble("attended_hours");
                double percentage    = totalHours == 0 ? 0.0 : (attendedHours * 100.0) / totalHours;
                list.add(new AttendanceCourseSummary(
                        rs.getString("C_code"),
                        rs.getString("C_name"),
                        attendedHours,
                        totalHours,
                        percentage
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public AttendanceCourseDetails getCourseDetails(String ugId, String cCode) {
        String courseName = cCode;
        List<Attendance> sessions = new ArrayList<>();
        String sql = "SELECT a.Week_no, a.Ug_id, a.Timetable_id, a.Date, " +
                "a.Status, t.Day, t.Start_time, t.End_time, t.C_code, t.Type, c.C_name " +
                "FROM ATTENDANCE a " +
                "JOIN TIMETABLE t ON a.Timetable_id = t.Timetable_id " +
                "JOIN COURSE c ON t.C_code = c.C_code " +
                "WHERE a.Ug_id = ? AND t.C_code = ? " +
                "ORDER BY a.Date DESC, t.Start_time";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ps.setString(2, cCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courseName = rs.getString("C_name");
                sessions.add(new Attendance(
                        rs.getInt("Week_no"),
                        rs.getString("Ug_id"),
                        rs.getInt("Timetable_id"),
                        rs.getDate("Date"),
                        rs.getString("Status"),
                        rs.getString("Day"),
                        rs.getString("Start_time"),
                        rs.getString("End_time"),
                        rs.getString("C_code"),
                        rs.getString("C_name"),
                        rs.getString("Type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double totalHours = 0.0, attendedHours = 0.0;
        for (Attendance s : sessions) {
            double h = s.getDurationHours();
            totalHours += h;
            if ("Present".equals(s.getStatus()) || "MedicalApproved".equals(s.getStatus()))
                attendedHours += h;
        }
        double percentage = totalHours == 0 ? 0.0 : (attendedHours * 100.0) / totalHours;
        return new AttendanceCourseDetails(cCode, courseName, sessions, attendedHours, totalHours, percentage);
    }
}