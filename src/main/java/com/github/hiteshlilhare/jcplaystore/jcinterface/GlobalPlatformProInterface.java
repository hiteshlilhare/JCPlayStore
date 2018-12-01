/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcinterface;

import apdu4j.HexUtils;
import apdu4j.TerminalManager;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardSecurityDomian;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardBean;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardReaderStatusListener;
import com.github.hiteshlilhare.jcplaystore.ui.util.CardReaderMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.javacard.AID;
import pro.javacard.CAPFile;
import pro.javacard.gp.GPData;
import pro.javacard.gp.GPException;
import pro.javacard.gp.GPKey;
import pro.javacard.gp.GPRegistry;
import pro.javacard.gp.GPRegistryEntry;
import pro.javacard.gp.GPRegistryEntryApp;
import pro.javacard.gp.GPRegistryEntryPkg;
import pro.javacard.gp.GPSessionKeyProvider;
import pro.javacard.gp.GPUtils;
import pro.javacard.gp.GlobalPlatform;
import pro.javacard.gp.PlaintextKeys;
import static pro.javacard.gp.PlaintextKeys.Diversification.EMV;
import static pro.javacard.gp.PlaintextKeys.Diversification.KDF3;
import static pro.javacard.gp.PlaintextKeys.Diversification.VISA2;
import pro.javacard.gp.PythiaKeys;

/**
 *
 * @author Hitesh
 */
public class GlobalPlatformProInterface extends GPCommandLineInterface implements Messages {

    public enum COMMANDS {
        LIST, DELETE, INSTALL, UNINSTALL;
    }

    private static final Logger logger = LoggerFactory.getLogger(GlobalPlatformProInterface.class);
    private static boolean isVerbose = false;
    private final static String CMD_CONNECT = "Connect";
    private final static String CMD_LIST = "List";
    private final static String CMD_INSTALL = "Install";
    private final static String CMD_DELETE = "Delete";
    private static final String JNA_CLASS = "jnasmartcardio.Smartcardio";
    private String[] commandList = new String[]{CMD_CONNECT, CMD_LIST, CMD_INSTALL, CMD_DELETE};

    private final static String DEF_AID_COMBO_TXT = "<Please provide AID>";
    private final static String DEF_CAP_FILE_COMBO_TXT = "<Please provide CAP file path>";

    // Database related constants.
    private final static String JC_APP_DIR = "JCAPPStore";
    private final static String JC_DB_FILE = "jcsqlite.db";
    private final static String DB_URL = "jdbc:sqlite:" + FileSystemView.getFileSystemView().getDefaultDirectory() + "/" + JC_APP_DIR + "/" + JC_DB_FILE;

    private static GlobalPlatformProInterface _instance;

    public static GlobalPlatformProInterface getInstance(String... cmdLineArgs) {
        if (_instance == null) {
            _instance = new GlobalPlatformProInterface(cmdLineArgs);
        }
        return _instance;
    }

    public GlobalPlatformProInterface(String... cmdLineArgs) {
        javaCardBeanList = new ArrayList<>();

        makeArgs(new String[]{"-v"});

    }

    private boolean makeArgs(String[] cmdLineOptions) {
        boolean flag = true;
        try {
            args = parseArguments(cmdLineOptions);
        } catch (IOException ex) {
            flag = false;
            if (commandPrompt) {
                logger.error(ex.getMessage() + " : " + PARSING_ARGS_ERROR);
                fail(ex.getLocalizedMessage() + " : " + PARSING_ARGS_ERROR);
            } else {
                logger.info(PARSING_ARGS_ERROR);
            }
        }
        return flag;
    }

