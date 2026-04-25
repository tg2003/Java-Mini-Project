package gui;

import db.DBConnection;
import models.GradeCalculator;
import models.Mark;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;


public class CAEligibilityReport extends JFrame {


    private static final Color BG_DARK      = new Color(28,  18,  10);
    private static final Color BG_CARD      = new Color(50,  33,  18);
    private static final Color BG_CARD2     = new Color(38,  24,  12);
    private static final Color TEXT_PRIMARY = new Color(245, 230, 210);
    private static final Color TEXT_MUTED   = new Color(175, 145, 110);
    private static final Color BORDER_CLR   = new Color(80,  55,  30);
    private static final Color ROW_ALT      = new Color(42,  27,  13);


    private static final Color ACCENT       = new Color(200, 155, 220);


    private static final Color COL_ELIGIBLE      = new Color(170, 215, 120);  // warm green ✅
    private static final Color COL_INELIGIBLE    = new Color(220, 100,  80);  // warm red   ❌
    private static final Color COL_BORDERLINE    = new Color(230, 175,  60);  // amber      🟡
    private static final Color COL_ELIGIBLE_BG   = new Color(28,  48,  18);   // dark green row bg
    private static final Color COL_INELIGIBLE_BG = new Color(55,  18,  12);   // dark red row bg
    private static final Color COL_BLUE          = new Color(160, 200, 230);  // info blue

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_BADGE   = new Font("Segoe UI", Font.BOLD,  11);


    private static final String[] COLUMNS = {
            "Student ID",
            "Enroll Status",
            "Quiz 01", "Quiz 02", "Quiz 03",
            "Assign 1", "Assign 2",
            "Project",
            "Mid Theory", "Mid Practical",
            "CA Total /90",
            "CA %",
            "CA Eligible?"
    };


    private final String lecturerId;
    private String selectedCourse = null;

    private JComboBox<String> cboCourse;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblStatus;

    private JLabel lblTotal, lblEligible, lblNotEligible, lblRate;


    public CAEligibilityReport(String lecturerId) {
        this.lecturerId = lecturerId;
        initUI();
        loadCourses();
        setVisible(true);
    }


