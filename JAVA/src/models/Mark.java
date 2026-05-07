package models;


public class Mark {

    private int markId;


    private String ugId;
    private String cCode;

    private double quiz01;
    private double quiz02;
    private double quiz03;
    private double assignment1;
    private double assignment2;
    private double project;
    private double midT;
    private double midP;
    private double finalT;
    private double finalP;


    private String attemptType;

    // constructor
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