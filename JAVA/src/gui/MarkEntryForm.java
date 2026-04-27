package gui;

import db.DBConnection;
import models.Mark;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;


public class MarkEntryForm extends JFrame {


    private static final Color BG_DARK      = new Color(245,  210,  195);
    private static final Color BG_CARD      = new Color(92, 64, 51);
    private static final Color BG_CARD2     = new Color(129, 68, 44);
    private static final Color BG_INPUT     = new Color(159, 113, 87);
    private static final Color ACCENT       = new Color(205, 133, 63);   // green for marks
    private static final Color ACCENT_HOVER = new Color(160, 82,  45);
    private static final Color TEXT_PRIMARY = new Color(245, 235, 220);
    private static final Color TEXT_MUTED   = new Color(200, 180, 160);
    private static final Color BORDER_CLR   = new Color(120, 80, 60);
    private static final Color ROW_ALT      = new Color(45, 30, 25);
    private static final Color HEADER_BG    = new Color(110, 70, 50);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD,  11);


    private static final String[] COLUMNS = {
            "Student ID", "Quiz 01", "Quiz 02", "Quiz 03",
            "Assign 1", "Assign 2", "Project",
            "Mid Theory", "Mid Practical", "Final Theory", "Final Practical",
            "Attempt Type"
    };


    private static final String[] ATTEMPT_TYPES = {"Proper", "Repeat", "MedicalRepeat"};


    private JComboBox<String> cboCourse;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;
    private JButton btnSave;
    private JButton btnRefresh;


    private String lecturerId;
    private String selectedCourse = null;


    public MarkEntryForm(String lecturerId) {
        this.lecturerId = lecturerId;
        initUI();
        loadCourses();
        setVisible(true);
    }


    private void initUI() {
        setTitle("Mark Entry Form");
        setSize(1200, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);


        root.add(buildTopBar(), BorderLayout.NORTH);


        root.add(buildTablePanel(), BorderLayout.CENTER);


        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }


    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(16, 24, 16, 24)
        ));


        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_CARD2);

        JLabel title = new JLabel("Mark Entry Form");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel sub = new JLabel("");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(sub);


        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(BG_CARD2);

        JLabel lbl = new JLabel("Course:");
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_MUTED);

        cboCourse = new JComboBox<>();
        cboCourse.setFont(FONT_BODY);
        cboCourse.setBackground(BG_CARD);
        cboCourse.setForeground(TEXT_PRIMARY);
        cboCourse.setPreferredSize(new Dimension(200, 34));
        cboCourse.addActionListener(e -> {
            if (cboCourse.getSelectedItem() != null) {
                String selected = (String) cboCourse.getSelectedItem();
                selectedCourse = selected.split(" - ")[0].trim();
                loadStudentsForCourse(selectedCourse);
            }
        });

        btnRefresh = makeButton("⟳ Refresh", new Color(99, 179, 237), 100);
        btnRefresh.addActionListener(e -> {
            if (selectedCourse != null) loadStudentsForCourse(selectedCourse);
        });

        right.add(lbl);
        right.add(cboCourse);
        right.add(btnRefresh);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 24, 0, 24));

        // Table model — col 0 (Student ID) not editable, rest editable
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col != 0;   // Student ID column is locked
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return String.class;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(190, 190, 180));
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                    c.setForeground(col == 0 ? ACCENT : TEXT_PRIMARY);
                }
                return c;
            }
        };

        styleTable(table);

        // Attempt type
        JComboBox<String> attemptCombo = new JComboBox<>(ATTEMPT_TYPES);
        attemptCombo.setFont(FONT_BODY);
        attemptCombo.setBackground(BG_INPUT);
        attemptCombo.setForeground(TEXT_PRIMARY);
        table.getColumnModel().getColumn(11).setCellEditor(new DefaultCellEditor(attemptCombo));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(new LineBorder(BORDER_CLR, 1));
        scroll.getVerticalScrollBar().setBackground(BG_CARD);
        scroll.getHorizontalScrollBar().setBackground(BG_CARD);

        // Empty state label
        JLabel empty = new JLabel("Select a course above to load students");
        empty.setFont(FONT_BODY);
        empty.setForeground(TEXT_MUTED);
        empty.setHorizontalAlignment(SwingConstants.CENTER);
        //table.setEmptyComponent(empty);   // custom method below

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // Bottom bar: status + save button
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD2);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        lblStatus = new JLabel("Ready");
        lblStatus.setFont(FONT_BODY);
        lblStatus.setForeground(TEXT_MUTED);

        btnSave = makeButton("💾  Save All Marks", ACCENT, 160);
        btnSave.addActionListener(e -> saveAllMarks());

        JButton btnBack = makeButton("← Back", TEXT_MUTED, 80);
        btnBack.addActionListener(e -> dispose());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(BG_CARD2);
        right.add(btnBack);
        right.add(btnSave);

        bar.add(lblStatus, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    private void loadCourses() {
        cboCourse.removeAllItems();
        cboCourse.addItem("-- Select Course --");

        String sql = "SELECT C_code, C_name FROM COURSE ORDER BY C_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cboCourse.addItem(rs.getString("C_code") + " - " + rs.getString("C_name"));
            }
            setStatus("Courses loaded. Select one to begin.", TEXT_MUTED);

        } catch (SQLException e) {
            setStatus("Error loading courses: " + e.getMessage(), new Color(248, 113, 113));
            e.printStackTrace();
        }
    }


    private void loadStudentsForCourse(String cCode) {
        tableModel.setRowCount(0);
        setStatus("Loading students for " + cCode + "...", TEXT_MUTED);


        String sql = "SELECT E.Ug_id, " +
                "  M.Quiz01, M.Quiz02, M.Quiz03, " +
                "  M.Assignment1, M.Assignment2, M.Project, " +
                "  M.Mid_T, M.Mid_P, M.Final_T, M.Final_P, M.Attempt_type " +
                "FROM ENROLLS_IN E " +
                "LEFT JOIN MARKS M ON E.Ug_id = M.Ug_id AND M.C_code = E.C_code " +
                "WHERE E.C_code = ? " +
                "ORDER BY E.Ug_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cCode);
            ResultSet rs = ps.executeQuery();
            int count = 0;

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("Ug_id"));
                row.add(nullSafe(rs, "Quiz01"));
                row.add(nullSafe(rs, "Quiz02"));
                row.add(nullSafe(rs, "Quiz03"));
                row.add(nullSafe(rs, "Assignment1"));
                row.add(nullSafe(rs, "Assignment2"));
                row.add(nullSafe(rs, "Project"));
                row.add(nullSafe(rs, "Mid_T"));
                row.add(nullSafe(rs, "Mid_P"));
                row.add(nullSafe(rs, "Final_T"));
                row.add(nullSafe(rs, "Final_P"));
                String attempt = rs.getString("Attempt_type");
                row.add(attempt != null ? attempt : "Proper");
                tableModel.addRow(row);
                count++;
            }

            setStatus(count + " student(s) loaded for " + cCode, ACCENT);

        } catch (SQLException e) {
            setStatus("Error: " + e.getMessage(), new Color(248, 113, 113));
            e.printStackTrace();
        }
    }

    // Save all marks in the table to db
    private void saveAllMarks() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        int rows = tableModel.getRowCount();
        if (rows == 0) {
            setStatus("No students to save.", TEXT_MUTED);
            return;
        }

        int saved = 0, errors = 0;
        setStatus("Saving...", TEXT_MUTED);

        for (int i = 0; i < rows; i++) {
            String ugId = (String) tableModel.getValueAt(i, 0);
            try {
                double q1  = parseDouble(tableModel.getValueAt(i, 1));
                double q2  = parseDouble(tableModel.getValueAt(i, 2));
                double q3  = parseDouble(tableModel.getValueAt(i, 3));
                double a1  = parseDouble(tableModel.getValueAt(i, 4));
                double a2  = parseDouble(tableModel.getValueAt(i, 5));
                double prj = parseDouble(tableModel.getValueAt(i, 6));
                double mt  = parseDouble(tableModel.getValueAt(i, 7));
                double mp  = parseDouble(tableModel.getValueAt(i, 8));
                double ft  = parseDouble(tableModel.getValueAt(i, 9));
                double fp  = parseDouble(tableModel.getValueAt(i, 10));
                String attempt = (String) tableModel.getValueAt(i, 11);
                if (attempt == null || attempt.isBlank()) attempt = "Proper";

                upsertMark(ugId, selectedCourse, q1, q2, q3, a1, a2, prj, mt, mp, ft, fp, attempt);
                saved++;

            } catch (NumberFormatException ex) {
                errors++;
                setStatus("Row " + (i + 1) + ": invalid number for student " + ugId, new Color(248, 113, 113));
            } catch (SQLException ex) {
                errors++;
                ex.printStackTrace();
            }
        }

        if (errors == 0) {
            setStatus("✓  All " + saved + " records saved successfully!", ACCENT);
            JOptionPane.showMessageDialog(this,
                    saved + " student marks saved successfully!",
                    "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            setStatus(saved + " saved, " + errors + " errors — check values.", new Color(251, 191, 36));
        }
    }


    private void upsertMark(String ugId, String cCode,
                            double q1, double q2, double q3,
                            double a1, double a2, double prj,
                            double mt, double mp, double ft, double fp,
                            String attempt) throws SQLException {


        String checkSql = "SELECT Mark_id FROM MARKS WHERE Ug_id = ? AND C_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setString(1, ugId);
            check.setString(2, cCode);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // UPDATE
                String upd = "UPDATE MARKS SET " +
                        "Quiz01=?, Quiz02=?, Quiz03=?, " +
                        "Assignment1=?, Assignment2=?, Project=?, " +
                        "Mid_T=?, Mid_P=?, Final_T=?, Final_P=?, Attempt_type=? " +
                        "WHERE Ug_id=? AND C_code=?";
                try (PreparedStatement ps = con.prepareStatement(upd)) {
                    ps.setDouble(1, q1); ps.setDouble(2, q2); ps.setDouble(3, q3);
                    ps.setDouble(4, a1); ps.setDouble(5, a2); ps.setDouble(6, prj);
                    ps.setDouble(7, mt); ps.setDouble(8, mp);
                    ps.setDouble(9, ft); ps.setDouble(10, fp);
                    ps.setString(11, attempt);
                    ps.setString(12, ugId); ps.setString(13, cCode);
                    ps.executeUpdate();
                }
            } else {
                // INSERT
                String ins = "INSERT INTO MARKS " +
                        "(Ug_id, C_code, Quiz01, Quiz02, Quiz03, " +
                        " Assignment1, Assignment2, Project, " +
                        " Mid_T, Mid_P, Final_T, Final_P, Attempt_type) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(ins)) {
                    ps.setString(1, ugId); ps.setString(2, cCode);
                    ps.setDouble(3, q1); ps.setDouble(4, q2); ps.setDouble(5, q3);
                    ps.setDouble(6, a1); ps.setDouble(7, a2); ps.setDouble(8, prj);
                    ps.setDouble(9, mt); ps.setDouble(10, mp);
                    ps.setDouble(11, ft); ps.setDouble(12, fp);
                    ps.setString(13, attempt);
                    ps.executeUpdate();
                }
            }
        }
    }


    private void styleTable(JTable t) {
        t.setFont(FONT_TABLE);
        t.setForeground(TEXT_PRIMARY);
        t.setBackground(BG_CARD);
        t.setGridColor(BORDER_CLR);
        t.setRowHeight(32);
        t.setSelectionBackground(new Color(38, 58, 90));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setShowGrid(true);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.getTableHeader().setFont(FONT_HEADER);
        t.getTableHeader().setBackground(HEADER_BG);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        t.getTableHeader().setReorderingAllowed(false);


        int[] widths = {95, 65, 65, 65, 70, 70, 68, 80, 90, 85, 90, 105};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultCellEditor sce = new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                JTextField tf = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                tf.setBackground(new Color(38, 58, 90));
                tf.setForeground(TEXT_PRIMARY);
                tf.setCaretColor(ACCENT);
                tf.setFont(FONT_TABLE);
                tf.setBorder(new LineBorder(ACCENT, 1));
                tf.selectAll();
                return tf;
            }
        };
        for (int i = 1; i <= 10; i++) {
            table.getColumnModel().getColumn(i).setCellEditor(sce);
        }
    }

    // Helpers
    private String nullSafe(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? "" : String.valueOf(v);
    }

    private double parseDouble(Object val) {
        if (val == null || val.toString().isBlank()) return 0.0;
        return Double.parseDouble(val.toString().trim());
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
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setPreferredSize(new Dimension(width, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(38, 50, 70)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_CARD); }
        });
        return btn;
    }


    private void setEmptyMessage(JTable t, JLabel empty) {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarkEntryForm("L00001"));
    }
}