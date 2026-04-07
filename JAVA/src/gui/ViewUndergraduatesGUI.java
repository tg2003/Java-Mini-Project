package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewUndergraduatesGUI extends JFrame{
    private JPanel mainPanel;
    private JTable table;
    private JScrollPane scrollPanel;
    private JLabel title;
    private JPanel titlePanel;

    public ViewUndergraduatesGUI(){
        setContentPane(mainPanel);
        setTitle("View Undergraduates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200,600);
        setLocationRelativeTo(null);
        loadUndergraduates();
    }

    void loadUndergraduates(){
        String[] columns = {"ID", "Name", "Email", "NIC", "DOB", "Department", "No", "Street", "City","Profile Picture", "Phone","Action"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        String query = "SELECT u.Profile_pic,ug.* FROM UNDERGRADUATE ug JOIN user u ON ug.Ug_id = u.User_id";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Object row[]={
                        rs.getString("Ug_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
                        rs.getString("Dpt_name"),
                        rs.getString("No"),
                        rs.getString("Street"),
                        rs.getString("City"),
                        rs.getString("Profile_pic"),
                        "View", // view phone numbers
                        "Edit" //edit profile btn
                };
                model.addRow(row);
            }
            table.setModel(model);

            //show phone numbers btn
            table.getColumn("Phone").setCellRenderer((tbl, val, isSel, hasFocus, row, col) -> {
                JButton btn = new JButton("📞 Contact");
                btn.setBackground(Color.decode("#E1D4C2"));
                btn.setForeground(Color.decode("#291c0e"));
                return btn;
            });

            table.getColumn("Phone").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JButton btn = new JButton("📞 Contact");
                {
                    btn.addActionListener(e -> {
                        int row = table.getSelectedRow();
                        fireEditingStopped();
                        String ugId = table.getValueAt(row, 0).toString();
                        new ViewPhonesGUI(ugId, "UNDERGRADUATE_PHONE", "Ug_id").setVisible(true);
                    });
                }
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    return btn;
                }
            });

            table.getTableHeader().setBackground(Color.decode("#291c0e"));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setPreferredSize(new Dimension(0, 35));


            // Edit button - designing the button that need to be rendered
            table.getColumn("Action").setCellRenderer(new TableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean isSelected, boolean hasFocus, int row, int column) {
                    JButton btn = new JButton("📝️   Edit");
                    btn.setBackground(Color.decode("#E1D4C2"));
                    btn.setForeground(Color.decode("#291c0e"));
                    return btn;
                }
            });

            // Edit button editor - these values will be passed to EditUgGUI consturctor
            table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JButton btn = new JButton("📝️   Edit");

                {
                    btn.addActionListener(e -> {
                        int row = table.getSelectedRow();
                        fireEditingStopped();

                        String ugId   = table.getValueAt(row, 0).toString();
                        String name   = table.getValueAt(row, 1).toString();
                        String email  = table.getValueAt(row, 2).toString();
                        String nic    = table.getValueAt(row, 3).toString();
                        String dob    = table.getValueAt(row, 4).toString();
                        String dpt    = table.getValueAt(row, 5).toString();
                        String no     = table.getValueAt(row, 6).toString();
                        String street = table.getValueAt(row, 7).toString();
                        String city   = table.getValueAt(row, 8).toString();
                        String pic    = table.getValueAt(row, 9) != null ? table.getValueAt(row, 9).toString() : "";


                        new EditUndergraduatesGUI(ugId, name, email, nic, dob, dpt, no, street, city, pic, ViewUndergraduatesGUI.this).setVisible(true);
                        // passed ViewUndergraduatesGUI.this to identify the parent, so it can be refreshed.
                    });
                }

                public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int row, int column) {
                    return btn;
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
