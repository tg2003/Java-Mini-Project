package gui;

import models.AttendanceCourseSummary;
import models.Marks;
import models.Undergraduate;
import service.AttendanceService;
import service.MarksService;
import util.GPACalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarksPanel extends JPanel {

    private final Undergraduate student;
    private final MarksService svc = new MarksService();
    private final AttendanceService attendanceSvc = new AttendanceService();

    private JComboBox<String> courseSelector;
    private StyledTable table;

    private JLabel gradeValue;
    private JLabel gpvValue;
    private JLabel attendanceValue;
    private JLabel caValue;
    private JLabel endValue;
    private JLabel totalValue;

    private JLabel attendanceEligibilityValue;
    private JLabel caEligibilityValue;
    private JLabel endEligibilityValue;
    private JLabel finalEligibilityValue;

    private List<Marks> marksList;
    private final Map<String, Double> attendanceByCourse = new HashMap<>();

    public MarksPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        top.add(UITheme.sectionHeader("Marks, Grade & GPA"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        JLabel courseLabel = new JLabel("Course");
        courseLabel.setFont(UITheme.F_BODY_B);

        courseSelector = new JComboBox<>();
        courseSelector.setFont(UITheme.F_BODY);
        courseSelector.addActionListener(e -> updateSelectedCourse());

        filterBar.add(courseLabel);
        filterBar.add(courseSelector);
        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 12));
        cards.setOpaque(false);
        gradeValue = addStatCard(cards, "Course Grade");
        gpvValue = addStatCard(cards, "Course GPV");
        attendanceValue = addStatCard(cards, "Attendance");
        caValue = addStatCard(cards, "CA Total");
        endValue = addStatCard(cards, "End Total");
        totalValue = addStatCard(cards, "Overall Total");
        center.add(cards, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Course Name", "Credits", "CA / 40", "End / 60",
                "Total / 100", "Grade", "GPV", "Attendance", "Att. Eligibility",
                "CA Eligibility", "End Eligibility", "Final Status"};
        table = new StyledTable(cols);
        center.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 4, 16, 0));
        bottom.setOpaque(false);
        attendanceEligibilityValue = addStatusCard(bottom, "Attendance Eligibility");
        caEligibilityValue = addStatusCard(bottom, "CA Eligibility");
        endEligibilityValue = addStatusCard(bottom, "End Eligibility");
        finalEligibilityValue = addStatusCard(bottom, "Overall Eligibility");
        center.add(bottom, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        JLabel note = new JLabel("  - means no component for that course, 0 means absent or zero score, GPA uses credit-weighted GPV.");
        note.setFont(UITheme.F_SMALL);
        note.setForeground(UITheme.PUTTY);
        note.setBorder(new EmptyBorder(8, 0, 0, 0));
        add(note, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        marksList = svc.getMarks(student.getUgId());
        attendanceByCourse.clear();
        for (AttendanceCourseSummary summary : attendanceSvc.getCourseSummaries(student.getUgId())) {
            attendanceByCourse.put(summary.getCourseCode(), summary.getPercentage());
        }

        courseSelector.removeAllItems();
        courseSelector.addItem("All Courses");

        table.clearRows();
        for (Marks marks : marksList) {
            courseSelector.addItem(marks.getCCode() + " - " + marks.getCName());
            double attendancePct = attendanceByCourse.getOrDefault(marks.getCCode(), 0.0);
            double total = marks.getTotal();

            table.addRow(new Object[]{
                    marks.getCCode(),
                    marks.getCName(),
                    marks.getCredit(),
                    String.format("%.1f", marks.getCaTotal()),
                    String.format("%.1f", marks.getEndTotal()),
                    String.format("%.1f", total),
                    GPACalculator.letterGrade(total),
                    String.format("%.1f", GPACalculator.toGradePoint(total)),
                    String.format("%.1f%%", attendancePct),
                    yesNo(marks.isAttendanceEligible(attendancePct)),
                    yesNo(marks.isCaEligible()),
                    yesNo(marks.isEndEligible()),
                    yesNo(marks.isFullyEligible(attendancePct))
            });
        }

        if (!marksList.isEmpty()) {
            courseSelector.setSelectedIndex(1);
            updateSelectedCourse();
        }
    }

    private JLabel addStatCard(JPanel parent, String title) {
        JPanel card = UITheme.statCard(UITheme.RUSSET);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.F_BODY_B);
        titleLabel.setForeground(UITheme.DARK_TEXT);

        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(UITheme.F_LARGE);
        valueLabel.setForeground(UITheme.ESPRESSO);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);
        parent.add(card);
        return valueLabel;
    }

    private JLabel addStatusCard(JPanel parent, String title) {
        JPanel card = UITheme.statCard(UITheme.SANDSTONE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.F_BODY_B);
        titleLabel.setForeground(UITheme.DARK_TEXT);

        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(UITheme.F_BODY_B);
        valueLabel.setForeground(UITheme.ESPRESSO);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);
        parent.add(card);
        return valueLabel;
    }

    private void updateSelectedCourse() {
        int index = courseSelector.getSelectedIndex();
        if (index <= 0 || marksList == null || marksList.isEmpty()) {
            return;
        }

        Marks selected = marksList.get(index - 1);
        double attendancePct = attendanceByCourse.getOrDefault(selected.getCCode(), 0.0);
        double total = selected.getTotal();

        gradeValue.setText(GPACalculator.letterGrade(total));
        gpvValue.setText(String.format("%.1f", GPACalculator.toGradePoint(total)));
        attendanceValue.setText(String.format("%.1f%%", attendancePct));
        caValue.setText(String.format("%.1f / 40", selected.getCaTotal()));
        endValue.setText(String.format("%.1f / 60", selected.getEndTotal()));
        totalValue.setText(String.format("%.1f / 100", total));

        setStatusLabel(attendanceEligibilityValue, selected.isAttendanceEligible(attendancePct), "Eligible", "Below 80%");
        setStatusLabel(caEligibilityValue, selected.isCaEligible(), "Eligible", "Below 20/40");
        setStatusLabel(endEligibilityValue, selected.isEndEligible(), "Eligible", "Below 30/60");
        setStatusLabel(finalEligibilityValue, selected.isFullyEligible(attendancePct), "Eligible", "Not Eligible");
    }

    private void setStatusLabel(JLabel label, boolean ok, String passText, String failText) {
        label.setText(ok ? passText : failText);
        label.setForeground(ok ? UITheme.STATUS_GREEN : UITheme.STATUS_RED);
    }

    private String yesNo(boolean ok) {
        return ok ? "Eligible" : "Not Eligible";
    }
}
