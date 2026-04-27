package gui;

import util.Logout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class TechOfficerDashboard extends JFrame {

    private String techOffId;
    private JButton btnVerifyMedicals;
    private JButton btnAttendanceReports;
    private JButton btnLogOut;
    private JButton btnMarkAttendance;
    private JPanel DashboardPanel;
    private JButton btnProfile;
    private JLabel jprofile;
    private JButton btnTimetable;
    private JButton btnNotice;


    public TechOfficerDashboard(String techOffId) {
        this.techOffId = techOffId;

        System.out.println("tech off id = " + techOffId);

        setTitle("Tech Officer Dashboard");
        setContentPane(DashboardPanel);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //load image every time  dashboard opens
        loadProfileImage();

        btnLogOut.addActionListener(e -> Logout.logout(TechOfficerDashboard.this));

        btnMarkAttendance.addActionListener(e -> {
            dispose();
            new MarkAttendanceForm(techOffId);
        });

        btnVerifyMedicals.addActionListener(e -> {
            dispose();
            new MedicalForm(techOffId);
        });

        btnAttendanceReports.addActionListener(e -> {
            dispose();
            new AttendanceReportForm(techOffId);
        });

        btnProfile.addActionListener(e -> {
            dispose();
            new TechOfficerProfileForm(techOffId);
        });

        setVisible(true);
        btnNotice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new NoticeView(techOffId, true).setVisible(true);
            }
        });
        btnTimetable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TimetableView(techOffId, true).setVisible(true);

            }
        });
    }

    //load from database
    private void loadProfileImage() {
        try {
            Connection con = db.DBConnection.getConnection();

            String sql = "SELECT profile_pic FROM user WHERE user_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, techOffId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                String path = rs.getString("profile_pic");

                if (path != null && !path.isEmpty()) {

                    // Handle relative paths
                    if (path.startsWith("src/")) {
                        path = System.getProperty("user.dir") + "/" + path;
                    }

                    File file = new File(path);

                    if (file.exists()) {

                        ImageIcon icon = new ImageIcon(path);

                        Image img = icon.getImage().getScaledInstance(
                                120, 120, Image.SCALE_SMOOTH
                        );

                        jprofile.setIcon(new ImageIcon(img));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}