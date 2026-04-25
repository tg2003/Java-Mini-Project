package models;

/**
 * Mark model class.
 * Maps exactly to the MARKS table in LMS_DB.
 *
 * DB columns mapped:
 *   Mark_id      -> markId        (AUTO_INCREMENT, set after DB insert)
 *   Ug_id        -> ugId          (FK → UNDERGRADUATE)
 *   C_code       -> cCode         (FK → COURSE)
 *   Quiz01       -> quiz01
 *   Quiz02       -> quiz02
 *   Quiz03       -> quiz03
 *   Assignment1  -> assignment1
 *   Assignment2  -> assignment2
 *   Project      -> project
 *   Mid_T        -> midT
 *   Mid_P        -> midP
 *   Final_T      -> finalT
 *   Final_P      -> finalP
 *   Attempt_type -> attemptType   (ENUM: "Proper" | "Repeat" | "MedicalRepeat")
 */
public class Mark {

    // ── Primary key (set by DB, 0 means not yet saved) ─────────────────────
    private int markId;

    // ── Foreign keys ───────────────────────────────────────────────────────
    private String ugId;
    private String cCode;

    // ── Mark components (all DECIMAL 5,2 in DB) ───────────────────────────
    private double quiz01;
    private double quiz02;
    private double quiz03;
    private double assignment1;
    private double assignment2;
    private double project;
    private double midT;           // Mid-term Theory
    private double midP;           // Mid-term Practical
    private double finalT;         // Final Theory
    private double finalP;         // Final Practical

    // ── Attempt type ───────────────────────────────────────────────────────
    private String attemptType;    // ENUM: "Proper" | "Repeat" | "MedicalRepeat"

    // ── Full constructor (used when loading from DB, markId is known) ───────
    public Mark(int markId, String ugId, String cCode,
                double quiz01, double quiz02, double quiz03,
                double assignment1, double assignment2, double project,
                double midT, double midP,
                double finalT, double finalP,
                String attemptType) {
        this.markId      = markId;
        this.ugId        = ugId;
        this.cCode       = cCode;
        this.quiz01      = quiz01;
        this.quiz02      = quiz02;
        this.quiz03      = quiz03;
        this.assignment1 = assignment1;
        this.assignment2 = assignment2;
        this.project     = project;
        this.midT        = midT;
        this.midP        = midP;
        this.finalT      = finalT;
        this.finalP      = finalP;
        this.attemptType = attemptType;
    }

    // ── Insert constructor (markId not known yet — DB will assign it) ───────
    public Mark(String ugId, String cCode,
                double quiz01, double quiz02, double quiz03,
                double assignment1, double assignment2, double project,
                double midT, double midP,
                double finalT, double finalP,
                String attemptType) {
        this(0, ugId, cCode,
                quiz01, quiz02, quiz03,
                assignment1, assignment2, project,
                midT, midP, finalT, finalP, attemptType);
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int    getMarkId()      { return markId; }
    public String getUgId()        { return ugId; }
    public String getCCode()       { return cCode; }
    public double getQuiz01()      { return quiz01; }
    public double getQuiz02()      { return quiz02; }
    public double getQuiz03()      { return quiz03; }
    public double getAssignment1() { return assignment1; }
    public double getAssignment2() { return assignment2; }
    public double getProject()     { return project; }
    public double getMidT()        { return midT; }
    public double getMidP()        { return midP; }
    public double getFinalT()      { return finalT; }
    public double getFinalP()      { return finalP; }
    public String getAttemptType() { return attemptType; }

    // ── Setters (used when updating marks) ─────────────────────────────────
    public void setMarkId(int markId)           { this.markId = markId; }
    public void setQuiz01(double quiz01)        { this.quiz01 = quiz01; }
    public void setQuiz02(double quiz02)        { this.quiz02 = quiz02; }
    public void setQuiz03(double quiz03)        { this.quiz03 = quiz03; }
    public void setAssignment1(double v)        { this.assignment1 = v; }
    public void setAssignment2(double v)        { this.assignment2 = v; }
    public void setProject(double project)      { this.project = project; }
    public void setMidT(double midT)            { this.midT = midT; }
    public void setMidP(double midP)            { this.midP = midP; }
    public void setFinalT(double finalT)        { this.finalT = finalT; }
    public void setFinalP(double finalP)        { this.finalP = finalP; }
    public void setAttemptType(String type)     { this.attemptType = type; }

    // ── toString (useful for debugging / printing in tables) ───────────────
    @Override
    public String toString() {
        return "Mark{id=" + markId + ", ugId=" + ugId + ", course=" + cCode +
                ", q1=" + quiz01 + ", q2=" + quiz02 + ", q3=" + quiz03 +
                ", a1=" + assignment1 + ", a2=" + assignment2 +
                ", proj=" + project + ", midT=" + midT + ", midP=" + midP +
                ", finalT=" + finalT + ", finalP=" + finalP +
                ", attempt=" + attemptType + "}";
    }
}