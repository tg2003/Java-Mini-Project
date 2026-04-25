package gui;

import models.Notice;
import models.Undergraduate;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final Undergraduate     student;
    private final StudentService    studentSvc    = new StudentService();
    private final AttendanceService attendanceSvc = new AttendanceService();
    private final MarksService      marksSvc      = new MarksService();
    private final NoticeService     noticeSvc     = new NoticeService();

    public DashboardPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        // Hero
        JPanel hero = UITheme.heroPanel();
        hero.setLayout(new BorderLayout());
        hero.setPreferredSize(new Dimension(0, 130));
        hero.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel greet = new JLabel("Good Morning,  " + student.getName() + "  \uD83D\uDC4B");
        greet.setFont(new Font("Georgia", Font.BOLD, 22));
        greet.setForeground(Color.WHITE);

        JLabel sub = new JLabel(student.getDptName() + " Faculty  \u00B7  Student ID: " + student.getUgId());
        sub.setFont(UITheme.F_SUBHEAD);
        sub.setForeground(UITheme.PUTTY);
        sub.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.add(greet);
        heroText.add(sub);
        hero.add(heroText, BorderLayout.CENTER);

        // Stats
        int    courseCount = studentSvc.getCourseCount(student.getUgId());
        double attPct      = attendanceSvc.getAttendancePercent(student.getUgId());
        double gpa         = marksSvc.getGPA(student.getUgId());
        int    notices     = studentSvc.getUnreadNoticeCount();

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(20, 0, 20, 0));
        stats.add(statCard("Courses",    String.valueOf(courseCount),     "Enrolled",       UITheme.RUSSET));
        stats.add(statCard("Attendance", String.format("%.1f%%", attPct), "This Semester",  UITheme.ESPRESSO));
        stats.add(statCard("GPA",        String.format("%.2f", gpa),      "Cumulative",     UITheme.RUSSET));
        stats.add(statCard("Notices",    String.valueOf(notices),          "Posted",         new Color(0x4A2C1A)));

        // Notices
        JLabel noticeHdr = UITheme.sectionHeader("Recent Notices");
        JPanel noticeList = buildNoticeList();

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(noticeHdr,  BorderLayout.NORTH);
        bottom.add(noticeList, BorderLayout.CENTER);

        add(hero,   BorderLayout.NORTH);
        add(stats,  BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel statCard(String label, String value, String sub, Color accent) {
        JPanel card = UITheme.statCard(accent);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(UITheme.F_LARGE);
        valLbl.setForeground(UITheme.ESPRESSO);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(UITheme.F_BODY_B);
        lblLbl.setForeground(UITheme.DARK_TEXT);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(UITheme.F_SMALL);
        subLbl.setForeground(UITheme.PUTTY);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(lblLbl);
        text.add(subLbl);

        card.add(valLbl, BorderLayout.NORTH);
        card.add(text,   BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildNoticeList() {
        List<Notice> list = noticeSvc.getAll();
        int n = Math.min(list.size(), 4);
        JPanel p = new JPanel(new GridLayout(Math.max(n, 1), 1, 0, 8));
        p.setOpaque(false);

        if (n == 0) {
            JLabel e = new JLabel("No notices.", SwingConstants.CENTER);
            e.setFont(UITheme.F_BODY);
            e.setForeground(UITheme.PUTTY);
            p.add(e);
            return p;
        }
        for (int i = 0; i < n; i++) p.add(noticeRow(list.get(i)));
        return p;
    }

    private JPanel noticeRow(Notice n) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UITheme.ALMOND);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));

        String icon = n.hasDownload() ? "\uD83D\uDCCB  " : "\uD83D\uDCE2  ";
        JLabel title = new JLabel(icon + n.getTitle());
        title.setFont(UITheme.F_BODY);
        title.setForeground(UITheme.DARK_TEXT);

        JLabel date = new JLabel(DateFormatter.format(n.getDateTime()));
        date.setFont(UITheme.F_SMALL);
        date.setForeground(UITheme.PUTTY);

        row.add(title, BorderLayout.WEST);
        row.add(date,  BorderLayout.EAST);
        return row;
    }
}
