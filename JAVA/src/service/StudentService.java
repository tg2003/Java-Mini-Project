// ─── StudentService.java ─────────────────────────────────────────────────────
package service;

import dao.StudentDAO;
import models.Undergraduate;
import java.util.List;

public class StudentService {
    private final StudentDAO dao = new StudentDAO();

    public Undergraduate login(String userId, String password) {
        if (userId == null || userId.isBlank() || password == null || password.isBlank())
            return null;
        return dao.login(userId.trim(), password);
    }

    public Undergraduate getProfile(String ugId) {
        return dao.getById(ugId);
    }

    public List<String> getPhones(String ugId) {
        return dao.getPhones(ugId);
    }

    public int getCourseCount(String ugId)   { return dao.getCourseCount(ugId); }
    public int getUnreadNoticeCount()        { return dao.getUnreadNoticeCount(); }
}
