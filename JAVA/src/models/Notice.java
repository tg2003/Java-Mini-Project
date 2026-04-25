// ─── Notice.java ─────────────────────────────────────────────────────────────
package models;
import java.sql.Timestamp;

public class Notice {
    private int       noticeNo;
    private String    title;
    private Timestamp dateTime;
    private String    downloadLink;

    public Notice(int noticeNo, String title, Timestamp dateTime, String downloadLink) {
        this.noticeNo     = noticeNo;
        this.title        = title;
        this.dateTime     = dateTime;
        this.downloadLink = downloadLink;
    }

    public int       getNoticeNo()     { return noticeNo;     }
    public String    getTitle()        { return title;        }
    public Timestamp getDateTime()     { return dateTime;     }
    public String    getDownloadLink() { return downloadLink; }
    public boolean   hasDownload()     { return downloadLink != null && !downloadLink.isBlank(); }
}
