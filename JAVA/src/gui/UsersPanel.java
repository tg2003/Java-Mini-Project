package gui;

import javax.swing.*;
import java.sql.SQLException;

public class UsersPanel extends JPanel{
    private JPanel usersMainPanel;
    private JButton viewAdminButton;
    private JButton viewLecturersButton;
    private JButton viewTechnicalOfficersButton;
    private JButton viewUndergraduatesButton;
    private JPanel viewPanel;
    private JPanel createPanel;
    private JLabel viewuserLabel;
    private JLabel createLabel;
    private JButton createAAdminButton;
    private JButton createALecturerButton;
    private JButton createATechnicalOfficerButton;
    private JButton createAUndergraduateButton;

    public UsersPanel() {
        add(usersMainPanel);

        //view admin
        viewAdminButton.addActionListener(e->{
            new ViewAdminGUI().setVisible(true);
        });
        //view UG
        viewUndergraduatesButton.addActionListener(e->{
            new ViewUndergraduatesGUI().setVisible(true);
        });

        //view Lec
        viewLecturersButton.addActionListener(e -> {
            new ViewLecturers().setVisible(true);
        });

        //view TO
        viewTechnicalOfficersButton.addActionListener(e -> {
            new ViewTOGUI().setVisible(true);
        });

        //create new user - admin
        createAAdminButton.addActionListener(e->{
            new CreateAdmin().setVisible(true);
        });

        //create new user - UG
        createAUndergraduateButton.addActionListener(e->{
            new CreateUG().setVisible(true);
        });

        //create new user - Lec
        createALecturerButton.addActionListener(e->{
            new CreateLecturer().setVisible(true);
        });

        //create new user - TO
        createATechnicalOfficerButton.addActionListener(e->{
            new CreateTO().setVisible(true);
        });
    }

}
