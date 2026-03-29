import gui.LoginGUI;
import db.DBConnection;
import java.sql.Connection;
import gui.LoginGUI;
import models.Login;

public class Main {
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