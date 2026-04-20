import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TechOfficerDashboard extends JFrame {
    private JFormattedTextField technicalOfficerDashboardFormattedTextField;
    private JButton btnVerifyMedicals;
    private JButton btnAttendanceReports;
    private JButton btnLogOut;
    private JButton btnMarkAttendance;
    private JPanel DashboardPanel;

    public TechOfficerDashboard(){
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
                new MarkAttendanceForm();

            }
        });
        btnVerifyMedicals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MedicalForm();
            }
        });
        btnAttendanceReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AttendanceReportForm();
            }
        });
        setVisible(true);
    }



    static void main(String[] args) {
        TechOfficerDashboard dboard= new TechOfficerDashboard();

    }
}
