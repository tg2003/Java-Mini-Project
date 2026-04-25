package gui;

import models.*;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class MarksPanel extends JPanel {

    private final Undergraduate student;
    private final MarksService  svc = new MarksService();
    private StyledTable table;

    public MarksPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("Academic Marks"), BorderLayout.NORTH);

        String[] cols = {"Course Code", "Course Name",
                         "Q1", "Q2", "Q3",
                         "Asn 1", "Asn 2", "Project",
                         "Mid-T", "Mid-P", "Final-T", "Final-P",
                         "Total", "Grade", "Type"};
        table = new StyledTable(cols);
        loadData();
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // Bottom note
        JLabel note = new JLabel("  \u2014 = Not applicable for this course");
        note.setFont(UITheme.F_SMALL);
        note.setForeground(UITheme.PUTTY);
        note.setBorder(new EmptyBorder(8, 0, 0, 0));
        add(note, BorderLayout.SOUTH);
    }

    private void loadData() {
        List<Marks> list = svc.getMarks(student.getUgId());
        table.clearRows();
        for (Marks m : list) {
            double total = m.getTotal();
            table.addRow(new Object[]{
                m.getCCode(), m.getCName(),
                m.fmt(m.getQuiz01()), m.fmt(m.getQuiz02()), m.fmt(m.getQuiz03()),
                m.fmt(m.getAssignment1()), m.fmt(m.getAssignment2()),
                m.fmt(m.getProject()),
                m.fmt(m.getMidT()), m.fmt(m.getMidP()),
                m.fmt(m.getFinalT()), m.fmt(m.getFinalP()),
                String.format("%.1f", total),
                GPACalculator.letterGrade(total),
                m.getAttemptType()
            });
        }
    }
}
