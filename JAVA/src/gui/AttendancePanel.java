package gui;

import models.Attendance;
import models.AttendanceCourseDetails;
import models.AttendanceCourseSummary;
import models.Undergraduate;
import service.AttendanceService;
import util.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final Undergraduate student;
    private final AttendanceService svc = new AttendanceService();

    private final CardLayout viewLayout = new CardLayout();
    private final JPanel viewPanel = new JPanel(viewLayout);

    private StyledTable summaryTable;
    private StyledTable detailTable;
    private List<AttendanceCourseSummary> summaries = new ArrayList<>();

    private JLabel detailHeading;
    private JLabel detailSubHeading;
    private JPanel detailStats;
    private JLabel detailPercentageLabel;

    public AttendancePanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 30, 28, 30));

        viewPanel.setOpaque(false);
        viewPanel.add(buildSummaryView(), "summary");
        viewPanel.add(buildDetailView(), "detail");
        add(viewPanel, BorderLayout.CENTER);

        loadSummaryData();
        showSummaryView();
    }

    private JPanel buildSummaryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel hero = UITheme.heroPanel();
        hero.setLayout(new BorderLayout());
        hero.setBorder(new EmptyBorder(24, 28, 24, 28));
        hero.setPreferredSize(new Dimension(0, 120));

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Attendance Panel");
        title.setFont(new Font("Georgia", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Inside this panel, click View to open the selected course attendance details.");
        subtitle.setFont(UITheme.F_BODY);
        subtitle.setForeground(UITheme.PUTTY);
        subtitle.setBorder(new EmptyBorder(8, 0, 0, 0));

        heroText.add(title);
        heroText.add(subtitle);
        hero.add(heroText, BorderLayout.CENTER);

        String[] cols = {"Course Code", "Course Name", "Participated Hours", "Total Hours", "Percentage", "Action"};
        summaryTable = new StyledTable(cols);
        summaryTable.getColumnModel().getColumn(4).setCellRenderer(new PercentageRenderer());
        summaryTable.getColumnModel().getColumn(5).setCellRenderer((table, value, isSelected, hasFocus, row, column) ->
                actionButton("View"));
        summaryTable.getColumnModel().getColumn(5).setMinWidth(100);
        summaryTable.getColumnModel().getColumn(5).setMaxWidth(120);
        summaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = summaryTable.rowAtPoint(e.getPoint());
                int col = summaryTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5 && row < summaries.size()) {
                    openCourseDetails(summaries.get(row));
                }
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(hero, BorderLayout.NORTH);
        wrapper.add(UITheme.scrollPane(summaryTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildDetailView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);

        JButton backButton = UITheme.primaryButton("Back to Subjects");
        backButton.addActionListener(e -> showSummaryView());
        titleWrap.add(backButton, BorderLayout.WEST);

        JPanel headingWrap = new JPanel();
        headingWrap.setOpaque(false);
        headingWrap.setLayout(new BoxLayout(headingWrap, BoxLayout.Y_AXIS));

        detailHeading = new JLabel("Attendance Course");
        detailHeading.setFont(UITheme.F_HEADING);
        detailHeading.setForeground(UITheme.ESPRESSO);

        detailSubHeading = new JLabel("Course ID, course name, date, hours, type, start time, and status");
        detailSubHeading.setFont(UITheme.F_BODY);
        detailSubHeading.setForeground(UITheme.RUSSET);
        detailSubHeading.setBorder(new EmptyBorder(6, 0, 0, 0));

        headingWrap.add(detailHeading);
        headingWrap.add(detailSubHeading);
        titleWrap.add(headingWrap, BorderLayout.SOUTH);

        detailStats = new JPanel(new GridLayout(1, 2, 16, 0));
        detailStats.setOpaque(false);

        String[] cols = {"Date", "Hours", "Type", "Start Time", "Status"};
        detailTable = new StyledTable(cols);
        detailTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        JPanel footer = new JPanel(new BorderLayout(0, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));

        detailPercentageLabel = new JLabel("Attendance Percentage: 0.0%");
        detailPercentageLabel.setFont(UITheme.F_BODY_B);
        detailPercentageLabel.setForeground(UITheme.ESPRESSO);
        detailPercentageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel percentagePanel = UITheme.statCard(UITheme.SANDSTONE);
        percentagePanel.setLayout(new BorderLayout());
        percentagePanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        percentagePanel.add(detailPercentageLabel, BorderLayout.CENTER);

        footer.add(detailStats, BorderLayout.NORTH);
        footer.add(percentagePanel, BorderLayout.CENTER);

        panel.add(titleWrap, BorderLayout.NORTH);
        panel.add(UITheme.scrollPane(detailTable), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSummaryData() {
        summaries = svc.getCourseSummaries(student.getUgId());
        summaryTable.clearRows();
        for (AttendanceCourseSummary summary : summaries) {
            summaryTable.addRow(new Object[]{
                    summary.getCourseCode(),
                    summary.getCourseName(),
                    fmtHours(summary.getAttendedHours()),
                    fmtHours(summary.getTotalHours()),
                    String.format("%.1f%%", summary.getPercentage()),
                    "View"
            });
        }
    }

    private void openCourseDetails(AttendanceCourseSummary summary) {
        AttendanceCourseDetails details = svc.getCourseDetails(student.getUgId(), summary.getCourseCode());
        detailHeading.setText("Attendance Course: " + details.getCourseCode() + " - " + details.getCourseName());
        detailSubHeading.setText("Date | Hours | Type (Lecture/Practical) | Start Time | Status");

        detailStats.removeAll();
        detailStats.add(statCard("Participated Hours", fmtHours(details.getAttendedHours()), UITheme.STATUS_GREEN));
        detailStats.add(statCard("Total Hours", fmtHours(details.getTotalHours()), UITheme.RUSSET));
        detailPercentageLabel.setText("Attendance Percentage: " + String.format("%.1f%%", details.getPercentage()));
        detailPercentageLabel.setForeground(details.getPercentage() >= 80 ? UITheme.STATUS_GREEN : UITheme.STATUS_RED);

        detailTable.clearRows();
        for (Attendance session : details.getSessions()) {
            detailTable.addRow(new Object[]{
                    DateFormatter.format(session.getDate()),
                    fmtHours(session.getDurationHours()),
                    session.getSessionType(),
                    session.getStartTime(),
                    session.getStatus()
            });
        }

        if (details.getSessions().isEmpty()) {
            detailTable.addRow(new Object[]{"No marked sessions yet", "-", "-", "-", "-"});
        }

        detailStats.revalidate();
        detailStats.repaint();
        viewLayout.show(viewPanel, "detail");
    }

    private void showSummaryView() {
        viewLayout.show(viewPanel, "summary");
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = UITheme.statCard(accent);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel labelText = new JLabel(label);
        labelText.setFont(UITheme.F_BODY_B);
        labelText.setForeground(UITheme.DARK_TEXT);

        JLabel valueText = new JLabel(value);
        valueText.setFont(UITheme.F_LARGE);
        valueText.setForeground(UITheme.ESPRESSO);

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueText, BorderLayout.SOUTH);
        return card;
    }

    private JButton actionButton(String text) {
        JButton button = UITheme.primaryButton(text);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }

    private String fmtHours(double hours) {
        return String.format("%.1f h", hours);
    }

    private static class PercentageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                try {
                    double percentage = Double.parseDouble(value.toString().replace("%", ""));
                    label.setForeground(percentage >= 80 ? UITheme.STATUS_GREEN : UITheme.STATUS_RED);
                } catch (Exception ex) {
                    label.setForeground(UITheme.DARK_TEXT);
                }
            }
            return label;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String status = value == null ? "" : value.toString();
                if ("Present".equals(status) || "MedicalApproved".equals(status)) {
                    label.setForeground(UITheme.STATUS_GREEN);
                } else if ("Absent".equals(status) || "MedicalDeclined".equals(status)) {
                    label.setForeground(UITheme.STATUS_RED);
                } else {
                    label.setForeground(UITheme.STATUS_AMBER);
                }
            }
            return label;
        }
    }
}
