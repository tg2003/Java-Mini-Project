package models;

import java.sql.Date;


public class Attendance {


    private int    weekNo;
    private String ugId;
    private int    timetableId;
    private String techOffId;       // officer who marked attendance
    private String status;          // Present / Absent / MedicalApproved / MedicalDeclined


    private Date   date;
    private String day;
    private String startTime;
    private String endTime;
    private String cCode;
    private String cName;
    private String sessionType;     // Theory / Practical


    public Attendance(int weekNo, String ugId, int timetableId, Date date,
                      String techOffId, String status,
                      String day, String startTime, String endTime,
                      String cCode, String cName, String sessionType) {
        this.weekNo      = weekNo;
        this.ugId        = ugId;
        this.timetableId = timetableId;
        this.date        = date;
        this.techOffId   = techOffId;
        this.status      = status;
        this.day         = day;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.cCode       = cCode;
        this.cName       = cName;
        this.sessionType = sessionType;
    }


    public Attendance(int weekNo, String ugId, int timetableId,
                      String date, String techOffId, String status) {
        this.weekNo      = weekNo;
        this.ugId        = ugId;
        this.timetableId = timetableId;
        this.date        = date != null ? Date.valueOf(date) : null;
        this.techOffId   = techOffId;
        this.status      = status;
    }


    public Attendance(int weekNo, String ugId, int timetableId, Date date,
                      String status, String day, String startTime, String endTime,
                      String cCode, String cName, String sessionType) {
        this.weekNo      = weekNo;
        this.ugId        = ugId;
        this.timetableId = timetableId;
        this.date        = date;
        this.status      = status;
        this.day         = day;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.cCode       = cCode;
        this.cName       = cName;
        this.sessionType = sessionType;
    }

    // getters
    public int    getWeekNo()      { return weekNo;      }
    public String getUgId()        { return ugId;        }
    public int    getTimetableId() { return timetableId; }
    public Date   getDate()        { return date;        }
    public String getTechOffId()   { return techOffId;   }
    public String getStatus()      { return status;      }
    public String getDay()         { return day;         }
    public String getStartTime()   { return startTime;   }
    public String getEndTime()     { return endTime;     }
    public String getCCode()       { return cCode;       }
    public String getCName()       { return cName;       }
    public String getSessionType() { return sessionType; }

    //setters
    public void setStatus(String status) { this.status = status; }


    public String getTimeSlot() {
        return (startTime != null ? startTime : "?") + " – " + (endTime != null ? endTime : "?");
    }
    public double getDurationHours() {
        if (startTime == null || endTime == null) return 0.0;
        try {
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            long minutes = java.time.Duration.between(start, end).toMinutes();
            return Math.max(0L, minutes) / 60.0;
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
