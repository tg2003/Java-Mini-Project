package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimetableView extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JScrollPane scrollPanel;
    private JTable table;
    private String role;

    // Admin - with edit button
    public TimetableView(String role){
        this.role = role;
        init();
    }

    // Lecturer/Student/T.O. - No edit button for viewers
    public TimetableView() {
        this.role = "Viewer";
        init();
    }

    private void init(){
        setContentPane(mainPanel);
        setTitle("View Timetable");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200,600);
        setLocationRelativeTo(null);
        loadtimetable();
    }

    void loadtimetable(){
        String cols[];
        if (role.equals("Admin")){
            cols = new String[]{"ID", "Day", "Start", "End", "Course Code", "Type", "Action"};
        }
        else{
            cols = new String[]{"ID", "Day", "Start", "End", "Course Code", "Type"};
        }
        DefaultTableModel model = new DefaultTableModel(cols,0);

        try (Connection con = DBConnection.getConnection();){
            String query = "SELECT * FROM timetable ORDER BY FIELD(Day, 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'), Start_time";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                if(role.equals("Admin")){
                    Object[] row={
                            rs.getString("Timetable_id"),
                            rs.getString("Day"),
                            rs.getTime("Start_time"),
                            rs.getTime("End_time"),
                            rs.getString("C_code"),
                            rs.getString("Type"),
                            "Edit"
                    };
                    model.addRow(row);
                }
                else{
                    Object[] row={
                            rs.getString("Timetable_id"),
                            rs.getString("Day"),
                            rs.getTime("Start_time"),
                            rs.getTime("End_time"),
                            rs.getString("C_code"),
                            rs.getString("Type")
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
            if(role.equals("Admin")) {
                table.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    JButton btn = new JButton("📝️   Edit");
                    btn.setBackground(Color.decode("#E1D4C2"));
                    btn.setForeground(Color.decode("#291c0e"));
                    return btn;
                });

                table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()){
                    private JButton btn = new JButton("📝️   Edit");
                    {
                        btn.addActionListener(e -> {
                            int row = table.getSelectedRow();
                            fireEditingStopped();

                            int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                            String day = table.getValueAt(row, 1).toString();
                            String start = table.getValueAt(row, 2).toString();
                            String end = table.getValueAt(row, 3).toString();
                            String code = table.getValueAt(row, 4).toString();
                            String type = table.getValueAt(row, 5).toString();

                            new TimetableEdit(id, day, start, end, code, type, TimetableView.this).setVisible(true);
                            // Parent is passed with the constructor to identify the parent, so it can be refreshed.
                        });
                    }
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        return btn;
                    }
                });

            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading Timetable!", "Error",JOptionPane.ERROR_MESSAGE);
        }
    }


}
