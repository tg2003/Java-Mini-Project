package dao;

import db.DBConnection;
import models.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {
    public List<Notice> getAll() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM NOTICE ORDER BY Date_time DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notice(
                    rs.getInt("Notice_no"), rs.getString("Title"),
                    rs.getTimestamp("Date_time"), rs.getString("Download_link")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
