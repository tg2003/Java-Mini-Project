package service;

import dao.MarksDAO;
import models.Marks;
import util.GPACalculator;
import java.util.List;

public class MarksService {
    private final MarksDAO dao = new MarksDAO();

    public List<Marks> getMarks(String ugId)  { return dao.getByStudent(ugId); }

    public double getGPA(String ugId) {
        return GPACalculator.calculate(getMarks(ugId));
    }

    public String getGPAString(String ugId) {
        return String.format("%.2f", getGPA(ugId));
    }
}
