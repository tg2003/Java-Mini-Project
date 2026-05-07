package models;

public class Course {
    private String code;
    private String name;
    private String credit;
    private String enrollStatus;
    private int    sem;
    private int    batchYear;
    private int    level;

    public Course(String code, String name, String credit,
                  String enrollStatus, int sem, int batchYear, int level) {
        this.code         = code;
        this.name         = name;
        this.credit       = credit;
        this.enrollStatus = enrollStatus;
        this.sem          = sem;
        this.batchYear    = batchYear;
        this.level        = level;
    }

    public String getCode()         { return code;         }
    public String getName()         { return name;         }
    public String getCredit()       { return credit;       }
    public String getEnrollStatus() { return enrollStatus; }
    public int    getSem()          { return sem;          }
    public int    getBatchYear()    { return batchYear;    }
    public int    getLevel()        { return level;        }
}
