package gui;

import models.Notice;
import models.Timetable;
import models.Undergraduate;
import service.MarksService;
import service.NoticeService;
import service.StudentService;
import service.TimetableService;
import util.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class DashboardPanel extends JPanel {

    private final Undergraduate student;
    private final Consumer<String> navigator;
    private final StudentService studentSvc = new StudentService();
    private final MarksService marksSvc = new MarksService();
    private final NoticeService noticeSvc = new NoticeService();
    private final TimetableService timetableSvc = new TimetableService();

    public DashboardPanel(Undergraduate student, Consumer<String> navigator) {
        this.student = student;
        this.navigator = navigator;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(buildHero(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(buildQuickAccess(), BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0));
        lower.setOpaque(false);
        lower.add(buildTodaySchedule());
        lower.add(buildNoticePanel());
        center.add(lower, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildHero() {
        JPanel hero = UITheme.heroPanel();
        hero.setLayout(new BorderLayout(18, 0));
        hero.setBorder(new EmptyBorder(26, 28, 26, 28));
        hero.setPreferredSize(new Dimension(0, 180));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel name = new JLabel( student.getName() + "   " + student.getUgId());
        name.setFont(new Font("Georgia", Font.BOLD, 24));
        name.setForeground(Color.WHITE);

        JLabel faculty = new JLabel("Faculty of Technology    " + student.getDptName());
        faculty.setFont(UITheme.F_BODY);
        faculty.setForeground(UITheme.PUTTY);
        faculty.setBorder(new EmptyBorder(8, 0, 0, 0));

        left.add(name);
        left.add(faculty);

        JPanel right = new JPanel(new GridLayout(2, 2, 12, 12));
        right.setOpaque(false);
        right.add(heroStat("Current Semester", "Semester I"));
        right.add(heroStat("GPA", String.format("%.2f", marksSvc.getGPA(student.getUgId()))));
        right.add(heroStat("Courses", String.valueOf(studentSvc.getCourseCount(student.getUgId()))));
        right.add(heroStat("New Notices", String.valueOf(studentSvc.getUnreadNoticeCount())));

        hero.add(left, BorderLayout.CENTER);
        hero.add(right, BorderLayout.EAST);
        return hero;
    }

    private JPanel heroStat(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.F_SMALL);
        titleLabel.setForeground(UITheme.PUTTY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildQuickAccess() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setOpaque(false);
        wrap.add(UITheme.sectionHeader("Quick Access"), BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 5, 16, 0));
        cards.setOpaque(false);
        cards.add(shortcutCard("\uD83D\uDCD8 Courses", "Browse enrolled subjects", "Courses"));
        cards.add(shortcutCard("\uD83D\uDCE2 Notices", "Check latest notices", "Notice"));
        cards.add(shortcutCard("\uD83D\uDCCA GPA / Results", "View grades and GPA", "GPA"));
        cards.add(shortcutCard("\uD83D\uDCC5 Timetable", "Open weekly schedule", "Timetable"));
        cards.add(shortcutCard("\uD83D\uDC64 My Profile", "Edit profile details", "Profile"));
        wrap.add(cards, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel shortcutCard(String title, String subtitle, String target) {
        JPanel card = UITheme.statCard(UITheme.RUSSET);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.F_BODY_B);
        titleLabel.setForeground(UITheme.ESPRESSO);

        JLabel subLabel = new JLabel("<html><body style='width:160px'>" + subtitle + "</body></html>");
        subLabel.setFont(UITheme.F_SMALL);
        subLabel.setForeground(UITheme.RUSSET);

        JButton open = UITheme.primaryButton("Open");
        open.addActionListener(e -> navigator.accept(target));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(subLabel, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTodaySchedule() {
        JPanel panel = cardBlock("Today's Schedule");
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        JPanel list = new JPanel(new GridLayout(0, 1, 0, 10));
        list.setOpaque(false);

        String todayCode = dayCode(LocalDate.now().getDayOfWeek());
        List<Timetable> all = timetableSvc.getTimetable(student.getUgId());
        int count = 0;
        for (Timetable timetable : all) {
            if (timetable.getDay().equalsIgnoreCase(todayCode)) {
                list.add(scheduleRow(timetable));
                count++;
            }
        }

        if (count == 0) {
            JLabel empty = new JLabel("No classes scheduled for today.");
            empty.setFont(UITheme.F_BODY);
            empty.setForeground(UITheme.PUTTY);
            list.add(empty);
        }

        body.add(list, BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel scheduleRow(Timetable timetable) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 12, 12, 12));
        row.setBackground(UITheme.ROW_EVEN);

        JPanel inner = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UITheme.ROW_EVEN);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel time = new JLabel(timetable.getStartTime() + " - " + timetable.getEndTime());
        time.setFont(UITheme.F_BODY_B);
        time.setForeground(UITheme.ESPRESSO);

        JLabel subject = new JLabel(timetable.getCCode() + "  •  " + timetable.getCName());
        subject.setFont(UITheme.F_BODY);
        subject.setForeground(UITheme.DARK_TEXT);

        JLabel type = new JLabel(timetable.getType());
        type.setFont(UITheme.F_SMALL_B);
        type.setForeground(UITheme.STATUS_AMBER);

        inner.add(time, BorderLayout.WEST);
        inner.add(subject, BorderLayout.CENTER);
        inner.add(type, BorderLayout.EAST);
        row.add(inner, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildNoticePanel() {
        JPanel panel = cardBlock("Recent Notices");
        JPanel list = new JPanel(new GridLayout(0, 1, 0, 10));
        list.setOpaque(false);

        List<Notice> notices = noticeSvc.getAll();
        int show = Math.min(4, notices.size());
        if (show == 0) {
            JLabel empty = new JLabel("No notices available.");
            empty.setFont(UITheme.F_BODY);
            empty.setForeground(UITheme.PUTTY);
            list.add(empty);
        } else {
            for (int i = 0; i < show; i++) {
                list.add(noticeRow(notices.get(i)));
            }
        }

        JButton more = UITheme.primaryButton("Open Notice Board");
        more.addActionListener(e -> navigator.accept("Notice"));

        panel.add(list, BorderLayout.CENTER);
        panel.add(more, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel noticeRow(Notice notice) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel("• " + notice.getTitle());
        title.setFont(UITheme.F_BODY);
        title.setForeground(UITheme.DARK_TEXT);

        JLabel date = new JLabel(DateFormatter.format(notice.getDateTime()));
        date.setFont(UITheme.F_SMALL);
        date.setForeground(UITheme.PUTTY);

        row.add(title, BorderLayout.CENTER);
        row.add(date, BorderLayout.EAST);
        return row;
    }

    private JPanel cardBlock(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel(title);
        header.setFont(UITheme.F_HEADING);
        header.setForeground(UITheme.ESPRESSO);
        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private String dayCode(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Mon";
            case TUESDAY: return "Tue";
            case WEDNESDAY: return "Wed";
            case THURSDAY: return "Thu";
            case FRIDAY: return "Fri";
            case SATURDAY: return "Sat";
            default: return "Sun";
        }
    }
}
