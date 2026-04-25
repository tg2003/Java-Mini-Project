import db.DBConnection;
import gui.AdminGUI;
import gui.LecturerDashboard;
import gui.LoginGUI;

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

        new LoginGUI().setVisible(true);
<<<<<<< HEAD
        //new AdminGUI().setVisible(true); //this is a test line.delete after the work done
        //new TechOfficerDashboard("TO0002").setVisible(true);
=======
        //new AdminGUI().setVisible(true);

        //new LecturerDashboard().setVisible(true);
        // this is a test line.delete after the work done

>>>>>>> 87b52a52f090d60432d7b48c10fce0f637156320

    }//end of main method
}//end of main cls