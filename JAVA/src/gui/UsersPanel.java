package gui;

import javax.swing.*;

public class UsersPanel extends JPanel{
    private JPanel usersMainPanel;
    private JButton viewAdminButton;
    private JButton viewLecturersButton;
    private JButton viewTechnicalOfficersButton;
    private JButton viewUndergraduatesButton;
    private JButton editAdminButton;
    private JButton editLecturersButton;
    private JButton editTechnicalOfficersButton;
    private JButton editUndergraduatesButton;
    private JButton createButton;
    private JPanel viewPanel;
    private JPanel editPanel;
    private JPanel createPanel;
    private JLabel edituserLabel;
    private JLabel viewuserLabel;
    private JLabel createLabel;

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
    }
}
