package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class StudentDashboardUI extends JFrame {
    private int studentId = 1; // from login

    // ── Palette ────────────────────────────────────────────────────────────────
    static final Color C_ESPRESSO   = Color.decode("#291C0E"); // darkest
    static final Color C_RUSSET     = Color.decode("#6E473B"); // sidebar
    static final Color C_SANDSTONE  = Color.decode("#A78D78"); // accent / hover
    static final Color C_PUTTY      = Color.decode("#BEB5A9"); // secondary text / borders
    static final Color C_ALMOND     = Color.decode("#E1D4C2"); // content bg
    static final Color C_WHITE      = Color.decode("#FAF7F4"); // card bg / text on dark
    static final Color C_DARK_TEXT  = Color.decode("#1A1008"); // body text

    // ── Fonts ──────────────────────────────────────────────────────────────────
    // Using fonts available in standard JDK; we pick Georgia for serif elegance
    // and Dialog (maps to system sans) for body.
    static final Font F_DISPLAY  = new Font("Georgia",    Font.BOLD,  13);
    static final Font F_HEADING  = new Font("Georgia",    Font.BOLD,  22);
    static final Font F_SUB      = new Font("Georgia",    Font.ITALIC,13);
    static final Font F_BODY     = new Font("SansSerif",  Font.PLAIN, 13);
    static final Font F_BODY_B   = new Font("SansSerif",  Font.BOLD,  13);
    static final Font F_SMALL    = new Font("SansSerif",  Font.PLAIN, 11);
    static final Font F_NAV      = new Font("SansSerif",  Font.BOLD,  12);
    static final Font F_HUGE     = new Font("Georgia",    Font.BOLD,  52);

    // ── State ──────────────────────────────────────────────────────────────────
    private JPanel    contentPanel;
    private CardLayout cardLayout;
    private JLabel    activeNavLabel;

    // ── Nav items ──────────────────────────────────────────────────────────────
    private static final String[][] NAV = {
            {"⌂",  "Dashboard"},
            {"▤",  "Courses"},
            {"◷",  "Attendance"},
            {"✎",  "Marks"},
            {"★",  "GPA"},
            {"☷",  "Timetable"},
            {"☰",  "Notice"},
            {"♥",  "Medical"},
            {"◉",  "Profile"}
    };

    // ══════════════════════════════════════════════════════════════════════════
    public StudentDashboardUI() {
        setTitle("Student Portal");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Global UI tweaks
        UIManager.put("Table.font",           F_BODY);
        UIManager.put("Table.rowHeight",      32);
        UIManager.put("TableHeader.font",     F_BODY_B);
        UIManager.put("TableHeader.background", C_RUSSET);
        UIManager.put("TableHeader.foreground", C_WHITE);
        UIManager.put("Table.background",     C_WHITE);
        UIManager.put("Table.foreground",     C_DARK_TEXT);
        UIManager.put("Table.selectionBackground", C_SANDSTONE);
        UIManager.put("Table.selectionForeground", C_WHITE);
        UIManager.put("Table.gridColor",      C_ALMOND);
        UIManager.put("ScrollPane.background",C_ALMOND);
        UIManager.put("ScrollBar.width",      8);

        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);

        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(C_ALMOND);

        contentPanel.add(buildDashboard(),   "Dashboard");
        contentPanel.add(buildCourses(),     "Courses");
        contentPanel.add(buildAttendance(),  "Attendance");
        contentPanel.add(buildMarks(),       "Marks");
        contentPanel.add(buildGPA(),         "GPA");
        contentPanel.add(buildTimetable(),   "Timetable");
        contentPanel.add(buildNotice(),      "Notice");
        contentPanel.add(buildMedical(),     "Medical");
        contentPanel.add(buildProfile(),     "Profile");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "Dashboard");
        setVisible(true);
    }

    // ══════════════════════ TOP BAR ══════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ESPRESSO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle bottom line
                g2.setColor(C_RUSSET);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(1100, 58));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Logo / title
        JLabel logo = new JLabel("STUDENT PORTAL");
        logo.setFont(new Font("Georgia", Font.BOLD, 16));
        logo.setForeground(C_WHITE);
        logo.setIcon(makeCircleIcon(C_SANDSTONE, 10));
        logo.setIconTextGap(10);

        // Right: student badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        JLabel semester = pill("Semester II  ·  2024/25", C_RUSSET, C_PUTTY);
        JLabel student  = pill("John Doe  ▾", C_SANDSTONE, C_ESPRESSO);

        right.add(semester);
        right.add(student);

        bar.add(logo,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JLabel pill(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(F_SMALL);
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(5, 14, 5, 14));
        lbl.setOpaque(false);
        return lbl;
    }

    // ══════════════════════ SIDEBAR ══════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1E1509));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // right border
                g2.setColor(C_RUSSET);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        outer.setPreferredSize(new Dimension(210, 700));

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(18, 0, 18, 0));

        for (String[] item : NAV) {
            JLabel lbl = buildNavItem(item[0], item[1]);
            nav.add(lbl);
            nav.add(Box.createVerticalStrut(2));
        }

        // Bottom: version tag
        JLabel ver = new JLabel("v2.0  ·  ICT Faculty", SwingConstants.CENTER);
        ver.setFont(F_SMALL);
        ver.setForeground(new Color(0x5A4030));
        ver.setBorder(new EmptyBorder(12, 0, 12, 0));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);

        outer.add(nav,   BorderLayout.NORTH);
        outer.add(ver,   BorderLayout.SOUTH);
        return outer;
    }

    private JLabel buildNavItem(String icon, String text) {
        JLabel lbl = new JLabel() {
            boolean active = false;
            boolean hover  = false;
            {
                setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                setFont(F_NAV);
                setForeground(C_PUTTY);
                setMaximumSize(new Dimension(210, 44));
                setPreferredSize(new Dimension(210, 44));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(0, 22, 0, 0));

                // icon + label manually
                setText("  " + icon + "   " + text);
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    // active: warm accent strip + fill
                    g2.setColor(new Color(0xA78D78, false));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2.setColor(C_SANDSTONE);
                    g2.fillRect(0, 8, 3, getHeight()-16);
                    setForeground(C_WHITE);
                } else if (hover) {
                    g2.setColor(new Color(0xA78D78, false));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    setForeground(C_SANDSTONE);
                } else {
                    setForeground(C_PUTTY);
                }
                g2.dispose();
                super.paintComponent(g);
            }

            void setActive(boolean a) { active = a; repaint(); }
            boolean isActive()        { return active; }
        };

        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (activeNavLabel != null) {
                    try { activeNavLabel.getClass().getMethod("setActive", boolean.class)
                            .invoke(activeNavLabel, false); } catch (Exception ex) {}
                }
                try { lbl.getClass().getMethod("setActive", boolean.class)
                        .invoke(lbl, true); } catch (Exception ex) {}
                activeNavLabel = lbl;
                cardLayout.show(contentPanel, text);
            }
            @Override public void mouseEntered(MouseEvent e) {
                try { lbl.getClass().getDeclaredField("hover")
                        .set(lbl, true); } catch (Exception ex) {}
                lbl.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                try { lbl.getClass().getDeclaredField("hover")
                        .set(lbl, false); } catch (Exception ex) {}
                lbl.repaint();
            }
        });

        if (text.equals("Dashboard")) {
            try { lbl.getClass().getMethod("setActive", boolean.class)
                    .invoke(lbl, true); } catch (Exception ex) {}
            activeNavLabel = lbl;
        }
        return lbl;
    }

    // ══════════════════════ CONTENT PANELS ═══════════════════════════════════

    // ── DASHBOARD ──────────────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_ALMOND);
        root.setBorder(new EmptyBorder(28, 30, 28, 30));

        // Hero greeting card
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // gradient fill
                GradientPaint gp = new GradientPaint(
                        0, 0,             C_ESPRESSO,
                        getWidth(), 0,    C_RUSSET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        hero.setPreferredSize(new Dimension(0, 140));
        hero.setBorder(new EmptyBorder(28, 32, 28, 32));
        hero.setOpaque(false);

        JLabel greet = new JLabel("Good Morning, John 👋");
        greet.setFont(new Font("Georgia", Font.BOLD, 24));
        greet.setForeground(C_WHITE);

        JLabel sub = new JLabel("ICT Faculty  ·  Year 2, Semester II  ·  Student ID: ICT20211009");
        sub.setFont(F_SUB);
        sub.setForeground(C_PUTTY);
        sub.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.add(greet);
        heroText.add(sub);

        hero.add(heroText, BorderLayout.CENTER);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(20, 0, 20, 0));

        stats.add(statCard("Courses",    "7",    "Enrolled",      C_RUSSET));
        stats.add(statCard("Attendance", "91%",  "This Semester", C_ESPRESSO));
        stats.add(statCard("GPA",        "3.75", "Current",       C_RUSSET));
        stats.add(statCard("Notices",    "3",    "Unread",        new Color(0x4A2C1A)));

        // Recent notices
        JLabel noticeHdr = sectionHeader("Recent Notices");
        JPanel noticeList = buildNoticeList();

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(noticeHdr,  BorderLayout.NORTH);
        bottom.add(noticeList, BorderLayout.CENTER);

        root.add(hero,   BorderLayout.NORTH);
        root.add(stats,  BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JPanel statCard(String label, String value, String sub, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // top accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                // subtle shadow via border
                g2.setColor(new Color(0x291C0E, false));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Georgia", Font.BOLD, 32));
        valLbl.setForeground(C_ESPRESSO);

        JLabel lbl1 = new JLabel(label);
        lbl1.setFont(F_BODY_B);
        lbl1.setForeground(C_DARK_TEXT);

        JLabel lbl2 = new JLabel(sub);
        lbl2.setFont(F_SMALL);
        lbl2.setForeground(C_PUTTY);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(lbl1);
        text.add(lbl2);

        card.add(valLbl, BorderLayout.NORTH);
        card.add(text,   BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildNoticeList() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setOpaque(false);
        p.add(noticeRow("📋  Examination Timetable Released", "2025-04-15", C_ALMOND));
        p.add(noticeRow("📢  Registration Deadline: April 30", "2025-04-10", C_ALMOND));
        return p;
    }

    private JPanel noticeRow(String title, String date, Color bg) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(C_ALMOND);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel t = new JLabel(title); t.setFont(F_BODY); t.setForeground(C_DARK_TEXT);
        JLabel d = new JLabel(date);  d.setFont(F_SMALL); d.setForeground(C_PUTTY);
        row.add(t, BorderLayout.WEST);
        row.add(d, BorderLayout.EAST);
        return row;
    }

    // ── COURSES ────────────────────────────────────────────────────────────
    private JPanel buildCourses() {
        String[] col  = {"Course Code", "Course Name", "Credits", "Status"};
        Object[][] data = {
                {"ICT2122", "Object Oriented Programming",  "3", "Active"},
                {"ICT2132", "OOP Practicum",                "1", "Active"},
                {"ICT2142", "Object Oriented Analysis",     "3", "Active"},
                {"ICT2152", "E-Commerce Technology",        "3", "Active"},
                {"ENG2122", "English for IT Professionals", "2", "Active"},
                {"TCS2112", "Business Economics",           "2", "Active"},
                {"TCS2122", "Soft Skills Development",      "1", "Active"}
        };
        return tablePanel("Enrolled Courses", col, data);
    }

    // ── ATTENDANCE ─────────────────────────────────────────────────────────
    private JPanel buildAttendance() {
        String[] col  = {"Date", "Course", "Type", "Time Slot", "Status"};
        Object[][] data = {
                {"2025-01-06", "OOP",        "Lecture",   "10:00 – 12:00", "Present"},
                {"2025-01-07", "DB",         "Practical", "13:00 – 15:00", "Absent"},
                {"2025-01-08", "E-Commerce", "Lecture",   "09:00 – 11:00", "Present"},
                {"2025-01-09", "OOP",        "Practical", "14:00 – 16:00", "Present"},
                {"2025-01-10", "Soft Skills","Tutorial",  "11:00 – 12:00", "Absent"}
        };
        return tablePanel("Attendance Record", col, data);
    }

    // ── MARKS ──────────────────────────────────────────────────────────────
    private JPanel buildMarks() {
        String[] col  = {"Course", "Assignment", "Mid Exam", "Final Exam", "Total", "Grade"};
        Object[][] data = {
                {"OOP",        "18/20", "38/40", "78/100", "85", "A"},
                {"DB",         "15/20", "34/40", "72/100", "78", "B+"},
                {"E-Commerce", "17/20", "36/40", "74/100", "80", "A-"},
                {"English",    "19/20", "39/40", "80/100", "88", "A+"},
                {"Economics",  "14/20", "30/40", "68/100", "72", "B"},
        };
        return tablePanel("Academic Marks", col, data);
    }

    // ── GPA ────────────────────────────────────────────────────────────────
    private JPanel buildGPA() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(C_ALMOND);

        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_ESPRESSO, 0, getHeight(), C_RUSSET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(360, 280));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel gpaLbl = new JLabel("3.75", SwingConstants.CENTER);
        gpaLbl.setFont(F_HUGE);
        gpaLbl.setForeground(C_WHITE);

        JLabel gpaSub = new JLabel("Cumulative GPA", SwingConstants.CENTER);
        gpaSub.setFont(F_SUB);
        gpaSub.setForeground(C_PUTTY);

        JLabel clsLbl = new JLabel("First Class Honours", SwingConstants.CENTER);
        clsLbl.setFont(F_BODY_B);
        clsLbl.setForeground(C_SANDSTONE);
        clsLbl.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(gpaLbl, BorderLayout.CENTER);
        JPanel btm = new JPanel(new BorderLayout()); btm.setOpaque(false);
        btm.add(gpaSub, BorderLayout.NORTH);
        btm.add(clsLbl, BorderLayout.SOUTH);
        card.add(btm, BorderLayout.SOUTH);

        root.add(card);
        return root;
    }

    // ── TIMETABLE ──────────────────────────────────────────────────────────
    private JPanel buildTimetable() {
        String[] col  = {"Day", "Start", "End", "Course", "Room", "Type"};
        Object[][] data = {
                {"Monday",    "09:00", "11:00", "OOP",         "Lab 04",   "Lecture"},
                {"Monday",    "13:00", "15:00", "E-Commerce",  "Hall B",   "Lecture"},
                {"Tuesday",   "10:00", "12:00", "DB",          "Lab 02",   "Practical"},
                {"Wednesday", "09:00", "11:00", "English",     "Room 201", "Tutorial"},
                {"Thursday",  "14:00", "16:00", "Soft Skills", "Room 105", "Workshop"},
                {"Friday",    "11:00", "13:00", "Economics",   "Hall A",   "Lecture"}
        };
        return tablePanel("Class Timetable", col, data);
    }

    // ── NOTICE ─────────────────────────────────────────────────────────────
    private JPanel buildNotice() {
        String[] col  = {"#", "Title", "Date Posted", "Category", "Action"};
        Object[][] data = {
                {"1", "Examination Timetable – Semester II",   "2025-04-15", "Exam",      "Download"},
                {"2", "Registration Deadline Reminder",        "2025-04-10", "Admin",     "View"},
                {"3", "ICT Department Sports Day",             "2025-04-05", "Event",     "View"},
                {"4", "Supplementary Exam Registration Open",  "2025-03-28", "Exam",      "Download"},
        };
        return tablePanel("Notice Board", col, data);
    }

    // ── MEDICAL ────────────────────────────────────────────────────────────
    private JPanel buildMedical() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_ALMOND);
        root.setBorder(new EmptyBorder(28, 30, 28, 30));
        root.add(sectionHeader("Medical Leave"), BorderLayout.NORTH);

        String[] col  = {"Date From", "Date To", "Reason", "Status", "Approved By"};
        Object[][] data = {
                {"2025-02-10", "2025-02-12", "Fever & Flu",       "Approved", "Dr. Perera"},
                {"2025-03-04", "2025-03-04", "Dental Appointment","Approved", "Dr. Fernando"},
                {"2025-04-01", "2025-04-02", "Viral Infection",   "Pending",  "—"},
        };

        JTable table = styledTable(col, data);
        JScrollPane sp = styledScroll(table);
        root.add(sp, BorderLayout.CENTER);

        // Submit button
        JButton btn = new JButton("+ Submit New Request") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_RUSSET);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BODY_B);
        btn.setForeground(C_WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));

        JPanel btmBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btmBar.setOpaque(false);
        btmBar.add(btn);
        root.add(btmBar, BorderLayout.SOUTH);
        return root;
    }

    // ── PROFILE ────────────────────────────────────────────────────────────
    private JPanel buildProfile() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_ALMOND);
        root.setBorder(new EmptyBorder(28, 30, 28, 30));
        root.add(sectionHeader("My Profile"), BorderLayout.NORTH);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 0));
        body.setOpaque(false);

        // Avatar card
        JPanel avatar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                // top gradient
                GradientPaint gp = new GradientPaint(0, 0, C_ESPRESSO, 0, 100, C_RUSSET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 100, 18, 18);
                // circle
                g2.setColor(C_SANDSTONE);
                g2.fillOval(getWidth()/2 - 36, 28, 72, 72);
                g2.setColor(C_WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("JD", getWidth()/2 - fm.stringWidth("JD")/2, 74);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(200, 320));
        avatar.setOpaque(false);
        avatar.setBorder(new EmptyBorder(110, 16, 16, 16));

        JLabel nameL = new JLabel("John Doe", SwingConstants.CENTER);
        nameL.setFont(F_HEADING); nameL.setForeground(C_ESPRESSO);
        JLabel roleL = new JLabel("Undergraduate", SwingConstants.CENTER);
        roleL.setFont(F_SUB); roleL.setForeground(C_SANDSTONE);

        JPanel names = new JPanel(new BorderLayout()); names.setOpaque(false);
        names.add(nameL, BorderLayout.NORTH);
        names.add(roleL, BorderLayout.SOUTH);
        avatar.add(names, BorderLayout.CENTER);

        // Info card
        JPanel info = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        info.setOpaque(false);
        info.setPreferredSize(new Dimension(420, 320));
        info.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel fields = new JPanel(new GridLayout(8, 1, 0, 6));
        fields.setOpaque(false);

        String[][] rows = {
                {"Student ID",   "ICT20211009"},
                {"Full Name",    "John Doe"},
                {"Email",        "john@student.rjt.ac.lk"},
                {"Faculty",      "ICT"},
                {"Degree",       "BSc (Hons) in IT"},
                {"Year",         "Year 2"},
                {"Status",       "Active"},
                {"Advisor",      "Dr. Nimalka Jayawardena"}
        };
        for (String[] r : rows) fields.add(profileRow(r[0], r[1]));
        info.add(fields, BorderLayout.CENTER);

        body.add(avatar);
        body.add(info);
        root.add(body, BorderLayout.CENTER);
        return new ProfilePanel(studentId);
    }

    private JPanel profileRow(String key, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new MatteBorder(0, 0, 1, 0, C_ALMOND));
        JLabel k = new JLabel(key + " :");
        k.setFont(F_SMALL); k.setForeground(C_PUTTY); k.setPreferredSize(new Dimension(110, 24));
        JLabel v = new JLabel(val);
        v.setFont(F_BODY); v.setForeground(C_DARK_TEXT);
        p.add(k, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════ HELPERS ═══════════════════════════════════════════

    private JPanel tablePanel(String title, String[] col, Object[][] data) {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(C_ALMOND);
        root.setBorder(new EmptyBorder(28, 30, 28, 30));

        root.add(sectionHeader(title), BorderLayout.NORTH);

        JTable table = styledTable(col, data);
        JScrollPane sp = styledScroll(table);
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    private JTable styledTable(String[] col, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, col) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_WHITE : new Color(0xF5F0EA));
                }
                return c;
            }
        };
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(F_BODY);
        table.setForeground(C_DARK_TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(F_BODY_B);
        header.setBackground(C_RUSSET);
        header.setForeground(C_WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Center-align all cells
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < col.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        return table;
    }

    private JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(C_PUTTY, 1));
        sp.getViewport().setBackground(C_WHITE);
        sp.setBackground(C_WHITE);
        return sp;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_HEADING);
        lbl.setForeground(C_ESPRESSO);
        lbl.setBorder(new EmptyBorder(0, 0, 18, 0));
        return lbl;
    }

    private Icon makeCircleIcon(Color color, int r) {
        int d = r * 2;
        BufferedImage img = new BufferedImage(d, d, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, d, d);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ══════════════════════ ENTRY POINT ══════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(StudentDashboardUI::new);
    }
}