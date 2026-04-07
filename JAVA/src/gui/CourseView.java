package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CourseView extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JScrollPane scrollPanel;
    private JTable table;
    private String role;

    // Admin - with edit button
    public CourseView(String role) {
        this.role = role;
        init();
    }

    // Others - No edit button for viewers
    public CourseView() {
        this.role = "Viewer";
        init();
    }

    private void init() {
        setContentPane(mainPanel);
        setTitle("View Courses");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        loadCourses();
    }

    void loadCourses() {
        String[] columns;
        if (role.equals("Admin")) {
            columns = new String[]{"Course Code", "Course Name", "Credits", "Action"};
        } else {
            columns = new String[]{"Course Code", "Course Name", "Credits"};
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String query = "SELECT * FROM COURSE";

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (role.equals("Admin")) {
                    Object[] row = {
                            rs.getString("C_code"),
                            rs.getString("C_name"),
                            rs.getString("Credit"),
                            "Edit" //edit btn
                    };
                    model.addRow(row);
                } else {
                    Object[] row = {
                            rs.getString("C_code"),
                            rs.getString("C_name"),
                            rs.getString("Credit")
                    };
                    model.addRow(row);
                }
            }

            table.setModel(model);
            table.getTableHeader().setBackground(Color.decode("#291c0e"));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 35));
            table.setRowHeight(30);

            // only show EDIT button to admin
            if (role.equals("Admin")) {
                // Edit button - designing the button that need to be rendered
                table.getColumn("Action").setCellRenderer((tbl, val, isSel, hasFocus, row, col) -> {
                    JButton btn = new JButton("📝️   Edit");
                    btn.setBackground(Color.decode("#E1D4C2"));
                    btn.setForeground(Color.decode("#291c0e"));
                    return btn;
                });

                // Edit button editor - these values will be passed to CourseEdit constructor
                table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                    private JButton btn = new JButton("📝️   Edit");
                    {
                        btn.addActionListener(e -> {
                            int row = table.getSelectedRow();
                            fireEditingStopped();

                            String cCode  = table.getValueAt(row, 0).toString();
                            String cName  = table.getValueAt(row, 1).toString();
                            String credit = table.getValueAt(row, 2).toString();

                            new CourseEdit(cCode, cName, credit, CourseView.this).setVisible(true);
                            // Parent is passed with the constructor to identify the parent, so it can be refreshed.
                        });
                    }
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        return btn;
                    }
                });
            } // end of IF

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading courses!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } // end of loadCourses()
}
