package models;

public class Medical {
    private int medicalId;
    private String ugId;
    private String fromDate;
    private String toDate;
    private String status;
    private String sessionType;
    private String techOffId;
    private String cCode;




            public Medical(int medicalId, String ugId, String fromDate, String toDate, String status, String sessionType, String techOffId, String cCode) {
                this.medicalId = medicalId;
                this.ugId = ugId;
                this.fromDate = fromDate;
                this.toDate = toDate;
                this.status = status;
                this.sessionType = sessionType;
                this.techOffId = techOffId;
                this.cCode = cCode;
            }

            public int getMedicalId() {
                return medicalId;
            }

            public String getUgId() {
                return ugId;
            }

            public String getFromDate() {
                return fromDate;
            }

            public String getToDate() {
                return toDate;
            }

            public String getStatus() {
                return status;
            }

            public String getSessionType() {
                return sessionType;
            }

            public String getTechOffId() {
                return techOffId;
            }

            public String getcCode() {
                return cCode;
            }

            public void setToDate(String toDate) {
                this.toDate = toDate;
            }

            public void setFromDate(String fromDate) {
                this.fromDate = fromDate;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setSessionType(String sessionType) {
                this.sessionType = sessionType;
            }
        }


