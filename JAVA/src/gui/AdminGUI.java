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
    private JButton logoutButton;
    private JLabel adminDashboardTitle;
    private JLabel pplabel;

    // labels array
    JLabel[] menuLabels = {usersLabel, coursesLabel, noticesLabel, timetableLabel};

    // highlight the active panel
    private void setActive(JLabel active) {
        for (JLabel label : menuLabels) {
            label.setForeground(new Color(57, 42, 16));
            label.setFont(new Font("Arial", Font.PLAIN, 14));
        }
        active.setForeground(new Color(167, 141, 120));
        active.setFont(new Font("Arial", Font.BOLD, 16));
    }

    public AdminGUI (){
        setContentPane(mainPanel);
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,500);
        setLocationRelativeTo(null);

        //setting the sidePanel


        //Users
        usersLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        usersLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                usersLabel.setForeground(new Color(167,141,120));
            }
            public void mouseExited(MouseEvent e){usersLabel.setForeground(new Color(57, 42, 16));}
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "users");
                setActive(usersLabel);
            }
        });

        //Courses
        coursesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        coursesLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                coursesLabel.setForeground(new Color(167,141,120));
            }
            public void mouseExited(MouseEvent e){
                coursesLabel.setForeground(new Color(57, 42, 16));
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "courses");
                setActive(coursesLabel);
            }
        });

        //Notice
        noticesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noticesLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                noticesLabel.setForeground(new Color(167,141,120));
            }
            public void mouseExited(MouseEvent e){
                noticesLabel.setForeground(new Color(57, 42, 16));
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "notices");
                setActive(noticesLabel);
            }
        });

        //timetable
        timetableLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        timetableLabel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e) {
                timetableLabel.setForeground(new Color(167,141,120));
            }
            public void mouseExited(MouseEvent e){
                timetableLabel.setForeground(new Color(57, 42, 16));
            }
            public void mouseClicked(MouseEvent e){
                CardLayout c = (CardLayout) contentPanel.getLayout();
                c.show(contentPanel, "timetable");
                setActive(timetableLabel);
            }
        });

        contentPanel.add(new UsersPanel(), "users");
        contentPanel.add(new CoursesPanel(), "courses");
        contentPanel.add(new NoticePanel(), "notices");
        contentPanel.add(new TimetablePanel(), "timetable");
        //contentPanel.add(new HomePanel(), "home");

        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "home");



        }//end of AdminGUI constructor



}//end of class




