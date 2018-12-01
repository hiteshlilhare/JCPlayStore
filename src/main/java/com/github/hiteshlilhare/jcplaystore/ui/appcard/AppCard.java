/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.appcard;

import com.github.hiteshlilhare.jcplaystore.StarRater;
import com.github.hiteshlilhare.jcplaystore.ui.util.RoundedPanel;
import info.clearthought.layout.TableLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Hitesh
 */
public class AppCard extends RoundedPanel{
    public AppCard(){
        super(200, 250);
        initialize();
        double size[][]
                = {{10, TableLayout.FILL, 10}, // Columns
                {10, 75, 10, TableLayout.FILL, 10, 20, 10}}; // Rows
        setLayout(new TableLayout(size));
        add(iconLabel,"1,1");
        add(shortDescription,"1,3");
        add(starRater,"1,5");
        //setVisible(true);
        handleRating();
    }
    
    private void handleRating() {
        starRater.addStarListener(new StarRater.StarListener() {
            @Override
            public void handleSelection(int selection) {
                System.out.println("Star Rater is clickied: selection=" +selection);
            }
        });
    }
    
    private void initialize(){
        setBackground(new java.awt.Color(255, 255, 255));
        iconLabel = new JLabel();
        iconLabel.setFont(new java.awt.Font("Segoe UI", 0, 14));
        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/security_app_1.png")));
        
        shortDescription = new JTextArea();
        shortDescription.setFont(new java.awt.Font("Segoe UI", 0, 14));
        shortDescription.setText("Java Card applet");
        shortDescription.setEditable(false);
        
        starRater = new StarRater();
    }
    private JLabel iconLabel;
    private JTextArea shortDescription;
    private StarRater starRater;
}
