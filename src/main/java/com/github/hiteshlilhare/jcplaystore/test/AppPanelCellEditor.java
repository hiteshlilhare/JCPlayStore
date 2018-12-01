/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.test;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Hitesh
 */
public class AppPanelCellEditor extends AbstractCellEditor implements TableCellEditor{
    AppPanel appPanel;
    public AppPanelCellEditor() {
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        appPanel = (AppPanel) value;
        return appPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return appPanel;
    }
}
