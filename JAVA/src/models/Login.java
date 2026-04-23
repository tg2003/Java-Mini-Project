package models;
import db.DBConnection;
import java.sql.Connection;
import java.sql.*;

//return ROLE - which related to the username+pwd
public class Login {
    public static String login(String userId, String password){
        String query = "SELECT Role FROM User WHERE User_id= ? AND Password = ?";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,userId);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getString("Role");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
