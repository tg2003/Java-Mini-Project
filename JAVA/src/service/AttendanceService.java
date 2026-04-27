package service;

import dao.AttendanceDAO;
import models.Attendance;
import models.AttendanceCourseDetails;
import models.AttendanceCourseSummary;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO dao = new AttendanceDAO();

    public List<Attendance> getAttendance(String ugId) {
        return dao.getByStudent(ugId);
    }

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

    /**
     * Returns Absent sessions within [fromDate, toDate].
     * Validates that the range is at most 14 days.
     * Returns empty list if range is invalid or > 14 days.
     */
    public List<Attendance> getAbsentSessionsInRange(String ugId, LocalDate from, LocalDate to) {
        if (from == null || to == null) return Collections.emptyList();
        if (to.isBefore(from)) return Collections.emptyList();
        long days = ChronoUnit.DAYS.between(from, to);
        if (days > 13) return Collections.emptyList(); // max 14 days span (0..13 = 14 days inclusive)
        return dao.getAbsentSessionsInRange(ugId, Date.valueOf(from), Date.valueOf(to));
    }
}