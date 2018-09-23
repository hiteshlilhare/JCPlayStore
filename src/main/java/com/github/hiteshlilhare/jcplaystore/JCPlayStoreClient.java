/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

import apdu4j.HexUtils;
import apdu4j.TerminalManager;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import pro.javacard.AID;
import pro.javacard.CAPFile;
import pro.javacard.gp.GPData;
import static pro.javacard.gp.GPData.CPLC.toDateFailsafe;
import static pro.javacard.gp.GPData.fetchCPLC;
import static pro.javacard.gp.GPData.fetchKeyInfoTemplate;
import static pro.javacard.gp.GPData.getData;
import static pro.javacard.gp.GPData.pretty_print_card_capabilities;
import static pro.javacard.gp.GPData.pretty_print_card_data;
import static pro.javacard.gp.GPData.pretty_print_key_template;
import pro.javacard.gp.GPDataException;
import pro.javacard.gp.GPException;
import pro.javacard.gp.GPKey;
import pro.javacard.gp.GPRegistry;
import pro.javacard.gp.GPRegistryEntry;
import pro.javacard.gp.GPRegistryEntry.Privilege;
import pro.javacard.gp.GPRegistryEntry.Privileges;
import pro.javacard.gp.GPRegistryEntryApp;
import pro.javacard.gp.GPRegistryEntryPkg;
import pro.javacard.gp.GPSessionKeyProvider;
import pro.javacard.gp.GPUtils;
import pro.javacard.gp.GlobalPlatform;
import pro.javacard.gp.GlobalPlatform.GPSpec;
import pro.javacard.gp.PlaintextKeys;
import static pro.javacard.gp.PlaintextKeys.Diversification.EMV;
import static pro.javacard.gp.PlaintextKeys.Diversification.KDF3;
import static pro.javacard.gp.PlaintextKeys.Diversification.VISA2;
import pro.javacard.gp.PythiaKeys;

/**
 *
 * @author Hitesh
 */
public class JCPlayStoreClient extends javax.swing.JFrame {

    private final static String OPT_ALLOW_TO = "allow-to";
    private final static String OPT_ALLOW_FROM = "allow-from";
    private final static String OPT_APDU = "apdu";
    private final static String OPT_APPLET = "applet"; // can always be shortened, so -app is valid
    private final static String OPT_BS = "bs";
    private final static String OPT_CAP = "cap";
    private final static String OPT_CREATE = "create";
    private final static String OPT_DEBUG = "debug";
    private final static String OPT_DEFAULT = "default";
    private final static String OPT_DELETE = "delete";
    private final static String OPT_DELETE_KEY = "delete-key";
    private final static String OPT_DOMAIN = "domain";
    private final static String OPT_MOVE = "move";
    private final static String OPT_DUMP = "dump";
    private final static String OPT_EMV = "emv";
    private final static String OPT_FORCE = "force";
    private final static String OPT_INFO = "info";
    private final static String OPT_INITIALIZE_CARD = "initialize-card";
    private final static String OPT_INSTALL = "install";
    private final static String OPT_KCV = "kcv";
    private final static String OPT_KDF3 = "kdf3";
    private final static String OPT_KEY = "key";
    private final static String OPT_KEYS = "keys";
    private final static String OPT_KEY_ENC = "key-enc";
    private final static String OPT_KEY_ID = "key-id";
    private final static String OPT_KEY_DEK = "key-dek";
    private final static String OPT_KEY_MAC = "key-mac";
    private final static String OPT_KEY_VERSION = "key-ver";
    private final static String OPT_LIST = "list";
    private final static String OPT_LIST_PRIVS = "list-privs";
    private final static String OPT_LOAD = "load";
    private final static String OPT_LOCK = "lock";
    private final static String OPT_LOCK_ENC = "lock-enc";
    private final static String OPT_LOCK_MAC = "lock-mac";
    private final static String OPT_LOCK_DEK = "lock-dek";
    private final static String OPT_LOCK_APPLET = "lock-applet";
    private final static String OPT_LOCK_CARD = "lock-card";
    private final static String OPT_MAKE_DEFAULT = "make-default";
    private final static String OPT_NEW_KEY_VERSION = "new-keyver";
    private final static String OPT_OP201 = "op201";
    private final static String OPT_PACKAGE = "package";
    private final static String OPT_PARAMS = "params";
    private final static String OPT_PRIVS = "privs";
    private final static String OPT_PUT_KEY = "put-key";
    private final static String OPT_READER = "reader";
    private final static String OPT_RENAME_ISD = "rename-isd";
    private final static String OPT_REPLAY = "replay";
    private final static String OPT_SC_MODE = "mode";
    private final static String OPT_SDAID = "sdaid";
    private final static String OPT_SECURE_APDU = "secure-apdu";
    private final static String OPT_SECURE_CARD = "secure-card";
    private final static String OPT_SET_PRE_PERSO = "set-pre-perso";
    private final static String OPT_SET_PERSO = "set-perso";
    private final static String OPT_SHA256 = "sha256";
    private final static String OPT_STORE_DATA = "store-data";
    private final static String OPT_TERMINALS = "terminals";
    private final static String OPT_TERMINATE = "terminate";
    private final static String OPT_TODAY = "today";
    private final static String OPT_TO = "to";
    private final static String OPT_UNINSTALL = "uninstall";
    private final static String OPT_UNLOCK = "unlock";
    private final static String OPT_UNLOCK_APPLET = "unlock-applet";
    private final static String OPT_UNLOCK_CARD = "unlock-card";
    private final static String OPT_VERBOSE = "verbose";
    private final static String OPT_VERSION = "version";
    private final static String OPT_VISA2 = "visa2";
    private final static String OPT_ORACLE = "oracle";
    private final static String OPT_ACR_LIST = "acr-list";
    private final static String OPT_ACR_ADD = "acr-add";
    private final static String OPT_ACR_DELETE = "acr-delete";
    private final static String OPT_ACR_RULE = "acr-rule";
    private final static String OPT_ACR_CERT_HASH = "acr-hash";

    /**
     * Instance variables added.
     */
    private OptionSet args = null;
    // GlobalPlatform specific
    private GlobalPlatform gp = null;
    private TreeMap<String, CardTerminal> cardReaderMap = new TreeMap<>();
    private final static String CMD_CONNECT = "Connect";
    private final static String CMD_LIST = "List";
    private final static String CMD_INSTALL = "Install";
    private final static String CMD_DELETE = "Delete";
    private String[] commandList = new String[]{CMD_CONNECT, CMD_LIST, CMD_INSTALL, CMD_DELETE};

    private final static String DEF_AID_COMBO_TXT = "<Please provide AID>";
    private final static String DEF_CAP_FILE_COMBO_TXT = "<Please provide CAP file path>";

    // Database related constants.
    private final static String JC_APP_DIR = "JCAPPStore";
    private final static String JC_DB_FILE = "jcsqlite.db";
    private final static String DB_URL = "jdbc:sqlite:" + FileSystemView.getFileSystemView().getDefaultDirectory() + "/" + JC_APP_DIR + "/" + JC_DB_FILE;

