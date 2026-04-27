package service;

import dao.MedicalDAO;
import models.Attendance;
import models.Medical;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MedicalService {
    private final MedicalDAO dao = new MedicalDAO();

    public List<Medical> getMedicals(String ugId) {
        return dao.getByStudent(ugId);
    }

    public List<Integer> submitMedicalForSessions(String ugId, List<Attendance> sessions) {
        List<Integer> refs = new ArrayList<>();
        for (Attendance s : sessions) {
            int id = dao.createMedicalForSession(
                    ugId,
                    s.getDate(),
                    s.getSessionType(),
                    s.getCCode()
            );
            if (id > 0) refs.add(id);
        }
        return refs;
    }

    /** Legacy single submit (used by edit flow). */
    public int submitMedical(String ugId, Date fromDate, Date toDate,
                             String sessionType, String cCode) {
        return dao.createMedical(ugId, fromDate, toDate, sessionType, cCode);
    }

    public boolean updatePendingMedical(int medicalId, Date fromDate, Date toDate,
                                        String sessionType, String cCode) {
        return dao.updatePendingMedical(medicalId, fromDate, toDate, sessionType, cCode);
    }
}