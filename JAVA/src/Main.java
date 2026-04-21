import gui.AdminGUI;
import gui.LoginGUI;
import db.DBConnection;
import java.sql.Connection;

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

        //new LoginGUI().setVisible(true);
        new AdminGUI().setVisible(true); //this is a test line.delete after the work done
        //in the checking period, comment out 'new AdminGUI().setVisible(true);' and add your Dashboard's constructor
        //-- add your constructor here

    }//end of main method
}//end of main cls