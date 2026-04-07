package gui;

import javax.swing.*;

public class CoursesPanel extends JPanel{
    private JPanel mainCoursesPanel;
    private JPanel noticesMainPanel;
    private JButton viewCoursesButton;
    private JButton editCoursesButton;
    private JButton addANewCourseButton;

    public CoursesPanel() {
        add(mainCoursesPanel);

        viewCoursesButton.addActionListener(e -> {
            new CourseView().setVisible(true);
        });

        editCoursesButton.addActionListener(e -> {
            new CourseView("Admin").setVisible(true);
        });

        addANewCourseButton.addActionListener(e -> {
            new CourseCreate().setVisible(true);
        });
    }
}
