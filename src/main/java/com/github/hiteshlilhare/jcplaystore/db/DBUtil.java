package com.github.hiteshlilhare.jcplaystore.db;

import apdu4j.HexUtils;
import com.github.hiteshlilhare.jcplaystore.CardDetails;
import com.github.hiteshlilhare.jcplaystore.CardDetailsTableFields;
import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.ui.util.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import pro.javacard.gp.GPData;
import static pro.javacard.gp.GPData.CPLC.toDateFailsafe;

/**
 *
 * @author Hitesh
 */
public class DBUtil {

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(DBUtil.class);
    private final static String JC_DB_FILE = "jcsqlite.db";
    private final static String DB_URL = "jdbc:sqlite:"
            + JCConstants.JC_APP_BASE_DIR + "/"
            + JCConstants.JC_DB_DIR + "/" 
            + JC_DB_FILE;

    public static void connectAndCreateTableIfNotExists() {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            Statement statement = conn.createStatement();
            int ret = statement.executeUpdate("create table if not exists card_details (ICFabricator varchar(50),ICSerialNumber varchar(20),ICType varchar(20),OperatingSystemID varchar(20),OperatingSystemReleaseDate varchar(20),OperatingSystemReleaseLevel varchar(20),ICFabricationDate varchar(20),ICBatchIdentifier varchar(20),ICModuleFabricator varchar(20),ICModulePackagingDate varchar(20),ICCManufacturer varchar(20),ICEmbeddingDate varchar(20),ICPrePersonalizer varchar(20),ICPrePersonalizationEquipmentDate varchar(20),ICPrePersonalizationEquipmentID varchar(20),ICPersonalizer varchar(20),ICPersonalizationDate varchar(20),ICPersonalizationEquipmentID varchar(20),ATR varchar(50),INN varchar(50),CIN varchar(50),CardData varchar(200),CardCapability varchar(200),KeyInfo varchar(200),PRIMARY KEY (ICFabricator,ICSerialNumber,ICType))");
            if (ret == 0) {
                System.out.println("card_details table created successfully!!!");
            } else {
                System.out.println("Failed to create card_details table ");
            }
            ret = statement.executeUpdate("create table if not exists card_app (ICFabricator varchar(50),ICSerialNumber varchar(20),ICType varchar(20),AID varchar(50),Version varchar(10),Description varchar(100),PRIMARY KEY (AID,Version,ICFabricator,ICSerialNumber,ICType),FOREIGN KEY (ICFabricator,ICSerialNumber,ICType) REFERENCES card_details (ICFabricator,ICSerialNumber,ICType) ON DELETE CASCADE ON UPDATE NO ACTION)");
            if (ret == 0) {
                System.out.println("card_app table created successfully!!!");
            } else {
                System.out.println("Failed to create card_app table ");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void insertCardDetailsIfNotExists() {
        Connection conn = null;
        try {
            // create a connection to the database
            CardDetails cardDetails = Util.getCardDetails();
            GPData.CPLC cplc = cardDetails.getCplc();
            if (cplc == null) {
                System.out.println("No CPLC data");
                return;
            }
            String selectQuery = "select * from card_details where ICFabricator='" + HexUtils.bin2hex(cplc.get(GPData.CPLC.Field.ICFabricator)) + "' and ICSerialNumber='" + HexUtils.bin2hex(cplc.get(GPData.CPLC.Field.ICSerialNumber)) + "' and ICType='" + HexUtils.bin2hex(cplc.get(GPData.CPLC.Field.ICType)) + "'";
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);
            if (rs.next()) {
                System.out.println("Card details already present:" + rs.getString(GPData.CPLC.Field.ICFabricationDate.toString()));
            } else {
                String strColNames = Arrays.asList(CardDetailsTableFields.values()).stream().map((CardDetailsTableFields i) -> i.toString()).collect(Collectors.joining(","));
                String strColValues = Arrays.asList(GPData.CPLC.Field.values()).stream().map((GPData.CPLC.Field i) -> (i.toString().endsWith("Date") ? "'" + toDateFailsafe(cplc.get(i)) + "'" : "'" + HexUtils.bin2hex(cplc.get(i)) + "'")).collect(Collectors.joining(", "));
                strColValues += ", '" + HexUtils.bin2hex(cardDetails.getAtr().getBytes()) + "', "
                        + (cardDetails.getInn() != null ? "'" + HexUtils.bin2hex(cardDetails.getInn()) + "'" : null) + ", "
                        + (cardDetails.getCin() != null ? "'" + HexUtils.bin2hex(cardDetails.getCin()) + "'" : null) + ", "
                        + (cardDetails.getCardData() != null ? "'" + HexUtils.bin2hex(cardDetails.getCardData()) + "'" : null) + ", "
                        + (cardDetails.getCardCapabilities() != null ? "'" + HexUtils.bin2hex(cardDetails.getCardCapabilities()) + "'" : null) + ", "
                        + (cardDetails.getKeyInfo() != null ? "'" + HexUtils.bin2hex(cardDetails.getKeyInfo()) + "'" : null);
                String insertQuery = "insert into card_details (" + strColNames + ") values (" + strColValues + ")";
                System.out.println(insertQuery);
                int ret = statement.executeUpdate(insertQuery);
                if (ret == 1) {
                    System.out.println("Record inserted into card_details table successfully!!!");
                } else {
                    System.out.println("Failed to insert record");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
