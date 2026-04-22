import gui.AdminGUI;
import gui.LoginGUI;
import gui.TechOfficerDashboard;
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
        new TechOfficerDashboard(). setVisible(true);

    }//end of main method
}//end of main cls