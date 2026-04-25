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

        JLabel title = new JLabel("Attendance Overview");
        title.setFont(new Font("Georgia", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("See all subject codes and open each course for a full session-by-session attendance breakdown.");
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
        summaryTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton button = actionButton("View");

            {
                button.addActionListener(e -> {
                    int row = summaryTable.getSelectedRow();
                    fireEditingStopped();
                    if (row >= 0 && row < summaries.size()) {
                        openCourseDetails(summaries.get(row));
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return button;
            }
        });
        summaryTable.getColumnModel().getColumn(5).setMinWidth(100);
        summaryTable.getColumnModel().getColumn(5).setMaxWidth(120);

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(hero, BorderLayout.NORTH);
        wrapper.add(UITheme.scrollPane(summaryTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildDetailView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);

        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);

        JButton backButton = UITheme.primaryButton("Back to Subjects");
        backButton.addActionListener(e -> showSummaryView());
        titleWrap.add(backButton, BorderLayout.WEST);

        JPanel headingWrap = new JPanel();
        headingWrap.setOpaque(false);
        headingWrap.setLayout(new BoxLayout(headingWrap, BoxLayout.Y_AXIS));

        detailHeading = new JLabel("Course Attendance");
        detailHeading.setFont(UITheme.F_HEADING);
        detailHeading.setForeground(UITheme.ESPRESSO);

        detailSubHeading = new JLabel("Detailed attendance list");
        detailSubHeading.setFont(UITheme.F_BODY);
        detailSubHeading.setForeground(UITheme.RUSSET);
        detailSubHeading.setBorder(new EmptyBorder(6, 0, 0, 0));

        headingWrap.add(detailHeading);
        headingWrap.add(detailSubHeading);
        titleWrap.add(headingWrap, BorderLayout.SOUTH);

        detailStats = new JPanel(new GridLayout(1, 3, 16, 0));
        detailStats.setOpaque(false);

        top.add(titleWrap, BorderLayout.NORTH);
        top.add(detailStats, BorderLayout.CENTER);

        String[] cols = {"Date", "Hours", "Type", "Start Time", "Status"};
        detailTable = new StyledTable(cols);
        detailTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        panel.add(top, BorderLayout.NORTH);
        panel.add(UITheme.scrollPane(detailTable), BorderLayout.CENTER);
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
        detailHeading.setText(details.getCourseCode() + "  •  " + details.getCourseName());
        detailSubHeading.setText("Date, hours, type, start time, and status for each marked session");

        detailStats.removeAll();
        detailStats.add(statCard("Participated Hours", fmtHours(details.getAttendedHours()), UITheme.STATUS_GREEN));
        detailStats.add(statCard("Total Hours", fmtHours(details.getTotalHours()), UITheme.RUSSET));
        detailStats.add(statCard("Percentage", String.format("%.1f%%", details.getPercentage()),
                details.getPercentage() >= 80 ? UITheme.STATUS_GREEN : UITheme.STATUS_RED));

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
