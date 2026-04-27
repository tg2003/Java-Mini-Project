package models;

/**
 * This class stores student course details.

 * Used to check student info and enrollment.
 */
public class EnrollsIn {


    private String ugId;
    private String cCode;
    private String status;
    private int    sem;
    private int    batchYr;
    private int    level;

    // Constructor
    public EnrollsIn(String ugId, String cCode, String status,
                     int sem, int batchYr, int level) {
        this.ugId    = ugId;
        this.cCode   = cCode;
        this.status  = status;
        this.sem     = sem;
        this.batchYr = batchYr;
        this.level   = level;
    }

    public String getUgId()    { return ugId; }
    public String getCCode()   { return cCode; }
    public String getStatus()  { return status; }
    public int    getSem()     { return sem; }
    public int    getBatchYr() { return batchYr; }
    public int    getLevel()   { return level; }


    public void setStatus(String status)   { this.status  = status; }
    public void setSem(int sem)            { this.sem     = sem; }
    public void setBatchYr(int batchYr)    { this.batchYr = batchYr; }
    public void setLevel(int level)        { this.level   = level; }

    // student type
    public boolean isProper()      { return "Proper".equals(status); }
    public boolean isRepeat()      { return "Repeat".equals(status); }
    public boolean isBatchMissed() { return "Batchmissed".equals(status); }

    // print
    @Override
    public String toString() {
        return "EnrollsIn{ugId=" + ugId + ", course=" + cCode +
                ", status=" + status + ", sem=" + sem +
                ", batch=" + batchYr + ", level=" + level + "}";
    }
}

