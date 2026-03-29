package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel sidePanel;
    private JPanel contentPanel;
    private JLabel usersLabel;
    private JLabel coursesLabel;
    private JLabel noticesLabel;
    private JLabel timetableLabel;
    private JLabel menuLabel;
    private JLabel logoutLabel;

    public AdminGUI (){
        setContentPane(mainPanel);
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);

        //setting the sidePanel
        //Users
        usersLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        usersLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                usersLabel.setForeground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e){
                usersLabel.setForeground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "users");
            }
        });

        //Courses
        coursesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        coursesLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                coursesLabel.setForeground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e){
                coursesLabel.setForeground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "courses");
            }
        });

        //Notice
        noticesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noticesLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                noticesLabel.setForeground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e){
                noticesLabel.setForeground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "notices");
            }
        });

        //timetable
        timetableLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        timetableLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                timetableLabel.setForeground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e){
                timetableLabel.setForeground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "timetable");
            }
        });

        //logout
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setForeground(Color.RED);
            }
            public void mouseExited(MouseEvent e){
                logoutLabel.setForeground(Color.GRAY);
            }
            public void mouseClicked(MouseEvent e){
                System.out.println("done test");
                dispose();//it removes the current window without exit(0)
                new LoginGUI().setVisible(true);//showing the login page again
            }
        });

        contentPanel.add(new UsersPanel(), "users");
        //contentPanel.add(new CoursesPanel(), "courses");
        //contentPanel.add(new NoticesPanel(), "notices");
        //contentPanel.add(new TimetablePanel(), "timetable");
        //contentPanel.add(new HomePanel(), "home");

        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "home");

        }//end of AdminGUI constructor
}//end of class


