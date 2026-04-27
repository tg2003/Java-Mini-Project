package gui;

import models.Medical;
import models.Undergraduate;
import service.MedicalService;
import util.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalPanel extends JPanel {

    private final Undergraduate student;
    private final MedicalService svc = new MedicalService();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private StyledTable table;
    private List<Medical> medicals = new ArrayList<>();

    public MedicalPanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
        loadData();
        showCard("Instructions");
    }

    private void build() {
        add(UITheme.sectionHeader("Submission of Medicals for Attendance of Classes"), BorderLayout.NORTH);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nav.setOpaque(false);

        JButton instructions = UITheme.primaryButton("Instructions to Submit Medicals");
        instructions.addActionListener(e -> showCard("Instructions"));
        JButton submit = UITheme.primaryButton("Submit Medicals");
        submit.addActionListener(e -> openSubmissionDialog(null));
        JButton view = UITheme.primaryButton("View Medicals");
        view.addActionListener(e -> showCard("View"));

        nav.add(instructions);
        nav.add(submit);
        nav.add(view);

        add(nav, BorderLayout.CENTER);

        cardPanel.setOpaque(false);
        cardPanel.add(buildInstructionsCard(), "Instructions");
        cardPanel.add(buildViewCard(), "View");
        add(cardPanel, BorderLayout.SOUTH);
    }

    private JPanel buildInstructionsCard() {
        JPanel card = cardBlock();

        JTextArea area = new JTextArea(
                "Instructions for submitting medicals\n\n" +
                        "1. You can upload a medical certificate and the dates covered by the medical certificate.\n" +
                        "2. TECMIS will give you a reference number (REF No) for each medical.\n" +
                        "3. The original medical certificate should be submitted to the Dean's Office with the Reference number and Student ID number.\n" +
                        "4. If you make any mistake you may edit during the \"Pending Approval\" period.\n" +
                        "5. If the conditions stated above are not fulfilled, the medical certificate will not be considered."
        );
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(UITheme.F_BODY);
        area.setForeground(UITheme.DARK_TEXT);
        area.setBackground(UITheme.CARD_BG);
        area.setBorder(new EmptyBorder(8, 4, 0, 4));

        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildViewCard() {
        JPanel card = cardBlock();

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);
        JButton refresh = UITheme.primaryButton("Refresh");
        refresh.addActionListener(e -> loadData());
        JButton edit = UITheme.primaryButton("Edit Selected Pending");
        edit.addActionListener(e -> editSelectedPending());
        toolbar.add(refresh);
        toolbar.add(edit);

        String[] cols = {"REF No", "From", "To", "Session", "Course", "Status", "Approved By"};
        table = new StyledTable(cols);

        card.add(toolbar, BorderLayout.NORTH);
        card.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel cardBlock() {
        JPanel card = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(0, 460));
        return card;
    }

    private void loadData() {
        medicals = svc.getMedicals(student.getUgId());
        if (table != null) {
            table.clearRows();
            for (Medical medical : medicals) {
                table.addRow(new Object[]{
                        medical.getReferenceNo(),
                        DateFormatter.shortFormat(medical.getFromDate()),
                        DateFormatter.shortFormat(medical.getToDate()),
                        medical.getSessionType(),
                        medical.getCCode(),
                        medical.getStatus(),
                        medical.getOfficerName()
                });
            }
        }
    }

    private void editSelectedPending() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= medicals.size()) {
            JOptionPane.showMessageDialog(this, "Select a medical record first.", "Edit Medical", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Medical selected = medicals.get(row);
        if (!"Pending".equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending medicals can be edited.", "Edit Medical", JOptionPane.WARNING_MESSAGE);
            return;
        }

        openSubmissionDialog(selected);
    }

    private void openSubmissionDialog(Medical medical) {
        MedicalSubmissionDialog dialog = new MedicalSubmissionDialog(
                SwingUtilities.getWindowAncestor(this),
                student,
                medical,
                () -> {
                    loadData();
                    showCard("View");
                }
        );
        dialog.setVisible(true);
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }
}
