import db.DBConnection;
import gui.LoginGUI;

import java.sql.Connection;

public class                    Main {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                System.out.println("Database connected successfully!");
            }
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }

        new LoginGUI().setVisible(true);
    }//end of main method
}//end of main cls