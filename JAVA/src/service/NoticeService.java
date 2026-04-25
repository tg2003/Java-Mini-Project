// ─── NoticeService.java ──────────────────────────────────────────────────────
package service;

import models.Notice;
import java.util.List;

public class NoticeService {
    private final dao.NoticeDAO dao = new dao.NoticeDAO();
    public List<Notice> getAll() { return dao.getAll(); }
}

// ─── MedicalService.java ─────────────────────────────────────────────────────
// (put in its own file in production — combined here for brevity)
// package service;
// import model.Medical; import java.util.List;
// public class MedicalService { ... }
