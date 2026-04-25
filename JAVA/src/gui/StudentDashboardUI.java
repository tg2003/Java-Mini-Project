package gui;

import models.Undergraduate;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application frame for the Student Dashboard.
 * Wires: TopBar + Sidebar (nav) + CardLayout content area.
 * Each nav item maps to a dedicated JPanel built lazily on first click.
 */
public class StudentDashboardUI extends JFrame {

    private final Undergraduate student;

    private JPanel    contentPanel;
    private CardLayout cardLayout;
    private JLabel    activeNav;

    // Nav icon + card key pairs
    private static final String[][] NAV = {
        {"\u2302", "Dashboard"},
        {"\u25A4", "Courses"},
        {"\u25F7", "Attendance"},
        {"\u270E", "Marks"},
        {"\u2605", "GPA"},
        {"\u2637", "Timetable"},
        {"\u2630", "Notice"},
        {"\u2665", "Medical"},
        {"\u25C9", "Profile"}
    };

    public StudentDashboardUI(Undergraduate student) {
        this.student = student;

        setTitle("Student Portal  \u2013  " + student.getName());
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UITheme.applyGlobalDefaults();

        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.ALMOND);

        // Add all panels
        contentPanel.add(new DashboardPanel(student),  "Dashboard");
        contentPanel.add(new CoursesPanel1(student),    "Courses");
        contentPanel.add(new AttendancePanel(student), "Attendance");
        contentPanel.add(new MarksPanel(student),      "Marks");
        contentPanel.add(new GPAPanel(student),        "GPA");
        contentPanel.add(new TimetablePanel1(student),  "Timetable");
        contentPanel.add(new NoticePanel1(),            "Notice");
        contentPanel.add(new MedicalPanel(student),    "Medical");
        contentPanel.add(new ProfilePanel(student),    "Profile");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "Dashboard");

        setVisible(true);
    }

    // ── Top Bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UITheme.ESPRESSO);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(UITheme.RUSSET);
                g.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        bar.setPreferredSize(new Dimension(1150, 58));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: logo
        JLabel logo = new JLabel("  STUDENT PORTAL");
        logo.setFont(new Font("Georgia", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        logo.setIcon(circleIcon(UITheme.SANDSTONE, 10));
        logo.setIconTextGap(8);

        // Right: semester pill + student name pill
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(UITheme.pill("Semester I  \u00B7  2025/26", UITheme.RUSSET, UITheme.PUTTY));
        right.add(UITheme.pill(student.getName() + "  \u25BE", UITheme.SANDSTONE, UITheme.ESPRESSO));

        bar.add(logo,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x1E1509));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(UITheme.RUSSET);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        outer.setPreferredSize(new Dimension(215, 720));

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(18, 0, 18, 0));

        for (String[] item : NAV) {
            JLabel lbl = buildNavItem(item[0], item[1]);
            nav.add(lbl);
            nav.add(Box.createVerticalStrut(2));
            if (item[1].equals("Dashboard")) {
                setNavActive(lbl, true);
                activeNav = lbl;
            }
        }

        JLabel ver = new JLabel("LMS  v1.0  \u00B7  ICT Faculty", SwingConstants.CENTER);
        ver.setFont(UITheme.F_SMALL);
        ver.setForeground(new Color(0x5A4030));
        ver.setBorder(new EmptyBorder(12, 0, 12, 0));

        outer.add(nav, BorderLayout.NORTH);
        outer.add(ver, BorderLayout.SOUTH);
        return outer;
    }

    private JLabel buildNavItem(String icon, String text) {
        // Each nav item is a custom-painted JLabel
        JLabel lbl = new JLabel("  " + icon + "   " + text) {
            boolean active = false;
            boolean hover  = false;

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(new Color(167, 141, 120, 30));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(UITheme.SANDSTONE);
                    g2.fillRect(0, 8, 3, getHeight() - 16);
                    setForeground(Color.WHITE);
                } else if (hover) {
                    g2.setColor(new Color(167, 141, 120, 18));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    setForeground(UITheme.SANDSTONE);
                } else {
                    setForeground(UITheme.PUTTY);
                }
                g2.dispose();
                super.paintComponent(g);
            }

            { // instance init block
                setFont(UITheme.F_NAV);
                setForeground(UITheme.PUTTY);
                setMaximumSize(new Dimension(215, 44));
                setPreferredSize(new Dimension(215, 44));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setHorizontalAlignment(SwingConstants.LEFT);
            }
        };

        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (activeNav != null) setNavActive(activeNav, false);
                setNavActive(lbl, true);
                activeNav = lbl;
                cardLayout.show(contentPanel, text);
            }
            @Override public void mouseEntered(MouseEvent e) { setNavHover(lbl, true);  }
            @Override public void mouseExited (MouseEvent e) { setNavHover(lbl, false); }
        });
        return lbl;
    }

    // Reflection-free active/hover toggle using stored booleans via name trick
    private void setNavActive(JLabel lbl, boolean val) {
        lbl.putClientProperty("active", val);
        lbl.repaint();
    }
    private void setNavHover(JLabel lbl, boolean val) {
        lbl.putClientProperty("hover", val);
        lbl.repaint();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static Icon circleIcon(Color color, int d) {
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(d, d, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, d, d);
        g2.dispose();
        return new ImageIcon(img);
    }
}
