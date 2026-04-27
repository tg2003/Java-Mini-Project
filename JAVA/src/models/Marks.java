package models;

public class Marks {
    private int     markId;
    private String  ugId;
    private String  cCode;
    private String  cName;
    private String  credit;
    private Double  quiz01, quiz02, quiz03;
    private Double  assignment1, assignment2;
    private Double  project;
    private Double  midT, midP;
    private Double  finalT, finalP;
    private String  attemptType;

    public Marks(int markId, String ugId, String cCode, String cName, String credit,
                 Double quiz01, Double quiz02, Double quiz03,
                 Double assignment1, Double assignment2,
                 Double project,
                 Double midT, Double midP,
                 Double finalT, Double finalP,
                 String attemptType) {
        this.markId      = markId;
        this.ugId        = ugId;
        this.cCode       = cCode;
        this.cName       = cName;
        this.credit      = credit;
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

    public int getMarkId()         { return markId;      }
    public String getUgId()        { return ugId;        }
    public String getCCode()       { return cCode;       }
    public String getCName()       { return cName;       }
    public String getCredit()      { return credit;      }
    public String getAttemptType() { return attemptType; }
    public Double getQuiz01()      { return quiz01;      }
    public Double getQuiz02()      { return quiz02;      }
    public Double getQuiz03()      { return quiz03;      }
    public Double getAssignment1() { return assignment1; }
    public Double getAssignment2() { return assignment2; }
    public Double getProject()     { return project;     }
    public Double getMidT()        { return midT;        }
    public Double getMidP()        { return midP;        }
    public Double getFinalT()      { return finalT;      }
    public Double getFinalP()      { return finalP;      }

    public double getCreditValue() {
        if (credit == null || credit.isBlank()) return 0.0;
        try {
            return Double.parseDouble(credit.trim());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    public boolean hasCaComponents() {
        return countOf(quiz01, quiz02, quiz03, assignment1, assignment2, project) > 0;
    }

    public boolean hasEndComponents() {
        return countOf(midT, midP, finalT, finalP) > 0;
    }

    public double getCaAverage() {
        return averageOf(quiz01, quiz02, quiz03, assignment1, assignment2, project);
    }

    public double getEndAverage() {
        return averageOf(midT, midP, finalT, finalP);
    }

    public double getCaTotal() {
        return getCaAverage() * 0.40;
    }

    public double getEndTotal() {
        return getEndAverage() * 0.60;
    }

    public double getTotal() {
        return getCaTotal() + getEndTotal();
    }

    public boolean isAttendanceEligible(double attendancePct) {
        return attendancePct >= 80.0;
    }

    public boolean isCaEligible() {
        return !hasCaComponents() || getCaTotal() >= 20.0;
    }

    public boolean isEndEligible() {
        return !hasEndComponents() || getEndTotal() >= 30.0;
    }

    public boolean isFullyEligible(double attendancePct) {
        return isAttendanceEligible(attendancePct) && isCaEligible() && isEndEligible();
    }

    private double averageOf(Double... values) {
        int count = 0;
        double total = 0.0;
        for (Double value : values) {
            if (value != null) {
                total += value;
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    private int countOf(Double... values) {
        int count = 0;
        for (Double value : values) {
            if (value != null) count++;
        }
        return count;
    }

    public String fmt(Double v) {
        return v == null ? "-" : String.format("%.0f", v);
    }
}
