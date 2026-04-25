package util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateFormatter {
    private static final SimpleDateFormat DATE_FMT  = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat DT_FMT    = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    private static final SimpleDateFormat SHORT_FMT = new SimpleDateFormat("yyyy-MM-dd");

    public static String format(Date d)       { return d  == null ? "—" : DATE_FMT.format(d); }
    public static String format(Timestamp ts) { return ts == null ? "—" : DT_FMT.format(ts);  }
    public static String shortFormat(Date d)  { return d  == null ? "—" : SHORT_FMT.format(d);}
}
