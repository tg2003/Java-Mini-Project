package models;

/**
 * GradeCalculator — pure logic class (no DB table).
 *
 * Takes a Mark object and calculates:
 *   - CA Total          (Quiz + Assignment + Project + Mid)
 *   - Final Exam Total  (FinalT + FinalP)
 *   - Overall Total     (CA + Final)
 *   - Letter Grade      (A+ down to F)
 *   - GPA value         (4.0 scale)
 *   - CA Eligibility    (student needs >= 50% of CA marks to sit final)
 *
 * Mark distribution assumed (adjust if your system differs):
 * ┌─────────────────┬────────────┐
 * │ Component        │ Max Marks  │
 * ├─────────────────┼────────────┤
 * │ Quiz 01          │    10      │
 * │ Quiz 02          │    10      │
 * │ Quiz 03          │    10      │
 * │ Assignment 1     │    10      │
 * │ Assignment 2     │    10      │
 * │ Project          │    10      │
 * │ Mid Theory       │    15      │
 * │ Mid Practical    │    15      │
 * │ ── CA Total ──   │   (90)     │
 * ├─────────────────┼────────────┤
 * │ Final Theory     │    50      │
 * │ Final Practical  │    50      │
 * │ ── Final Total ──│  (100)     │
 * ├─────────────────┼────────────┤
 * │ OVERALL TOTAL    │  (190)     │
 * └─────────────────┴────────────┘
 *
 * NOTE: CA eligibility = caTotal >= CA_ELIGIBILITY_MINIMUM (default 45 out of 90 = 50%)
 */
public class GradeCalculator {

    // ── The Mark object this calculator works on ───────────────────────────
    private Mark mark;

    // ── CA eligibility threshold: student must score at least this much in CA ──
    // 45 out of 90 = 50%
    private static final double CA_ELIGIBILITY_MINIMUM = 45.0;

    // ── Max possible marks (useful for % calculations) ─────────────────────
    public static final double MAX_CA    = 90.0;
    public static final double MAX_FINAL = 100.0;
    public static final double MAX_TOTAL = 190.0;

    // ── Constructor ────────────────────────────────────────────────────────
    public GradeCalculator(Mark mark) {
        this.mark = mark;
    }

    // ── CA Total (Quiz + Assignment + Project + Mid) ───────────────────────
    /**
     * Returns the raw CA total (out of 90).
     */
    public double getCATotal() {
        return mark.getQuiz01()
                + mark.getQuiz02()
                + mark.getQuiz03()
                + mark.getAssignment1()
                + mark.getAssignment2()
                + mark.getProject()
                + mark.getMidT()
                + mark.getMidP();
    }

    // ── Final Exam Total ───────────────────────────────────────────────────
    /**
     * Returns the raw Final total (out of 100).
     */
    public double getFinalTotal() {
        return mark.getFinalT() + mark.getFinalP();
    }

    // ── Overall Total ──────────────────────────────────────────────────────
    /**
     * Returns overall total (out of 190).
     */
    public double getOverallTotal() {
        return getCATotal() + getFinalTotal();
    }

    // ── CA as percentage ───────────────────────────────────────────────────
    /**
     * Returns CA as a percentage out of 100.
     * Useful to display "CA: 72.5%"
     */
    public double getCАPercentage() {
        return (getCATotal() / MAX_CA) * 100.0;
    }

    // ── Overall as percentage ──────────────────────────────────────────────
    /**
     * Returns overall marks as a percentage out of 100.
     */
    public double getOverallPercentage() {
        return (getOverallTotal() / MAX_TOTAL) * 100.0;
    }

    // ── CA Eligibility ─────────────────────────────────────────────────────
    /**
     * Returns true if the student has enough CA marks to sit the final exam.
     * Threshold: caTotal >= 45 (50% of 90).
     */
    public boolean isCAEligible() {
        return getCATotal() >= CA_ELIGIBILITY_MINIMUM;
    }

    // ── Letter Grade ───────────────────────────────────────────────────────
    /**
     * Returns a letter grade based on the OVERALL PERCENTAGE.
     *
     * Grade scale (standard Sri Lankan university scale):
     *   A+  >= 75%
     *   A   >= 70%
     *   A-  >= 65%
     *   B+  >= 60%
     *   B   >= 55%
     *   B-  >= 50%
     *   C+  >= 45%
     *   C   >= 40%
     *   C-  >= 35%
     *   D   >= 30%
     *   F   < 30%
     */
    public String getLetterGrade() {
        // If student didn't pass CA eligibility, they cannot pass the course
        if (!isCAEligible()) return "F";

        double pct = getOverallPercentage();

        if      (pct >= 75) return "A+";
        else if (pct >= 70) return "A";
        else if (pct >= 65) return "A-";
        else if (pct >= 60) return "B+";
        else if (pct >= 55) return "B";
        else if (pct >= 50) return "B-";
        else if (pct >= 45) return "C+";
        else if (pct >= 40) return "C";
        else if (pct >= 35) return "C-";
        else if (pct >= 30) return "D";
        else                return "F";
    }

    // ── GPA Value ──────────────────────────────────────────────────────────
    /**
     * Returns the GPA value (4.0 scale) based on letter grade.
     */
    public double getGPA() {
        return switch (getLetterGrade()) {
            case "A+"  -> 4.0;
            case "A"   -> 4.0;
            case "A-"  -> 3.7;
            case "B+"  -> 3.3;
            case "B"   -> 3.0;
            case "B-"  -> 2.7;
            case "C+"  -> 2.3;
            case "C"   -> 2.0;
            case "C-"  -> 1.7;
            case "D"   -> 1.0;
            default    -> 0.0;   // F
        };
    }

    // ── Summary String (useful for reports / debugging) ────────────────────
    /**
     * Returns a formatted summary of the student's results.
     */
    public String getSummary() {
        return String.format(
                "Student: %-8s | Course: %-7s | CA: %5.1f/90 | Final: %5.1f/100 | " +
                        "Total: %6.1f/190 (%5.1f%%) | Grade: %-2s | GPA: %.1f | CA Eligible: %s",
                mark.getUgId(),
                mark.getCCode(),
                getCATotal(),
                getFinalTotal(),
                getOverallTotal(),
                getOverallPercentage(),
                getLetterGrade(),
                getGPA(),
                isCAEligible() ? "YES" : "NO"
        );
    }

    // ── Getter for the Mark object ─────────────────────────────────────────
    public Mark getMark() {
        return mark;
    }

    // ── Setter (if you need to reuse the same calculator) ─────────────────
    public void setMark(Mark mark) {
        this.mark = mark;
    }
}