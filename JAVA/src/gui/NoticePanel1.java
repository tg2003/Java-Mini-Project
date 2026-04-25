package gui;

import models.*;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class NoticePanel1 extends JPanel {

    private final NoticeService svc = new NoticeService();
    private StyledTable table;

    public NoticePanel1() {
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("Notice Board"), BorderLayout.NORTH);

        String[] cols = {"#", "Title", "Date & Time", "Download"};
        table = new StyledTable(cols);
        loadData();
        add(UITheme.scrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        List<Notice> list = svc.getAll();
        table.clearRows();
        for (Notice n : list) {
            table.addRow(new Object[]{
                n.getNoticeNo(),
                n.getTitle(),
                DateFormatter.format(n.getDateTime()),
                n.hasDownload() ? "\uD83D\uDCE5  " + n.getDownloadLink() : "—"
            });
        }
    }
}
