/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.test;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Hitesh
 */
public class AppPanelCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        AppPanel appPanel = (AppPanel) value;
        //Required to set the width of column.
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
//        System.out.println("column count = " + count);
        for (int i = 0; i < count; i++) {
            columnModel.getColumn(i).setPreferredWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMaxWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMinWidth(AppPanel.WIDTH);
        }
        //appPanel.setBackground(Color.yellow);
        return appPanel;
    }
}
