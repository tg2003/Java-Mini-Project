package gui;

import models.*;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class GPAPanel extends JPanel {

    private final Undergraduate student;
    private final MarksService  svc = new MarksService();

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
        body.add(buildGPACard(),        BorderLayout.WEST);
        body.add(buildBreakdownTable(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildGPACard() {
        double gpa = svc.getGPA(student.getUgId());
        String cls = GPACalculator.classify(gpa);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ESPRESSO, 0, getHeight(), UITheme.RUSSET);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 280));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel title = centred("Cumulative GPA", UITheme.F_SUBHEAD, UITheme.PUTTY);
        JLabel val   = centred(String.format("%.2f", gpa), UITheme.F_HUGE, Color.WHITE);
        JLabel scale = centred("/ 4.00", UITheme.F_BODY,  UITheme.PUTTY);
        JLabel clsL  = centred(cls, UITheme.F_BODY_B, UITheme.SANDSTONE);
        clsL.setBorder(new EmptyBorder(10, 0, 0, 0));

        inner.add(title);
        inner.add(Box.createVerticalStrut(8));
        inner.add(val);
        inner.add(scale);
        inner.add(clsL);
        card.add(inner);
        return card;
    }

    private JScrollPane buildBreakdownTable() {
        List<Marks> list = svc.getMarks(student.getUgId());
        String[] cols = {"Course", "Total Marks", "Grade Points", "Letter"};
        StyledTable t = new StyledTable(cols);
        for (Marks m : list) {
            double total = m.getTotal();
            t.addRow(new Object[]{
                m.getCName(),
                String.format("%.1f", total),
                String.format("%.1f", GPACalculator.toGradePoint(total)),
                GPACalculator.letterGrade(total)
            });
        }
        return UITheme.scrollPane(t);
    }

    private JLabel centred(String text, Font f, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(f); l.setForeground(fg);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
