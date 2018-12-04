/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.ui.mainframe.MainFrame;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

/**
 *
 * @author Hitesh
 */
public class Util {

    public static final String SERVER_URL = Config.getServerUrl();
    public static final String APP_LIST_SERVICE = SERVER_URL + "/gvrad";
    public static final String GET_APP_SERVICE = SERVER_URL + "/gaa";
    public static final String DOWNLOAD_APP_SERVICE = SERVER_URL + "/downloadAppZip";

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
