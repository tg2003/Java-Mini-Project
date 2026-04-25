// ─── CourseService.java ──────────────────────────────────────────────────────
package service;

import dao.CourseDAO;
import models.Course;
import java.util.List;

public class CourseService {
    private final CourseDAO dao = new CourseDAO();
    public List<Course>  getCourses(String ugId)            { return dao.getEnrolledCourses(ugId); }
    public List<String>  getMaterials(String cCode)          { return dao.getMaterials(cCode); }
}
