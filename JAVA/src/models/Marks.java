// ─── Marks.java ──────────────────────────────────────────────────────────────
package models;

public class Marks {
    private int     markId;
    private String  ugId;
    private String  cCode;
    private String  cName;
    private Double  quiz01, quiz02, quiz03;
    private Double  assignment1, assignment2;
    private Double  project;
    private Double  midT, midP;
    private Double  finalT, finalP;
    private String  attemptType;

    public Marks(int markId, String ugId, String cCode, String cName,
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

    public String getCCode()       { return cCode;       }
    public String getCName()       { return cName;       }
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

    /** Best-effort total: sum of all non-null components. */
    public double getTotal() {
        double t = 0;
        if (quiz01      != null) t += quiz01;
        if (quiz02      != null) t += quiz02;
        if (quiz03      != null) t += quiz03;
        if (assignment1 != null) t += assignment1;
        if (assignment2 != null) t += assignment2;
        if (project     != null) t += project;
        if (midT        != null) t += midT;
        if (midP        != null) t += midP;
        if (finalT      != null) t += finalT;
        if (finalP      != null) t += finalP;
        return t;
    }

    public String fmt(Double v) { return v == null ? "—" : String.format("%.0f", v); }
}
