package util;

import models.Marks;

import java.util.List;

public class GPACalculator {

    public static double calculate(List<Marks> marksList) {
        if (marksList == null || marksList.isEmpty()) return 0.0;
        double totalWeightedPoints = 0.0;
        double totalCredits = 0.0;

        for (Marks marks : marksList) {
            double credits = marks.getCreditValue();
            if (credits <= 0.0) continue;
            totalWeightedPoints += toGradePoint(marks.getTotal()) * credits;
            totalCredits += credits;
        }

        return totalCredits == 0.0 ? 0.0 : totalWeightedPoints / totalCredits;
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
        if (total >= 35) return 1.3;
        return 0.0;
    }

    public static String classify(double gpa) {
        if (gpa >= 3.70) return "First Class";
        if (gpa >= 3.30) return "Second Class (Upper Division)";
        if (gpa >= 3.00) return "Second Class (Lower Division)";
        if (gpa >= 2.00) return "General Degree";
        return "Below Graduation Requirement";
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
        if (total >= 35) return "D";
        return "E";
    }
}