    /**
     * Creates new form JCPlayStoreClient
     */
    public JCPlayStoreClient() {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // handle exception
        }
        //Code for creating and setting Java Card Applet files.
        File appStoreDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + "/" + JC_APP_DIR);
        if (!appStoreDir.exists() || appStoreDir.isFile()) {
            if (!appStoreDir.mkdir()) {
                JOptionPane.showMessageDialog(null, "Unable to create local App Store directory.", "JCPlayStore", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        try {
            args = parseArguments(new String[]{"--list"});
            if (args == null) {
                JOptionPane.showMessageDialog(null, "Error while parsing arguments");
                fail("Constructor : Error while parsing arguments");
            }
            System.out.println(args.asMap());
            // Set up slf4j simple in a way that pleases us
            System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
            System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true");
            System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
            String version = GlobalPlatform.getVersion();
            version += "\nRunning on " + System.getProperty("os.name");
            version += " " + System.getProperty("os.version");
            version += " " + System.getProperty("os.arch");
            version += ", Java " + System.getProperty("java.version");
            version += " by " + System.getProperty("java.vendor");
            System.out.println("GlobalPlatformPro " + version);
            try {
                // Test for unlimited crypto
                if (Cipher.getMaxAllowedKeyLength("AES") == 128) {
                    System.out.println("Unlimited crypto policy is NOT installed!");
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Try to get connected with card reader.
            try {
                final TerminalFactory tf;
                tf = TerminalManager.getTerminalFactory((String) args.valueOf(OPT_TERMINALS));
                CardTerminals terminals = tf.terminals();
                // List terminals if needed
                System.out.println("# Detected readers from " + tf.getProvider().getName());
                for (CardTerminal term : terminals.list()) {
                    cardReaderMap.put(term.getName(), term);
                    System.out.println((term.isCardPresent() ? "[*] " : "[ ] ") + term.getName());
                }
            } catch (NoSuchAlgorithmException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Start", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CardException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Start", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Initialize UI related componenets.
            //setLocationRelativeTo(null);
            initComponents();
            debugRadioButtonMenuItem.setSelected(true);
            commandComboBox.setSelectedIndex(0);
            setInstallAppletWidgetsVisible(false);
            //warningRadioButtonMenuItem.setSelected(true);
            connectAndCreateTableIfNotExists();
            insertCardDetailsIfNotExists();
        } catch (IOException ex) {
            Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            fail("IOException: " + ex.getMessage());
        }
    }

    public static void connect() {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
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

    public void connectAndCreateTableIfNotExists() {
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
            ret = statement.executeUpdate("create table if not exists card_app (ICFabricator varchar(50),ICSerialNumber varchar(20),ICType varchar(20),AID varchar(50),Version varchar(10),Description varchar(100),PRIMARY KEY (AID,ICFabricator,ICSerialNumber,ICType),FOREIGN KEY (ICFabricator,ICSerialNumber,ICType) REFERENCES card_details (ICFabricator,ICSerialNumber,ICType) ON DELETE CASCADE ON UPDATE NO ACTION)");
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

    public void insertCardDetailsIfNotExists() {
        Connection conn = null;
        try {
            // create a connection to the database
            CardDetails cardDetails = getCardDetails(new StringBuilder());
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

    /**
     * Card Production Life Cycle Data (CPLC data)
     */
    private CardDetails getCardDetails(StringBuilder statusMessage) {
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        CardDetails cardDetails = new CardDetails();
                        cardDetails.setAtr(card.getATR());
                        setCardDetails(channel, cardDetails);
                        return cardDetails;
                    } catch (CardException e) {
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } catch (GPException ex) {
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "List", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public void setCardDetails(CardChannel channel, CardDetails cardDetails) throws CardException, GPException {
        byte[] cplc = fetchCPLC(channel);
        if (cplc != null) {
            cardDetails.setCplc(GPData.CPLC.fromBytes(cplc));
        }
        // IIN (issuer identification number)in the card manager
        byte[] iin = getData(channel, 0x00, 0x42, "IIN", false);
        if (iin != null) {
            cardDetails.setInn(iin);
        }
        // CIN (card image number) in the card manager
        byte[] cin = getData(channel, 0x00, 0x45, "CIN", false);
        if (cin != null) {
            cardDetails.setCin(cin);
        }
        // Print Card Data
        byte[] cardData = getData(channel, 0x00, 0x66, "Card Data", false);
        if (cardData != null) {
            cardDetails.setCardData(cardData);
        }
        // Print Card Capabilities
        byte[] cardCapabilities = getData(channel, 0x00, 0x67, "Card Capabilities", false);
        if (cardCapabilities != null) {
            cardDetails.setCardCapabilities(cardCapabilities);
        }
        // Print Key Info Template
        byte[] keyInfo = fetchKeyInfoTemplate(channel);
        if (keyInfo != null) {
            cardDetails.setKeyInfo(keyInfo);
        }
    }

    public static void pretty_print_cplc(byte[] data, PrintStream out) {
        if (data == null || data.length == 0) {
            out.println("NO CPLC");
            return;
        }
        try {
            GPData.CPLC cplc = GPData.CPLC.fromBytes(data);
            out.println(cplc.toPrettyString());
        } catch (GPDataException ex) {
            Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static OptionSet parseArguments(String[] argv) throws IOException {
        OptionSet args = null;
        OptionParser parser = new OptionParser();

        // Generic options
        parser.acceptsAll(Arrays.asList("V", OPT_VERSION), "Show information about the program");
        parser.acceptsAll(Arrays.asList("h", "?", "help"), "Shows this help").forHelp();
        parser.acceptsAll(Arrays.asList("d", OPT_DEBUG), "Show PC/SC and APDU trace");
        parser.acceptsAll(Arrays.asList("v", OPT_VERBOSE), "Be verbose about operations");
        parser.acceptsAll(Arrays.asList("r", OPT_READER), "Use specific reader").withRequiredArg();
        parser.acceptsAll(Arrays.asList("l", OPT_LIST), "List the contents of the card");
        parser.acceptsAll(Arrays.asList("i", OPT_INFO), "Show information");
        parser.acceptsAll(Arrays.asList("a", OPT_APDU), "Send raw APDU (hex)").withRequiredArg().describedAs("APDU");
        parser.acceptsAll(Arrays.asList("s", OPT_SECURE_APDU), "Send raw APDU (hex) via SCP").withRequiredArg().describedAs("APDU");
        parser.acceptsAll(Arrays.asList("f", OPT_FORCE), "Force operation");
        parser.accepts(OPT_DUMP, "Dump APDU communication to <File>").withRequiredArg().ofType(File.class);
        parser.accepts(OPT_REPLAY, "Replay APDU responses from <File>").withRequiredArg().ofType(File.class);

        // Special options
        parser.accepts(OPT_TERMINALS, "Use PC/SC provider from <jar:class>").withRequiredArg();

        // Applet operation options
        parser.accepts(OPT_CAP, "Use a CAP file as source").withRequiredArg().ofType(File.class);
        parser.accepts(OPT_LOAD, "Load a CAP file").withRequiredArg().ofType(File.class);

        parser.accepts(OPT_INSTALL, "Install applet(s) from CAP").withOptionalArg().ofType(File.class);
        parser.accepts(OPT_PARAMS, "Installation parameters").withRequiredArg().describedAs("HEX");
        parser.accepts(OPT_PRIVS, "Specify privileges for installation").withRequiredArg();
        parser.accepts(OPT_LIST_PRIVS, "List known privileges");

        parser.accepts(OPT_UNINSTALL, "Uninstall applet/package").withRequiredArg().ofType(File.class);
        parser.accepts(OPT_DEFAULT, "Indicate Default Selected privilege");
        parser.accepts(OPT_TERMINATE, "Indicate Card Lock+Terminate privilege");
        parser.accepts(OPT_DOMAIN, "Create supplementary security domain").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_LOCK_APPLET, "Lock applet").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_UNLOCK_APPLET, "Unlock applet").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_LOCK_CARD, "Lock card");
        parser.accepts(OPT_UNLOCK_CARD, "Unlock card");
        parser.accepts(OPT_SECURE_CARD, "Transition ISD to SECURED state");
        parser.accepts(OPT_INITIALIZE_CARD, "Transition ISD to INITIALIZED state");
        parser.accepts(OPT_MOVE, "Move something").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_TO, "Destination security domain").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_ALLOW_TO, "Accept extradition to SSD");
        parser.accepts(OPT_ALLOW_FROM, "Accept extradition from SSD");
        parser.accepts(OPT_SHA256, "Use SHA-256 for LFDB hash");

        parser.accepts(OPT_SET_PRE_PERSO, "Set PrePerso data in CPLC").withRequiredArg().describedAs("data");
        parser.accepts(OPT_SET_PERSO, "Set Perso data in CPLC").withRequiredArg().describedAs("data");
        parser.accepts(OPT_TODAY, "Set date to today when updating CPLC");

        parser.accepts(OPT_STORE_DATA, "STORE DATA to applet").withRequiredArg().describedAs("data");

        parser.accepts(OPT_MAKE_DEFAULT, "Make AID the default").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_RENAME_ISD, "Rename ISD").withRequiredArg().describedAs("new AID");

        parser.accepts(OPT_DELETE, "Delete applet/package").withOptionalArg().describedAs("AID");
        parser.accepts(OPT_DELETE_KEY, "Delete key with version").withRequiredArg();

        parser.accepts(OPT_CREATE, "Create new instance of an applet").withRequiredArg().describedAs("AID");
        parser.accepts(OPT_APPLET, "Applet AID").withRequiredArg().describedAs("AID");
        parser.acceptsAll(Arrays.asList(OPT_PACKAGE, "pkg"), "Package AID").withRequiredArg().describedAs("AID");

        // Key options
        parser.accepts(OPT_KEY, "Specify master key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_KCV, "Specify master key check value").withRequiredArg().describedAs("KCV");

        parser.accepts(OPT_KEY_MAC, "Specify card MAC key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_KEY_ENC, "Specify card ENC key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_KEY_DEK, "Specify card DEK key").withRequiredArg().describedAs("key");

        parser.accepts(OPT_EMV, "Use EMV diversification");
        parser.accepts(OPT_VISA2, "Use VISA2 diversification");
        parser.accepts(OPT_KDF3, "Use SCP03 KDF diversification");

        parser.accepts(OPT_ORACLE, "Use an oracle for keying information").withOptionalArg().describedAs("URL");

        parser.accepts(OPT_KEY_ID, "Specify key ID").withRequiredArg();
        parser.accepts(OPT_KEY_VERSION, "Specify key version").withRequiredArg();
        parser.accepts(OPT_PUT_KEY, "Put a new key").withRequiredArg().describedAs("PEM file");

        parser.accepts(OPT_LOCK, "Set new key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_LOCK_ENC, "Set new ENC key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_LOCK_MAC, "Set new MAC key").withRequiredArg().describedAs("key");
        parser.accepts(OPT_LOCK_DEK, "Set new DEK key").withRequiredArg().describedAs("key");

        parser.accepts(OPT_UNLOCK, "Set default key for card key");
        parser.accepts(OPT_NEW_KEY_VERSION, "Key version for the new key").withRequiredArg();

        // GP SE access rules
        parser.accepts(OPT_ACR_LIST, "List access rules");
        parser.accepts(OPT_ACR_ADD, "Add an access rule");
        parser.accepts(OPT_ACR_DELETE, "Delete an access rule");
        parser.accepts(OPT_ACR_RULE, "Access control rule (can be 0x00(NEVER),0x01(ALWAYS) or an apdu filter").withRequiredArg();
        parser.accepts(OPT_ACR_CERT_HASH, "Certificate hash (sha1)").withRequiredArg();

        // General GP options
        parser.accepts(OPT_SC_MODE, "Secure channel to use (mac/enc/clr)").withRequiredArg();
        parser.accepts(OPT_BS, "maximum APDU payload size").withRequiredArg().ofType(Integer.class);
        parser.accepts(OPT_OP201, "Enable OpenPlatform 2.0.1 mode");

        parser.accepts(OPT_SDAID, "ISD AID").withRequiredArg().describedAs("AID");

        // Parse arguments
        try {
            args = parser.parse(argv);
        } catch (OptionException e) {
            if (e.getCause() != null) {
                System.err.println(e.getMessage() + ": " + e.getCause().getMessage());
            } else {
                System.err.println(e.getMessage());
            }
            System.err.println();
            parser.printHelpOn(System.err);
            System.exit(1);
        }

        if (args.has("help") || args.specs().size() == 0) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        return args;
    }

    private static void fail(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cardReaderListComboBox = new javax.swing.JComboBox<>();
        goButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        commandComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        commonLabel = new javax.swing.JLabel();
        commonButton = new javax.swing.JButton();
        commonComboBox = new javax.swing.JComboBox<>();
        jMenuBarMain = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        tasksMenu = new javax.swing.JMenu();
        listCardReadersMenuItem = new javax.swing.JMenuItem();
        settingMenu = new javax.swing.JMenu();
        warningRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        verboseRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        debugRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("WELCOME !!!");

        jLabel2.setText("Card Reader List:");

        cardReaderListComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(cardReaderMap.keySet().toArray(new String[cardReaderMap.size()])));

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Command:");

        commandComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(commandList));
        commandComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                commandComboBoxItemStateChanged(evt);
            }
        });

        statusTextArea.setEditable(false);
        statusTextArea.setColumns(20);
        statusTextArea.setRows(5);
        jScrollPane1.setViewportView(statusTextArea);

        jLabel4.setText("Status:");

        commonLabel.setText("CAP File:");

        commonButton.setText("Select CAP File");
        commonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commonButtonActionPerformed(evt);
            }
        });

        commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        fileMenu.setText("File");
        jMenuBarMain.add(fileMenu);

        tasksMenu.setText("Tasks");

        listCardReadersMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        listCardReadersMenuItem.setText("List Card Readers");
        listCardReadersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listCardReadersMenuItemActionPerformed(evt);
            }
        });
        tasksMenu.add(listCardReadersMenuItem);

        jMenuBarMain.add(tasksMenu);

        settingMenu.setText("Setting");
        buttonGroup1.add(settingMenu);

        warningRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        buttonGroup1.add(warningRadioButtonMenuItem);
        warningRadioButtonMenuItem.setText("Warning");
        warningRadioButtonMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                warningRadioButtonMenuItemItemStateChanged(evt);
            }
        });
        settingMenu.add(warningRadioButtonMenuItem);

        verboseRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        buttonGroup1.add(verboseRadioButtonMenuItem);
        verboseRadioButtonMenuItem.setSelected(true);
        verboseRadioButtonMenuItem.setText("Verbose");
        verboseRadioButtonMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                verboseRadioButtonMenuItemItemStateChanged(evt);
            }
        });
        settingMenu.add(verboseRadioButtonMenuItem);

        debugRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        buttonGroup1.add(debugRadioButtonMenuItem);
        debugRadioButtonMenuItem.setText("Debug");
        debugRadioButtonMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                debugRadioButtonMenuItemItemStateChanged(evt);
            }
        });
        settingMenu.add(debugRadioButtonMenuItem);

        jMenuBarMain.add(settingMenu);

        setJMenuBar(jMenuBarMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cardReaderListComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(commonButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                        .addComponent(goButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(commonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(commonComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cardReaderListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(commandComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(goButton)
                    .addComponent(commonButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commonLabel)
                    .addComponent(commonComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void verboseRadioButtonMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_verboseRadioButtonMenuItemItemStateChanged
        // TODO add your handling code here:
        if (verboseRadioButtonMenuItem.isSelected()) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        }
    }//GEN-LAST:event_verboseRadioButtonMenuItemItemStateChanged

    private void debugRadioButtonMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_debugRadioButtonMenuItemItemStateChanged
        // TODO add your handling code here:
        if (debugRadioButtonMenuItem.isSelected()) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        }
    }//GEN-LAST:event_debugRadioButtonMenuItemItemStateChanged

    private void warningRadioButtonMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_warningRadioButtonMenuItemItemStateChanged
        // TODO add your handling code here:
        if (warningRadioButtonMenuItem.isSelected()) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        }
    }//GEN-LAST:event_warningRadioButtonMenuItemItemStateChanged

    private void listCardReadersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listCardReadersMenuItemActionPerformed
        updateCardReaderListComboBox();
    }//GEN-LAST:event_listCardReadersMenuItemActionPerformed

    private void updateCardReaderListComboBox() {
        updateCardReaderMap();
        cardReaderListComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(cardReaderMap.keySet().toArray(new String[cardReaderMap.size()])));
    }

    private void updateCardReaderMap() {
        try {
            cardReaderMap.clear();
            final TerminalFactory tf;
            tf = TerminalManager.getTerminalFactory((String) args.valueOf(OPT_TERMINALS));
            CardTerminals terminals = tf.terminals();
            // List terminals if needed
            System.out.println("# Detected readers from " + tf.getProvider().getName() + "Type: " + tf.getType());
            for (CardTerminal term : terminals.list()) {
                cardReaderMap.put(term.getName(), term);
                System.out.println((term.isCardPresent() ? "[*] " : "[ ] ") + term.getName());
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CardException ex) {
            Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        //updateTextArea("", false);
        ///////
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                statusTextArea.setText("");
//            }
//        });

        //////
        String command = commandComboBox.getSelectedItem().toString();
        if (command != null && command.length() > 0) {
            switch (command) {
                case CMD_CONNECT:
                    StringBuilder statusMessage = new StringBuilder();
                    connectToCard(statusMessage);
                    statusTextArea.setText(statusMessage.toString());
                    getCardDetails(new StringBuilder());
                    break;
                case CMD_LIST:
                    statusMessage = new StringBuilder();
                    listInstalledApplets(statusMessage);
                    statusTextArea.setText(statusMessage.toString());
                    break;
                case CMD_INSTALL:
                    if (commonComboBox.getSelectedItem() == null || commonComboBox.getSelectedItem().toString().length() == 0
                            || commonComboBox.getSelectedItem().toString().equals(DEF_CAP_FILE_COMBO_TXT)) {
                        JOptionPane.showMessageDialog(this, "Please provice correct CAP file path.", "Delete", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    statusMessage = new StringBuilder();
                    File capfile = new File(commonComboBox.getSelectedItem().toString());
                    if (capfile.exists() && capfile.isFile()) {
                        statusTextArea.setText("Installing Applet..." + System.lineSeparator());
                        //updateTextArea("Installing Applet..." +  System.lineSeparator(), false);
                        installApplet(capfile, statusMessage);
                        statusTextArea.append(statusMessage.toString());
                    } else {
                        statusTextArea.setText("Please select a CAP file");
                    }
                    break;
                case CMD_DELETE:
                    if (commonComboBox.getSelectedItem() == null || commonComboBox.getSelectedItem().toString().length() == 0
                            || commonComboBox.getSelectedItem().toString().equals(DEF_AID_COMBO_TXT)) {
                        JOptionPane.showMessageDialog(this, "Please provide proper AID.", "Delete", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    statusMessage = new StringBuilder();
                    AID aid = AID.fromString(commonComboBox.getSelectedItem().toString());
                    //statusMessage.append("Deleting ").append(commonComboBox.getSelectedItem().toString()).append(" Applet...").append(System.lineSeparator());
                    //updateTextArea("Deleting " + commonComboBox.getSelectedItem().toString() + " Applet..." + System.lineSeparator(),false);
                    statusTextArea.setText("Deleting " + commonComboBox.getSelectedItem().toString() + " Applet..." + System.lineSeparator());
                    deleteInstalledApplets(aid, statusMessage);
                    statusTextArea.append(statusMessage.toString());
                    populateInstalledAppletCombobox();
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_goButtonActionPerformed

    private void commandComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_commandComboBoxItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            System.out.println("Item: " + evt.getItem());
            if (evt.getItem().toString().equals(CMD_INSTALL)) {
                commonButton.setText("Select CAP File");
                commonLabel.setText("CAP File: ");
                commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{DEF_CAP_FILE_COMBO_TXT}));
                commonComboBox.setEnabled(false);
                setInstallAppletWidgetsVisible(true);
            } else if (evt.getItem().toString().equals(CMD_DELETE)) {
                //setInstallAppletWidgetsVisible(false);
                commonButton.setText("List Installed Applets");
                commonLabel.setText("AID: ");
                commonComboBox.setEnabled(true);
                commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{DEF_AID_COMBO_TXT}));
                setInstallAppletWidgetsVisible(true);
            } else {
                setInstallAppletWidgetsVisible(false);
            }
        }
    }//GEN-LAST:event_commandComboBoxItemStateChanged

    private void commonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commonButtonActionPerformed
        // TODO add your handling code here:
        if (((JButton) evt.getSource()).getText().equals("Select CAP File")) {
            // create an object of JFileChooser class 
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            File appStoreDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + "/JCAPPStore");
            if (!appStoreDir.exists() || appStoreDir.isFile()) {
                if (appStoreDir.mkdir()) {
                    fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory() + "/JCAPPStore");
                } else {
                    System.out.println("Unable to create local App Store directory.");
                }
            } else {
                fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory() + "/JCAPPStore");
            }
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Java Card Applets", "cap"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            // invoke the showsOpenDialog function to show the save dialog 
            int r = fileChooser.showOpenDialog(null);
            // if the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected file 
                commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{fileChooser.getSelectedFile().getAbsolutePath()}));
            } // if the user cancelled the operation 
            else {
                commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"<Please provide CAP file path>"}));
            }
        } else if (((JButton) evt.getSource()).getText().equals("List Installed Applets")) {
            populateInstalledAppletCombobox();
        }
    }//GEN-LAST:event_commonButtonActionPerformed

    private void populateInstalledAppletCombobox() {
        ArrayList<String> aidList = new ArrayList<>();
        StringBuilder statusMessage = new StringBuilder();
        getAIDListOfInstalledApplets(aidList, statusMessage);
        statusTextArea.setText(statusMessage.toString());
        String[] strAIDArray = new String[aidList.size()];
        commonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(aidList.toArray(strAIDArray)));
    }

    private void setInstallAppletWidgetsVisible(boolean flag) {
        commonButton.setVisible(flag);
        commonLabel.setVisible(flag);
        commonComboBox.setVisible(flag);
    }

    private boolean listInstalledApplets(StringBuilder statusMessage) {
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        System.out.println("Reader: " + cardTerminal.getName());
                        System.out.println("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
//                        statusMessage.append("Reader: ").append(cardTerminal.getName()).append(System.lineSeparator())
//                                .append("ATR: ").append(HexUtils.bin2hex(card.getATR().getBytes())).append(System.lineSeparator());
                        // GlobalPlatform specific
                        if (args.has(OPT_SDAID)) {
                            gp = GlobalPlatform.connect(channel, AID.fromString(args.valueOf(OPT_SDAID)));
                        } else {
                            // Oracle only applies if no other arguments given
                            gp = GlobalPlatform.discover(channel);
                            // FIXME: would like to get AID from oracle as well.
                        }
                        // Extract information
                        if (args.has(OPT_INFO)) {
                            GPData.dump(channel);
                        }
                        // Normally assume a single master key
                        final GPSessionKeyProvider keys;

                        if (args.has(OPT_KEYS)) {
                            // keys come from custom provider
                            fail("Not yet implemented");
                            keys = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                        } else if (args.has(OPT_ORACLE)) {
                            keys = PythiaKeys.ask(card.getATR().getBytes(), GPData.fetchCPLC(channel), GPData.fetchKeyInfoTemplate(channel));
                        } else {
                            PlaintextKeys keyz;
                            if (args.has(OPT_KEY)) {
                                GPKey k = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY)));
                                if (args.has(OPT_KCV)) {
                                    byte[] given = HexUtils.stringToBin((String) args.valueOf(OPT_KCV));
                                    byte[] expected = k.getKCV();
                                    if (expected.length == 0) {
                                        fail("Don't know how to calculate KCV for the key"); // FIXME: all keys are RAW currently
                                    }
                                    // Check KCV
                                    if (!Arrays.equals(given, expected)) {
                                        fail("KCV does not match, expected " + HexUtils.bin2hex(expected) + " but given " + HexUtils.bin2hex(given));
                                    }
                                }
                                keyz = PlaintextKeys.fromMasterKey(k);
                            } else {
                                Map<String, String> env = System.getenv();
                                // XXX: better checks for exclusive key options
                                if (args.has(OPT_KEY_MAC) && args.has(OPT_KEY_ENC) && args.has(OPT_KEY_DEK)) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_ENC)));
                                    GPKey mac = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_MAC)));
                                    GPKey dek = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_DEK)));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                } else if (env.containsKey("GP_KEY_ENC") && env.containsKey("GP_KEY_MAC") && env.containsKey("GP_KEY_DEK")) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_ENC")));
                                    GPKey mac = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_MAC")));
                                    GPKey dek = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_DEK")));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                    if (env.containsKey("GP_KEY_VERSION")) {
                                        keyz.setVersion(GPUtils.intValue(env.get("GP_KEY_VERSION")));
                                    }
                                } else {
                                    if (needsAuthentication(args)) {
                                        System.out.println("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes()));
                                        statusMessage.append("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes())).append(System.lineSeparator());
                                    }
                                    keyz = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                                }
                            }
                            // "gp -l -emv" should still work
                            if (args.has(OPT_VISA2)) {
                                keyz.setDiversifier(VISA2);
                            } else if (args.has(OPT_EMV)) {
                                keyz.setDiversifier(EMV);
                            } else if (args.has(OPT_KDF3)) {
                                keyz.setDiversifier(KDF3);
                            }

                            if (args.has(OPT_KEY_VERSION)) {
                                keyz.setVersion(GPUtils.intValue((String) args.valueOf(OPT_KEY_VERSION)));
                            }
                            keys = keyz;
                        }

                        // XXX: leftover
                        if (args.has(OPT_OP201)) {
                            gp.setSpec(GPSpec.OP201);
                        }

                        // Override block size for stupidly broken readers.
                        // See https://github.com/martinpaljak/GlobalPlatformPro/issues/32
                        // The name of the option comes from a common abbreviation as well as dd utility
                        if (args.has(OPT_BS)) {
                            gp.setBlockSize((int) args.valueOf(OPT_BS));
                        }
                        // Authenticate, only if needed
                        if (needsAuthentication(args)) {
                            EnumSet<GlobalPlatform.APDUMode> mode = GlobalPlatform.defaultMode.clone();
                            // Override default mode if needed.
                            if (args.has(OPT_SC_MODE)) {
                                mode.clear();
                                for (Object s : args.valuesOf(OPT_SC_MODE)) {
                                    mode.add(GlobalPlatform.APDUMode.fromString((String) s));
                                }
                            }
                            // IMPORTANT PLACE. Possibly brick the card now, if keys don't match.
                            gp.openSecureChannel(keys, null, 0, mode);
                            listRegistry(gp.getRegistry(), statusMessage, true);
                            return true;
                            //listRegistry(gp.getRegistry(), statusMessage, args.has(OPT_VERBOSE));
//                            if (args.has(OPT_LIST)) {
//                                //GPCommands.listRegistry(gp.getRegistry(), System.out, args.has(OPT_VERBOSE));
//                                listRegistry(gp.getRegistry(), statusMessage, args.has(OPT_VERBOSE));
//                            }

                        }
                    } catch (CardException e) {
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } catch (GPException ex) {
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "List", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private boolean installApplet(File capfile, StringBuilder statusMessage) {
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        System.out.println("Reader: " + cardTerminal.getName());
                        System.out.println("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
//                        statusMessage.append("Reader: ").append(cardTerminal.getName()).append(System.lineSeparator())
//                                .append("ATR: ").append(HexUtils.bin2hex(card.getATR().getBytes())).append(System.lineSeparator());
                        // GlobalPlatform specific
                        if (args.has(OPT_SDAID)) {
                            gp = GlobalPlatform.connect(channel, AID.fromString(args.valueOf(OPT_SDAID)));
                        } else {
                            // Oracle only applies if no other arguments given
                            gp = GlobalPlatform.discover(channel);
                            // FIXME: would like to get AID from oracle as well.
                        }
                        /////////////////
                        // Extract information
                        if (args.has(OPT_INFO)) {
                            GPData.dump(channel);
                        }
                        // Normally assume a single master key
                        final GPSessionKeyProvider keys;

                        if (args.has(OPT_KEYS)) {
                            // keys come from custom provider
                            fail("Not yet implemented");
                            keys = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                        } else if (args.has(OPT_ORACLE)) {
                            keys = PythiaKeys.ask(card.getATR().getBytes(), GPData.fetchCPLC(channel), GPData.fetchKeyInfoTemplate(channel));
                        } else {
                            PlaintextKeys keyz;
                            if (args.has(OPT_KEY)) {
                                GPKey k = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY)));
                                if (args.has(OPT_KCV)) {
                                    byte[] given = HexUtils.stringToBin((String) args.valueOf(OPT_KCV));
                                    byte[] expected = k.getKCV();
                                    if (expected.length == 0) {
                                        fail("Don't know how to calculate KCV for the key"); // FIXME: all keys are RAW currently
                                    }
                                    // Check KCV
                                    if (!Arrays.equals(given, expected)) {
                                        fail("KCV does not match, expected " + HexUtils.bin2hex(expected) + " but given " + HexUtils.bin2hex(given));
                                    }
                                }
                                keyz = PlaintextKeys.fromMasterKey(k);
                            } else {
                                Map<String, String> env = System.getenv();
                                // XXX: better checks for exclusive key options
                                if (args.has(OPT_KEY_MAC) && args.has(OPT_KEY_ENC) && args.has(OPT_KEY_DEK)) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_ENC)));
                                    GPKey mac = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_MAC)));
                                    GPKey dek = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_DEK)));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                } else if (env.containsKey("GP_KEY_ENC") && env.containsKey("GP_KEY_MAC") && env.containsKey("GP_KEY_DEK")) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_ENC")));
                                    GPKey mac = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_MAC")));
                                    GPKey dek = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_DEK")));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                    if (env.containsKey("GP_KEY_VERSION")) {
                                        keyz.setVersion(GPUtils.intValue(env.get("GP_KEY_VERSION")));
                                    }
                                } else {
                                    if (needsAuthentication(args)) {
                                        System.out.println("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes()));
                                        statusMessage.append("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes())).append(System.lineSeparator());
                                    }
                                    keyz = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                                }
                            }
                            // "gp -l -emv" should still work
                            if (args.has(OPT_VISA2)) {
                                keyz.setDiversifier(VISA2);
                            } else if (args.has(OPT_EMV)) {
                                keyz.setDiversifier(EMV);
                            } else if (args.has(OPT_KDF3)) {
                                keyz.setDiversifier(KDF3);
                            }

                            if (args.has(OPT_KEY_VERSION)) {
                                keyz.setVersion(GPUtils.intValue((String) args.valueOf(OPT_KEY_VERSION)));
                            }
                            keys = keyz;
                        }

                        // XXX: leftover
                        if (args.has(OPT_OP201)) {
                            gp.setSpec(GPSpec.OP201);
                        }

                        // Override block size for stupidly broken readers.
                        // See https://github.com/martinpaljak/GlobalPlatformPro/issues/32
                        // The name of the option comes from a common abbreviation as well as dd utility
                        if (args.has(OPT_BS)) {
                            gp.setBlockSize((int) args.valueOf(OPT_BS));
                        }
                        // Authenticate, only if needed
                        if (needsAuthentication(args)) {
                            EnumSet<GlobalPlatform.APDUMode> mode = GlobalPlatform.defaultMode.clone();
                            // Override default mode if needed.
                            if (args.has(OPT_SC_MODE)) {
                                mode.clear();
                                for (Object s : args.valuesOf(OPT_SC_MODE)) {
                                    mode.add(GlobalPlatform.APDUMode.fromString((String) s));
                                }
                            }
                            // IMPORTANT PLACE. Possibly brick the card now, if keys don't match.
                            gp.openSecureChannel(keys, null, 0, mode);
                            //--install
                            //if (args.has(OPT_INSTALL)) {
                            //final File capfile;
                            //capfile = (File) args.valueOf(OPT_INSTALL);
                            CAPFile instcap = CAPFile.fromStream(new FileInputStream(capfile));

                            if (args.has(OPT_VERBOSE)) {
                                instcap.dump(System.out);
                            }
                            GPRegistry reg = gp.getRegistry();
                            // Remove existing load file
                            if (args.has(OPT_FORCE) && reg.allPackageAIDs().contains(instcap.getPackageAID())) {
                                gp.deleteAID(instcap.getPackageAID(), true);
                            }
                            // Load
                            // TODO: handle DAP here as well
                            if (instcap.getAppletAIDs().size() <= 1) {
                                try {
                                    AID target = null;
                                    if (args.has(OPT_TO)) {
                                        target = AID.fromString(args.valueOf(OPT_TO));
                                    }
                                    gp.loadCapFile(instcap, target);
                                    System.out.println("CAP loaded");
                                    statusMessage.append("CAP loaded").append(System.lineSeparator());
                                } catch (GPException e) {
                                    if (e.sw == 0x6985 || e.sw == 0x6A80) {
                                        System.err.println("Loading failed. Are you sure the CAP file (JC version, packages, sizes) is compatible with your card?");
                                        statusMessage.append("Loading failed. Are you sure the CAP file (JC version, packages, sizes) is compatible with your card?");
                                    }
                                    throw e;
                                }
                            }

                            // Install
                            final AID appaid;
                            final AID instanceaid;
                            if (instcap.getAppletAIDs().size() == 0) {
                                return false;
                            } else if (instcap.getAppletAIDs().size() > 1) {
                                if (args.has(OPT_APPLET)) {
                                    appaid = AID.fromString(args.valueOf(OPT_APPLET));
                                } else {
                                    //fail("CAP contains more than one applet, specify the right one with --" + OPT_APPLET);
                                    statusMessage.append("CAP contains more than one applet, specify the right one with --" + OPT_APPLET).append(System.lineSeparator());
                                    return false;
                                }
                            } else {
                                appaid = instcap.getAppletAIDs().get(0);
                            }

                            // override
                            if (args.has(OPT_CREATE)) {
                                instanceaid = AID.fromString(args.valueOf(OPT_CREATE));
                            } else {
                                instanceaid = appaid;
                            }

                            GPRegistryEntry.Privileges privs = getInstPrivs(args);

                            // Remove existing default app
                            if (args.has(OPT_FORCE) && (reg.getDefaultSelectedAID() != null && privs.has(GPRegistryEntry.Privilege.CardReset))) {
                                gp.deleteAID(reg.getDefaultSelectedAID(), false);
                            }
                            // warn
                            if (gp.getRegistry().allAppletAIDs().contains(instanceaid)) {
                                System.err.println("WARNING: Applet " + instanceaid + " already present on card");
                                statusMessage.append("WARNING: Applet " + instanceaid + " already present on card").append(System.lineSeparator());
                            }

                            // shoot
                            //statusMessage.append("Installing ").append(HexUtils.bin2hex(appaid.getBytes())).append(" Applet...").append(System.lineSeparator());
                            gp.installAndMakeSelectable(instcap.getPackageAID(), appaid, instanceaid, privs, getInstParams(args), null);
                            statusMessage.append("Applet ").append(HexUtils.bin2hex(appaid.getBytes())).append(" installed!!!").append(System.lineSeparator());
                            //}
                        }
                        /////////////////
                        return true;
                    } catch (CardException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Install", JOptionPane.INFORMATION_MESSAGE);
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } catch (GPException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Install", JOptionPane.INFORMATION_MESSAGE);
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Install", JOptionPane.INFORMATION_MESSAGE);
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Install", JOptionPane.INFORMATION_MESSAGE);
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Install", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    // FIXME: get rid
    private static Privileges getInstPrivs(OptionSet args) {
        Privileges privs = new Privileges();
        if (args.has(OPT_PRIVS)) {
            addPrivs(privs, (String) args.valueOf(OPT_PRIVS));
        }
        if (args.has(OPT_DEFAULT)) {
            privs.add(Privilege.CardReset);
        }
        if (args.has(OPT_TERMINATE)) {
            privs.add(Privilege.CardLock);
            privs.add(Privilege.CardTerminate);
        }
        return privs;
    }

    private boolean getAIDListOfInstalledApplets(ArrayList<String> aidList, StringBuilder statusMessage) {
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        System.out.println("Reader: " + cardTerminal.getName());
                        System.out.println("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
//                        statusMessage.append("Reader: ").append(cardTerminal.getName()).append(System.lineSeparator())
//                                .append("ATR: ").append(HexUtils.bin2hex(card.getATR().getBytes())).append(System.lineSeparator());
                        // GlobalPlatform specific
                        if (args.has(OPT_SDAID)) {
                            gp = GlobalPlatform.connect(channel, AID.fromString(args.valueOf(OPT_SDAID)));
                        } else {
                            // Oracle only applies if no other arguments given
                            gp = GlobalPlatform.discover(channel);
                            // FIXME: would like to get AID from oracle as well.
                        }
                        /////////////////
                        // Extract information
                        if (args.has(OPT_INFO)) {
                            GPData.dump(channel);
                        }
                        // Normally assume a single master key
                        final GPSessionKeyProvider keys;

                        if (args.has(OPT_KEYS)) {
                            // keys come from custom provider
                            fail("Not yet implemented");
                            keys = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                        } else if (args.has(OPT_ORACLE)) {
                            keys = PythiaKeys.ask(card.getATR().getBytes(), GPData.fetchCPLC(channel), GPData.fetchKeyInfoTemplate(channel));
                        } else {
                            PlaintextKeys keyz;
                            if (args.has(OPT_KEY)) {
                                GPKey k = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY)));
                                if (args.has(OPT_KCV)) {
                                    byte[] given = HexUtils.stringToBin((String) args.valueOf(OPT_KCV));
                                    byte[] expected = k.getKCV();
                                    if (expected.length == 0) {
                                        fail("Don't know how to calculate KCV for the key"); // FIXME: all keys are RAW currently
                                    }
                                    // Check KCV
                                    if (!Arrays.equals(given, expected)) {
                                        fail("KCV does not match, expected " + HexUtils.bin2hex(expected) + " but given " + HexUtils.bin2hex(given));
                                    }
                                }
                                keyz = PlaintextKeys.fromMasterKey(k);
                            } else {
                                Map<String, String> env = System.getenv();
                                // XXX: better checks for exclusive key options
                                if (args.has(OPT_KEY_MAC) && args.has(OPT_KEY_ENC) && args.has(OPT_KEY_DEK)) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_ENC)));
                                    GPKey mac = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_MAC)));
                                    GPKey dek = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_DEK)));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                } else if (env.containsKey("GP_KEY_ENC") && env.containsKey("GP_KEY_MAC") && env.containsKey("GP_KEY_DEK")) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_ENC")));
                                    GPKey mac = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_MAC")));
                                    GPKey dek = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_DEK")));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                    if (env.containsKey("GP_KEY_VERSION")) {
                                        keyz.setVersion(GPUtils.intValue(env.get("GP_KEY_VERSION")));
                                    }
                                } else {
                                    if (needsAuthentication(args)) {
                                        System.out.println("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes()));
                                        statusMessage.append("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes())).append(System.lineSeparator());
                                    }
                                    keyz = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                                }
                            }
                            // "gp -l -emv" should still work
                            if (args.has(OPT_VISA2)) {
                                keyz.setDiversifier(VISA2);
                            } else if (args.has(OPT_EMV)) {
                                keyz.setDiversifier(EMV);
                            } else if (args.has(OPT_KDF3)) {
                                keyz.setDiversifier(KDF3);
                            }

                            if (args.has(OPT_KEY_VERSION)) {
                                keyz.setVersion(GPUtils.intValue((String) args.valueOf(OPT_KEY_VERSION)));
                            }
                            keys = keyz;
                        }

                        // XXX: leftover
                        if (args.has(OPT_OP201)) {
                            gp.setSpec(GPSpec.OP201);
                        }

                        // Override block size for stupidly broken readers.
                        // See https://github.com/martinpaljak/GlobalPlatformPro/issues/32
                        // The name of the option comes from a common abbreviation as well as dd utility
                        if (args.has(OPT_BS)) {
                            gp.setBlockSize((int) args.valueOf(OPT_BS));
                        }
                        // Authenticate, only if needed
                        if (needsAuthentication(args)) {
                            EnumSet<GlobalPlatform.APDUMode> mode = GlobalPlatform.defaultMode.clone();
                            // Override default mode if needed.
                            if (args.has(OPT_SC_MODE)) {
                                mode.clear();
                                for (Object s : args.valuesOf(OPT_SC_MODE)) {
                                    mode.add(GlobalPlatform.APDUMode.fromString((String) s));
                                }
                            }
                            // IMPORTANT PLACE. Possibly brick the card now, if keys don't match.
                            gp.openSecureChannel(keys, null, 0, mode);
                            GPRegistry reg = gp.getRegistry();
                            for (GPRegistryEntry e : reg) {
                                if (e.getType() != GPRegistryEntry.Kind.IssuerSecurityDomain) {
                                    aidList.add(e.getAID().toString());
                                }
                            }
//                            for (AID aid : reg.allAIDs()) {
//                                aidList.add(aid.toString());
//                            }
                            statusMessage.append("AIDs:").append(aidList.toString()).append(System.lineSeparator());
                        }
                        return true;
                    } catch (CardException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Delete", JOptionPane.INFORMATION_MESSAGE);
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } catch (GPException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete", JOptionPane.INFORMATION_MESSAGE);
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private boolean deleteInstalledApplets(AID aid, StringBuilder statusMessage) {
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        System.out.println("Reader: " + cardTerminal.getName());
                        System.out.println("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
//                        statusMessage.append("Reader: ").append(cardTerminal.getName()).append(System.lineSeparator())
//                                .append("ATR: ").append(HexUtils.bin2hex(card.getATR().getBytes())).append(System.lineSeparator());
                        // GlobalPlatform specific
                        if (args.has(OPT_SDAID)) {
                            gp = GlobalPlatform.connect(channel, AID.fromString(args.valueOf(OPT_SDAID)));
                        } else {
                            // Oracle only applies if no other arguments given
                            gp = GlobalPlatform.discover(channel);
                            // FIXME: would like to get AID from oracle as well.
                        }
                        /////////////////
                        // Extract information
                        if (args.has(OPT_INFO)) {
                            GPData.dump(channel);
                        }
                        // Normally assume a single master key
                        final GPSessionKeyProvider keys;

                        if (args.has(OPT_KEYS)) {
                            // keys come from custom provider
                            fail("Not yet implemented");
                            keys = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                        } else if (args.has(OPT_ORACLE)) {
                            keys = PythiaKeys.ask(card.getATR().getBytes(), GPData.fetchCPLC(channel), GPData.fetchKeyInfoTemplate(channel));
                        } else {
                            PlaintextKeys keyz;
                            if (args.has(OPT_KEY)) {
                                GPKey k = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY)));
                                if (args.has(OPT_KCV)) {
                                    byte[] given = HexUtils.stringToBin((String) args.valueOf(OPT_KCV));
                                    byte[] expected = k.getKCV();
                                    if (expected.length == 0) {
                                        fail("Don't know how to calculate KCV for the key"); // FIXME: all keys are RAW currently
                                    }
                                    // Check KCV
                                    if (!Arrays.equals(given, expected)) {
                                        fail("KCV does not match, expected " + HexUtils.bin2hex(expected) + " but given " + HexUtils.bin2hex(given));
                                    }
                                }
                                keyz = PlaintextKeys.fromMasterKey(k);
                            } else {
                                Map<String, String> env = System.getenv();
                                // XXX: better checks for exclusive key options
                                if (args.has(OPT_KEY_MAC) && args.has(OPT_KEY_ENC) && args.has(OPT_KEY_DEK)) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_ENC)));
                                    GPKey mac = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_MAC)));
                                    GPKey dek = new GPKey(HexUtils.stringToBin((String) args.valueOf(OPT_KEY_DEK)));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                } else if (env.containsKey("GP_KEY_ENC") && env.containsKey("GP_KEY_MAC") && env.containsKey("GP_KEY_DEK")) {
                                    GPKey enc = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_ENC")));
                                    GPKey mac = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_MAC")));
                                    GPKey dek = new GPKey(HexUtils.stringToBin(env.get("GP_KEY_DEK")));
                                    keyz = PlaintextKeys.fromKeys(enc, mac, dek);
                                    if (env.containsKey("GP_KEY_VERSION")) {
                                        keyz.setVersion(GPUtils.intValue(env.get("GP_KEY_VERSION")));
                                    }
                                } else {
                                    if (needsAuthentication(args)) {
                                        System.out.println("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes()));
                                        statusMessage.append("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes())).append(System.lineSeparator());
                                    }
                                    keyz = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
                                }
                            }
                            // "gp -l -emv" should still work
                            if (args.has(OPT_VISA2)) {
                                keyz.setDiversifier(VISA2);
                            } else if (args.has(OPT_EMV)) {
                                keyz.setDiversifier(EMV);
                            } else if (args.has(OPT_KDF3)) {
                                keyz.setDiversifier(KDF3);
                            }

                            if (args.has(OPT_KEY_VERSION)) {
                                keyz.setVersion(GPUtils.intValue((String) args.valueOf(OPT_KEY_VERSION)));
                            }
                            keys = keyz;
                        }

                        // XXX: leftover
                        if (args.has(OPT_OP201)) {
                            gp.setSpec(GPSpec.OP201);
                        }

                        // Override block size for stupidly broken readers.
                        // See https://github.com/martinpaljak/GlobalPlatformPro/issues/32
                        // The name of the option comes from a common abbreviation as well as dd utility
                        if (args.has(OPT_BS)) {
                            gp.setBlockSize((int) args.valueOf(OPT_BS));
                        }
                        // Authenticate, only if needed
                        if (needsAuthentication(args)) {
                            EnumSet<GlobalPlatform.APDUMode> mode = GlobalPlatform.defaultMode.clone();
                            // Override default mode if needed.
                            if (args.has(OPT_SC_MODE)) {
                                mode.clear();
                                for (Object s : args.valuesOf(OPT_SC_MODE)) {
                                    mode.add(GlobalPlatform.APDUMode.fromString((String) s));
                                }
                            }
                            // IMPORTANT PLACE. Possibly brick the card now, if keys don't match.
                            gp.openSecureChannel(keys, null, 0, mode);
                            GPRegistry reg = gp.getRegistry();
                            try {
                                // If the AID represents a package or otherwise force is enabled.
                                //statusMessage.append("Deleting ").append(commonComboBox.getSelectedItem().toString()).append(" Applet...").append(System.lineSeparator());
                                for (GPRegistryEntry e : reg) {
                                    if (e.getAID().toString().equals(aid.toString())) {
                                        ///////////////////
                                        if (e.getType() == GPRegistryEntry.Kind.ExecutableLoadFile) {
                                            StringBuilder msg = new StringBuilder();
                                            GPRegistryEntryPkg pkg = (GPRegistryEntryPkg) e;
                                            if (pkg.getModules().size() > 0) {
                                                msg.append("Selected Package AID ");
                                                if (pkg.getVersion() != null) {
                                                    msg.append("Version: ").append(pkg.getVersionString()).append(" ");
                                                }
                                                msg.append("contains following applications").append(System.lineSeparator());
                                                for (AID a : pkg.getModules()) {
                                                    msg.append(GPUtils.byteArrayToReadableString(a.getBytes())).append(HexUtils.bin2hex(a.getBytes())).append(System.lineSeparator());
                                                }
                                                msg.append("Would you like to delete?");
                                                int option = JOptionPane.showConfirmDialog(this, msg.toString(), "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                                if (option == JOptionPane.YES_OPTION) {
                                                    gp.deleteAID(aid, true);
                                                }
                                            } else {
                                                gp.deleteAID(aid, true);
                                            }

                                        } else {
                                            gp.deleteAID(aid, true);
                                        }
                                        ///////////////////
                                    }
                                }
                                //gp.deleteAID(aid, reg.allPackageAIDs().contains(aid) || args.has(OPT_FORCE));
                                System.out.println("Deleted successfully!!!");
                                statusMessage.append("Deleted successfully!!!").append(System.lineSeparator());
                            } catch (GPException e) {
                                if (!gp.getRegistry().allAIDs().contains(aid)) {
                                    System.err.println("Could not delete AID (not present on card): " + aid);
                                    statusMessage.append("Could not delete AID (not present on card): " + aid).append(System.lineSeparator());
                                } else {
                                    System.err.println("Could not delete AID: " + aid);
                                    statusMessage.append("Could not delete AID: " + aid).append(System.lineSeparator());
                                    if (e.sw == 0x6985) {
                                        System.err.println("Deletion not allowed. Some app still active?");
                                        statusMessage.append("Deletion not allowed. Some app still active?").append(System.lineSeparator());
                                    } else {
                                        throw e;
                                    }
                                }
                            }
                        }
                        /////////////////
                        return true;
                    } catch (CardException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Delete", JOptionPane.INFORMATION_MESSAGE);
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } catch (GPException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete", JOptionPane.INFORMATION_MESSAGE);
                        Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private static Privileges addPrivs(Privileges privs, String v) {
        if (v == null) {
            return privs;
        }
        String[] parts = v.split(",");
        for (String s : parts) {
            Privilege p = Privilege.lookup(s.trim());
            if (p == null) {
                throw new IllegalArgumentException("Unknown privilege: " + s.trim());
            } else {
                privs.add(p);
            }
        }
        return privs;
    }

    private static byte[] getInstParams(OptionSet args) {
        if (args.has(OPT_PARAMS)) {
            String arg = (String) args.valueOf(OPT_PARAMS);
            return HexUtils.stringToBin(arg);
        } else {
            return new byte[0];
        }
    }

    public static void listRegistry(GPRegistry reg, StringBuilder out, boolean verbose) {
        String tab = "";//5 spaces
        for (GPRegistryEntry e : reg) {
            AID aid = e.getAID();
            System.out.println(e.getType().toShortString() + ": " + HexUtils.bin2hex(aid.getBytes()) + " (" + e.getLifeCycleString() + ")");
            out.append(e.getType().toShortString() + ": " + HexUtils.bin2hex(aid.getBytes()) + " (" + e.getLifeCycleString() + ")").append(System.lineSeparator());
            if (e.getType() != GPRegistryEntry.Kind.IssuerSecurityDomain && verbose) {
                out.append(" (" + GPUtils.byteArrayToReadableString(aid.getBytes()) + ")").append(System.lineSeparator());
            } else {
                //out.append(System.lineSeparator());
            }

            if (e.getDomain() != null) {
                out.append(tab + "Parent:  " + e.getDomain()).append(System.lineSeparator());
            }
            if (e.getType() == GPRegistryEntry.Kind.ExecutableLoadFile) {
                GPRegistryEntryPkg pkg = (GPRegistryEntryPkg) e;
                if (pkg.getVersion() != null) {
                    out.append(tab + "Version: " + pkg.getVersionString()).append(System.lineSeparator());
                }
                for (AID a : pkg.getModules()) {
                    out.append(tab + "Applet:  " + HexUtils.bin2hex(a.getBytes())).append(System.lineSeparator());
                    if (verbose) {
                        out.append(" (" + GPUtils.byteArrayToReadableString(a.getBytes()) + ")").append(System.lineSeparator());
                    } else {
                        //out.append(System.lineSeparator());
                    }
                }
            } else {
                GPRegistryEntryApp app = (GPRegistryEntryApp) e;
                if (app.getLoadFile() != null) {
                    out.append(tab + "From:    " + app.getLoadFile()).append(System.lineSeparator());
                }
                //if (!app.getPrivileges().isEmpty()) {
                out.append(tab + "Privs:   " + app.getPrivileges()).append(System.lineSeparator());
                //}
            }
            out.append(System.lineSeparator());
        }
    }

    private boolean connectToCard(StringBuilder statusMessage) {
        //System.out.println("combobox: " + cardReaderListComboBox.getSelectedItem().toString());
        String reader = cardReaderListComboBox.getSelectedItem().toString();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = cardReaderMap.get(reader);
                if (cardTerminal.isCardPresent()) {
                    System.out.println("Connect to card");
                    //statusMessage.append("Connect to card").append(System.lineSeparator());
                    Card card = null;
                    CardChannel channel = null;
                    try {
                        card = cardTerminal.connect("*");
                        // We use apdu4j which by default uses jnasmartcardio
                        // which uses real SCardBeginTransaction
                        card.beginExclusive();
                        channel = card.getBasicChannel();
                        System.out.println("Reader: " + cardTerminal.getName());
                        System.out.println("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
                        statusMessage.append("Reader: ").append(cardTerminal.getName()).append(System.lineSeparator())
                                .append("ATR: ").append(HexUtils.bin2hex(card.getATR().getBytes())).append(System.lineSeparator());
                        return true;
                    } catch (CardException e) {
                        System.err.println("Could not connect to " + cardTerminal.getName() + ": " + TerminalManager.getExceptionMessage(e));
                        statusMessage.append("Could not connect to ").append(cardTerminal.getName()).append(": ").append(TerminalManager.getExceptionMessage(e)).append(System.lineSeparator());
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    System.out.println("Card is not present!!!");
                    statusMessage.append("Card is not present!!!").append(System.lineSeparator());
                }
            } catch (CardException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Connect", JOptionPane.INFORMATION_MESSAGE);
                Logger.getLogger(JCPlayStoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private static boolean needsAuthentication(OptionSet args) {
        String[] yes = new String[]{OPT_LIST, OPT_LOAD, OPT_INSTALL, OPT_DELETE, OPT_DELETE_KEY, OPT_CREATE,
            OPT_ACR_ADD, OPT_ACR_DELETE, OPT_LOCK, OPT_UNLOCK, OPT_LOCK_ENC, OPT_LOCK_MAC, OPT_LOCK_DEK, OPT_MAKE_DEFAULT,
            OPT_UNINSTALL, OPT_SECURE_APDU, OPT_DOMAIN, OPT_LOCK_CARD, OPT_UNLOCK_CARD, OPT_LOCK_APPLET, OPT_UNLOCK_APPLET,
            OPT_STORE_DATA, OPT_INITIALIZE_CARD, OPT_SECURE_CARD, OPT_RENAME_ISD, OPT_SET_PERSO, OPT_SET_PRE_PERSO, OPT_MOVE,
            OPT_PUT_KEY};

        for (String s : yes) {
            if (args.has(s)) {
                return true;
            }
        }
        return false;
    }

    private void updateTextArea(final String message, final boolean append) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (append) {
                    statusTextArea.append(message);
                } else {
                    statusTextArea.setText(message);
                }

            }
        });
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                if(append){
//                    statusTextArea.append(message);
//                }else{
//                    statusTextArea.setText(message);
//                }
//                
//            }
//        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JCPlayStoreClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cardReaderListComboBox;
    private javax.swing.JComboBox<String> commandComboBox;
    private javax.swing.JButton commonButton;
    private javax.swing.JComboBox<String> commonComboBox;
    private javax.swing.JLabel commonLabel;
    private javax.swing.JRadioButtonMenuItem debugRadioButtonMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton goButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem listCardReadersMenuItem;
    private javax.swing.JMenu settingMenu;
    private javax.swing.JTextArea statusTextArea;
    private javax.swing.JMenu tasksMenu;
    private javax.swing.JRadioButtonMenuItem verboseRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem warningRadioButtonMenuItem;
    // End of variables declaration//GEN-END:variables
}
