package gui;

import models.Marks;
import models.Undergraduate;
import service.MarksService;
import util.GPACalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class GPAPanel extends JPanel {

    private final Undergraduate student;
    private final MarksService svc = new MarksService();

    public GPAPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("GPA Overview"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.add(buildGPACard(), BorderLayout.WEST);
        body.add(buildBreakdownTable(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildGPACard() {
        double gpa = svc.getGPA(student.getUgId());
        String cls = GPACalculator.classify(gpa);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ESPRESSO, 0, getHeight(), UITheme.RUSSET);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 300));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel title = centered("Weighted GPA", UITheme.F_SUBHEAD, UITheme.PUTTY);
        JLabel value = centered(String.format("%.2f", gpa), UITheme.F_HUGE, Color.WHITE);
        JLabel scale = centered("/ 4.00", UITheme.F_BODY, UITheme.PUTTY);
        JLabel classStanding = centered(cls, UITheme.F_BODY_B, UITheme.SANDSTONE);
        classStanding.setBorder(new EmptyBorder(10, 0, 0, 0));

        inner.add(title);
        inner.add(Box.createVerticalStrut(8));
        inner.add(value);
        inner.add(scale);
        inner.add(classStanding);
        card.add(inner);
        return card;
    }

    private JScrollPane buildBreakdownTable() {
        List<Marks> list = svc.getMarks(student.getUgId());
        String[] cols = {"Course", "Credits", "CA / 40", "End / 60", "Total / 100", "GPV", "Grade"};
        StyledTable table = new StyledTable(cols);
        for (Marks marks : list) {
            double total = marks.getTotal();
            table.addRow(new Object[]{
                    marks.getCName(),
                    marks.getCredit(),
                    String.format("%.1f", marks.getCaTotal()),
                    String.format("%.1f", marks.getEndTotal()),
                    String.format("%.1f", total),
                    String.format("%.1f", GPACalculator.toGradePoint(total)),
                    GPACalculator.letterGrade(total)
            });
        }
        return UITheme.scrollPane(table);
    }

    private JLabel centered(String text, Font font, Color fg) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(fg);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
