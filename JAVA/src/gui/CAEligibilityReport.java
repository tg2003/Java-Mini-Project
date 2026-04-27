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

    private static final Color BG_DARK          = new Color(28, 18, 10);
    private static final Color BG_CARD          = new Color(50, 33, 18);
    private static final Color BG_CARD2         = new Color(38, 24, 12);
    private static final Color TEXT_PRIMARY     = new Color(245,230,210);
    private static final Color TEXT_MUTED       = new Color(175,145,110);
    private static final Color BORDER_CLR       = new Color(80, 55, 30);
    private static final Color ROW_ALT          = new Color(42, 27, 13);
    private static final Color ACCENT           = new Color(200,155,220);
    private static final Color COL_ELIGIBLE     = new Color(170,215,120);
    private static final Color COL_INELIGIBLE   = new Color(220,100, 80);
    private static final Color COL_BORDERLINE   = new Color(230,175, 60);
    private static final Color COL_ELIGIBLE_BG  = new Color(28, 48, 18);
    private static final Color COL_INELIG_BG    = new Color(55, 18, 12);
    private static final Color COL_BLUE         = new Color(160,200,230);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,  11);


    private static final String[] COLUMNS = {
            "Student ID", "Enroll Status", "CA (weighted)", "CA %", "CA Eligible?"
    };

    private final String lecturerId;
    private String selectedCourse = null;

    private JComboBox<String> cboCourse;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblStatus;
    private JLabel            lblTotal, lblEligible, lblNotEligible, lblRate;

    public CAEligibilityReport(String lecturerId) {
        this.lecturerId = lecturerId;
        initUI(); loadCourses(); setVisible(true);
    }

    private void initUI() {
        setTitle("CA Eligibility Report — Lecturer Module");
        setSize(780, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK); setContentPane(root);
        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER_CLR), new EmptyBorder(16,24,16,24)));

        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_CARD2);
        JLabel title = new JLabel("CA Eligibility Report"); title.setFont(FONT_TITLE); title.setForeground(ACCENT);
        JLabel sub = new JLabel("");
        sub.setFont(FONT_BODY); sub.setForeground(TEXT_MUTED);
        left.add(title); left.add(Box.createVerticalStrut(4)); left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0)); right.setBackground(BG_CARD2);
        JLabel lbl = new JLabel("Select Course:"); lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
        cboCourse = new JComboBox<>(); cboCourse.setFont(FONT_BODY);
        cboCourse.setBackground(BG_CARD); cboCourse.setForeground(TEXT_PRIMARY);
        cboCourse.setPreferredSize(new Dimension(260,34));
        cboCourse.addActionListener(e -> {
            if (cboCourse.getSelectedIndex() > 0) {
                selectedCourse = ((String)cboCourse.getSelectedItem()).split(" - ")[0].trim();
                loadEligibilityData(selectedCourse);
            }
        });
        right.add(lbl); right.add(cboCourse);
        bar.add(left, BorderLayout.WEST); bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setBackground(BG_DARK); panel.setBorder(new EmptyBorder(16,24,0,24));
        panel.add(buildSummaryCards(), BorderLayout.NORTH);
        panel.add(buildTablePanel(),   BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1,4,12,0));
        row.setBackground(BG_DARK); row.setPreferredSize(new Dimension(0,78));
        lblTotal       = addStatCard(row, "Total Students",  "—", COL_BLUE);
        lblEligible    = addStatCard(row, "✅  Eligible",     "—", COL_ELIGIBLE);
        lblNotEligible = addStatCard(row, "❌  Not Eligible", "—", COL_INELIGIBLE);
        lblRate        = addStatCard(row, "Eligibility Rate", "—", ACCENT);
        return row;
    }

    private JLabel addStatCard(JPanel parent, String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true), new EmptyBorder(12,16,12,16)));
        JLabel lbl = new JLabel(label); lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
        JLabel val = new JLabel(value); val.setFont(new Font("Segoe UI",Font.BOLD,22)); val.setForeground(accent);
        card.add(lbl, BorderLayout.NORTH); card.add(val, BorderLayout.CENTER);
        parent.add(card); return val;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(BG_DARK);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                String eligibility = (String) tableModel.getValueAt(row, COLUMNS.length-1);
                boolean eligible = eligibility != null && eligibility.startsWith("✅");

                if (isRowSelected(row)) c.setBackground(new Color(80,52,22));
                else c.setBackground(eligible
                        ? (row%2==0 ? COL_ELIGIBLE_BG : new Color(32,55,22))
                        : (row%2==0 ? COL_INELIG_BG   : new Color(62,22,15)));
                c.setForeground(TEXT_PRIMARY);

                // Student ID
                if (col == 0) { c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE); c.setFont(new Font("Segoe UI",Font.BOLD,12)); }

                // CA (weighted) column — highlight borderline
                if (col == 2) {
                    try {
                        double ca = Double.parseDouble(tableModel.getValueAt(row,2).toString());
                        double caMax = Double.parseDouble(tableModel.getValueAt(row,3).toString().replace("%",""));
                        if (!eligible && caMax >= 30) c.setForeground(COL_BORDERLINE);
                        else c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE);
                        c.setFont(new Font("Segoe UI",Font.BOLD,12));
                    } catch (NumberFormatException ignored) {}
                }

                // Eligibility badge column
                if (col == COLUMNS.length-1) { c.setForeground(eligible ? COL_ELIGIBLE : COL_INELIGIBLE); c.setFont(FONT_BADGE); }
                return c;
            }
        };

        // Style
        table.setFont(FONT_BODY); table.setForeground(TEXT_PRIMARY); table.setBackground(BG_CARD);
        table.setGridColor(new Color(65,42,20)); table.setRowHeight(34);
        table.setSelectionBackground(new Color(80,52,22)); table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowGrid(true); table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(new Color(22,14,6));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setReorderingAllowed(false);

        // Centre all except Student ID and Enroll Status
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 2; i < COLUMNS.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(centre);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK); scroll.setBorder(new LineBorder(BORDER_CLR,1));
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,22,6));
        legend.setBackground(BG_CARD2); legend.setBorder(new MatteBorder(1,0,0,0,BORDER_CLR));
        legend.add(makeLegend("■  Eligible  (CA ≥ 40% of CA portion)",    COL_ELIGIBLE));
        legend.add(makeLegend("■  Not Eligible  (CA < 40%)",              COL_INELIGIBLE));
        legend.add(makeLegend("■  Borderline  (CA% 30–39%)",              COL_BORDERLINE));
        return legend;
    }

    private JLabel makeLegend(String text, Color color) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI",Font.PLAIN,11)); l.setForeground(color); return l;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout()); bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,BORDER_CLR), new EmptyBorder(12,24,12,24)));
        lblStatus = new JLabel("Select a course to generate the eligibility report.");
        lblStatus.setFont(FONT_BODY); lblStatus.setForeground(TEXT_MUTED);

        JButton btnBack   = makeButton("← Back",         TEXT_MUTED, 90);
        JButton btnExport = makeButton("⬇ Export CSV",   ACCENT,     140);
        JButton btnPrint  = makeButton("🖨 Print Report", COL_BLUE,  135);
        btnBack.addActionListener(e -> dispose());
        btnExport.addActionListener(e -> exportCSV());
        btnPrint.addActionListener(e -> printReport());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0)); right.setBackground(BG_CARD2);
        right.add(btnBack); right.add(btnExport); right.add(btnPrint);
        bar.add(lblStatus, BorderLayout.WEST); bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Load courses ──────────────────────────────────────────────────────
    private void loadCourses() {
        cboCourse.removeAllItems(); cboCourse.addItem("-- Select a Course --");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cboCourse.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
            setStatus("Courses loaded. Select one to view eligibility.", TEXT_MUTED);
        } catch (SQLException e) { setStatus("Error: " + e.getMessage(), COL_INELIGIBLE); }
    }

    // ── Load marks, apply course-specific CA formula, check eligibility ───
    private void loadEligibilityData(String cCode) {
        tableModel.setRowCount(0);
        setStatus("Checking eligibility for " + cCode + "...", TEXT_MUTED);

        // LEFT JOIN so students with no marks still appear
        String sql =
                "SELECT E.Ug_id, E.Status AS enroll_status, " +
                        "COALESCE(M.Quiz01,0) AS Quiz01, COALESCE(M.Quiz02,0) AS Quiz02, COALESCE(M.Quiz03,0) AS Quiz03, " +
                        "COALESCE(M.Assignment1,0) AS Assignment1, COALESCE(M.Assignment2,0) AS Assignment2, " +
                        "COALESCE(M.Project,0) AS Project, " +
                        "COALESCE(M.Mid_T,0) AS Mid_T, COALESCE(M.Mid_P,0) AS Mid_P " +
                        "FROM ENROLLS_IN E " +
                        "LEFT JOIN MARKS M ON E.Ug_id=M.Ug_id AND M.C_code=E.C_code " +
                        "WHERE E.C_code=? ORDER BY E.Ug_id";

        int total = 0, eligibleCount = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ugId   = rs.getString("Ug_id");
                String enroll = rs.getString("enroll_status");


                Mark mark = new Mark(ugId, cCode,
                        rs.getDouble("Quiz01"),   rs.getDouble("Quiz02"),   rs.getDouble("Quiz03"),
                        rs.getDouble("Assignment1"), rs.getDouble("Assignment2"),
                        rs.getDouble("Project"),
                        rs.getDouble("Mid_T"),    rs.getDouble("Mid_P"),
                        0.0, 0.0, "Proper");

                // Course-specific CA calculation
                GradeCalculator gc  = new GradeCalculator(mark);
                double caTotal      = gc.getCATotal();        // 0–40 or 0–30 depending on course
                double caPct        = gc.getCAPercentage();   // 0–100%
                boolean ok          = gc.isCAEligible();      // CA >= 40% of CA portion

                if (ok) eligibleCount++;
                total++;

                Vector<Object> row = new Vector<>();
                row.add(ugId);
                row.add(enroll != null ? enroll : "Proper");
                row.add(String.format("%.2f", caTotal));   // scaled CA (0–30 or 0–40)
                row.add(String.format("%.1f%%", caPct));   // CA as % of CA portion
                row.add(ok ? "✅  Eligible" : "❌  Not Eligible");
                tableModel.addRow(row);
            }

            final int t = total, e = eligibleCount, ne = total - eligibleCount;
            final String pct = total > 0 ? String.format("%.0f%%", (eligibleCount*100.0)/total) : "—";
            SwingUtilities.invokeLater(() -> {
                lblTotal.setText(String.valueOf(t));
                lblEligible.setText(String.valueOf(e));
                lblNotEligible.setText(String.valueOf(ne));
                lblRate.setText(pct);
            });

            Color sc = eligibleCount == total ? COL_ELIGIBLE : COL_BORDERLINE;
            setStatus(total + " student(s) — " + eligibleCount + " eligible, " + (total-eligibleCount) + " not eligible.", sc);

        } catch (SQLException e) {
            setStatus("Database error: " + e.getMessage(), COL_INELIGIBLE); e.printStackTrace();
        }
    }

    private void exportCSV() {
        if (tableModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this,"No data to export.","Empty",JOptionPane.WARNING_MESSAGE); return; }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("CA_Eligibility_" + (selectedCourse!=null?selectedCourse:"report") + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.println("Course: " + selectedCourse);
                pw.println(String.join(",", COLUMNS));
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    StringBuilder sb = new StringBuilder();
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        if (c>0) sb.append(",");
                        sb.append(tableModel.getValueAt(r,c).toString().replace("✅  ","").replace("❌  ",""));
                    }
                    pw.println(sb);
                }
                setStatus("Exported → " + fc.getSelectedFile().getName(), COL_ELIGIBLE);
            } catch (Exception ex) { setStatus("Export failed: " + ex.getMessage(), COL_INELIGIBLE); }
        }
    }

    private void printReport() {
        if (tableModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this,"No data to print.","Empty",JOptionPane.WARNING_MESSAGE); return; }
        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("CA Eligibility - " + (selectedCourse!=null?selectedCourse:"")),
                    new java.text.MessageFormat("Page {0}"));
        } catch (java.awt.print.PrinterException e) { JOptionPane.showMessageDialog(this,"Print failed: "+e.getMessage()); }
    }

    private void setStatus(String text, Color color) { lblStatus.setText(text); lblStatus.setForeground(color); }

    private JButton makeButton(String text, Color fg, int width) {
        JButton btn = new JButton(text); btn.setFont(FONT_BTN); btn.setForeground(fg); btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(fg,1,true), new EmptyBorder(6,14,6,14)));
        btn.setPreferredSize(new Dimension(width,34)); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(65,42,18)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_CARD); }
        });
        return btn;
    }
}
