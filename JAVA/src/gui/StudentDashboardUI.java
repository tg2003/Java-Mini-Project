package gui;

import models.Undergraduate;
import service.StudentService;
import util.Logout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentDashboardUI extends JFrame {

    private final Undergraduate student;
    private final StudentService studentService = new StudentService();

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel;
    private JLabel notificationBadge;

    public StudentDashboardUI(Undergraduate student) {
        this.student = student;

        setTitle("Faculty of Technology Student Portal - " + student.getName());
        setSize(1260, 820);
        setMinimumSize(new Dimension(1080, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UITheme.applyGlobalDefaults();

        setLayout(new BorderLayout(0, 0));
        add(buildTopShell(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        showCard("Dashboard");
        setVisible(true);
    }

    private JPanel buildTopShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(UITheme.ESPRESSO);
        shell.add(buildTopBar(), BorderLayout.NORTH);
        shell.add(buildNavBar(), BorderLayout.SOUTH);
        return shell;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UITheme.ESPRESSO);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(UITheme.RUSSET);
                g.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        bar.setBorder(new EmptyBorder(16, 20, 14, 20));

        JButton logoutButton = ghostButton("Logout");
        logoutButton.addActionListener(e -> Logout.logout(this));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(logoutButton);

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Faculty of Technology");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Student Portal");
        subtitle.setFont(UITheme.F_BODY);
        subtitle.setForeground(UITheme.PUTTY);

        brand.add(title);
        brand.add(Box.createVerticalStrut(2));
        brand.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        notificationBadge = notificationBadge(studentService.getUnreadNoticeCount());
        JButton noticeButton = ghostButton("Notifications");
        noticeButton.addActionListener(e -> showCard("Notice"));

        JButton profileButton = UITheme.primaryButton("My Profile");
        profileButton.addActionListener(e -> showCard("Profile"));

        right.add(notificationBadge);
        right.add(noticeButton);
        right.add(profileButton);
        right.add(roundAvatar(student.getInitials(), 42));

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(brand, BorderLayout.WEST);

        bar.add(left, BorderLayout.WEST);
        bar.add(centerWrap, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(0, 18, 12, 18));

        addNavButton(nav, "Dashboard");
        addNavButton(nav, "Courses");
        addNavButton(nav, "Notice");
        addNavButton(nav, "GPA");
        addNavButton(nav, "Timetable");
        addNavButton(nav, "Attendance");
        addNavButton(nav, "Marks");
        addNavButton(nav, "Medical");
        return nav;
    }

    private JPanel buildContent() {
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.ALMOND);
        contentPanel.add(new DashboardPanel(student, this::showCard), "Dashboard");
        contentPanel.add(new CoursesPanel1(student), "Courses");
        contentPanel.add(new NoticePanel1(), "Notice");
        contentPanel.add(new GPAPanel(student), "GPA");
        contentPanel.add(new TimetablePanel1(student), "Timetable");
        contentPanel.add(new AttendancePanel(student), "Attendance");
        contentPanel.add(new MarksPanel(student), "Marks");
        contentPanel.add(new MedicalPanel(student), "Medical");
        contentPanel.add(new ProfilePanel(student, this::refreshHeader), "Profile");
        return contentPanel;
    }

    private void addNavButton(JPanel parent, String card) {
        JButton button = navButton(card);
        button.addActionListener(e -> showCard(card));
        navButtons.put(card, button);
        parent.add(button);
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.F_BODY_B);
        button.setForeground(UITheme.PUTTY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton ghostButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.F_BODY_B);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SANDSTONE, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    private JLabel notificationBadge(int count) {
        JLabel label = new JLabel("\uD83D\uDD14 " + count);
        label.setFont(UITheme.F_BODY_B);
        label.setForeground(UITheme.ESPRESSO);
        label.setOpaque(true);
        label.setBackground(UITheme.SANDSTONE);
        label.setBorder(new EmptyBorder(8, 12, 8, 12));
        return label;
    }

    private JComponent roundAvatar(String initials, int size) {
        return new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(size, size);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SANDSTONE);
                g2.fillOval(0, 0, size, size);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(initials)) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
    }

    private void showCard(String card) {
        cardLayout.show(contentPanel, card);
        navButtons.forEach((key, value) -> value.setForeground(key.equals(card) ? Color.WHITE : UITheme.PUTTY));
    }

    private void refreshHeader() {
        notificationBadge.setText("\uD83D\uDD14 " + studentService.getUnreadNoticeCount());
        revalidate();
        repaint();
    }
}
