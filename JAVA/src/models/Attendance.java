package models;

public class Attendance {
    private int weekNo;
    private String ugId;
    private int timetableId;
    private String date;
    private String techOffId;
    private String status;

    public Attendance(int weekNo, String ugId, int timetableId, String date, String techOffId, String status) {
        this.weekNo = weekNo;
        this.ugId = ugId;
        this.timetableId = timetableId;
        this.date = date;
        this.techOffId = techOffId;
        this.status = status;
    }

    public int getWeekNo() {
        return weekNo;
    }

    public String getUgId() {
        return ugId;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public String getDate() {
        return date;
    }

    public String getTechOffId() {
        return techOffId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
