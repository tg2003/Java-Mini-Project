package models;

import java.util.List;

public class AttendanceCourseDetails {
    private final String courseCode;
    private final String courseName;
    private final List<Attendance> sessions;
    private final double attendedHours;
    private final double totalHours;
    private final double percentage;

    public AttendanceCourseDetails(String courseCode, String courseName, List<Attendance> sessions,
                                   double attendedHours, double totalHours, double percentage) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.sessions = sessions;
        this.attendedHours = attendedHours;
        this.totalHours = totalHours;
        this.percentage = percentage;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public List<Attendance> getSessions() { return sessions; }
    public double getAttendedHours() { return attendedHours; }
    public double getTotalHours() { return totalHours; }
    public double getPercentage() { return percentage; }
}
