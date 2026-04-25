package models;

public class AttendanceCourseSummary {
    private final String courseCode;
    private final String courseName;
    private final double attendedHours;
    private final double totalHours;
    private final double percentage;

    public AttendanceCourseSummary(String courseCode, String courseName,
                                   double attendedHours, double totalHours, double percentage) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.attendedHours = attendedHours;
        this.totalHours = totalHours;
        this.percentage = percentage;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public double getAttendedHours() { return attendedHours; }
    public double getTotalHours() { return totalHours; }
    public double getPercentage() { return percentage; }
}
