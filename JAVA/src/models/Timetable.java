package models;

public class Timetable {
    private int    timetableId;
    private String day;
    private String startTime;
    private String endTime;
    private String cCode;
    private String cName;
    private String type;   // Theory / Practical

    public Timetable(int timetableId, String day, String startTime, String endTime,
                     String cCode, String cName, String type) {
        this.timetableId = timetableId;
        this.day         = day;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.cCode       = cCode;
        this.cName       = cName;
        this.type        = type;
    }

    public int    getTimetableId() { return timetableId; }
    public String getDay()         { return day;         }
    public String getStartTime()   { return startTime;   }
    public String getEndTime()     { return endTime;     }
    public String getCCode()       { return cCode;       }
    public String getCName()       { return cName;       }
    public String getType()        { return type;        }
    public String getTimeSlot()    { return startTime + " – " + endTime; }
}
