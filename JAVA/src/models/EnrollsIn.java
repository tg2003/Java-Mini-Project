package models;

/**
 * EnrollsIn model class.
 * Maps to the ENROLLS_IN table in LMS_DB.
 *
 * DB columns mapped:
 *   Ug_id    -> ugId       (composite PK part 1, FK → UNDERGRADUATE)
 *   C_code   -> cCode      (composite PK part 2, FK → COURSE)
 *   Status   -> status     (ENUM: "Proper" | "Repeat" | "Batchmissed")
 *   Sem      -> sem        (TINYINT: semester number)
 *   Batch_yr -> batchYr    (INT: e.g. 2023)
 *   Level    -> level      (TINYINT: e.g. 1, 2, 3, 4)
 *
 * Used by the Lecturer to:
 *   - Know which students are enrolled in a course (for mark entry)
 *   - Check enrollment status for CA eligibility report
 *   - Filter by level/semester/batch for bulk operations
 */
public class EnrollsIn {

    // ── DB fields ──────────────────────────────────────────────────────────
    private String ugId;     // FK → UNDERGRADUATE(Ug_id)
    private String cCode;    // FK → COURSE(C_code)
    private String status;   // ENUM: "Proper" | "Repeat" | "Batchmissed"
    private int    sem;      // Semester number
    private int    batchYr;  // Batch year e.g. 2023
    private int    level;    // Study level e.g. 1, 2, 3, 4

    // ── Constructor ────────────────────────────────────────────────────────
    public EnrollsIn(String ugId, String cCode, String status,
                     int sem, int batchYr, int level) {
        this.ugId    = ugId;
        this.cCode   = cCode;
        this.status  = status;
        this.sem     = sem;
        this.batchYr = batchYr;
        this.level   = level;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getUgId()    { return ugId; }
    public String getCCode()   { return cCode; }
    public String getStatus()  { return status; }
    public int    getSem()     { return sem; }
    public int    getBatchYr() { return batchYr; }
    public int    getLevel()   { return level; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setStatus(String status)   { this.status  = status; }
    public void setSem(int sem)            { this.sem     = sem; }
    public void setBatchYr(int batchYr)    { this.batchYr = batchYr; }
    public void setLevel(int level)        { this.level   = level; }

    // ── Convenience: check if this is a Proper attempt ─────────────────────
    public boolean isProper()      { return "Proper".equals(status); }
    public boolean isRepeat()      { return "Repeat".equals(status); }
    public boolean isBatchMissed() { return "Batchmissed".equals(status); }

    // ── toString ───────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "EnrollsIn{ugId=" + ugId + ", course=" + cCode +
                ", status=" + status + ", sem=" + sem +
                ", batch=" + batchYr + ", level=" + level + "}";
    }
}