    public void initVerbose() {
        if (args.has(OPT_VERSION) || args.has(OPT_VERBOSE) || args.has(OPT_DEBUG) || args.has(OPT_INFO)) {
            String version = GlobalPlatform.getVersion();
            // Append host information
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
                logger.info(ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Return Reader's name for all readers
     *
     * @return
     */
    public Set<String> getReadersName() {
        return cardReadersMap.keySet();
    }

    public JavaCardReaderBean removeReaderBean(String readerName) {
        return cardReadersMap.remove(readerName);
    }

    /**
     * Return JavaCardReaderBeans corresponding to all readers
     *
     * @return
     */
    public ArrayList<JavaCardReaderBean> getAllReadersBean() {
        return new ArrayList<>(cardReadersMap.values());
    }

    /**
     * Return JavaCardReaderBean corresponding to given reader
     *
     * @param readerName
     * @return
     */
    public JavaCardReaderBean getReaderBean(String readerName) {
        return cardReadersMap.get(readerName);
    }

    /**
     *
     * @param spec provider specification
     * @return
     */
    public CardReaderMap getReaders(String spec) {
        final TerminalFactory tf;
        try {
            if (commandPrompt) {
                tf = TerminalManager.getTerminalFactory((String) args.valueOf(OPT_TERMINALS));
            } else {
                tf = TerminalManager.getTerminalFactory(spec);
            }
            CardTerminals terminals = tf.terminals();
            Set<String> presentReaders = cardReadersMap.keySet();
            //1. Prepare list of present terninal/reader
            //2. Notify listeners about new reader addition
            //3. Add JavaCardReaderBean to cardReaderMap for new Reader.
            ArrayList<String> terminalNames = new ArrayList<>();
            ArrayList<String> readerAdded = new ArrayList<>();
            ArrayList<String> readerRemoved = new ArrayList<>();
            boolean notifyListeners = false;

            for (CardTerminal term : terminals.list()) {
                //Prepare List of Present readers
                terminalNames.add(term.getName());
                if (!presentReaders.contains(term.getName())) {
                    //Add Reader to card reader map
                    JavaCardReaderBean cardReaderBean = new JavaCardReaderBean();
                    cardReaderBean.setReaderName(term.getName());
                    cardReaderBean.setCardTerminal(term);
                    //Commented since only monitoring thread can change the status. 
                    //cardReaderBean.setCardPresent(term.isCardPresent());
                    //System.out.println((term.isCardPresent() ? "[*] " : "[ ] ") + term.getName());
                    cardReaderBean.setFresh(true); // Can notify to get new applet informations
                    cardReadersMap.put(term.getName(), cardReaderBean);
                    //Add it to readerAdded list
                    readerAdded.add(term.getName());
                    //Set Notify listener Flag.
                    notifyListeners = true;
                }
            }
            //Notify listeners about new reader
            if (notifyListeners) {
                if (cardReaderStatusListener != null) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        cardReaderStatusListener.readerAdded(readerAdded);
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                cardReaderStatusListener.readerAdded(readerAdded);
                            }
                        });
                    }
//                    cardReaderStatusListener.readerAdded(readerAdded);
                }
            }
            //Reset Notify Listeners Flag
            notifyListeners = false;
            //1. Notify Listeners about reader removal
            for (String key : cardReadersMap.keySet()) {
                if (!terminalNames.contains(key)) {
                    //Add it to readerRemoved list
                    readerRemoved.add(key);
                    //Set Notify listener Flag.
                    notifyListeners = true;
                }
            }
            //Notify Listeners
            if (notifyListeners) {
                if (cardReaderStatusListener != null) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        cardReaderStatusListener.readerRemoved(readerRemoved);
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                cardReaderStatusListener.readerRemoved(readerRemoved);
                            }
                        });
                    }
                }
            }
            //Reset Notify Listeners Flag
            notifyListeners = false;
            //If no reader is for add/remove then notify listener to show
            //no reder present panel.
            if (readerAdded.isEmpty() && readerRemoved.isEmpty() && terminals.list().isEmpty()) {
                if (cardReaderStatusListener != null) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        cardReaderStatusListener.noReaderPresent();
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                cardReaderStatusListener.noReaderPresent();
                            }
                        });
                    }
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + System.lineSeparator() + GET_READERS_ERROR, "Start", JOptionPane.INFORMATION_MESSAGE);
            //fail(GET_READERS_ERROR);
        } catch (CardException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + System.lineSeparator() + GET_READERS_ERROR, "Start", JOptionPane.INFORMATION_MESSAGE);
            //fail(GET_READERS_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return cardReadersMap;
    }

    /**
     * This method fills information of java card applications, security domain
     * etc in JavaCardReaderBean. Note : It is being called from JTree selection
     * change listeners valueChanged method. which is being called when we
     * select any of JTree node therefore taken care for reader node selection
     * only.
     *
     * @param javaCardReaderBean
     * @param force
     * @param cmdOption
     * @throws Exception
     */
    public void getListOfApplets(JavaCardReaderBean javaCardReaderBean, boolean force, String... cmdOption) throws Exception {
        if (force) {
            //Though addition of app from outside application to card was visible
            //by bypassing isFresh check but removal of app was not visible so
            //cleared the JavaCardBean so that removal of any app from outside 
            //Application can be refreshed.
            javaCardReaderBean.getJavaCardBean().setFresh(false);
        } else {
            if (javaCardReaderBean.getJavaCardBean().isFresh()) {
                logger.info("JavaCardBean is fresh!!!");
                return;
            }
        }
        ExecuteArgs exeArgs = new ExecuteArgs();
        exeArgs.setJavaCardReaderBean(javaCardReaderBean);
        if (cmdOption.length == 0) {
            exeArgs.setCmdLineOPtions(new String[]{"-l"});
        } else {
            exeArgs.setCmdLineOPtions(cmdOption);
        }
        exeArgs.setCmd(COMMANDS.LIST);
        executeCommand(exeArgs);
    }

    /**
     * Deletes Applet from the java card Note: This method needs to be called on
     * fresh JavaCardBean which means after getting list of Applets in java
     * card.
     *
     * @param aid
     * @param readerName
     * @param cmdOPtion
     * @return
     * @throws Exception
     */
    // --delete <aid> or --delete --default
    public boolean deleteApplet(String readerName, String aid, String... cmdOPtion) throws Exception {
        logger.info("Deleting AID: " + aid);
        JavaCardReaderBean javaCardReaderBean = cardReadersMap.get(readerName);
        if (javaCardReaderBean == null) {
            logger.error("Reader corresponding to " + readerName + " does not exits in cardReaderMap.");
            return false;
        } else if (!javaCardReaderBean.isFresh() || !javaCardReaderBean.getJavaCardBean().isFresh()) {
            logger.error("JavaCardReaderBean(Fresh: " + javaCardReaderBean.isFresh()
                    + ") and  JavaCardBean(Fresh: " + javaCardReaderBean.getJavaCardBean().isFresh() + ")");
            return false;
        }
        ExecuteArgs exeArgs = new ExecuteArgs();
        exeArgs.setJavaCardReaderBean(javaCardReaderBean);
        if (cmdOPtion.length == 0) {
            if (javaCardReaderBean.getJavaCardBean().isEMV()) {
                exeArgs.setCmdLineOPtions(new String[]{"--emv", "--delete", aid});
            } else {
                exeArgs.setCmdLineOPtions(new String[]{"--delete", aid});
            }

        } else {
            ArrayList<String> newOptions = new ArrayList<>(Arrays.asList(cmdOPtion));
            if (javaCardReaderBean.getJavaCardBean().isEMV()) {
                newOptions.add("--emv");
                newOptions.add("--delete");
                newOptions.add(aid);
            } else {
                newOptions.add("--delete");
                newOptions.add(aid);
            }
            exeArgs.setCmdLineOPtions(newOptions.toArray(new String[0]));
        }
        exeArgs.setCmd(COMMANDS.DELETE);
        return executeCommand(exeArgs);
    }

