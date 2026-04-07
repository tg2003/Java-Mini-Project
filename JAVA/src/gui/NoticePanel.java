package gui;

import javax.swing.*;

public class NoticePanel extends JPanel{
    private JPanel noticesMainPanel;
    private JButton viewNoticesButton;
    private JButton createANewNoticeButton;
    private JButton editNoticesButton;

    public NoticePanel(){
        add(noticesMainPanel);

        viewNoticesButton.addActionListener(e->{
            new NoticeView().setVisible(true);
        });

        editNoticesButton.addActionListener(e->{
            new NoticeView("Admin").setVisible(true);
        });

        createANewNoticeButton.addActionListener(e->{
            new NoticeCreate().setVisible(true);
        });
    }


}
