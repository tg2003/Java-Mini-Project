package gui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;

public class ViewTOGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JLabel title;
    private JScrollPane scrollPanel;
    private JTable table;

    public ViewTOGUI() {
        setContentPane(mainPanel);
        setTitle("View Technical Officers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        loadTechOfficers();
    }

    void loadTechOfficers() {
        String[] columns = {"ID", "Name", "Email", "NIC", "DOB", "No", "Street", "City", "Profile Picture", "Phone", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String query = "SELECT t.*, u.Profile_pic FROM TECH_OFFICER t JOIN USER u ON t.Techoff_id = u.User_id";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("Techoff_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Nic"),
                        rs.getString("Dob"),
                        rs.getString("No"),
                        rs.getString("Street"),
                        rs.getString("City"),
                        rs.getString("Profile_pic"),
                        "View", // view phone numbers
                        "Edit"  //edit profile btn
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
                        String techId = table.getValueAt(row, 0).toString();
                        new ViewPhonesGUI(techId, "TECH_OFFICER_PHONE", "Techoff_id").setVisible(true);
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

            // Edit button editor - these values will be passed to EditTOGUI constructor
            table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JButton btn = new JButton("📝️   Edit");
                {
                    btn.addActionListener(e -> {
                        int row = table.getSelectedRow();
                        fireEditingStopped();

                        String techId = table.getValueAt(row, 0).toString();
                        String name   = table.getValueAt(row, 1).toString();
                        String email  = table.getValueAt(row, 2).toString();
                        String nic    = table.getValueAt(row, 3).toString();
                        String dob    = table.getValueAt(row, 4).toString();
                        String no     = table.getValueAt(row, 5).toString();
                        String street = table.getValueAt(row, 6).toString();
                        String city   = table.getValueAt(row, 7).toString();
                        String pic    = table.getValueAt(row, 8) != null ? table.getValueAt(row, 8).toString() : "";

                        new EditTOGUI(techId, name, email, nic, dob, no, street, city, pic, ViewTOGUI.this).setVisible(true);
                    });
                }
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    return btn;
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}