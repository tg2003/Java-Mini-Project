import gui.AdminGUI;
import gui.LoginGUI;

import db.DBConnection;
import gui.TechOfficerDashboard;

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

<<<<<<< HEAD
        //new LoginGUI().setVisible(true);
        new AdminGUI("A00001").setVisible(true); //this is a test line.delete after the work done
=======
        new LoginGUI().setVisible(true);
        //new AdminGUI().setVisible(true); //this is a test line.delete after the work done
        //new TechOfficerDashboard().setVisible(true);
>>>>>>> Maneesha

    }//end of main method
}//end of main cls