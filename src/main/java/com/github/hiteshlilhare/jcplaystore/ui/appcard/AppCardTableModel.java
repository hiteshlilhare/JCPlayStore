/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.appcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hitesh
 */
public class AppCardTableModel extends DefaultTableModel {

    /**
     * 
     * @param columnIndex
     * @return 
     */
    @Override
    public Class getColumnClass(int columnIndex) {
        return AppCard.class;
    }
    /**
     * 
     * @param columnIndex
     * @return 
     */
    @Override
    public String getColumnName(int columnIndex) {
        return "APP";
    }

    public Object getValueAt(int rowIndex, int columnIndex) { 
        Vector data=getDataVector(); 
        return (data == null) ? null : (AppCard)(((Vector)(data.elementAt(rowIndex))).get(columnIndex));
        //return null;
    }
    @Override
    public boolean isCellEditable(int columnIndex, int rowIndex) {
        return true;
    }

    public void addColumn(AppCard ap) {
        List colData = new ArrayList();
        colData.add(ap);
        super.addColumn("APP", colData.toArray());
    }

}
