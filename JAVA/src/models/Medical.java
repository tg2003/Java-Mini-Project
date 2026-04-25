package models;

import java.sql.Date;

/**
 * Merged Medical model.
 * Combines fields from both the original models.Medical
 * (fromDate/toDate as String, techOffId, cCode)
 * and the richer model.Medical
 * (fromDate/toDate as Date, officerName added).
 */
public class Medical {

    // ── Core fields (present in both versions) ─────────────────────────────
    private int    medicalId;
    private String ugId;
    private Date   fromDate;
    private Date   toDate;
    private String status;          // Pending / MedicalApproved / MedicalDeclined
    private String sessionType;     // Lecture / Exam  (was Theory/Practical in old)
    private String techoffId;       // unified spelling: techoffId
    private String cCode;

    // ── Extra field (from model.Medical only) ──────────────────────────────
    private String officerName;     // display name of the tech officer

    // ── Constructor 1: Full constructor (all fields, Date types) ───────────
    public Medical(int medicalId, String ugId, Date fromDate, Date toDate,
                   String status, String sessionType,
                   String techoffId, String officerName, String cCode) {
        this.medicalId   = medicalId;
        this.ugId        = ugId;
        this.fromDate    = fromDate;
        this.toDate      = toDate;
        this.status      = status;
        this.sessionType = sessionType;
        this.techoffId   = techoffId;
        this.officerName = officerName;
        this.cCode       = cCode;
    }

    // ── Constructor 2: Legacy constructor (String dates, no officerName) ───
    // Matches original models.Medical signature exactly.
    public Medical(int medicalId, String ugId, String fromDate, String toDate,
                   String status, String sessionType,
                   String techOffId, String cCode) {
        this.medicalId   = medicalId;
        this.ugId        = ugId;
        this.fromDate    = fromDate != null ? Date.valueOf(fromDate) : null;
        this.toDate      = toDate   != null ? Date.valueOf(toDate)   : null;
        this.status      = status;
        this.sessionType = sessionType;
        this.techoffId   = techOffId;
        this.cCode       = cCode;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int    getMedicalId()   { return medicalId;   }
    public String getUgId()        { return ugId;        }
    public Date   getFromDate()    { return fromDate;    }
    public Date   getToDate()      { return toDate;      }
    public String getStatus()      { return status;      }
    public String getSessionType() { return sessionType; }
    public String getTechoffId()   { return techoffId;   }
    public String getOfficerName() { return officerName != null ? officerName : "—"; }
    public String getCCode()       { return cCode       != null ? cCode       : "—"; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setFromDate(String fromDate)   { this.fromDate    = fromDate != null ? Date.valueOf(fromDate) : null; }
    public void setFromDate(Date fromDate)     { this.fromDate    = fromDate;    }
    public void setToDate(String toDate)       { this.toDate      = toDate   != null ? Date.valueOf(toDate)   : null; }
    public void setToDate(Date toDate)         { this.toDate      = toDate;      }
    public void setStatus(String status)       { this.status      = status;      }
    public void setSessionType(String type)    { this.sessionType = type;        }
}
