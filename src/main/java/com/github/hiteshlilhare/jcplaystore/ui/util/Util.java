/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import apdu4j.TerminalManager;
import com.github.hiteshlilhare.jcplaystore.CardDetails;
import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.JCPlayStoreClient;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.MainFrame;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.slf4j.LoggerFactory;
import pro.javacard.gp.GPData;
import static pro.javacard.gp.GPData.fetchCPLC;
import static pro.javacard.gp.GPData.fetchKeyInfoTemplate;
import static pro.javacard.gp.GPData.getData;
import pro.javacard.gp.GPException;

/**
 *
 * @author Hitesh
 */
public class Util {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Util.class);
    public static final String SERVER_URL = Config.getServerUrl();
    public static final String APP_LIST_SERVICE = SERVER_URL + "/gvrad";
    public static final String GET_APP_SERVICE = SERVER_URL + "/gaa";
    public static final String DOWNLOAD_APP_SERVICE = SERVER_URL + "/downloadAppZip";
    public static final String RBUILD_APP_SERVICE = SERVER_URL + "/rbuild";

    // post request media type
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * Do post request to given url with json as payload.
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Do get request to the given url.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGetRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void showInformationMessageDialog(String message,
            String title) {
        JOptionPane.showMessageDialog(MainFrame.getInstance(), message,
                title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Returns true if <code>zipFile</code> is a valid zip file.
     *
     * @param zipFie
     * @return
     */
    public static boolean isZipFile(File zipFie) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFie);
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    /**
     * Verify the detached signature.
     *
     * @param fileName
     * @param sigFileName
     * @param keyIn
     * @return
     */
    public static boolean verifySignature(String fileName,
            String sigFileName,
            InputStream keyIn) {

        InputStream sinIn = null;
        InputStream dIn = null;
        try {
            sinIn = new BufferedInputStream(
                    new FileInputStream(sigFileName));
            sinIn = PGPUtil.getDecoderStream(sinIn);
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(sinIn);
            PGPSignatureList p3;
            Object o = pgpFact.nextObject();
            if (o instanceof PGPCompressedData) {
                PGPCompressedData c1 = (PGPCompressedData) o;

                pgpFact = new JcaPGPObjectFactory(c1.getDataStream());

                p3 = (PGPSignatureList) pgpFact.nextObject();
            } else {
                p3 = (PGPSignatureList) o;
            }
            PGPPublicKeyRingCollection pgpPubRingCollection
                    = new PGPPublicKeyRingCollection(
                            PGPUtil.getDecoderStream(keyIn),
                            new JcaKeyFingerprintCalculator());
            dIn = new BufferedInputStream(new FileInputStream(fileName));
            PGPSignature sig = p3.get(0);
            PGPPublicKey key = pgpPubRingCollection.getPublicKey(sig.getKeyID());
            sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
            int ch;
            while ((ch = dIn.read()) >= 0) {
                sig.update((byte) ch);
            }
            return sig.verify();
        } catch (Exception ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (sinIn != null) {
                try {
                    sinIn.close();
                } catch (IOException ex) {
                    //Ignore
                }
            }
            if (dIn != null) {
                try {
                    dIn.close();
                } catch (IOException ex) {
                    //Ignore
                }
            }
        }
        return false;
    }

    /**
     * Creates required directory structure a
     *
     * @throws HeadlessException
     */
    public static void createDirectoryStructure() throws HeadlessException {
        //Create local app store directory if not exist.
        File appStoreDir = new File(JCConstants.JC_APP_BASE_DIR);
        //Create App Base directory.
        if (!appStoreDir.exists()) {
            if (appStoreDir.isFile()) {
                try {
                    FileUtils.forceDelete(appStoreDir);
                } catch (IOException ex) {
                    logger.info("createDirectoryStructure", ex);
                    showInformationMessageDialog(
                            "Unable to delete " + appStoreDir + " file, "
                            + "please contact system administrator",
                            "Create Directory");

                    System.exit(0);
                }
            }
            if (appStoreDir.mkdir()) {
                //Create App Directory if not exists.
                File appDir = new File(JCConstants.JC_APP_BASE_DIR
                        + "/" + JCConstants.JC_APPS_DIR);
                if (!appDir.exists() || appDir.isFile()) {
                    if (!appDir.mkdir()) {
                        logger.info("Unable to create "
                                + JCConstants.JC_APP_BASE_DIR
                                + "/" + JCConstants.JC_APPS_DIR
                                + " directory.");
                        showInformationMessageDialog(
                                "Unable to create "
                                + JCConstants.JC_APP_BASE_DIR
                                + "/" + JCConstants.JC_APPS_DIR
                                + " directory, "
                                + "please contact system administrator.",
                                "Create Directory");
                        System.exit(0);
                    }
                }
                //Create Database Directory if not exists.
                File dbDir = new File(JCConstants.JC_APP_BASE_DIR
                        + "/" + JCConstants.JC_DB_DIR);
                if (!dbDir.exists() || dbDir.isFile()) {
                    if (!dbDir.mkdir()) {
                        logger.info("Unable to create "
                                + JCConstants.JC_APP_BASE_DIR
                                + "/" + JCConstants.JC_DB_DIR
                                + " directory.");
                        showInformationMessageDialog(
                                "Unable to create "
                                + JCConstants.JC_APP_BASE_DIR
                                + "/" + JCConstants.JC_DB_DIR
                                + " directory, "
                                + "please contact system administrator.",
                                "Create Directory");
                        System.exit(0);
                    }
                }
                //Create Sources Directory if not exists.
                File sourceDir = new File(JCConstants.JC_APP_BASE_DIR
                        + "/" + JCConstants.JC_SOURCES_DIR);
                if (!sourceDir.exists() || sourceDir.isFile()) {
                    if (!sourceDir.mkdir()) {
                        logger.info("Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_SOURCES_DIR
                                + " directory.");
                        showInformationMessageDialog(
                                "Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_SOURCES_DIR
                                + " directory, "
                                + "please contact system administrator.",
                                "Create Directory");
                        System.exit(0);
                    }
                }
                //Create tools Directory if not exists.
                File toolsDir = new File(JCConstants.JC_APP_BASE_DIR
                        + "/" + JCConstants.JC_TOOLS_DIR);
                if (!toolsDir.exists() || toolsDir.isFile()) {
                    if (!toolsDir.mkdir()) {
                        logger.info("Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_TOOLS_DIR
                                + " directory.");
                        showInformationMessageDialog(
                                "Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_TOOLS_DIR
                                + " directory, "
                                + "please contact system administrator.",
                                "Create Directory");
                        System.exit(0);
                    }
                }
                //Create temp Directory if not exists.
                File tempDir = new File(JCConstants.JC_APP_BASE_DIR
                        + "/" + JCConstants.JC_TEMP_DIR);
                if (!tempDir.exists() || tempDir.isFile()) {
                    if (!tempDir.mkdir()) {
                        logger.info("Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_TEMP_DIR
                                + " directory.");
                        showInformationMessageDialog(
                                "Unable to create "
                                + JCConstants.JC_APP_BASE_DIR + "/"
                                + JCConstants.JC_TEMP_DIR
                                + " directory, "
                                + "please contact system administrator.",
                                "Create Directory");
                        System.exit(0);
                    }
                }
            } else {
                logger.info("Unable to create "
                        + JCConstants.JC_APP_BASE_DIR + " directory.");
                showInformationMessageDialog(
                        "Unable to create local App Store directory.",
                        "JCPlayStore");
                System.exit(0);
            }
        }
    }

    public static CardDetails getCardDetails() {
        String reader = MainFrame.getInstance().getSelectedReader();
        if (reader != null && reader.length() > 0) {
            try {
                CardTerminal cardTerminal = GlobalPlatformProInterface
                        .getInstance().getReaderBean(reader).getCardTerminal();
                if (cardTerminal.isCardPresent()) {
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
                    } catch (CardException ex) {
                        logger.info("getCardDetails:Could not connect to "
                                + cardTerminal.getName() + ": "
                                + TerminalManager.getExceptionMessage(ex), ex);
                    } catch (GPException ex) {
                        logger.info("getCardDetails", ex);
                    } finally {
                        if (card != null) {
                            card.endExclusive();
                            card.disconnect(true);
                            card = null;
                        }
                    }
                } else {
                    logger.info("getCardDetails: Card is not present!!!");
                }
            } catch (jnasmartcardio.Smartcardio.JnaPCSCException ex) {
                logger.error("getCardDetails:Could not connect to "
                        + reader, ex);
            } catch (CardException ex) {
                logger.info("getCardDetails", ex);
            }
        }
        return null;
    }

    public static void setCardDetails(CardChannel channel,
            CardDetails cardDetails)
            throws CardException, GPException {
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
        byte[] cardCapabilities = getData(channel, 0x00, 0x67,
                "Card Capabilities", false);
        if (cardCapabilities != null) {
            cardDetails.setCardCapabilities(cardCapabilities);
        }
        // Print Key Info Template
        byte[] keyInfo = fetchKeyInfoTemplate(channel);
        if (keyInfo != null) {
            cardDetails.setKeyInfo(keyInfo);
        }
    }

//    public static void downloadWithAHC(String url, String localFilename) 
//            throws ExecutionException, InterruptedException, IOException {
//
//        FileOutputStream stream = new FileOutputStream(localFilename);
//        AsyncHttpClient client = Dsl.asyncHttpClient();
//
//        client.prepareGet(url).execute(new AsyncCompletionHandler<FileOutputStream>() {
//
//                @Override
//                public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
//                    stream.getChannel()
//                        .write(bodyPart.getBodyByteBuffer());
//                    return State.CONTINUE;
//                }
//                
//                public FileOutputStream onCompleted(Response response) throws Exception {
//                    return stream;
//                }
//            })
//            .get();
//
//        stream.getChannel().close();
//        client.close();
//    }
}
