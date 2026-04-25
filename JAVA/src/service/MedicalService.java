package service;

import dao.MedicalDAO;
import models.Medical;

import java.sql.Date;
import java.util.List;

public class MedicalService {
    private final MedicalDAO dao = new MedicalDAO();

    public List<Medical> getMedicals(String ugId) {
        return dao.getByStudent(ugId);
    }

    public int submitMedical(String ugId, Date fromDate, Date toDate, String sessionType, String cCode) {
        return dao.createMedical(ugId, fromDate, toDate, sessionType, cCode);
    }

    public boolean updatePendingMedical(int medicalId, Date fromDate, Date toDate, String sessionType, String cCode) {
        return dao.updatePendingMedical(medicalId, fromDate, toDate, sessionType, cCode);
    }
}
