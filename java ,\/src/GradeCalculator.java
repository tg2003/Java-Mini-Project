public class GradeCalculator {
    public static String[] calculateGrade(int marks) {

        String grade;
        double gpa;

        if (marks >= 80) { grade="A"; gpa=4.00; }
        else if (marks >= 75) { grade="A-"; gpa=3.70; }
        else if (marks >= 70) { grade="B+"; gpa=3.30; }
        else if (marks >= 65) { grade="B"; gpa=3.00; }
        else if (marks >= 60) { grade="B-"; gpa=2.70; }
        else if (marks >= 55) { grade="C+"; gpa=2.30; }
        else if (marks >= 45) { grade="C"; gpa=2.00; }
        else if (marks >= 40) { grade="C-"; gpa=1.70; }
        else { grade="F"; gpa=0.00; }

        return new String[]{grade, String.valueOf(gpa)};
    }

    public static boolean checkEligibility(double attendance, int caMarks) {
        return (attendance >= 80 && caMarks >= 40);
    }




}
