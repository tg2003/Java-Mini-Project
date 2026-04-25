package gui;

import models.*;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class MedicalPanel extends JPanel {

    private final Undergraduate student;
    private final MedicalService svc = new MedicalService();
    private StyledTable table;

    public MedicalPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel hdr = new JLabel("Medical Leave Records");
        hdr.setFont(UITheme.F_HEADING);
        hdr.setForeground(UITheme.ESPRESSO);
        topBar.add(hdr, BorderLayout.WEST);

        JButton btn = UITheme.primaryButton("+ Submit New Request");
        btn.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Medical leave submission form would open here.\n(Connect to backend to implement saving.)",
            "Submit Medical Leave", JOptionPane.INFORMATION_MESSAGE));
        topBar.add(btn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "From", "To", "Session", "Status", "Approved By", "Course"};
        table = new StyledTable(cols);
        loadData();
        add(UITheme.scrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        List<Medical> list = svc.getMedicals(student.getUgId());
        table.clearRows();
        for (Medical m : list) {
            table.addRow(new Object[]{
                m.getMedicalId(),
                DateFormatter.shortFormat(m.getFromDate()),
                DateFormatter.shortFormat(m.getToDate()),
                m.getSessionType(),
                m.getStatus(),
                m.getOfficerName(),
                m.getCCode()
            });
        }
    }
}
