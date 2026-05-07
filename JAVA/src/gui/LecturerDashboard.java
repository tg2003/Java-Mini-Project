package gui;

import dao.AttendanceDAO;
import dao.MedicalDAO;
import db.DBConnection;
import models.AttendanceCourseSummary;
import models.Medical;
import util.Logout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.List;


public class LecturerDashboard extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color ESPRESSO = Color.decode("#291C0E");
    private static final Color RUSSET = Color.decode("#6E473B");
    private static final Color SANDSTONE = Color.decode("#A78D78");
    private static final Color PUTTY = Color.decode("#BEB5A9");
    private static final Color ALMOND = Color.decode("#E1D4C2");
    private static final Color CARD_BG = Color.decode("#FAF7F4");
    private static final Color DARK_TEXT = Color.decode("#1A1008");
    private static final Color SIDEBAR_BG = Color.decode("#3B2010");
    private static final Color HOVER_BG = Color.decode("#5A3020");
    private static final Color ACTIVE_BG = Color.decode("#6E473B");
    private static final Color GREEN = Color.decode("#2D6A4F");
    private static final Color RED = Color.decode("#9B2335");

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_DISPLAY = new Font("Georgia", Font.BOLD, 14);
    private static final Font F_HEADING = new Font("Georgia", Font.BOLD, 22);
    private static final Font F_BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_BODY_B = new Font("SansSerif", Font.BOLD, 13);
    private static final Font F_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_NAV = new Font("SansSerif", Font.BOLD, 12);

    private final String lecturerId;
    private JPanel contentArea;
    private JLabel lblSidebarName;
    private JLabel lblSidebarDept;
    private JLabel avatarLabel;
    private JButton activeNavBtn = null;

    private String lecturerName = "";
    private String lecturerDept = "";
    private String profilePicPath = "";

    // ── Constructor ───────────────────────────────────────────────────────────
    public LecturerDashboard(String lecturerId) {
        this.lecturerId = lecturerId;
        UITheme.applyGlobalDefaults();
        initUI();
        loadLecturerData();
        showDashboard();
        setVisible(true);
    }


    private void initUI() {
        setTitle("Lecturer Portal");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ALMOND);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ALMOND);
        root.add(contentArea, BorderLayout.CENTER);
    }


    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(220, 680));
        sb.setBackground(SIDEBAR_BG);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("LMS");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Georgia", Font.BOLD, 26));
        logo.setBorder(new EmptyBorder(22, 22, 2, 22));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(logo);

        JLabel sub = new JLabel("Lecturer Portal");
        sub.setForeground(SANDSTONE);
        sub.setFont(F_SMALL);
        sub.setBorder(new EmptyBorder(0, 22, 14, 22));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(sub);


        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        avatarRow.setOpaque(false);
        avatarRow.setMaximumSize(new Dimension(220, 68));
        avatarRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                if (getIcon() != null) {
                    getIcon().paintIcon(this, g2, 0, 0);
                } else {
                    g2.setColor(SANDSTONE);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Georgia", Font.BOLD, 18));
                    String init = lecturerName.isEmpty() ? "L"
                            : String.valueOf(lecturerName.charAt(0)).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(init, (getWidth() - fm.stringWidth(init)) / 2, (getHeight() + fm.getAscent()) / 2 - 3);
                }
                g2.dispose();
            }
        };
        avatarLabel.setPreferredSize(new Dimension(44, 44));

        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        lblSidebarName = new JLabel("Loading...");
        lblSidebarName.setForeground(Color.WHITE);
        lblSidebarName.setFont(F_BODY_B);
        lblSidebarDept = new JLabel("");
        lblSidebarDept.setForeground(SANDSTONE);
        lblSidebarDept.setFont(F_SMALL);
        namePanel.add(lblSidebarName);
        namePanel.add(lblSidebarDept);
        avatarRow.add(avatarLabel);
        avatarRow.add(namePanel);
        sb.add(avatarRow);
        sb.add(sep());


        Object[][] navDef = {
                {"Dashboard", "\u229E"},
                {null, "VIEW"},
                {"Notices", "\uD83D\uDCE2"},
                {"Timetable", "\uD83D\uDCC5"},
                {"UG Attendance", "\u2705"},
                {"UG Medical Records", "\uD83C\uDFE5"},
                {"UG Eligibility", "\uD83D\uDCCA"},
                {"UG Details", "\uD83D\uDC65"},
                {null, "MARKS"},
                {"Mark Entry", "\u270F"},
                {"Grade & GPA", "\uD83C\uDF93"},
                {null, "MATERIAL"},
                {"Course Materials", "\uD83D\uDCC1"},
                {null, "PROFILE"},
                {"My Profile", "\uD83D\uDC64"},
        };

        for (Object[] item : navDef) {
            if (item[0] == null) {
                JLabel lbl = new JLabel((String) item[1]);
                lbl.setForeground(PUTTY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
                lbl.setBorder(new EmptyBorder(12, 22, 4, 22));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                sb.add(lbl);
            } else {
                String label = (String) item[0];
                String icon = (String) item[1];
                JButton btn = navBtn(icon + "  " + label);
                btn.addActionListener(e -> handleNav(label, btn));
                sb.add(btn);
            }
        }

        sb.add(Box.createVerticalGlue());
        sb.add(sep());
        JButton logout = navBtn("\u23FB  Logout");
        logout.addActionListener(e -> {
            DBConnection.closeConnection();
            new Logout().logout(this);
            dispose();
        });
        sb.add(logout);
        sb.add(Box.createVerticalStrut(12));
        return sb;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(220, 38));
        b.setMinimumSize(new Dimension(220, 38));
        b.setPreferredSize(new Dimension(220, 38));
        b.setBackground(SIDEBAR_BG);
        b.setForeground(new Color(230, 215, 200));
        b.setFont(F_NAV);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(8, 22, 8, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (b != activeNavBtn) b.setBackground(HOVER_BG);
            }

            public void mouseExited(MouseEvent e) {
                if (b != activeNavBtn) b.setBackground(SIDEBAR_BG);
            }
        });
        return b;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(220, 1));
        s.setForeground(HOVER_BG);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    private void handleNav(String item, JButton btn) {
        if (activeNavBtn != null) activeNavBtn.setBackground(SIDEBAR_BG);
        activeNavBtn = btn;
        btn.setBackground(ACTIVE_BG);
        switch (item) {
            case "Dashboard" -> showDashboard();
            case "Notices" -> showNotices();
            case "Timetable" -> showTimetable();
            case "UG Attendance" -> showUGAttendance();
            case "UG Medical Records" -> showUGMedical();
            case "UG Eligibility" -> showEligibility();
            case "UG Details" -> showUGDetails();
            case "Mark Entry" -> showMarkEntry();
            case "Grade & GPA" -> showGradeGPA();
            case "Course Materials" -> showCourseMaterials();
            case "My Profile" -> showProfile();
        }
    }


    //  DATA LOAD

    private void loadLecturerData() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT l.Name, l.Dpt_name, u.Profile_pic " +
                             "FROM LECTURER l JOIN user u ON l.Lec_id = u.User_id WHERE l.Lec_id = ?")) {
            ps.setString(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lecturerName = rs.getString("Name");
                lecturerDept = rs.getString("Dpt_name");
                String pic = rs.getString("Profile_pic");
                profilePicPath = (pic == null) ? "" : pic;
                lblSidebarName.setText(lecturerName);
                lblSidebarDept.setText(lecturerDept);
                refreshAvatar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshAvatar() {
        if (!profilePicPath.isBlank()) {
            try {
                File f = resolveFile(profilePicPath);
                if (f != null) {
                    Image img = ImageIO.read(f).getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                }
            } catch (Exception ignored) {
            }
        }
        avatarLabel.repaint();
    }

    private File resolveFile(String path) {
        if (path == null || path.isBlank()) return null;
        File f = new File(path);
        if (f.exists()) return f;
        f = new File(System.getProperty("user.dir") + "/JAVA/src/" + path);
        if (f.exists()) return f;
        return null;
    }

    //  CONTENT SWITCHER

    private void setContent(JPanel p) {
        contentArea.removeAll();
        contentArea.add(p, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }


    //  DASHBOARD

    private void showDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ALMOND);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ESPRESSO, getWidth(), 0, RUSSET));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        hero.setLayout(new BorderLayout());
        hero.setPreferredSize(new Dimension(0, 110));
        hero.setBorder(new EmptyBorder(24, 30, 24, 30));
        JLabel wlc = new JLabel("Welcome back, " + lecturerName);
        wlc.setFont(new Font("Georgia", Font.BOLD, 22));
        wlc.setForeground(Color.WHITE);
        JLabel dpt = new JLabel(lecturerDept + "  \u2022  Lecturer Portal");
        dpt.setFont(F_SMALL);
        dpt.setForeground(SANDSTONE);
        JPanel ht = new JPanel();
        ht.setOpaque(false);
        ht.setLayout(new BoxLayout(ht, BoxLayout.Y_AXIS));
        ht.add(wlc);
        ht.add(Box.createVerticalStrut(6));
        ht.add(dpt);
        hero.add(ht, BorderLayout.CENTER);
        p.add(hero, BorderLayout.NORTH);

        String[][] items = {
                {"\uD83D\uDCE2", "Notices", "Notices"},
                {"\uD83D\uDCC5", "Timetable", "Timetable"},
                {"\u2705", "UG Attendance", "UG Attendance"},
                {"\u270F", "Mark Entry", "Mark Entry"},
                {"\uD83D\uDCC1", "Course Materials", "Course Materials"},
                {"\uD83D\uDC64", "My Profile", "My Profile"}
        };
        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 16));
        cards.setBackground(ALMOND);
        cards.setBorder(new EmptyBorder(24, 0, 0, 0));
        for (String[] s : items) cards.add(dashCard(s[0], s[1], s[2]));
        p.add(cards, BorderLayout.CENTER);
        setContent(p);
    }

    private JPanel dashCard(String icon, String label, String target) {
        JPanel c = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(22, 20, 22, 20));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel il = new JLabel(icon, SwingConstants.CENTER);
        il.setFont(new Font("Dialog", Font.PLAIN, 28));
        il.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel ll = new JLabel(label, SwingConstants.CENTER);
        ll.setFont(F_BODY_B);
        ll.setForeground(ESPRESSO);
        ll.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.add(Box.createVerticalGlue());
        c.add(il);
        c.add(Box.createVerticalStrut(8));
        c.add(ll);
        c.add(Box.createVerticalGlue());
        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleNav(target, navBtn(""));
            }
        });
        return c;
    }


    //notice
    private void showNotices() {
        JPanel p = page("Notices");
        String[] cols = {"No", "Title", "Date & Time", "_path", "Download"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };
        JTable table = styledTable(model);
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setWidth(0);

        table.getColumn("Download").setCellRenderer((t, v, s, f, r, co) -> {
            JButton b = new JButton("\uD83D\uDCE5 Download");
            b.setBackground(ALMOND);
            b.setForeground(ESPRESSO);
            b.setFont(F_SMALL);
            return b;
        });
        table.getColumn("Download").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            final JButton b = new JButton("\uD83D\uDCE5 Download");

            {
                b.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    fireEditingStopped();
                    openFile(model.getValueAt(row, 3).toString());
                });
            }

            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return b;
            }
        });

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM NOTICE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{
                    rs.getString("Notice_no"), rs.getString("Title"),
                    rs.getString("Date_time"), rs.getString("Download_link"), "Download"
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        setContent(p);
    }

    private void openFile(String path) {
        if (path == null || path.isBlank()) {
            JOptionPane.showMessageDialog(this, "No file available.");
            return;
        }
        try {
            File f = new File(System.getProperty("user.dir") + "/JAVA/src/" + path);
            if (!f.exists()) f = new File(path);
            if (f.exists()) Desktop.getDesktop().open(f);
            else JOptionPane.showMessageDialog(this, "File not found.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //  time table view only

    private void showTimetable() {
        JPanel p = page("Timetable");
        String[] cols = {"ID", "Day", "Start", "End", "Course Code", "Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = styledTable(model);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM timetable ORDER BY FIELD(Day,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), Start_time");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{
                    rs.getString("Timetable_id"), rs.getString("Day"),
                    rs.getTime("Start_time"), rs.getTime("End_time"),
                    rs.getString("C_code"), rs.getString("Type")
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        setContent(p);
    }


    //  ug attendance
    private void showUGAttendance() {
        JPanel p = page("Undergraduate Attendance");


        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterBar.setOpaque(false);

        JLabel idLbl = new JLabel("Student ID:");
        idLbl.setFont(F_BODY);
        JTextField idField = new JTextField(12);
        idField.setFont(F_BODY);

        JLabel batchLbl = new JLabel("Batch Year:");
        batchLbl.setFont(F_BODY);
        JComboBox<String> batchCbo = new JComboBox<>();
        batchCbo.setFont(F_BODY);
        batchCbo.setPreferredSize(new Dimension(110, 28));

        JLabel courseLbl = new JLabel("Course:");
        courseLbl.setFont(F_BODY);
        JComboBox<String> courseCbo = new JComboBox<>();
        courseCbo.setFont(F_BODY);
        courseCbo.setPreferredSize(new Dimension(250, 28));

        JButton searchBtn = btn("Search");
        JButton clearBtn = btn("Clear");

        filterBar.add(idLbl);
        filterBar.add(idField);
        filterBar.add(batchLbl);
        filterBar.add(batchCbo);
        filterBar.add(courseLbl);
        filterBar.add(courseCbo);
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);

        JLabel hint = new JLabel("  Use any combination of filters, then click Search.");
        hint.setFont(F_SMALL);
        hint.setForeground(SANDSTONE);
        JPanel hintRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        hintRow.setOpaque(false);
        hintRow.add(hint);

        JPanel filterWrap = new JPanel();
        filterWrap.setOpaque(false);
        filterWrap.setLayout(new BoxLayout(filterWrap, BoxLayout.Y_AXIS));
        filterWrap.add(filterBar);
        filterWrap.add(hintRow);

        // Table
        String[] cols = {"Student ID", "Student Name", "Course Code", "Course Name",
                "Attended Hrs", "Total Hrs", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = styledTable(model);
        p.add(filterWrap, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        //  dropdowns
        try (Connection con = DBConnection.getConnection()) {
            batchCbo.addItem("-- All Batches --");
            ResultSet rs = con.prepareStatement(
                    "SELECT DISTINCT Batch_yr FROM ENROLLS_IN ORDER BY Batch_yr DESC").executeQuery();
            while (rs.next()) batchCbo.addItem(String.valueOf(rs.getInt("Batch_yr")));

            courseCbo.addItem("-- All Courses --");
            ResultSet rs2 = con.prepareStatement(
                    "SELECT C_code, C_name FROM COURSE ORDER BY C_code").executeQuery();
            while (rs2.next())
                courseCbo.addItem(rs2.getString("C_code") + " - " + rs2.getString("C_name"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Runnable doSearch = () -> {
            String ugId = idField.getText().trim();
            String batch = batchCbo.getSelectedIndex() > 0 ? batchCbo.getSelectedItem().toString() : null;
            String cCode = courseCbo.getSelectedIndex() > 0
                    ? courseCbo.getSelectedItem().toString().split(" - ")[0].trim() : null;

            if (ugId.isEmpty() && batch == null && cCode == null) {
                hint.setText("  Please enter at least one search criterion.");
                hint.setForeground(SANDSTONE);
                return;
            }

            // attendance = present + medical aproove
            StringBuilder sql = new StringBuilder(
                    "SELECT e.Ug_id, ug.Name, c.C_code, c.C_name, " +
                            "COALESCE(SUM(CASE WHEN a.Ug_id IS NOT NULL " +
                            "  THEN TIME_TO_SEC(TIMEDIFF(t.End_time,t.Start_time))/3600.0 ELSE 0 END),0) AS total_hours, " +
                            "COALESCE(SUM(CASE WHEN a.Status IN ('Present','MedicalApproved') " +
                            "  THEN TIME_TO_SEC(TIMEDIFF(t.End_time,t.Start_time))/3600.0 ELSE 0 END),0) AS attended_hours " +
                            "FROM ENROLLS_IN e " +
                            "JOIN UNDERGRADUATE ug ON ug.Ug_id=e.Ug_id " +
                            "JOIN COURSE c ON c.C_code=e.C_code " +
                            "LEFT JOIN TIMETABLE t ON t.C_code=e.C_code " +
                            "LEFT JOIN ATTENDANCE a ON a.Timetable_id=t.Timetable_id AND a.Ug_id=e.Ug_id " +
                            "WHERE 1=1");
            java.util.List<String> params = new java.util.ArrayList<>();
            if (!ugId.isEmpty()) {
                sql.append(" AND e.Ug_id=?");
                params.add(ugId);
            }
            if (batch != null) {
                sql.append(" AND e.Batch_yr=?");
                params.add(batch);
            }
            if (cCode != null) {
                sql.append(" AND e.C_code=?");
                params.add(cCode);
            }
            sql.append(" GROUP BY e.Ug_id, ug.Name, c.C_code, c.C_name ORDER BY e.Ug_id, c.C_code");

            model.setRowCount(0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
                ResultSet rs = ps.executeQuery();
                int count = 0;
                while (rs.next()) {
                    double tot = rs.getDouble("total_hours");
                    double att = rs.getDouble("attended_hours");
                    double pct = tot > 0 ? (att / tot) * 100.0 : 0.0;
                    model.addRow(new Object[]{
                            rs.getString("Ug_id"), rs.getString("Name"),
                            rs.getString("C_code"), rs.getString("C_name"),
                            String.format("%.1f", att), String.format("%.1f", tot),
                            String.format("%.1f%%", pct)
                    });
                    count++;
                }
                hint.setText("  " + count + " row(s) found.");
                hint.setForeground(count > 0 ? ESPRESSO : SANDSTONE);
            } catch (Exception ex) {
                ex.printStackTrace();
                hint.setText("  Error: " + ex.getMessage());
                hint.setForeground(RED);
            }
        };

        searchBtn.addActionListener(e -> doSearch.run());
        idField.addActionListener(e -> doSearch.run());
        clearBtn.addActionListener(e -> {
            idField.setText("");
            batchCbo.setSelectedIndex(0);
            courseCbo.setSelectedIndex(0);
            model.setRowCount(0);
            hint.setText("  Use any combination of filters, then click Search.");
            hint.setForeground(SANDSTONE);
        });
        setContent(p);
    }


    //  UG MEDICAL
    private void showUGMedical() {
        JPanel p = page("Undergraduate Medical Records");
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JTextField idField = new JTextField(18);
        idField.setFont(F_BODY);
        JButton searchBtn = btn("Search");
        JLabel hint = new JLabel("  Enter a Student ID to view records");
        hint.setFont(F_SMALL);
        hint.setForeground(SANDSTONE);
        searchBar.add(new JLabel("Student ID:"));
        searchBar.add(idField);
        searchBar.add(searchBtn);
        searchBar.add(hint);

        String[] cols = {"Ref No", "Student ID", "From Date", "To Date", "Session Type", "Status", "Course"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = styledTable(model);
        p.add(searchBar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        MedicalDAO dao = new MedicalDAO();
        searchBtn.addActionListener(e -> {
            String ugId = idField.getText().trim();
            model.setRowCount(0);
            if (ugId.isEmpty()) {
                hint.setText("  Please enter a Student ID.");
                return;
            }
            List<Medical> list = dao.getByStudent(ugId);
            if (list.isEmpty()) {
                hint.setText("  No records found for: " + ugId);
                return;
            }
            for (Medical m : list)
                model.addRow(new Object[]{
                        m.getReferenceNo(), m.getUgId(),
                        m.getFromDate(), m.getToDate(),
                        m.getSessionType(), m.getStatus(), m.getCCode()
                });
            hint.setText("  " + list.size() + " record(s) found for: " + ugId);
        });
        setContent(p);
    }

// ugeligibility
    private void showEligibility() {
        JPanel p = page("CA Eligibility Report");


        JPanel filterWrap = new JPanel();
        filterWrap.setOpaque(false);
        filterWrap.setLayout(new BoxLayout(filterWrap, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row1.setOpaque(false);

        JLabel regLbl = new JLabel("Reg No (optional):");
        regLbl.setFont(F_BODY);
        JTextField regField = new JTextField(14);
        regField.setFont(F_BODY);

        JLabel batchLbl = new JLabel("Batch Year:");
        batchLbl.setFont(F_BODY);
        JComboBox<String> batchCbo = new JComboBox<>();
        batchCbo.setFont(F_BODY);
        batchCbo.setPreferredSize(new Dimension(110, 28));

        row1.add(regLbl);
        row1.add(regField);
        row1.add(batchLbl);
        row1.add(batchCbo);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row2.setOpaque(false);

        JLabel crsLbl = new JLabel("Course (required):");
        crsLbl.setFont(F_BODY);
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(F_BODY);
        cbo.setPreferredSize(new Dimension(300, 28));

        JButton loadBtn = btn("Load Report");
        JButton clearBtn = btn("Clear");

        row2.add(crsLbl);
        row2.add(cbo);
        row2.add(loadBtn);
        row2.add(clearBtn);

        filterWrap.add(row1);
        filterWrap.add(row2);


        JPanel summaryRow = new JPanel(new GridLayout(1, 4, 12, 0));
        summaryRow.setOpaque(false);
        summaryRow.setPreferredSize(new Dimension(0, 70));
        JLabel[] sVals = new JLabel[4];
        String[] sLabels = {"Total Students", "Eligible", "Not Eligible", "Eligibility Rate"};
        Color[] sColors = {Color.decode("#A0C8E6"), Color.decode("#AAD778"), Color.decode("#DC6450"), Color.decode("#C87EBC")};
        for (int i = 0; i < 4; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ALMOND, 1, true), new EmptyBorder(8, 14, 8, 14)));
            JLabel lbl = new JLabel(sLabels[i]);
            lbl.setFont(F_SMALL);
            lbl.setForeground(PUTTY);
            sVals[i] = new JLabel("\u2014");
            sVals[i].setFont(new Font("Georgia", Font.BOLD, 20));
            sVals[i].setForeground(sColors[i]);
            card.add(lbl, BorderLayout.NORTH);
            card.add(sVals[i], BorderLayout.CENTER);
            summaryRow.add(card);
        }

        // Table
        String[] cols = {"Student ID", "Enroll Status", "CA (weighted)", "CA %", "CA Eligible?"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = styledTable(model);
        JLabel statusLbl = new JLabel("Select a course and click Load Report. Reg No and Batch Year are optional filters.");
        statusLbl.setFont(F_SMALL);
        statusLbl.setForeground(SANDSTONE);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(summaryRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(statusLbl, BorderLayout.SOUTH);

        p.add(filterWrap, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);

        // Populate dropdowns
        try (Connection con = DBConnection.getConnection()) {
            batchCbo.addItem("-- All Batches --");
            ResultSet rs = con.prepareStatement(
                    "SELECT DISTINCT Batch_yr FROM ENROLLS_IN ORDER BY Batch_yr DESC").executeQuery();
            while (rs.next()) batchCbo.addItem(String.valueOf(rs.getInt("Batch_yr")));

            cbo.addItem("-- Select a Course --");
            ResultSet rs2 = con.prepareStatement(
                    "SELECT C_code, C_name FROM COURSE ORDER BY C_code").executeQuery();
            while (rs2.next()) cbo.addItem(rs2.getString("C_code") + " - " + rs2.getString("C_name"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Runnable doLoad = () -> {
            if (cbo.getSelectedIndex() <= 0) {
                statusLbl.setText("Please select a course first.");
                return;
            }
            String cCode = cbo.getSelectedItem().toString().split(" - ")[0].trim();
            String regNo = regField.getText().trim();
            String batch = batchCbo.getSelectedIndex() > 0 ? batchCbo.getSelectedItem().toString() : null;
            loadEligibilityData(cCode, regNo.isEmpty() ? null : regNo, batch, model, sVals, statusLbl);
        };

        loadBtn.addActionListener(e -> doLoad.run());
        regField.addActionListener(e -> doLoad.run());
        clearBtn.addActionListener(e -> {
            regField.setText("");
            batchCbo.setSelectedIndex(0);
            cbo.setSelectedIndex(0);
            model.setRowCount(0);
            for (JLabel v : sVals) v.setText("\u2014");
            statusLbl.setText("Cleared.");
            statusLbl.setForeground(SANDSTONE);
        });

        setContent(p);
    }

    private void loadEligibilityData(String cCode, String regNoFilter, String batchFilter,
                                     DefaultTableModel model, JLabel[] sVals, JLabel statusLbl) {
        model.setRowCount(0);
        statusLbl.setText("Loading...");
        statusLbl.setForeground(SANDSTONE);

        StringBuilder sql = new StringBuilder(
                "SELECT E.Ug_id, E.Status AS enroll_status, " +
                        "COALESCE(M.Quiz01,0) AS Quiz01, COALESCE(M.Quiz02,0) AS Quiz02, COALESCE(M.Quiz03,0) AS Quiz03, " +
                        "COALESCE(M.Assignment1,0) AS Assignment1, COALESCE(M.Assignment2,0) AS Assignment2, " +
                        "COALESCE(M.Project,0) AS Project, COALESCE(M.Mid_T,0) AS Mid_T, COALESCE(M.Mid_P,0) AS Mid_P " +
                        "FROM ENROLLS_IN E " +
                        "LEFT JOIN MARKS M ON E.Ug_id=M.Ug_id AND M.C_code=E.C_code " +
                        "WHERE E.C_code=?");
        java.util.List<String> params = new java.util.ArrayList<>();
        params.add(cCode);
        if (regNoFilter != null) {
            sql.append(" AND E.Ug_id=?");
            params.add(regNoFilter);
        }
        if (batchFilter != null) {
            sql.append(" AND E.Batch_yr=?");
            params.add(batchFilter);
        }
        sql.append(" ORDER BY E.Ug_id");

        int total = 0, eligible = 0;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                models.Mark mark = new models.Mark(rs.getString("Ug_id"), cCode,
                        rs.getDouble("Quiz01"), rs.getDouble("Quiz02"), rs.getDouble("Quiz03"),
                        rs.getDouble("Assignment1"), rs.getDouble("Assignment2"), rs.getDouble("Project"),
                        rs.getDouble("Mid_T"), rs.getDouble("Mid_P"), 0.0, 0.0, "Proper");
                models.GradeCalculator gc = new models.GradeCalculator(mark);
                double caTotal = gc.getCATotal(), caPct = gc.getCAPercentage();
                boolean ok = gc.isCAEligible();
                if (ok) eligible++;
                total++;
                model.addRow(new Object[]{
                        rs.getString("Ug_id"), rs.getString("enroll_status"),
                        String.format("%.2f", caTotal), String.format("%.1f%%", caPct),
                        ok ? "\u2705  Eligible" : "\u274C  Not Eligible"
                });
            }
            sVals[0].setText(String.valueOf(total));
            sVals[1].setText(String.valueOf(eligible));
            sVals[2].setText(String.valueOf(total - eligible));
            sVals[3].setText(total > 0 ? String.format("%.0f%%", (eligible * 100.0) / total) : "\u2014");
            statusLbl.setText(total + " student(s) \u2014 " + eligible + " eligible, " + (total - eligible) + " not eligible.");
            statusLbl.setForeground(GREEN);
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLbl.setText("Error: " + ex.getMessage());
            statusLbl.setForeground(RED);
        }
    }

// ug details
    private void showUGDetails() {
        JPanel p = page("Undergraduate Details");
        String[] cols = {"ID", "Name", "Email", "NIC", "DOB", "Department", "Address", "Phones"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 7;
            }
        };
        JTable table = styledTable(model);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        table.getColumn("Phones").setCellRenderer((t, v, s, f, r, c) -> {
            JButton b = new JButton("\uD83D\uDCDE View");
            b.setBackground(ALMOND);
            b.setForeground(ESPRESSO);
            b.setFont(F_SMALL);
            return b;
        });
        table.getColumn("Phones").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            final JButton b = new JButton("\uD83D\uDCDE View");

            {
                b.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    fireEditingStopped();
                    new ViewPhonesGUI(model.getValueAt(row, 0).toString(), "UNDERGRADUATE_PHONE", "Ug_id").setVisible(true);
                });
            }

            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return b;
            }
        });

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Ug_id, Name, Email, Nic, Dob, Dpt_name, " +
                             "CONCAT(No, ', ', Street, ', ', City) AS Addr FROM UNDERGRADUATE ORDER BY Ug_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{
                    rs.getString("Ug_id"), rs.getString("Name"), rs.getString("Email"),
                    rs.getString("Nic"), rs.getString("Dob"), rs.getString("Dpt_name"),
                    rs.getString("Addr"), "View"
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        JButton refresh = btn("\u21BB  Refresh");
        refresh.addActionListener(e -> showUGDetails());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(refresh);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        setContent(p);
    }

// mark entry
    private void showMarkEntry() {
        JPanel p = page("Mark Entry");


        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterBar.setOpaque(false);

        JLabel regLabel = new JLabel("Reg No (Student ID):");
        regLabel.setFont(F_BODY);
        JTextField regField = new JTextField(14);
        regField.setFont(F_BODY);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(F_BODY);
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(F_BODY);
        cbo.setPreferredSize(new Dimension(240, 30));

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(F_BODY);
        JComboBox<String> typeCbo = new JComboBox<>(new String[]{"Both (Theory & Practical)", "Theory Only", "Practical Only"});
        typeCbo.setFont(F_BODY);
        typeCbo.setPreferredSize(new Dimension(200, 30));

        JButton searchBtn = btn("Search");
        JButton clearBtn = btn("Clear");

        filterBar.add(regLabel);
        filterBar.add(regField);
        filterBar.add(courseLabel);
        filterBar.add(cbo);
        filterBar.add(typeLabel);
        filterBar.add(typeCbo);
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);

        // Table
        String[] cols = {"Student ID", "Quiz 01", "Quiz 02", "Quiz 03", "Assign 1", "Assign 2",
                "Project", "Mid Theory", "Mid Practical", "Final Theory", "Final Practical", "Attempt Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c != 0;
            }

            public Class<?> getColumnClass(int c) {
                return String.class;
            }
        };
        JTable table = styledTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {95, 65, 65, 65, 70, 70, 68, 90, 95, 90, 98, 120};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        JComboBox<String> attemptCbo = new JComboBox<>(new String[]{"Proper", "Repeat", "MedicalRepeat"});
        table.getColumnModel().getColumn(11).setCellEditor(new DefaultCellEditor(attemptCbo));


        JLabel statusLbl = new JLabel("Enter a Student ID and/or select a course, then click Search.");
        statusLbl.setFont(F_SMALL);
        statusLbl.setForeground(SANDSTONE);
        JButton saveBtn = btn("\uD83D\uDCBE  Save All Marks");
        JPanel botBar = new JPanel(new BorderLayout());
        botBar.setOpaque(false);
        botBar.setBorder(new EmptyBorder(8, 0, 0, 0));
        botBar.add(statusLbl, BorderLayout.WEST);
        botBar.add(saveBtn, BorderLayout.EAST);

        p.add(filterBar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(botBar, BorderLayout.SOUTH);

        // Load all courses
        final String[] selectedCourse = {null};
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            cbo.addItem("-- All Courses --");
            while (rs.next()) cbo.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Search logic
        Runnable doSearch = () -> {
            String regNo = regField.getText().trim();
            String typeStr = typeCbo.getSelectedItem().toString();
            String cCode = cbo.getSelectedIndex() > 0 ? getCCode(cbo) : null;


            StringBuilder sql = new StringBuilder(
                    "SELECT E.Ug_id, M.Quiz01, M.Quiz02, M.Quiz03, " +
                            "M.Assignment1, M.Assignment2, M.Project, M.Mid_T, M.Mid_P, M.Final_T, M.Final_P, M.Attempt_type " +
                            "FROM ENROLLS_IN E LEFT JOIN MARKS M ON E.Ug_id=M.Ug_id AND M.C_code=E.C_code WHERE 1=1");
            java.util.List<String> params = new java.util.ArrayList<>();
            if (!regNo.isEmpty()) {
                sql.append(" AND E.Ug_id=?");
                params.add(regNo);
            }
            if (cCode != null) {
                sql.append(" AND E.C_code=?");
                params.add(cCode);
            }
            // Attempt type filter
            if (typeStr.startsWith("Theory")) {
                sql.append(" AND (M.Attempt_type IS NULL OR M.Final_T IS NOT NULL)");
            }
            if (typeStr.startsWith("Practical")) {
                sql.append(" AND M.Final_P IS NOT NULL");
            }
            sql.append(" ORDER BY E.Ug_id");

            model.setRowCount(0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
                ResultSet rs = ps.executeQuery();
                int count = 0;
                while (rs.next()) {
                    java.util.Vector<Object> row = new java.util.Vector<>();
                    row.add(rs.getString("Ug_id"));
                    for (String col : new String[]{"Quiz01", "Quiz02", "Quiz03", "Assignment1", "Assignment2",
                            "Project", "Mid_T", "Mid_P", "Final_T", "Final_P"}) {
                        double v = rs.getDouble(col);
                        row.add(rs.wasNull() ? "" : String.valueOf(v));
                    }
                    String att = rs.getString("Attempt_type");
                    row.add(att != null ? att : "Proper");
                    model.addRow(row);
                    count++;
                }
                if (count == 0) {
                    statusLbl.setText("No records found.");
                    statusLbl.setForeground(SANDSTONE);
                } else {
                    statusLbl.setText(count + " student(s) loaded.");
                    statusLbl.setForeground(ESPRESSO);
                }
                selectedCourse[0] = cCode;
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLbl.setText("Error: " + ex.getMessage());
                statusLbl.setForeground(RED);
            }
        };

        searchBtn.addActionListener(e -> doSearch.run());
        regField.addActionListener(e -> doSearch.run()); // Enter key triggers search
        clearBtn.addActionListener(e -> {
            regField.setText("");
            cbo.setSelectedIndex(0);
            model.setRowCount(0);
            selectedCourse[0] = null;
            statusLbl.setText("Cleared.");
            statusLbl.setForeground(SANDSTONE);
        });

        saveBtn.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().stopCellEditing();
            int rows = model.getRowCount();
            if (rows == 0) {
                JOptionPane.showMessageDialog(this, "No data to save.");
                return;
            }
            int saved = 0, errors = 0;
            String cCode = cbo.getSelectedIndex() > 0 ? getCCode(cbo) : null;
            for (int i = 0; i < rows; i++) {
                String ugId = model.getValueAt(i, 0).toString();

                String rowCourse = cCode;
                if (rowCourse == null) {
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement("SELECT C_code FROM ENROLLS_IN WHERE Ug_id=? LIMIT 1")) {
                        ps.setString(1, ugId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) rowCourse = rs.getString("C_code");
                    } catch (Exception ignored) {
                    }
                }
                if (rowCourse == null) {
                    errors++;
                    continue;
                }
                try {
                    double[] vals = new double[10];
                    for (int j = 0; j < 10; j++) {
                        Object v = model.getValueAt(i, j + 1);
                        vals[j] = (v == null || v.toString().isBlank()) ? 0.0 : Double.parseDouble(v.toString().trim());
                    }
                    String att = model.getValueAt(i, 11) == null ? "Proper" : model.getValueAt(i, 11).toString();
                    upsertMark(ugId, rowCourse, vals, att);
                    saved++;
                } catch (Exception ex) {
                    errors++;
                }
            }
            if (errors == 0) {
                statusLbl.setText("\u2713 All " + saved + " records saved!");
                statusLbl.setForeground(GREEN);
                JOptionPane.showMessageDialog(this, saved + " marks saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLbl.setText(saved + " saved, " + errors + " errors.");
                statusLbl.setForeground(RED);
            }
        });
        setContent(p);
    }

    private void upsertMark(String ugId, String cCode, double[] v, String attempt) throws SQLException {
        String check = "SELECT Mark_id FROM MARKS WHERE Ug_id=? AND C_code=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(check)) {
            ps.setString(1, ugId);
            ps.setString(2, cCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement upd = con.prepareStatement(
                        "UPDATE MARKS SET Quiz01=?,Quiz02=?,Quiz03=?,Assignment1=?,Assignment2=?,Project=?,Mid_T=?,Mid_P=?,Final_T=?,Final_P=?,Attempt_type=? WHERE Ug_id=? AND C_code=?");
                for (int i = 0; i < 10; i++) upd.setDouble(i + 1, v[i]);
                upd.setString(11, attempt);
                upd.setString(12, ugId);
                upd.setString(13, cCode);
                upd.executeUpdate();
            } else {
                PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO MARKS (Ug_id,C_code,Quiz01,Quiz02,Quiz03,Assignment1,Assignment2,Project,Mid_T,Mid_P,Final_T,Final_P,Attempt_type) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ins.setString(1, ugId);
                ins.setString(2, cCode);
                for (int i = 0; i < 10; i++) ins.setDouble(i + 3, v[i]);
                ins.setString(13, attempt);
                ins.executeUpdate();
            }
        }
    }

    //  GRADE & GPA

    private void showGradeGPA() {
        JPanel p = page("Grade & GPA View");


        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterBar.setOpaque(false);

        JLabel regLabel = new JLabel("Reg No (Student ID):");
        regLabel.setFont(F_BODY);
        JTextField regField = new JTextField(14);
        regField.setFont(F_BODY);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(F_BODY);
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(F_BODY);
        cbo.setPreferredSize(new Dimension(260, 30));

        JButton searchBtn = btn("Search");
        JButton clearBtn = btn("Clear");
        filterBar.add(regLabel);
        filterBar.add(regField);
        filterBar.add(courseLabel);
        filterBar.add(cbo);
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);

        // Summary cards
        JPanel sumRow = new JPanel(new GridLayout(1, 5, 10, 0));
        sumRow.setOpaque(false);
        sumRow.setPreferredSize(new Dimension(0, 70));
        JLabel[] sVals = new JLabel[5];
        String[] sLabels = {"Students", "Passed (\u2265C-)", "Avg GPA", "Highest Grade", "SGPA"};
        Color[] sColors = {Color.decode("#A0C8E6"), Color.decode("#AAD778"),
                Color.decode("#CDB87A"), Color.decode("#C87EBC"), Color.decode("#E8A87C")};
        for (int i = 0; i < 5; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ALMOND, 1, true), new EmptyBorder(8, 14, 8, 14)));
            JLabel lbl = new JLabel(sLabels[i]);
            lbl.setFont(F_SMALL);
            lbl.setForeground(PUTTY);
            sVals[i] = new JLabel("\u2014");
            sVals[i].setFont(new Font("Georgia", Font.BOLD, 18));
            sVals[i].setForeground(sColors[i]);
            card.add(lbl, BorderLayout.NORTH);
            card.add(sVals[i], BorderLayout.CENTER);
            sumRow.add(card);
        }

        //Table
        String[] cols = {"Student ID", "Course", "CA (weighted)", "End (weighted)", "Final /100", "Grade", "GPA", "Credits"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = styledTable(model);

        JLabel statusLbl = new JLabel("Enter a Student ID and/or select a course, then click Search.");
        statusLbl.setFont(F_SMALL);
        statusLbl.setForeground(SANDSTONE);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(sumRow, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(statusLbl, BorderLayout.SOUTH);

        p.add(filterBar, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);

        // Load all courses
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            cbo.addItem("-- All Courses --");
            while (rs.next()) cbo.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Runnable doSearch = () -> {
            String regNo = regField.getText().trim();
            String cCode = cbo.getSelectedIndex() > 0 ? getCCode(cbo) : null;
            if (regNo.isEmpty() && cCode == null) {
                statusLbl.setText("Enter a Student ID or select a course first.");
                statusLbl.setForeground(SANDSTONE);
                return;
            }

            StringBuilder sql = new StringBuilder(
                    "SELECT E.Ug_id, E.C_code, c.Credit, " +
                            "COALESCE(M.Quiz01,0) AS Q1, COALESCE(M.Quiz02,0) AS Q2, COALESCE(M.Quiz03,0) AS Q3, " +
                            "COALESCE(M.Assignment1,0) AS A1, COALESCE(M.Assignment2,0) AS A2, COALESCE(M.Project,0) AS Prj, " +
                            "COALESCE(M.Mid_T,0) AS MT, COALESCE(M.Mid_P,0) AS MP, " +
                            "COALESCE(M.Final_T,0) AS FT, COALESCE(M.Final_P,0) AS FP, " +
                            "COALESCE(M.Attempt_type,'Proper') AS Att " +
                            "FROM ENROLLS_IN E " +
                            "JOIN COURSE c ON E.C_code=c.C_code " +
                            "LEFT JOIN MARKS M ON E.Ug_id=M.Ug_id AND M.C_code=E.C_code " +
                            "WHERE 1=1");
            java.util.List<String> params = new java.util.ArrayList<>();
            if (!regNo.isEmpty()) {
                sql.append(" AND E.Ug_id=?");
                params.add(regNo);
            }
            if (cCode != null) {
                sql.append(" AND E.C_code=?");
                params.add(cCode);
            }
            sql.append(" ORDER BY E.Ug_id, E.C_code");

            model.setRowCount(0);
            int total = 0, passed = 0;
            double gpaSum = 0, creditSum = 0, weightedGpaSum = 0;
            String topGrade = "F";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String rowCourse = rs.getString("C_code");
                    double credit = 0;
                    try {
                        credit = Double.parseDouble(rs.getString("Credit"));
                    } catch (Exception ignored) {
                    }
                    models.Mark mark = new models.Mark(rs.getString("Ug_id"), rowCourse,
                            rs.getDouble("Q1"), rs.getDouble("Q2"), rs.getDouble("Q3"),
                            rs.getDouble("A1"), rs.getDouble("A2"), rs.getDouble("Prj"),
                            rs.getDouble("MT"), rs.getDouble("MP"), rs.getDouble("FT"), rs.getDouble("FP"),
                            rs.getString("Att"));
                    models.GradeCalculator gc = new models.GradeCalculator(mark);
                    double ca = gc.getCATotal(), end = gc.getEndWeighted(), fin = gc.getFinalMark();
                    String grade = gc.getLetterGrade();
                    double gpa = gc.getGPA();
                    model.addRow(new Object[]{
                            rs.getString("Ug_id"), rowCourse,
                            String.format("%.2f", ca), String.format("%.2f", end),
                            String.format("%.1f", fin), grade, String.format("%.2f", gpa),
                            rs.getString("Credit")
                    });
                    total++;
                    gpaSum += gpa;
                    if (!grade.equals("F")) passed++;
                    if (gpa > 0 && (topGrade.equals("F") || gpa > gradeToGPA(topGrade))) topGrade = grade;
                    // SGPA calculation: weighted by credits
                    weightedGpaSum += gpa * credit;
                    creditSum += credit;
                }
                double sgpa = creditSum > 0 ? weightedGpaSum / creditSum : 0;
                sVals[0].setText(String.valueOf(total));
                sVals[1].setText(String.valueOf(passed));
                sVals[2].setText(total > 0 ? String.format("%.2f", gpaSum / total) : "\u2014");
                sVals[3].setText(topGrade.equals("F") && total == 0 ? "\u2014" : topGrade);
                sVals[4].setText(creditSum > 0 ? String.format("%.2f", sgpa) : "\u2014");
                statusLbl.setText(total > 0 ? total + " record(s) found." : "No records found.");
                statusLbl.setForeground(total > 0 ? GREEN : SANDSTONE);
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLbl.setText("Error: " + ex.getMessage());
                statusLbl.setForeground(RED);
            }
        };

        searchBtn.addActionListener(e -> doSearch.run());
        regField.addActionListener(e -> doSearch.run());
        clearBtn.addActionListener(e -> {
            regField.setText("");
            cbo.setSelectedIndex(0);
            model.setRowCount(0);
            for (JLabel v : sVals) v.setText("\u2014");
            statusLbl.setText("Cleared.");
            statusLbl.setForeground(SANDSTONE);
        });
        setContent(p);
    }

    private double gradeToGPA(String g) {
        return switch (g) {
            case "A+" -> 4.2;
            case "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "D+" -> 1.3;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }


    //  COURSE MATERIALS
    private void showCourseMaterials() {
        JPanel p = page("Course Materials");
        p.add(buildMaterialsPanel(), BorderLayout.CENTER);
        setContent(p);
    }

    private JPanel buildMaterialsPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);

        JLabel hint = new JLabel("Select a course then click Load. Use Upload to add a new PDF or Replace to update an existing one.");
        hint.setFont(F_SMALL);
        hint.setForeground(SANDSTONE);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(F_BODY);
        cbo.setPreferredSize(new Dimension(300, 30));
        JButton loadBtn = btn("Load");
        topBar.add(new JLabel("Course:"));
        topBar.add(cbo);
        topBar.add(loadBtn);

        String[] cols = {"#", "File Name", "_path", "View", "Replace"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 3 || c == 4;
            }
        };
        JTable table = styledTable(model);
        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setWidth(0);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);

        // button
        table.getColumn("View").setCellRenderer((t, v, s, f, r, c) -> {
            JButton b = new JButton("\uD83D\uDCC4 Open PDF");
            b.setBackground(Color.decode("#DFF0D8"));
            b.setForeground(Color.decode("#2D6A4F"));
            b.setFont(F_SMALL);
            return b;
        });
        table.getColumn("View").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            final JButton b = new JButton("\uD83D\uDCC4 Open PDF");

            {
                b.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    fireEditingStopped();
                    String path = model.getValueAt(row, 2).toString();
                    openMaterialFile(path);
                });
            }

            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return b;
            }
        });


        table.getColumn("Replace").setCellRenderer((t, v, s, f, r, c) -> {
            JButton b = new JButton("\uD83D\uDCC2 Replace PDF");
            b.setBackground(ALMOND);
            b.setForeground(ESPRESSO);
            b.setFont(F_SMALL);
            return b;
        });
        table.getColumn("Replace").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            final JButton b = new JButton("\uD83D\uDCC2 Replace PDF");

            {
                b.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    fireEditingStopped();
                    String oldPath = model.getValueAt(row, 2).toString();
                    String cCode = getCCode(cbo);
                    if (!cCode.isEmpty()) saveMaterial(cCode, oldPath, model, cbo);
                });
            }

            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return b;
            }
        });

        JButton uploadBtn = btn("\u2B06  Upload New PDF");
        uploadBtn.addActionListener(e -> {
            String cCode = getCCode(cbo);
            if (cCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a course first.");
                return;
            }
            saveMaterial(cCode, null, model, cbo);
        });
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bot.setOpaque(false);
        bot.add(uploadBtn);

        JPanel mid = new JPanel(new BorderLayout(0, 8));
        mid.setOpaque(false);
        mid.add(topBar, BorderLayout.NORTH);
        mid.add(new JScrollPane(table), BorderLayout.CENTER);
        mid.add(bot, BorderLayout.SOUTH);

        outer.add(hint, BorderLayout.NORTH);
        outer.add(mid, BorderLayout.CENTER);

        // Load ALL courses
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cbo.addItem(rs.getString("C_code") + " \u2013 " + rs.getString("C_name"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        loadBtn.addActionListener(e -> loadMaterials(getCCode(cbo), model));
        return outer;
    }

    private String getCCode(JComboBox<String> cbo) {
        Object sel = cbo.getSelectedItem();
        if (sel == null) return "";
        String s = sel.toString();
        if (s.contains(" \u2013 ")) return s.split(" \u2013 ")[0];
        if (s.contains(" - ")) return s.split(" - ")[0];
        return s;
    }

    private void loadMaterials(String cCode, DefaultTableModel model) {
        model.setRowCount(0);
        if (cCode.isEmpty()) return;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT Material FROM COURSE_MATERIAL WHERE C_code=? ORDER BY Material")) {
            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                String path = rs.getString("Material");
                model.addRow(new Object[]{i++, new File(path).getName(), path, "Open", "Replace"});
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No materials yet for: " + cCode + "\nUse 'Upload New PDF' to add one.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openMaterialFile(String path) {
        if (path == null || path.isBlank()) {
            JOptionPane.showMessageDialog(this, "No file path stored.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Same resolution as NoticeView (the working reference)
        File f = new File(System.getProperty("user.dir") + "/JAVA/src/" + path);
        if (!f.exists()) f = new File(path); // absolute fallback
        if (f.exists()) {
            try {
                Desktop.getDesktop().open(f);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot open file:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "File not found:\n" + f.getAbsolutePath(), "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveMaterial(String cCode, String oldPath, DefaultTableModel model, JComboBox<String> cbo) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(oldPath == null ? "Upload PDF" : "Select Replacement PDF");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            File chosen = fc.getSelectedFile();
            File destDir = new File(System.getProperty("user.dir") + "/JAVA/src/resources/materials/");
            if (!destDir.exists()) destDir.mkdirs();
            File dest = new File(destDir, cCode + "_" + System.currentTimeMillis() + "_" + chosen.getName());
            Files.copy(chosen.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String relPath = "resources/materials/" + dest.getName();
            try (Connection con = DBConnection.getConnection()) {
                if (oldPath != null && !oldPath.isBlank()) {
                    PreparedStatement ps = con.prepareStatement("UPDATE COURSE_MATERIAL SET Material=? WHERE C_code=? AND Material=?");
                    ps.setString(1, relPath);
                    ps.setString(2, cCode);
                    ps.setString(3, oldPath);
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO COURSE_MATERIAL (C_code,Material) VALUES(?,?)");
                    ps.setString(1, cCode);
                    ps.setString(2, relPath);
                    ps.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Material saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMaterials(cCode, model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    //  MY PROFILE  (change password + change profile picture)

    private void showProfile() {
        JPanel p = page("My Profile");
        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        body.setOpaque(false);
        body.add(buildAvatarCard());
        body.add(buildAccountCard());
        p.add(body, BorderLayout.CENTER);
        setContent(p);
    }

    private JPanel buildAvatarCard() {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setPaint(new GradientPaint(0, 0, ESPRESSO, 0, 110, RUSSET));
                g2.fillRoundRect(0, 0, getWidth(), 110, 18, 18);
                g2.fillRect(0, 90, getWidth(), 20);
                int cx = getWidth() / 2, cy = 110, r = 42;
                g2.setColor(SANDSTONE);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                File pf = resolveFile(profilePicPath);
                if (pf != null) {
                    try {
                        g2.setClip(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
                        g2.drawImage(ImageIO.read(pf), cx - r, cy - r, r * 2, r * 2, null);
                        g2.setClip(null);
                    } catch (Exception ignored) {
                    }
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Georgia", Font.BOLD, 28));
                    String init = lecturerName.isEmpty() ? "L" : String.valueOf(lecturerName.charAt(0)).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(init, cx - fm.stringWidth(init) / 2, cy + fm.getAscent() / 2 - 2);
                }
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(210, 310);
            }
        };
        card.setOpaque(false);
        JLabel nameL = new JLabel(lecturerName, SwingConstants.CENTER);
        nameL.setFont(F_DISPLAY);
        nameL.setForeground(ESPRESSO);
        nameL.setBounds(0, 185, 210, 22);
        JLabel roleL = new JLabel("Lecturer", SwingConstants.CENTER);
        roleL.setFont(F_SMALL);
        roleL.setForeground(SANDSTONE);
        roleL.setBounds(0, 208, 210, 18);
        JLabel deptL = new JLabel(lecturerDept, SwingConstants.CENTER);
        deptL.setFont(F_SMALL);
        deptL.setForeground(PUTTY);
        deptL.setBounds(0, 226, 210, 18);
        JLabel changeBtn = new JLabel("Change Photo", SwingConstants.CENTER);
        changeBtn.setFont(F_SMALL);
        changeBtn.setForeground(RUSSET);
        changeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeBtn.setBounds(30, 258, 150, 22);
        changeBtn.setBorder(BorderFactory.createLineBorder(RUSSET, 1, true));
        changeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                changeBtn.setOpaque(true);
                changeBtn.setBackground(RUSSET);
                changeBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                changeBtn.setOpaque(false);
                changeBtn.setForeground(RUSSET);
            }

            public void mouseClicked(MouseEvent e) {
                handleChangePhoto(card);
            }
        });
        card.add(nameL);
        card.add(roleL);
        card.add(deptL);
        card.add(changeBtn);
        return card;
    }

    private void handleChangePhoto(JPanel card) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Profile Photo");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            File chosen = fc.getSelectedFile();
            File destDir = new File(System.getProperty("user.dir") + "/JAVA/src/resources/userPP/");
            if (!destDir.exists()) destDir.mkdirs();
            File dest = new File(destDir, chosen.getName());
            Files.copy(chosen.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String relPath = "resources/userPP/" + dest.getName();
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE user SET profile_pic=? WHERE user_id=?")) {
                ps.setString(1, relPath);
                ps.setString(2, lecturerId);
                ps.executeUpdate();
            }
            profilePicPath = relPath;
            card.repaint();
            refreshAvatar();
            JOptionPane.showMessageDialog(this, "Profile photo updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildAccountCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 320));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Change Password");
        title.setFont(F_BODY_B);
        title.setForeground(RUSSET);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));
        card.add(title);

        // NOTE: user table has no Username column — only password can be changed
        card.add(fLabel("Current Password"));
        JPasswordField fCur = new JPasswordField();
        fCur.setFont(F_BODY);
        fCur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        card.add(fCur);
        card.add(Box.createVerticalStrut(10));

        card.add(fLabel("New Password"));
        JPasswordField fNew = new JPasswordField();
        fNew.setFont(F_BODY);
        fNew.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        card.add(fNew);
        card.add(Box.createVerticalStrut(10));

        card.add(fLabel("Confirm New Password"));
        JPasswordField fCon = new JPasswordField();
        fCon.setFont(F_BODY);
        fCon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        card.add(fCon);
        card.add(Box.createVerticalStrut(18));

        JLabel status = new JLabel(" ");
        status.setFont(F_SMALL);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(status);
        card.add(Box.createVerticalStrut(8));

        JButton save = btn("Save Changes");
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(save);

        save.addActionListener(e -> {
            String cur = new String(fCur.getPassword());
            String nw = new String(fNew.getPassword());
            String cnf = new String(fCon.getPassword());
            if (cur.isEmpty() || nw.isEmpty()) {
                status.setText("Fill in current and new password.");
                status.setForeground(RED);
                return;
            }
            if (!nw.equals(cnf)) {
                status.setText("New passwords do not match.");
                status.setForeground(RED);
                return;
            }
            if (nw.length() < 6) {
                status.setText("New password must be at least 6 characters.");
                status.setForeground(RED);
                return;
            }
            try (Connection con = DBConnection.getConnection()) {
                // Verify current password — column is 'Password' (case-insensitive in MySQL)
                PreparedStatement ps = con.prepareStatement("SELECT Password FROM user WHERE User_id=?");
                ps.setString(1, lecturerId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next() || !rs.getString("Password").equals(cur)) {
                    status.setText("Current password is incorrect.");
                    status.setForeground(RED);
                    return;
                }
                PreparedStatement ps2 = con.prepareStatement("UPDATE user SET Password=? WHERE User_id=?");
                ps2.setString(1, nw);
                ps2.setString(2, lecturerId);
                ps2.executeUpdate();
                status.setText("\u2713 Password changed successfully.");
                status.setForeground(GREEN);
                fCur.setText("");
                fNew.setText("");
                fCon.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("Error: " + ex.getMessage());
                status.setForeground(RED);
            }
        });
        return card;
    }


    //  HELPERS
    private JPanel page(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(ALMOND);
        p.setBorder(new EmptyBorder(28, 30, 28, 30));
        JLabel h = new JLabel(title);
        h.setFont(F_HEADING);
        h.setForeground(ESPRESSO);
        h.setBorder(new EmptyBorder(0, 0, 8, 0));
        p.add(h, BorderLayout.NORTH);
        return p;
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(F_BODY);
        t.setRowHeight(32);
        t.setBackground(CARD_BG);
        t.setForeground(DARK_TEXT);
        t.setSelectionBackground(SANDSTONE);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(ALMOND);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(RUSSET);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(F_BODY_B);
        t.getTableHeader().setPreferredSize(new Dimension(0, 36));
        return t;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setBackground(RUSSET);
        b.setForeground(Color.WHITE);
        b.setFont(F_BODY_B);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(ESPRESSO);
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(RUSSET);
            }
        });
        return b;
    }

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_SMALL);
        l.setForeground(PUTTY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LecturerDashboard("L00001"));
    }
}
                                    