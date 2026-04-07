package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class NoticeView extends JFrame{
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JLabel title;
    private JScrollPane scrollPanel;
    private JTable table;
    private String role;

    // Admin - with edit button
    public NoticeView(String role) {
        this.role = role;
        init();
    }

    // Lecturer/Student - No edit button for viewers
    public NoticeView() {
        this.role = "Viewer";
        init();
    }

    private void init() {
        setContentPane(mainPanel);
        setTitle("View Notices");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        loadNotices();
    }

    void loadNotices(){
        String[] columns;
        if (role.equals("Admin")) {
            //added hidden col 'path' to get the actual path
            columns = new String[]{"No", "Title", "Date & Time","path", "Download Link", "Action"};
        } else {
            columns = new String[]{"No", "Title", "Date & Time","path", "Download Link"};
        }
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String query = "SELECT * FROM NOTICE";

        try (Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (role.equals("Admin")) {
                    Object[] row = {
                            rs.getString("Notice_no"),
                            rs.getString("Title"),
                            rs.getString("Date_time"),
                            rs.getString("Download_link"),
                            "Download",
                            "Edit" //edit btn
                    };
                    model.addRow(row);
                }
                else {
                    Object[] row = {
                            rs.getString("Notice_no"),
                            rs.getString("Title"),
                            rs.getString("Date_time"),
                            rs.getString("Download_link"),
                            "Download"
                    };
                    model.addRow(row);
                }
            }
            table.setModel(model);
            //Hide col 'path' , then path will no longer shown in the table
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setWidth(0);

            // Download button renderer to the Download_link col
            table.getColumn("Download Link").setCellRenderer((tbl, val, isSel, hasFocus, row, col) -> {
                JButton btn = new JButton("📥 Download");
                btn.setBackground(Color.decode("#E1D4C2"));
                btn.setForeground(Color.decode("#291c0e"));
                return btn;
            });

            // Download button editor
            table.getColumn("Download Link").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JButton btn = new JButton("📥 Download");
                {
                    btn.addActionListener(e -> {
                        int row = table.getSelectedRow();
                        fireEditingStopped();
                        String path = table.getValueAt(row, 3).toString();
                        System.out.println(path);

                        if (path == null || path.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No file available!", "Warning", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // file open
                        try {
                            File file = new File(System.getProperty("user.dir") + "/JAVA/src/" + path);
                            if (file.exists()) {
                                Desktop.getDesktop().open(file);
                            } else {
                                JOptionPane.showMessageDialog(null, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    return btn;
                }
            });

            table.getTableHeader().setBackground(Color.decode("#291c0e"));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setPreferredSize(new Dimension(0, 35));
            table.setRowHeight(30);

            // only show EDIT button to admin
            if (role.equals("Admin")) {
                table.getColumn("Action").setCellRenderer((tbl, val, isSel, hasFocus, row, col) -> {
                    JButton btn = new JButton("📝️   Edit");
                    btn.setBackground(Color.decode("#E1D4C2"));
                    btn.setForeground(Color.decode("#291c0e"));
                    return btn;
                });

            // Edit button editor - these values will be passed to NoticeEdit constructor
            table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JButton btn = new JButton("📝️   Edit");
                {
                    btn.addActionListener(e -> {
                        int row = table.getSelectedRow();
                        fireEditingStopped();

                        String noticeNo     = table.getValueAt(row, 0).toString();
                        String title        = table.getValueAt(row, 1).toString();
                        String dateTime     = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
                        String downloadLink = table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "";

                        new NoticeEdit(noticeNo, title, dateTime, downloadLink, NoticeView.this).setVisible(true);
                        // Parent is passed with the constructor to identify the parent, so it can be refreshed.
                        });
                    }
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        return btn;
                    }
                });
            }//end of IF
        }
        catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading notices!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        }//end of loadnotices()
}
