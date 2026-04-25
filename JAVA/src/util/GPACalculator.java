package util;

import models.Marks;
import java.util.List;

/**
 * Calculates GPA from a list of Marks using a 4.0 scale.
 */
public class GPACalculator {

    public static double calculate(List<Marks> marksList) {
        if (marksList == null || marksList.isEmpty()) return 0.0;
        double totalPoints = 0;
        int    count       = 0;
        for (Marks m : marksList) {
            totalPoints += toGradePoint(m.getTotal());
            count++;
        }
        return count == 0 ? 0.0 : totalPoints / count;
    }

    public static double toGradePoint(double total) {
        if (total >= 85) return 4.0;
        if (total >= 75) return 4.0;
        if (total >= 70) return 3.7;
        if (total >= 65) return 3.3;
        if (total >= 60) return 3.0;
        if (total >= 55) return 2.7;
        if (total >= 50) return 2.3;
        if (total >= 45) return 2.0;
        if (total >= 40) return 1.7;
        return 0.0;
    }

    public static String classify(double gpa) {
        if (gpa >= 3.70) return "First Class Honours";
        if (gpa >= 3.30) return "Second Class Upper";
        if (gpa >= 3.00) return "Second Class Lower";
        if (gpa >= 2.00) return "Pass";
        return "Fail";
    }

    public static String letterGrade(double total) {
        if (total >= 85) return "A+";
        if (total >= 75) return "A";
        if (total >= 70) return "A-";
        if (total >= 65) return "B+";
        if (total >= 60) return "B";
        if (total >= 55) return "B-";
        if (total >= 50) return "C+";
        if (total >= 45) return "C";
        if (total >= 40) return "C-";
        return "F";
    }
}
