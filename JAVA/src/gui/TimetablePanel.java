package gui;

import javax.swing.*;

public class TimetablePanel extends JPanel {
    private JPanel mainTimetablePanel;
    private JPanel MainPanel;
    private JButton viewTimetableButton;
    private JButton editTimetableButton;
    private JButton createANewSessionButton;

    public TimetablePanel(){
        add(mainTimetablePanel);

        viewTimetableButton.addActionListener(e -> {
            new TimetableView().setVisible(true);
        });

        editTimetableButton.addActionListener(e -> {
            new TimetableView("Admin").setVisible(true);
        });

        createANewSessionButton.addActionListener(e -> {
            new TimetableAddNew().setVisible(true);
        });
    }
}