    private void initUI() {
        setTitle("CA Eligibility Report — Lecturer Module");
        setSize(1180, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }


    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(16, 24, 16, 24)));


        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_CARD2);

        JLabel title = new JLabel("CA Eligibility Report");
        title.setFont(FONT_TITLE);
        title.setForeground(ACCENT);

        JLabel sub = new JLabel(
                "Students need ≥ 45 / 90 CA marks (50%) to sit the final exam.");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        // ── Right: course dropdown ────────────────────────
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(BG_CARD2);

        JLabel lbl = new JLabel("Select Course:");
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_MUTED);

        cboCourse = new JComboBox<>();
        cboCourse.setFont(FONT_BODY);
        cboCourse.setBackground(BG_CARD);
        cboCourse.setForeground(TEXT_PRIMARY);
        cboCourse.setPreferredSize(new Dimension(240, 34));
        cboCourse.addActionListener(e -> {
            if (cboCourse.getSelectedIndex() > 0) {
                String sel = (String) cboCourse.getSelectedItem();
                selectedCourse = sel.split(" - ")[0].trim();
                loadEligibilityData(selectedCourse);
            }
        });

        right.add(lbl);
        right.add(cboCourse);
        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 24, 0, 24));
        panel.add(buildSummaryCards(), BorderLayout.NORTH);
        panel.add(buildTablePanel(),   BorderLayout.CENTER);
        return panel;
    }

    // ── Summary cards row ─────────────────────────────────
    private JPanel buildSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(BG_DARK);
        row.setPreferredSize(new Dimension(0, 78));

        lblTotal       = addStatCard(row, "Total Students",   "—", COL_BLUE);
        lblEligible    = addStatCard(row, "✅  Eligible",      "—", COL_ELIGIBLE);
        lblNotEligible = addStatCard(row, "❌  Not Eligible",  "—", COL_INELIGIBLE);
        lblRate        = addStatCard(row, "Eligibility Rate",  "—", ACCENT);
        return row;
    }

    private JLabel addStatCard(JPanel parent, String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(accent);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        parent.add(card);
        return val;
    }

    // ── Table panel ───────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Custom renderer — colour entire row based on eligibility
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);

                String eligibility = (String) tableModel.getValueAt(row, COLUMNS.length - 1);
                boolean eligible = eligibility != null && eligibility.startsWith("✅");

                // ── Row background ───────────────────────
                if (isRowSelected(row)) {
                    c.setBackground(new Color(80, 52, 22));
                } else {
                    c.setBackground(eligible
                            ? (row % 2 == 0 ? COL_ELIGIBLE_BG : new Color(32, 55, 22))
                            : (row % 2 == 0 ? COL_INELIGIBLE_BG : new Color(62, 22, 15)));
                }
                c.setForeground(TEXT_PRIMARY);

                // ── Student ID column ────────────────────
                if (col == 0) {
                    c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }

                // ── CA Total column: highlight borderline ─
                if (col == 10) {
                    try {
                        double ca = Double.parseDouble(
                                tableModel.getValueAt(row, 10).toString());
                        if (!eligible && ca >= 40) {
                            c.setForeground(COL_BORDERLINE);  // borderline ⚠
                        } else {
                            c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE);
                        }
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } catch (NumberFormatException ignored) {}
                }

                // ── Eligibility badge column ──────────────
                if (col == COLUMNS.length - 1) {
                    c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE);
                    c.setFont(FONT_BADGE);
                }

                return c;
            }
        };

        styleTable(table);

        // Center-align all columns except Student ID and Enroll Status
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 2; i < COLUMNS.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Column widths
        int[] widths = {95, 110, 60, 60, 60, 65, 65, 62, 80, 88, 90, 65, 105};
        for (int i = 0; i < Math.min(widths.length, table.getColumnCount()); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBackground(BG_DARK);
        scroll.setBorder(new LineBorder(BORDER_CLR, 1));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    // ── Legend below table ────────────────────────────────
    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 6));
        legend.setBackground(BG_CARD2);
        legend.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        legend.add(makeLegendItem("■  Eligible  (CA ≥ 45/90)",    COL_ELIGIBLE));
        legend.add(makeLegendItem("■  Not Eligible  (CA < 45/90)", COL_INELIGIBLE));
        legend.add(makeLegendItem("■  Borderline  (40 – 44)",      COL_BORDERLINE));
        return legend;
    }

    private JLabel makeLegendItem(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(color);
        return lbl;
    }

    // ──────────────────────────────────────────────────────
    //  BOTTOM BAR  —  status + Export CSV + Print + Back
    // ──────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(12, 24, 12, 24)));

        lblStatus = new JLabel("Select a course above to generate the eligibility report.");
        lblStatus.setFont(FONT_BODY);
        lblStatus.setForeground(TEXT_MUTED);

        JButton btnBack   = makeButton("← Back",          TEXT_MUTED,   90);
        JButton btnExport = makeButton("⬇  Export CSV",   ACCENT,      140);
        JButton btnPrint  = makeButton("🖨  Print Report", COL_BLUE,    135);

        btnBack.addActionListener(e -> dispose());
        btnExport.addActionListener(e -> exportCSV());
        btnPrint.addActionListener(e -> printReport());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(BG_CARD2);
        right.add(btnBack);
        right.add(btnExport);
        right.add(btnPrint);

        bar.add(lblStatus, BorderLayout.WEST);
        bar.add(right,     BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════
    //  DATABASE — load courses
    // ══════════════════════════════════════════════════════
    private void loadCourses() {
        cboCourse.removeAllItems();
        cboCourse.addItem("-- Select a Course --");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cboCourse.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
            setStatus("Courses loaded. Select one to view eligibility.", TEXT_MUTED);
        } catch (SQLException e) {
            setStatus("Error loading courses: " + e.getMessage(), COL_INELIGIBLE);
        }
    }

    // ══════════════════════════════════════════════════════
    //  DATABASE — load marks + enrollment, check eligibility
    // ══════════════════════════════════════════════════════
    private void loadEligibilityData(String cCode) {
        tableModel.setRowCount(0);
        setStatus("Checking eligibility for " + cCode + "...", TEXT_MUTED);

        /*
         *  LEFT JOIN so students enrolled but with NO marks entered
         *  still appear in the table (all zeros → not eligible).
         *  COALESCE converts NULL marks to 0.
         */
        String sql =
                "SELECT " +
                        "  E.Ug_id, " +
                        "  E.Status                   AS enroll_status, " +
                        "  COALESCE(M.Quiz01,      0) AS Quiz01, " +
                        "  COALESCE(M.Quiz02,      0) AS Quiz02, " +
                        "  COALESCE(M.Quiz03,      0) AS Quiz03, " +
                        "  COALESCE(M.Assignment1, 0) AS Assignment1, " +
                        "  COALESCE(M.Assignment2, 0) AS Assignment2, " +
                        "  COALESCE(M.Project,     0) AS Project, " +
                        "  COALESCE(M.Mid_T,       0) AS Mid_T, " +
                        "  COALESCE(M.Mid_P,       0) AS Mid_P " +
                        "FROM ENROLLS_IN E " +
                        "LEFT JOIN MARKS M " +
                        "  ON E.Ug_id = M.Ug_id AND M.C_code = E.C_code " +
                        "WHERE E.C_code = ? " +
                        "ORDER BY E.Ug_id";

        int total = 0, eligibleCount = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ugId    = rs.getString("Ug_id");
                String enroll  = rs.getString("enroll_status");
                double q1      = rs.getDouble("Quiz01");
                double q2      = rs.getDouble("Quiz02");
                double q3      = rs.getDouble("Quiz03");
                double a1      = rs.getDouble("Assignment1");
                double a2      = rs.getDouble("Assignment2");
                double proj    = rs.getDouble("Project");
                double midT    = rs.getDouble("Mid_T");
                double midP    = rs.getDouble("Mid_P");

                // Build Mark — final exam marks are 0 (not needed for CA check)
                Mark mark = new Mark(ugId, cCode,
                        q1, q2, q3, a1, a2, proj,
                        midT, midP, 0.0, 0.0, "Proper");

                // ── GradeCalculator decides eligibility ──
                GradeCalculator gc = new GradeCalculator(mark);
                double caTotal = gc.getCATotal();
                double caPct   = (caTotal / GradeCalculator.MAX_CA) * 100.0;
                boolean ok     = gc.isCAEligible();

                if (ok) eligibleCount++;
                total++;

                // ── Build table row ───────────────────────
                Vector<Object> row = new Vector<>();
                row.add(ugId);
                row.add(enroll != null ? enroll : "Proper");
                row.add(String.format("%.1f", q1));
                row.add(String.format("%.1f", q2));
                row.add(String.format("%.1f", q3));
                row.add(String.format("%.1f", a1));
                row.add(String.format("%.1f", a2));
                row.add(String.format("%.1f", proj));
                row.add(String.format("%.1f", midT));
                row.add(String.format("%.1f", midP));
                row.add(String.format("%.2f", caTotal));
                row.add(String.format("%.1f%%", caPct));
                row.add(ok ? "✅  Eligible" : "❌  Not Eligible");
                tableModel.addRow(row);
            }

            // ── Update summary cards ─────────────────────
            final int t = total, e = eligibleCount, ne = total - eligibleCount;
            final String pct = total > 0
                    ? String.format("%.0f%%", (eligibleCount * 100.0) / total) : "—";

            SwingUtilities.invokeLater(() -> {
                lblTotal.setText(String.valueOf(t));
                lblEligible.setText(String.valueOf(e));
                lblNotEligible.setText(String.valueOf(ne));
                lblRate.setText(pct);
            });

            Color statusColor = (eligibleCount == total) ? COL_ELIGIBLE : COL_BORDERLINE;
            setStatus(total + " student(s) — " + eligibleCount +
                    " eligible, " + (total - eligibleCount) + " not eligible.", statusColor);

        } catch (SQLException e) {
            setStatus("Database error: " + e.getMessage(), COL_INELIGIBLE);
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════
    //  EXPORT CSV
    // ══════════════════════════════════════════════════════
    private void exportCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No data to export. Select a course first.",
                    "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(
                "CA_Eligibility_" + (selectedCourse != null ? selectedCourse : "report") + ".csv"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.println("Course: " + selectedCourse);
                pw.println(String.join(",", COLUMNS));
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    StringBuilder sb = new StringBuilder();
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        if (c > 0) sb.append(",");
                        sb.append(tableModel.getValueAt(r, c)
                                .toString().replace("✅  ","").replace("❌  ",""));
                    }
                    pw.println(sb);
                }
                setStatus("Exported → " + fc.getSelectedFile().getName(), COL_ELIGIBLE);
                JOptionPane.showMessageDialog(this,
                        "Report exported!\n" + fc.getSelectedFile().getAbsolutePath(),
                        "Done", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                setStatus("Export failed: " + ex.getMessage(), COL_INELIGIBLE);
            }
        }
    }

    // ══════════════════════════════════════════════════════
    //  PRINT
    // ══════════════════════════════════════════════════════
    private void printReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No data to print. Select a course first.",
                    "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // MessageFormat cannot handle em-dash (—) or curly braces in the header text.
            // We use a plain hyphen and build the string safely.
            String header = "CA Eligibility Report - " +
                    (selectedCourse != null ? selectedCourse : "");
            table.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat(header),
                    new java.text.MessageFormat("Page {0}")
            );
            setStatus("Report sent to printer.", COL_ELIGIBLE);
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this, "Print failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    //  STYLE HELPERS
    // ══════════════════════════════════════════════════════
    private void styleTable(JTable t) {
        t.setFont(FONT_TABLE);
        t.setForeground(TEXT_PRIMARY);
        t.setBackground(BG_CARD);
        t.setGridColor(new Color(65, 42, 20));
        t.setRowHeight(34);
        t.setSelectionBackground(new Color(80, 52, 22));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setShowGrid(true);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.getTableHeader().setFont(FONT_HEADER);
        t.getTableHeader().setBackground(new Color(22, 14, 6));
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        t.getTableHeader().setReorderingAllowed(false);
        t.setIntercellSpacing(new Dimension(0, 1));
    }

    private void setStatus(String text, Color color) {
        lblStatus.setText(text);
        lblStatus.setForeground(color);
    }

    private JButton makeButton(String text, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(fg, 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setPreferredSize(new Dimension(width, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(65, 42, 18)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_CARD); }
        });
        return btn;
    }

    // ══════════════════════════════════════════════════════
    //  STANDALONE TEST — remove in final project
    //  In real project: open from LecturerDashboard
    // ══════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CAEligibilityReport("L00001"));
    }
}