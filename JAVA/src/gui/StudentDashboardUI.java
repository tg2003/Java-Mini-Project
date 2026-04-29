package gui;

import models.Undergraduate;
import service.StudentService;
import util.Logout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentDashboardUI extends JFrame {

    private final Undergraduate student;
    private final StudentService studentService = new StudentService();

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel;
    private JLabel notificationBadge;

    // Top-bar avatar — updated whenever photo changes
    private AvatarComponent topAvatar;

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

    // ── Top Shell (TopBar + NavBar) ───────────────────────────────────────────
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

        // Left: logout
        JButton logoutButton = ghostButton("Logout");
        logoutButton.addActionListener(e -> Logout.logout(this));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(logoutButton);

        // Center: brand
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
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(brand, BorderLayout.WEST);

        // Right: notifications + profile button + PHOTO AVATAR
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        notificationBadge = notificationBadge(studentService.getUnreadNoticeCount());
        JButton noticeButton = ghostButton("Notifications");
        noticeButton.addActionListener(e -> showCard("Notice"));

        JButton profileButton = UITheme.primaryButton("My Profile");
        profileButton.addActionListener(e -> showCard("Profile"));

        // AvatarComponent: shows photo if set, otherwise initials
        topAvatar = new AvatarComponent(student, 42);

        right.add(notificationBadge);
        right.add(noticeButton);
        right.add(profileButton);
        right.add(topAvatar);

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

    // ── Content (CardLayout) ──────────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.ALMOND);

        // ProfilePanel receives this::refreshTopAvatar so the top-bar avatar
        // updates immediately when the user changes their photo.
        contentPanel.add(new DashboardPanel(student, this::showCard),       "Dashboard");
        contentPanel.add(new CoursesPanel1(student),                        "Courses");
        contentPanel.add(new NoticeView().getMainPanel(),                   "Notice");
        contentPanel.add(new GPAPanel(student),                             "GPA");
        contentPanel.add(new TimetablePanel1(student),                      "Timetable");
        contentPanel.add(new AttendancePanel(student),                      "Attendance");
        contentPanel.add(new MarksPanel(student),                           "Marks");
        contentPanel.add(new MedicalPanel(student),                         "Medical");
        contentPanel.add(new ProfilePanel(student, this::refreshTopAvatar), "Profile");
        return contentPanel;
    }

    // ── Called by ProfilePanel after successful photo change ──────────────────
    public void refreshTopAvatar() {
        topAvatar.reload();          // re-read photo from student.getProfilePic()
        topAvatar.repaint();
    }

    // ── Nav helpers ───────────────────────────────────────────────────────────
    private void addNavButton(JPanel parent, String card) {
        JButton button = navButton(card);
        button.addActionListener(e -> showCard(card));
        navButtons.put(card, button);
        parent.add(button);
    }

    private void showCard(String card) {
        cardLayout.show(contentPanel, card);
        navButtons.forEach((key, btn) ->
                btn.setForeground(key.equals(card) ? Color.WHITE : UITheme.PUTTY));
    }


    // ── Button factories ──────────────────────────────────────────────────────
    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.F_BODY_B);
        b.setForeground(UITheme.PUTTY);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.F_BODY_B);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SANDSTONE, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        return b;
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

    // ── Inner class: AvatarComponent ─────────────────────────────────────────
    /**
     * Shows the student's profile photo*/
    private static class AvatarComponent extends JComponent {
        private final Undergraduate student;
        private final int size;
        private BufferedImage image;

        AvatarComponent(Undergraduate student, int size) {
            this.student = student;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            reload();
        }

        /** Re-reads the image from disk using the path in student.getProfilePic(). */
        void reload() {
            image = null;
            String path = student.getProfilePic();
            if (path == null || path.isBlank()) return;
            try {
                File f = new File(path);
                if (f.exists()) image = ImageIO.read(f);
            } catch (Exception ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clip to circle
            Shape circle = new Ellipse2D.Float(0, 0, size, size);
            g2.setClip(circle);

            if (image != null) {
                g2.drawImage(image, 0, 0, size, size, null);
            } else {
                // Fallback: coloured circle with initials
                g2.setColor(UITheme.SANDSTONE);
                g2.fillOval(0, 0, size, size);
                g2.setClip(null);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, size / 3));
                FontMetrics fm = g2.getFontMetrics();
                String initials = student.getInitials();
                int x = (size - fm.stringWidth(initials)) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
            }

            g2.setClip(null);
            // Thin border ring
            g2.setColor(UITheme.SANDSTONE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(1, 1, size - 2, size - 2);
            g2.dispose();
        }

    }

}