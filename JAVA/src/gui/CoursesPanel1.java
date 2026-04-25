package gui;

import models.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CoursesPanel1 extends JPanel {

    private final Undergraduate student;
    private final CourseService svc = new CourseService();
    private StyledTable table;

    public CoursesPanel1(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("Enrolled Courses"), BorderLayout.NORTH);

        String[] cols = {"Code", "Course Name", "Credits", "Enrolment", "Sem", "Batch Year"};
        table = new StyledTable(cols);
        loadData();

        JScrollPane sp = UITheme.scrollPane(table);

        // Info bar
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        JLabel info = new JLabel("Click a course to view materials.");
        info.setFont(UITheme.F_SMALL);
        info.setForeground(UITheme.PUTTY);
        bottom.add(info, BorderLayout.WEST);

        // Materials popup on click
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    String code = table.getValueAt(row, 0).toString();
                    String name = table.getValueAt(row, 1).toString();
                    showMaterials(code, name);
                }
            }
        });

        add(sp,     BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        List<Course> courses = svc.getCourses(student.getUgId());
        table.clearRows();
        for (Course c : courses) {
            table.addRow(new Object[]{
                c.getCode(), c.getName(), c.getCredit(),
                c.getEnrollStatus(), c.getSem(), c.getBatchYear()
            });
        }
    }

    private void showMaterials(String code, String name) {
        List<String> mats = svc.getMaterials(code);
        if (mats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No materials uploaded for " + code + ".",
                code + " – Materials", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] matArr = mats.stream()
            .map(m -> "  \uD83D\uDCC4  " + m)
            .toArray(String[]::new);
        JList<String> jlist = new JList<>(matArr);
        jlist.setFont(UITheme.F_BODY);
        jlist.setBackground(UITheme.CARD_BG);
        JScrollPane sp = new JScrollPane(jlist);
        sp.setPreferredSize(new Dimension(360, 200));
        JOptionPane.showMessageDialog(this, sp,
            code + " – " + name + " Materials", JOptionPane.PLAIN_MESSAGE);
    }
}
