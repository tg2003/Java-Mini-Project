package dao;

import db.DBConnection;
import models.Timetable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    public List<Timetable> getForStudent(String ugId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT tt.Timetable_id, tt.Day, tt.Start_time, tt.End_time, " +
                     "tt.C_code, tt.Type, c.C_name " +
                     "FROM TIMETABLE tt " +
                     "JOIN COURSE c ON tt.C_code = c.C_code " +
                     "JOIN ENROLLS_IN e ON tt.C_code = e.C_code " +
                     "WHERE e.Ug_id = ? " +
                     "ORDER BY FIELD(tt.Day,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), tt.Start_time";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ugId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Timetable(
                    rs.getInt("Timetable_id"), rs.getString("Day"),
                    rs.getString("Start_time"), rs.getString("End_time"),
                    rs.getString("C_code"), rs.getString("C_name"), rs.getString("Type")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
