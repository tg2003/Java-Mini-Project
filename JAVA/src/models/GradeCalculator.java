package models;

import java.util.Arrays;


public class GradeCalculator {

    private final Mark mark;

    public GradeCalculator(Mark mark) { this.mark = mark; }

    // ── Best 2 of 3 quiz average (each quiz stored /100) ──────────────────
    public double getBest2QuizAvg() {
        double[] q = { mark.getQuiz01(), mark.getQuiz02(), mark.getQuiz03() };
        Arrays.sort(q);               // ascending
        return (q[1] + q[2]) / 2.0;  // average of top two (0–100 scale)
    }

    // ── Combined Mid (MidT + MidP scaled to half each) ───────────────────
    // MidT and MidP are each /100 — combined mid weight is split equally
    private double combinedMid(double midWeight) {
        // midT and midP each contribute half of the total mid weight
        return (mark.getMidT() * (midWeight / 2.0))
                + (mark.getMidP() * (midWeight / 2.0));
    }

    // ── CA Total (course-specific formula) ───────────────────────────────
    public double getCATotal() {
        String code = mark.getCCode();
        double best2 = getBest2QuizAvg();
        double ass   = mark.getAssignment1();
        double prj   = mark.getProject();

        return switch (code) {
            // CA = Project(20%) + Mid(20%) → max 40
            case "ICT2132", "ICT2152" ->
                    (prj * 0.20) + combinedMid(0.20);

            // CA = Best2Quiz(10%) + Mid(20%) → max 30
            case "ICT2122", "TCS2112" ->
                    (best2 * 0.10) + combinedMid(0.20);

            // CA = Best2Quiz(10%) + Assignment(20%) → max 30
            case "ICT2142" ->
                    (best2 * 0.10) + (ass * 0.20);

            // CA = Best2Quiz(10%) + Mid(30%) → max 40
            case "ICT2113" ->
                    (best2 * 0.10) + combinedMid(0.30);

            // CA = Assignment(10%) + Mid(20%) → max 30
            case "ENG2122" ->
                    (ass * 0.10) + combinedMid(0.20);

            // CA = Assignment(30%), End = Project(70%) → max 30
            case "TCS2122" ->
                    (ass * 0.30);

            // Default: Best2Quiz(10%) + Mid(20%) → max 30
            default ->
                    (best2 * 0.10) + combinedMid(0.20);
        };
    }

    // ── CA max weight as a fraction (used for eligibility check) ────────
    public double getCAMaxWeight() {
        return switch (mark.getCCode()) {
            case "ICT2132", "ICT2152", "ICT2113" -> 0.40;
            default                               -> 0.30;
        };
    }

    // ── CA max actual mark (0–100 scale) — used for % calculation ────────
    // e.g. weight 0.40 means CA can contribute max 40 marks out of 100
    public double getCAMaxMark() {
        return getCAMaxWeight() * 100.0;   // 0.40 → 40,  0.30 → 30
    }

    // ── End weight (fraction of final mark) ──────────────────────────────
    public double getEndWeight() {
        return switch (mark.getCCode()) {
            case "ICT2132", "ICT2113" -> 0.60;
            // ICT2152: P type → 0.60, T type → 0.70
            case "ICT2152" -> 0.60;   // this project has no C_type so treat as P
            default        -> 0.70;
        };
    }

    // ── Weighted end mark ─────────────────────────────────────────────────
    public double getEndWeighted() {
        switch (mark.getCCode()) {
            // TCS2122: end component is Project (70%)
            case "TCS2122" -> { return mark.getProject() * 0.70; }
            // ICT2132: Practicum course — end component is FinalP (60%)
            case "ICT2132" -> { return mark.getFinalP() * 0.60; }
            // All other courses use FinalT
            default        -> { return mark.getFinalT() * getEndWeight(); }
        }
    }

    // ── CA as % of the CA portion (0–100) ────────────────────────────────
    // e.g. CA=19.80, CAMax=40 → 19.80/40*100 = 49.5%
    public double getCAPercentage() {
        double caMax = getCAMaxMark();
        return caMax > 0 ? (getCATotal() / caMax) * 100.0 : 0.0;
    }

    // ── Final overall mark (CA + End weighted) out of 100 ────────────────
    public double getFinalMark() {
        return getCATotal() + getEndWeighted();
    }

    // ── Overall percentage = final mark (already 0–100) ──────────────────
    public double getOverallPercentage() { return getFinalMark(); }

    // ── CA eligibility: CA >= 40% of the CA portion ───────────────────────
    public boolean isCAEligible() {
        double caMax = getCAMaxWeight();
        return caMax > 0 && (getCATotal() / caMax) * 100.0 >= 40.0;
    }

    // ── End eligibility: raw end mark >= 40 ──────────────────────────────
    public boolean isEndEligible() {
        switch (mark.getCCode()) {
            case "TCS2122" -> { return mark.getProject()  >= 40.0; }
            case "ICT2132" -> { return mark.getFinalP()   >= 40.0; }  // Practicum end = FinalP
            default        -> { return mark.getFinalT()   >= 40.0; }
        }
    }

    // ── Letter grade from final mark (0–100, UGC 2024 boundaries) ────────
    public String getLetterGrade() {
        if (!isCAEligible() || !isEndEligible()) return "F";
        double pct = getOverallPercentage();
        if (pct >= 75) return "A+";
        if (pct >= 70) return "A";
        if (pct >= 65) return "A-";
        if (pct >= 60) return "B+";
        if (pct >= 55) return "B";
        if (pct >= 50) return "B-";
        if (pct >= 45) return "C+";
        if (pct >= 40) return "C";
        if (pct >= 35) return "C-";
        if (pct >= 30) return "D";
        return "F";
    }

    // ── GPA point (4.0 scale, UGC 2024) ──────────────────────────────────
    public double getGPA() {
        return switch (getLetterGrade()) {
            case "A+", "A" -> 4.0;
            case "A-"      -> 3.7;
            case "B+"      -> 3.3;
            case "B"       -> 3.0;
            case "B-"      -> 2.7;
            case "C+"      -> 2.3;
            case "C"       -> 2.0;
            case "C-"      -> 1.7;
            case "D"       -> 1.3;
            default        -> 0.0;
        };
    }

    // ── GPA classification label ──────────────────────────────────────────
    public static String classify(double gpa) {
        if (gpa >= 3.7) return "First Class";
        if (gpa >= 3.0) return "Second Class (Upper)";
        if (gpa >= 2.0) return "Second Class (Lower)";
        if (gpa >= 1.0) return "Pass";
        return "Fail";
    }

    public Mark getMark() { return mark; }
}
