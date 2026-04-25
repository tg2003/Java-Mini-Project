package service;

import dao.MedicalDAO;
import models.Medical;
import java.util.List;

public class MedicalService {
    private final MedicalDAO dao = new MedicalDAO();
    public List<Medical> getMedicals(String ugId) { return dao.getByStudent(ugId); }
}
