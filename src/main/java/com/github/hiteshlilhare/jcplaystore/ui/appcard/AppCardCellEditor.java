/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.appcard;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Hitesh
 */
public class AppCardCellEditor extends AbstractCellEditor implements TableCellEditor{
    private AppCard appCard;
    public AppCardCellEditor() {
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        appCard = (AppCard) value;
        return appCard;
    }

    @Override
    public Object getCellEditorValue() {
        return appCard;
    }
}
