package service;

import dao.TimetableDAO;
import models.Timetable;
import java.util.List;

public class TimetableService {
    private final TimetableDAO dao = new TimetableDAO();
    public List<Timetable> getTimetable(String ugId) { return dao.getForStudent(ugId); }
}