// --install <applet.cap> (--applet <aid> --create <aid> --privs <privs> --params <params>)    
    public void installApplet(JavaCardReaderBean javaCardReaderBean) throws Exception {
        ExecuteArgs exeArgs = new ExecuteArgs();
        exeArgs.setJavaCardReaderBean(javaCardReaderBean);
        exeArgs.setCmdLineOPtions(new String[]{"--install"});
        exeArgs.setCmd(COMMANDS.INSTALL);
        executeCommand(exeArgs);
    }

// --uninstall <cap>
    public void uninstallApplet(JavaCardReaderBean javaCardReaderBean) throws Exception {
        ExecuteArgs exeArgs = new ExecuteArgs();
        exeArgs.setJavaCardReaderBean(javaCardReaderBean);
        exeArgs.setCmdLineOPtions(new String[]{"--uninstall"});
        exeArgs.setCmd(COMMANDS.UNINSTALL);
        executeCommand(exeArgs);
    }

    /**
     * Be careful of handling false status because it could be because of card
     * absent. Boolean return value is not sufficient to handle it.
     *
     * @param exeArgs
     * @return
     * @throws Exception
     */
    private boolean executeCommand(ExecuteArgs exeArgs) throws Exception {

        if (!makeArgs(exeArgs.getCmdLineOptions())) {
            return false;
        }
        
        if (exeArgs.getJavaCardReaderBean().getCardTerminal().isCardPresent()) {
            logger.info("Card present in reader.");
            Card card = null;
            CardChannel channel = null;
            try {
                card = exeArgs.getJavaCardReaderBean().getCardTerminal().connect("*");
                // We use apdu4j which by default uses jnasmartcardio
                // which uses real SCardBeginTransaction
                if (card == null) {
                    logger.info("Unable to connect");
                    return false;
                }
                card.beginExclusive();
                channel = card.getBasicChannel();
                logger.info("Reader: " + exeArgs.getJavaCardReaderBean().getReaderName());
                logger.info("ATR: " + HexUtils.bin2hex(card.getATR().getBytes()));
                //populate CARD ATR Value
                exeArgs.getJavaCardReaderBean().getJavaCardBean().getCardDetails().setAtr(card.getATR());
                exeArgs.getJavaCardReaderBean().getJavaCardBean().getCardDetails().setATRString(HexUtils.bin2hex(card.getATR().getBytes()));

                if (args.has(OPT_SDAID)) {
                    gp = GlobalPlatform.connect(channel, AID.fromString(args.valueOf(OPT_SDAID)));
                } else {
                    // Oracle only applies if no other arguments given
                    gp = GlobalPlatform.discover(channel);
                    // FIXME: would like to get AID from oracle as well.
                }

                // Extract information
                // Normally assume a single master key
                final GPSessionKeyProvider keys;
                if (args.has(OPT_KEYS)) {
                    // keys come from custom provider
                    logger.error(OPT_KEYS + "Command line opotion is not yet implemented");
                    return false;
                    //keys = PlaintextKeys.fromMasterKey(GPData.getDefaultKey());
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
                                logger.error("Don't know how to calculate KCV (Key Check Value) for the key"); // FIXME: all keys are RAW currently
                                return false;
                            }
                            // Check KCV (Key Check Value)
                            if (!Arrays.equals(given, expected)) {
                                logger.error("KCV (Key Check Value) does not match, expected " + HexUtils.bin2hex(expected) + " but given " + HexUtils.bin2hex(given));
                                return false;
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
                                logger.info("Warning: no keys given, using default test key " + HexUtils.bin2hex(GPData.getDefaultKey().getBytes()));
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
                    gp.setSpec(GlobalPlatform.GPSpec.OP201);
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
                    gp.setStrict(true);
                    try {
                        gp.openSecureChannel(keys, null, 0, mode);
                    } catch (GPException ex) {
                        //add --emv in option list
                        String[] options = exeArgs.getCmdLineOptions();
                        String[] newOptions = new String[options.length + 1];
                        newOptions[0] = "--emv";
                        int i = 1;
                        for (String option : options) {
                            newOptions[i] = option;
                            i = i + 1;
                        }
                        exeArgs.setCmdLineOPtions(newOptions);
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                        return executeCommand(exeArgs);
                    }

                    GPRegistry reg = gp.getRegistry();
                    File capfile;
                    CAPFile instcap;

                    switch (exeArgs.getCmd()) {
                        case LIST:
                            //if (exeArgs.has(OPT_LIST)) {
                            if (args.has(OPT_EMV)) {
                                logger.info("Set EMV option true for JavaCardBean");
                                exeArgs.getJavaCardReaderBean()
                                        .getJavaCardBean().setEMV(true);
                            }
                            
                            listRegistry(reg,
                                    exeArgs.getJavaCardReaderBean().getJavaCardBean(),
                                    true);
                            //}
                            break;
                        case DELETE:
                            //if (args.has(OPT_DELETE)) {
                            // DWIM: assume that default selected is the one to be deleted
                            if (args.has(OPT_DEFAULT) && reg.getDefaultSelectedAID() != null) {
                                if (reg.getDefaultSelectedPackageAID() != null) {
                                    gp.deleteAID(reg.getDefaultSelectedPackageAID(), true);
                                } else {
                                    logger.error("Could not identify default selected application!");
                                    return false;
                                }
                            }
                            logger.info("OPT_DELETE: " + args.valuesOf(OPT_DELETE).toString());
                            @SuppressWarnings("unchecked") List<String> aids = (List<String>) args.valuesOf(OPT_DELETE);

                            for (String s : aids) {
                                AID aid = AID.fromString(s);
                                try {
                                    // If the AID represents a package or otherwise force is enabled.
                                    gp.deleteAID(aid, reg.allPackageAIDs().contains(aid) || args.has(OPT_FORCE));
                                } catch (GPException e) {
                                    if (!gp.getRegistry().allAIDs().contains(aid)) {
                                        logger.error("Could not delete AID (not present on card): " + aid);
                                    } else {
                                        logger.error("Could not delete AID: " + aid);
                                        if (e.sw == 0x6985) {
                                            logger.error("Deletion not allowed. Some app still active?");
                                        } else {
                                            logger.error("", e);
                                        }
                                    }
                                    return false;
                                }
                            }
                            //}
                            break;
                        case INSTALL:
                            //if (args.has(OPT_INSTALL)) {
                            capfile = (File) args.valueOf(OPT_INSTALL);

                            instcap = CAPFile.fromStream(new FileInputStream(capfile));

                            if (args.has(OPT_VERBOSE)) {
                                instcap.dump(System.out);
                            }

                            //GPRegistry reg = gp.getRegistry();
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
                                    logger.info("CAP loaded");
                                } catch (GPException e) {
                                    if (e.sw == 0x6985 || e.sw == 0x6A80) {
                                        logger.error("Loading failed. Are you sure the CAP file (JC version, packages, sizes) is compatible with your card?");
                                    }
                                    logger.error("", e);
                                    return false;
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
                                    logger.error("CAP contains more than one applet, specify the right one");
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
                                logger.warn("WARNING: Applet " + instanceaid + " already present on card");
                            }

                            // shoot
                            gp.installAndMakeSelectable(instcap.getPackageAID(), appaid, instanceaid, privs, getInstParams(args), null);
                            //}
                            break;
                        case UNINSTALL:
                            //if (args.has(OPT_UNINSTALL)) {
                            capfile = (File) args.valueOf(OPT_UNINSTALL);
                            instcap = CAPFile.fromStream(new FileInputStream(capfile));
                            AID aid = instcap.getPackageAID();
                            if (!gp.getRegistry().allAIDs().contains(aid)) {
                                System.out.println(aid + " is not present on card!");
                            } else {
                                gp.deleteAID(aid, true);
                                System.out.println(aid + " deleted.");
                            }
                            //}
                            break;
                    }
                    return true;
                } else {
                    return false;
                }
            } catch (CardException e) {
                logger.error("Could not connect to " + exeArgs.getJavaCardReaderBean().getReaderName() + ": " + TerminalManager.getExceptionMessage(e));
                return false;
            } catch (GPException ex) {
                logger.error("Unable to get list of applets" + ex.getMessage());
                return false;
            } finally {
                if (card != null) {
                    card.endExclusive();
                    card.disconnect(true);
                    card = null;
                }
            }
        } else {
            logger.info("Card is not present in reader");
            return false;
        }
    }

    public static void listRegistry(GPRegistry reg, JavaCardBean javaCardBean, boolean verbose) {
        //Set package related details
        List<GPRegistryEntryPkg> pkgs = reg.allPackages();
        for (GPRegistryEntryPkg pkg : pkgs) {
            if (pkg.getModules().isEmpty()) {
//                CardAppDetail appDetails = new CardAppDetail(javaCardBean);
//                appDetails.setPkgVersion(pkg.getVersionString());
//                appDetails.setPkgAid(HexUtils.bin2hex(pkg.getAID().getBytes()));
//                appDetails.setPkgLifeCycleState(pkg.getLifeCycleString());
//                appDetails.setPkgDomainAID(
//                        pkg.getDomain() != null ? HexUtils.bin2hex(pkg.getDomain().getBytes()) : null);
//                //Set Freshness Status
//                appDetails.setFresh(true);
//                //Add pkg aid to pkgaid to appaid Map
//                javaCardBean.addPairToPkgMap(HexUtils.bin2hex(pkg.getAID().getBytes()), null);
            } else {
                for (AID module : pkg.getModules()) {
                    CardAppDetail appDetails = new CardAppDetail(javaCardBean);
                    //Set package related details.
                    appDetails.setPkgVersion(pkg.getVersionString());
                    appDetails.setPkgAid(HexUtils.bin2hex(pkg.getAID().getBytes()));
                    appDetails.setPkgLifeCycleState(pkg.getLifeCycleString());
                    appDetails.setPkgDomainAID(
                            pkg.getDomain() != null ? HexUtils.bin2hex(pkg.getDomain().getBytes()) : null);
                    //Set Applet AIDs
                    appDetails.setAid(HexUtils.bin2hex(module.getBytes()));
                    //Set Freshness Status
                    appDetails.setFresh(true);
                    //Populate Map of PKG AID and APP AID.
                    javaCardBean.addPairToPkgMap(HexUtils.bin2hex(pkg.getAID().getBytes()),
                            HexUtils.bin2hex(module.getBytes()));
                    javaCardBean.addPairToAppMap(HexUtils.bin2hex(module.getBytes()),
                            HexUtils.bin2hex(pkg.getAID().getBytes()));
                    //Add Card App Details to Map
                    javaCardBean.addCardAppDetail(HexUtils.bin2hex(module.getBytes()), appDetails);
                }
            }
        }
        //Set Applet Related Details
        List<GPRegistryEntryApp> apps = reg.allApplets();
        for (GPRegistryEntryApp app : apps) {
            String strAID = HexUtils.bin2hex(app.getAID().getBytes());
            CardAppDetail appDetails = javaCardBean.getCardAppDetail(strAID);
            //If Applet present without package.
            if (appDetails == null) {
                logger.info(strAID + " applet does not have package");
                appDetails = new CardAppDetail(javaCardBean);
                appDetails.setAid(strAID);
                appDetails.setFresh(true);
                //Add app id to App to pkg map.
                javaCardBean.addPairToAppMap(strAID, null);
                //Add card app details to card details map.
                javaCardBean.addCardAppDetail(strAID, appDetails);
            }
            //set othe app details.
            appDetails.setLifeCycleState(app.getLifeCycleString());
            appDetails.setPrivileges(HexUtils.bin2hex(app.getPrivileges().toBytes()));
            appDetails.setPrivilegesString(app.getPrivileges().toString());
            appDetails.setDomainAID(
                    app.getDomain() != null ? HexUtils.bin2hex(app.getAID().getBytes()) : null);
        }
        //Update Security Domian Related Information
        List<GPRegistryEntryApp> domains = reg.allDomains();
        for (GPRegistryEntryApp domain : domains) {
            if (domain.getType() == GPRegistryEntry.Kind.IssuerSecurityDomain) {
                //Fill Issuer's Security Domain information. 
                CardSecurityDomian issuerSD = javaCardBean.getIssuerSecurityDomian();
                issuerSD.setAid(HexUtils.bin2hex(domain.getAID().getBytes()));
                issuerSD.setLifeCycleState(domain.getLifeCycleString());
                issuerSD.setPrivileges(HexUtils.bin2hex(domain.getPrivileges().toBytes()));
                issuerSD.setPrivilegesString(domain.getPrivileges().toString());
                //Set Freshness 
                issuerSD.setFresh(true);
            } else {
                //Add Othet Security Domain Informations.
                CardSecurityDomian csd = new CardSecurityDomian();
                csd.setAid(HexUtils.bin2hex(domain.getAID().getBytes()));
                csd.setLifeCycleState(domain.getLifeCycleString());
                csd.setPrivileges(HexUtils.bin2hex(domain.getPrivileges().toBytes()));
                csd.setPrivilegesString(domain.getPrivileges().toString());
                csd.setFresh(true);
                javaCardBean.addSecurityDomain(HexUtils.bin2hex(domain.getAID().getBytes()), csd);
            }
            javaCardBean.setFresh(true);
        }
    }

    /**
     * Method is copied from file GPTool.java of GlobalPlatformPro project at
     * github https://github.com/martinpaljak/GlobalPlatformPro
     *
     * @param args
     * @return
     */
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

    private static GPRegistryEntry.Privileges getInstPrivs(OptionSet args) {
        GPRegistryEntry.Privileges privs = new GPRegistryEntry.Privileges();
        if (args.has(OPT_PRIVS)) {
            addPrivs(privs, (String) args.valueOf(OPT_PRIVS));
        }
        if (args.has(OPT_DEFAULT)) {
            privs.add(GPRegistryEntry.Privilege.CardReset);
        }
        if (args.has(OPT_TERMINATE)) {
            privs.add(GPRegistryEntry.Privilege.CardLock);
            privs.add(GPRegistryEntry.Privilege.CardTerminate);
        }
        return privs;
    }

    private static GPRegistryEntry.Privileges addPrivs(GPRegistryEntry.Privileges privs, String v) {
        if (v == null) {
            return privs;
        }
        String[] parts = v.split(",");
        for (String s : parts) {
            GPRegistryEntry.Privilege p = GPRegistryEntry.Privilege.lookup(s.trim());
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
    ////////////////////////////////////End Copied Methods//////////////////////

    public static void main(String[] args) {
        GlobalPlatformProInterface globalPlatformProInterface = new GlobalPlatformProInterface();
        globalPlatformProInterface.initVerbose();
    }

    public boolean isCommandPrompt() {
        return commandPrompt;
    }

    public void setCommandPrompt(boolean isCommandPrompt) {
        this.commandPrompt = isCommandPrompt;
    }

    public ArrayList<JavaCardBean> getJavaCardBeanList() {
        return javaCardBeanList;
    }

    public void setCardReaderStatusListener(CardReaderStatusListener listener) {
        cardReaderStatusListener = listener;
    }

    private CardReaderStatusListener cardReaderStatusListener;
    //private final TreeMap<String, JavaCardReaderBean> cardReadersMap = new TreeMap<>();
    private final CardReaderMap cardReadersMap = new CardReaderMap();
    private ArrayList<JavaCardBean> javaCardBeanList;
    private boolean commandPrompt = false;

    private OptionSet args = null;
    // GlobalPlatform specific
    private GlobalPlatform gp = null;
}

class ExecuteArgs {

    private JavaCardReaderBean javaCardReaderBean;
    private String[] cmdLineOPtions;
    private GlobalPlatformProInterface.COMMANDS cmd;

    public GlobalPlatformProInterface.COMMANDS getCmd() {
        return cmd;
    }

    public void setCmd(GlobalPlatformProInterface.COMMANDS cmd) {
        this.cmd = cmd;
    }

    public void setJavaCardReaderBean(JavaCardReaderBean javaCardReaderBean) {
        this.javaCardReaderBean = javaCardReaderBean;
    }

    public void setCmdLineOPtions(String[] cmdLineOPtions) {
        this.cmdLineOPtions = cmdLineOPtions;
    }

    public JavaCardReaderBean getJavaCardReaderBean() {
        return javaCardReaderBean;
    }

    public String[] getCmdLineOptions() {
        return cmdLineOPtions;
    }

}

//    public static void listRegistry(GPRegistry reg, JavaCardBean javaCardBean, boolean verbose) {
//        String tab = "     ";
//        for (GPRegistryEntry e : reg) {
//            AID aid = e.getAID();
//            System.out.println(e.getType().toShortString() + ": " + HexUtils.bin2hex(aid.getBytes()) + " (" + e.getLifeCycleString() + ")");
//            if (e.getType() != GPRegistryEntry.Kind.IssuerSecurityDomain && verbose) {
//                System.out.println(" (" + GPUtils.byteArrayToReadableString(aid.getBytes()) + ")");
//            } else {
//                System.out.println();
//            }
//
//            if (e.getDomain() != null) {
//                System.out.println(tab + "Parent:  " + e.getDomain());
//            }
//            if (e.getType() == GPRegistryEntry.Kind.ExecutableLoadFile) {
//                GPRegistryEntryPkg pkg = (GPRegistryEntryPkg) e;
//                if (pkg.getVersion() != null) {
//                    System.out.println(tab + "Version: " + pkg.getVersionString());
//                }
//                for (AID a : pkg.getModules()) {
//                    System.out.println(tab + "Applet:  " + HexUtils.bin2hex(a.getBytes()));
//                    if (verbose) {
//                        System.out.println(" (" + GPUtils.byteArrayToReadableString(a.getBytes()) + ")");
//                    } else {
//                        System.out.println();
//                    }
//                }
//            } else {
//                GPRegistryEntryApp app = (GPRegistryEntryApp) e;
//                if (app.getLoadFile() != null) {
//                    System.out.println(tab + "From:    " + app.getLoadFile());
//                }
//                //if (!app.getPrivileges().isEmpty()) {
//                System.out.println(tab + "Privs:   " + app.getPrivileges());
//                //}
//            }
//            System.out.println();
//        }
//    }
