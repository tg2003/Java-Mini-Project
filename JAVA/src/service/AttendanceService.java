package service;

import dao.AttendanceDAO;
import models.Attendance;
import models.AttendanceCourseDetails;
import models.AttendanceCourseSummary;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO dao = new AttendanceDAO();

    public List<Attendance> getAttendance(String ugId) {
        return dao.getByStudent(ugId);
    }

    /** Returns formatted percentage string, e.g. "91.3%" */
    public String getAttendancePercentStr(String ugId) {
        double pct = dao.getAttendancePercentage(ugId);
        return String.format("%.1f%%", pct);
    }

    public double getAttendancePercent(String ugId) {
        return dao.getAttendancePercentage(ugId);
    }

    public List<AttendanceCourseSummary> getCourseSummaries(String ugId) {
        return dao.getCourseSummaries(ugId);
    }

    public AttendanceCourseDetails getCourseDetails(String ugId, String cCode) {
        return dao.getCourseDetails(ugId, cCode);
    }
}
