package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TechOfficerDashboard extends JFrame {
    private String techOffId;
    private JFormattedTextField technicalOfficerDashboardFormattedTextField;
    private JButton btnVerifyMedicals;
    private JButton btnAttendanceReports;
    private JButton btnLogOut;
    private JButton btnMarkAttendance;
    private JPanel DashboardPanel;

    public TechOfficerDashboard(String techOffId){
        this.techOffId = techOffId;
        //String id = techOffId;
        //System.out.println("tech off id = "+techOffId);
        setTitle("Tech Officer Dashboard");
        setContentPane(DashboardPanel);
        setMinimumSize(new Dimension(800,500));



        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnMarkAttendance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
               new MarkAttendanceForm(techOffId);

            }
        });
        btnVerifyMedicals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
               new MedicalForm(techOffId);
            }
        });
        btnAttendanceReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
               new AttendanceReportForm(techOffId);
            }
        });
        setVisible(true);
    }


}
