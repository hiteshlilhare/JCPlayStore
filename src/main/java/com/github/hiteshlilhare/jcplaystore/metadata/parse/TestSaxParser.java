package com.github.hiteshlilhare.jcplaystore.metadata.parse;

import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TestSaxParser {

    public static void main(String[] args) throws FileNotFoundException {
        //Locate the file
        File xmlFile = new File("E:\\Sem-2\\Thesis\\DIAT-PUNE\\sax\\jcpkiapplet.xml");

        //Create the parser instance
        CardAppXmlParser parser = new CardAppXmlParser();

        //Parse the file
        CardAppMetaData appMetaData = parser.parseXml(new FileInputStream(xmlFile));

        //Verify the result
        System.out.println(appMetaData);
    }
}
