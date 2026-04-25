package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class StyledTable extends JTable {

    public StyledTable(String[] columns, Object[][] data) {
        super(new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        applyStyle();
    }

    public StyledTable(String[] columns) {
        super(new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        applyStyle();
    }

    private void applyStyle() {
        setRowHeight(36);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UITheme.F_BODY);
        setForeground(UITheme.DARK_TEXT);
        setBackground(UITheme.CARD_BG);
        setSelectionBackground(UITheme.SEL_BG);
        setSelectionForeground(UITheme.SEL_FG);
        setGridColor(UITheme.ALMOND);
        setFocusable(false);

        JTableHeader header = getTableHeader();
        header.setFont(UITheme.F_BODY_B);
        header.setBackground(UITheme.RUSSET);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer lr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? UITheme.ROW_ODD : UITheme.ROW_EVEN);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
        lr.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < getColumnCount(); i++)
            getColumnModel().getColumn(i).setCellRenderer(lr);
    }

    public void setData(Object[][] data) {
        DefaultTableModel m = (DefaultTableModel) getModel();
        m.setRowCount(0);
        for (Object[] row : data) m.addRow(row);
    }

    public void addRow(Object[] row) { ((DefaultTableModel) getModel()).addRow(row); }
    public void clearRows()          { ((DefaultTableModel) getModel()).setRowCount(0); }
}
