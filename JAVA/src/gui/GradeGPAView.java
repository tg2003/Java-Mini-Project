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


public class GradeGPAView extends JFrame {

    private static final Color BG_DARK      = new Color(25, 18, 12);
    private static final Color BG_CARD      = new Color(92, 64, 51);
    private static final Color BG_CARD2     = new Color(111, 78, 55);
    private static final Color ACCENT       = new Color(205, 133, 63);
    private static final Color TEXT_PRIMARY = new Color(245, 235, 220);
    private static final Color TEXT_MUTED   = new Color(210, 180, 140);
    private static final Color BORDER_CLR   = new Color(139, 94, 60);
    private static final Color ROW_ALT      = new Color(80, 55, 40);
    private static final Color HEADER_BG    = new Color(101, 67, 33);
    private static final Color COL_A        = new Color(160, 82,  45);
    private static final Color COL_B        = new Color(184,134,  11);
    private static final Color COL_C        = new Color(205,133,  63);
    private static final Color COL_D        = new Color(139, 69,  19);
    private static final Color COL_F        = new Color(101, 67,  33);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);


    private static final String[] COLUMNS = {
            "Student ID", "CA (weighted)", "End (weighted)", "Final /100", "Grade", "GPA"
    };

    private JComboBox<String>  cboCourse;
    private JTable             table;
    private DefaultTableModel  tableModel;
    private JLabel             lblStatus;
    private JLabel             lblAvgGPA, lblTopGrade, lblPassCount, lblTotalStudents;
    private String             selectedCourse = null;

    public GradeGPAView(String lecturerId) {
        initUI();
        loadCourses();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Grade & GPA View");
        setSize(900, 620);
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
                new MatteBorder(0,0,1,0, BORDER_CLR),
                new EmptyBorder(16,24,16,24)));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_CARD2);
        JLabel title = new JLabel("Grade & GPA View");
        title.setFont(FONT_TITLE); title.setForeground(TEXT_PRIMARY);
        JLabel sub = new JLabel("");
        sub.setFont(FONT_BODY); sub.setForeground(TEXT_MUTED);
        left.add(title); left.add(Box.createVerticalStrut(3)); left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(BG_CARD2);
        JLabel lbl = new JLabel("Course:"); lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
        cboCourse = new JComboBox<>();
        cboCourse.setFont(FONT_BODY); cboCourse.setBackground(BG_CARD);
        cboCourse.setForeground(TEXT_PRIMARY); cboCourse.setPreferredSize(new Dimension(240,34));
        cboCourse.addActionListener(e -> {
            if (cboCourse.getSelectedItem() != null && cboCourse.getSelectedIndex() > 0) {
                selectedCourse = ((String)cboCourse.getSelectedItem()).split(" - ")[0].trim();
                loadGrades(selectedCourse);
            }
        });
        right.add(lbl); right.add(cboCourse);
        bar.add(left, BorderLayout.WEST); bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16,24,0,24));
        panel.add(buildSummaryCards(), BorderLayout.NORTH);
        panel.add(buildTablePanel(),   BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1,4,12,0));
        row.setBackground(BG_DARK); row.setPreferredSize(new Dimension(0,72));
        lblTotalStudents = addStatCard(row, "Total Students", "—", new Color(99,179,237));
        lblPassCount     = addStatCard(row, "Passed (≥C-)",   "—", COL_A);
        lblAvgGPA        = addStatCard(row, "Average GPA",    "—", ACCENT);
        lblTopGrade      = addStatCard(row, "Highest Grade",  "—", COL_A);
        return row;
    }

    private JLabel addStatCard(JPanel parent, String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true), new EmptyBorder(10,14,10,14)));
        JLabel lbl = new JLabel(label); lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
        JLabel val = new JLabel(value); val.setFont(new Font("Segoe UI",Font.BOLD,20)); val.setForeground(accent);
        card.add(lbl, BorderLayout.NORTH); card.add(val, BorderLayout.CENTER);
        parent.add(card); return val;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                c.setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                c.setForeground(TEXT_PRIMARY);
                if (isRowSelected(row)) c.setBackground(new Color(38,58,90));

                // Grade column (index 4) — colour by grade
                if (col == 4) {
                    String grade = tableModel.getValueAt(row,4).toString();
                    c.setForeground(gradeColor(grade));
                    c.setFont(new Font("Segoe UI",Font.BOLD,13));
                }
                // GPA column (index 5)
                if (col == 5) {
                    try {
                        double gpa = Double.parseDouble(tableModel.getValueAt(row,5).toString());
                        c.setForeground(gpaColor(gpa));
                    } catch (NumberFormatException ignored) {}
                }
                // Student ID column (index 0)
                if (col == 0) c.setForeground(ACCENT);
                return c;
            }
        };


        table.setFont(FONT_BODY); table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD); table.setGridColor(BORDER_CLR);
        table.setRowHeight(32); table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setReorderingAllowed(false);

        // Centre all except Student ID
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < COLUMNS.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(centre);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(new LineBorder(BORDER_CLR,1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(new MatteBorder(1,0,0,0,BORDER_CLR), new EmptyBorder(12,24,12,24)));

        lblStatus = new JLabel("Select a course to load grades");
        lblStatus.setFont(FONT_BODY); lblStatus.setForeground(TEXT_MUTED);

        JButton btnBack   = makeButton("← Back",       TEXT_MUTED, 90);
        JButton btnExport = makeButton("⬇ Export CSV", ACCENT,     130);
        btnBack.addActionListener(e -> dispose());
        btnExport.addActionListener(e -> exportCSV());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));
        right.setBackground(BG_CARD2); right.add(btnBack); right.add(btnExport);
        bar.add(lblStatus, BorderLayout.WEST); bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // load course from db
    private void loadCourses() {
        cboCourse.removeAllItems();
        cboCourse.addItem("-- Select Course --");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT C_code, C_name FROM COURSE ORDER BY C_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cboCourse.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
        } catch (SQLException e) { setStatus("Error: " + e.getMessage(), COL_F); }
    }

    // load grades for select course
    private void loadGrades(String cCode) {
        tableModel.setRowCount(0);
        setStatus("Calculating grades for " + cCode + "...", TEXT_MUTED);

        String sql = "SELECT Ug_id, Quiz01, Quiz02, Quiz03, " +
                "Assignment1, Assignment2, Project, " +
                "Mid_T, Mid_P, Final_T, Final_P, Attempt_type " +
                "FROM MARKS WHERE C_code=? ORDER BY Ug_id";

        double gpaSum = 0; int count = 0, passCount = 0; String topGrade = "F";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //Mark object
                Mark m = new Mark(
                        rs.getString("Ug_id"), cCode,
                        rs.getDouble("Quiz01"),   rs.getDouble("Quiz02"),   rs.getDouble("Quiz03"),
                        rs.getDouble("Assignment1"), rs.getDouble("Assignment2"),
                        rs.getDouble("Project"),
                        rs.getDouble("Mid_T"),    rs.getDouble("Mid_P"),
                        rs.getDouble("Final_T"),  rs.getDouble("Final_P"),
                        rs.getString("Attempt_type")
                );

                // calculate results
                GradeCalculator gc = new GradeCalculator(m);
                double ca    = gc.getCATotal();
                double end   = gc.getEndWeighted();
                double final_ = gc.getFinalMark();
                String grade  = gc.getLetterGrade();
                double gpa    = gc.getGPA();

                Vector<Object> row = new Vector<>();
                row.add(m.getUgId());
                row.add(String.format("%.2f", ca));      // CA weighted (0–40 or 0–30)
                row.add(String.format("%.2f", end));     // End weighted
                row.add(String.format("%.2f", final_));  // Final out of 100
                row.add(grade);
                row.add(String.format("%.1f", gpa));
                tableModel.addRow(row);

                gpaSum += gpa; count++;
                if (!grade.equals("F")) passCount++;
                if (gradeRank(grade) > gradeRank(topGrade)) topGrade = grade;
            }

            final double avgGpa = count > 0 ? gpaSum / count : 0;
            final int total = count, passed = passCount; final String best = topGrade;
            SwingUtilities.invokeLater(() -> {
                lblTotalStudents.setText(String.valueOf(total));
                lblPassCount.setText(String.valueOf(passed));
                lblAvgGPA.setText(String.format("%.2f", avgGpa));
                lblTopGrade.setText(best);
            });
            setStatus(count + " student(s) loaded.", ACCENT);

        } catch (SQLException e) { setStatus("Error: " + e.getMessage(), COL_F); }
    }

    private void exportCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export."); return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("grades_" + (selectedCourse != null ? selectedCourse : "export") + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.println(String.join(",", COLUMNS));
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    StringBuilder sb = new StringBuilder();
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        if (c > 0) sb.append(",");
                        sb.append(tableModel.getValueAt(r,c));
                    }
                    pw.println(sb);
                }
                setStatus("Exported to " + fc.getSelectedFile().getName(), COL_A);
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage()); }
        }
    }

    private Color gradeColor(String g) {
        if (g == null) return TEXT_MUTED;
        return switch (g) {
            case "A+","A","A-" -> COL_A;
            case "B+","B","B-" -> COL_B;
            case "C+","C","C-" -> COL_C;
            case "D"           -> COL_D;
            default            -> COL_F;
        };
    }

    private Color gpaColor(double gpa) {
        if (gpa >= 3.5) return COL_A;
        if (gpa >= 2.5) return COL_B;
        if (gpa >= 1.5) return COL_C;
        return COL_F;
    }

    private int gradeRank(String g) {
        return switch (g) {
            case "A+" -> 11; case "A" -> 10; case "A-" -> 9;
            case "B+" -> 8;  case "B" -> 7;  case "B-" -> 6;
            case "C+" -> 5;  case "C" -> 4;  case "C-" -> 3;
            case "D"  -> 2;  default  -> 1;
        };
    }

    private void setStatus(String text, Color color) { lblStatus.setText(text); lblStatus.setForeground(color); }

    private JButton makeButton(String text, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN); btn.setForeground(fg); btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(fg,1,true), new EmptyBorder(6,14,6,14)));
        btn.setPreferredSize(new Dimension(width,34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(38,50,70)); }
            public void mouseExited (MouseEvent e) { btn.setBackground(BG_CARD); }
        });
        return btn;
    }
}
