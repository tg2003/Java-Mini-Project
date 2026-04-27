package gui;

import db.DBConnection;
import util.Logout;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LecturerDashboard extends JFrame {

    private static final Color BG_MAIN  = new Color(245, 235, 224);
    private static final Color BG_SIDE  = new Color(102, 51, 0);
    private static final Color BG_CARD  = new Color(222, 184, 135);
    private static final Color BG_HOVER = new Color(200, 155, 100);
    private static final Color TEXT_DARK  = new Color(60, 30, 10);
    private static final Color TEXT_LIGHT = Color.WHITE;

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BTN   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_CARD  = new Font("Segoe UI", Font.BOLD, 15);

    private String lecturerId;
    private JLabel lblName, lblDept;

    public LecturerDashboard(String lecturerId) {
        this.lecturerId = lecturerId;
        initUI();
        loadLecturerData();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Lecturer Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 600));
        panel.setBackground(BG_SIDE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("LMS");
        title.setForeground(TEXT_LIGHT);
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(title);

        lblName = new JLabel("Loading...");
        lblName.setForeground(TEXT_LIGHT);
        lblName.setBorder(new EmptyBorder(10, 20, 5, 20));
        panel.add(lblName);

        lblDept = new JLabel("");
        lblDept.setForeground(TEXT_LIGHT);
        lblDept.setBorder(new EmptyBorder(0, 20, 20, 20));
        panel.add(lblDept);

        panel.add(makeSideBtn("Mark Entry",  () -> openMarkEntry()));
        panel.add(makeSideBtn("Grade & GPA", () -> openGradeView()));

        panel.add(Box.createVerticalGlue());

        panel.add(makeSideBtn("Logout", () -> {
            DBConnection.closeConnection();
            new Logout().logout(this);
            dispose();

        }));

        return panel;
    }

    private JPanel buildMain() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_MAIN);

        JLabel title = new JLabel("Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 10));
        cards.setBorder(new EmptyBorder(20, 20, 20, 20));
        cards.setBackground(BG_MAIN);

        cards.add(makeCard("Mark Entry",    "Click to enter\nstudent marks",   () -> openMarkEntry()));
        cards.add(makeCard("Grade & GPA",   "Click to view\ngrades and GPA",   () -> openGradeView()));
        cards.add(makeCard("CA Eligibility","Click to check\nCA eligibility",  () -> openCAReport()));

        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeCard(String heading, String desc, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(TEXT_DARK, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lHead = new JLabel(heading);
        lHead.setFont(FONT_CARD);
        lHead.setForeground(TEXT_DARK);
        lHead.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lHead);

        for (String line : desc.split("\n")) {
            JLabel l = new JLabel(line);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            l.setForeground(new Color(90, 50, 10));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(l);
        }

        card.add(Box.createVerticalStrut(14));

        JButton btn = new JButton("Open");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(BG_SIDE);
        btn.setForeground(TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 20, 6, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());

        card.add(btn);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(BG_HOVER); }
            public void mouseExited(MouseEvent e)  { card.setBackground(BG_CARD);  }
            public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }

    private JButton makeSideBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setBackground(BG_SIDE);
        btn.setForeground(TEXT_LIGHT);
        btn.setFont(FONT_BTN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(130, 70, 10)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_SIDE); }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void loadLecturerData() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT Name, Dpt_name FROM LECTURER WHERE Lec_id=?");
            ps.setString(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblName.setText(rs.getString("Name"));
                lblDept.setText(rs.getString("Dpt_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMarkEntry() { new MarkEntryForm(lecturerId); }
    private void openGradeView()  { new GradeGPAView(lecturerId);  }
    private void openCAReport()   {  new CAEligibilityReport(lecturerId);  }

    public static void main(String[] args) {
        new LecturerDashboard("L00001");
    }
}