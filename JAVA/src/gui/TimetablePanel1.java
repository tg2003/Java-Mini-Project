package gui;

import models.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class TimetablePanel1 extends JPanel {

    private final Undergraduate  student;
    private final TimetableService svc = new TimetableService();
    private StyledTable table;

    public TimetablePanel1(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("Class Timetable"), BorderLayout.NORTH);

        String[] cols = {"Day", "Start", "End", "Course Code", "Course Name", "Type"};
        table = new StyledTable(cols);
        loadData();
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        JLabel note = new JLabel("  Sessions shown for all enrolled courses.");
        note.setFont(UITheme.F_SMALL);
        note.setForeground(UITheme.PUTTY);
        note.setBorder(new EmptyBorder(8, 0, 0, 0));
        add(note, BorderLayout.SOUTH);
    }

    private void loadData() {
        List<Timetable> list = svc.getTimetable(student.getUgId());
        table.clearRows();
        for (Timetable t : list) {
            table.addRow(new Object[]{
                t.getDay(), t.getStartTime(), t.getEndTime(),
                t.getCCode(), t.getCName(), t.getType()
            });
        }
    }
}
