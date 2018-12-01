/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcbeans;

import com.github.hiteshlilhare.jcplaystore.CardDetails;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class JavaCardBean {

    private static final Logger logger = LoggerFactory.getLogger(JavaCardBean.class);
    private boolean fresh;
    private boolean EMV;
    private final HashMap<String, String> AppAIDAndPkgAID = new HashMap<>();
    private final HashMap<String, ArrayList<String>> PkgAIDAndAppAID = new HashMap<>();
    private final CardDetails cardDetails = new CardDetails();
    private final HashMap<String, CardAppDetail> cardAppletMap = new HashMap<>();
    private final CardSecurityDomian issuerSecurityDomian = new CardSecurityDomian();
    private final HashMap<String, CardSecurityDomian> securityDomainMap = new HashMap<>();
    private String readerName;
    //Note: Do not set this flag while setting freshness flag as it is being set
    //after removal of bean from AppCart Panel.
    private boolean attachedToInstalledAppPanel;

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public boolean isAttachedToInstalledAppPanel() {
        return attachedToInstalledAppPanel;
    }
    //Do not call this method in setFresh method as it is being set after removal
    //of bean from AppCart Panel.
    public void setAttachedToInstalledAppPanel(boolean attachedToInstalledAppPanel) {
        this.attachedToInstalledAppPanel = attachedToInstalledAppPanel;
    }
    
    

    public boolean isEMV() {
        return EMV;
    }

    public void setEMV(boolean EMV) {
        this.EMV = EMV;
    }

    
    public boolean isFresh() {
        return fresh;
    }

    public String getAIDToDelete(String appAID) {
        String aidToDelete=appAID;
        String pkgAID = AppAIDAndPkgAID.get(appAID);
        logger.info("Applet " + appAID + " Pkg AID " + pkgAID);
        if (pkgAID != null) {
            ArrayList<String> AppAIDs = PkgAIDAndAppAID.get(pkgAID);
            logger.info(" Pkg AID " + pkgAID + " has " + AppAIDs + " AIDs");
            if(AppAIDs!=null){
                if(AppAIDs.size() == 1){
                    aidToDelete=pkgAID;
                }
            }
        }
        return aidToDelete;
    }

    /**
     * Sets freshness of contained information
     * Note:Very important method has to be called only by CardTerminalMonitor thread 
     * when invalidating the bean.
     * @param fresh 
     */
    public void setFresh(boolean fresh) {
        if (!fresh && this.fresh) {
            for (String aid : cardAppletMap.keySet()) {
                cardAppletMap.get(aid).setFresh(fresh);
            }
            for (String aid : securityDomainMap.keySet()) {
                securityDomainMap.get(aid).setFresh(fresh);
            }
            cardDetails.setFresh(fresh);
            issuerSecurityDomian.setFresh(fresh);
            //Clear all maps
            AppAIDAndPkgAID.clear();
            PkgAIDAndAppAID.clear();
            cardAppletMap.clear();
            securityDomainMap.clear();
            //Set Emv False
            EMV=false;
        } 
//        else if (fresh && !this.fresh) {
//            //1. Populate CardAppletMap.
//            //2. Populate CardDetails Bean.
//        }
        this.fresh = fresh;
    }

    public CardSecurityDomian getIssuerSecurityDomian() {
        return issuerSecurityDomian;
    }

    public void addSecurityDomain(String aid, CardSecurityDomian cardSecurityDomian) {
        securityDomainMap.put(aid, cardSecurityDomian);
    }

    public ArrayList<String> getSecuriyDomainAIDs() {
        return new ArrayList<>(securityDomainMap.keySet());
    }

    public ArrayList<CardSecurityDomian> getSecurityDomains() {
        return new ArrayList<>(securityDomainMap.values());
    }

    public ArrayList<String> getCardAppAIDs() {
        return new ArrayList<>(AppAIDAndPkgAID.keySet());
    }

    public ArrayList<CardAppDetail> getCardAppDetails() {
        return new ArrayList<>(cardAppletMap.values());
    }

    public CardAppDetail getCardAppDetail(String aid) {
        return cardAppletMap.get(aid);
    }

    public void addCardAppDetail(String aid, CardAppDetail appDetails) {
        cardAppletMap.put(aid, appDetails);
    }
    
    public CardAppDetail removeCardAppDetail(String aid){
        return cardAppletMap.remove(aid);
    }

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public void addPairToAppMap(String appAID, String pkgAID) {
        AppAIDAndPkgAID.put(appAID, pkgAID);
    }

    public void addPairToPkgMap(String pkgAID, String appAID) {
        if (PkgAIDAndAppAID.containsKey(pkgAID)) {
            PkgAIDAndAppAID.get(pkgAID).add(appAID);
        } else {
            ArrayList<String> appList = new ArrayList<>();
            appList.add(appAID);
            PkgAIDAndAppAID.put(pkgAID, appList);
        }
    }

    public String getPkgAID(String appAID) {
        return AppAIDAndPkgAID.get(appAID);
    }

    public ArrayList<String> getAppAIDList(String pkgAID) {
        return PkgAIDAndAppAID.get(pkgAID);
    }

    public void removeAppDetail(String aid) {
        String pkgaid = AppAIDAndPkgAID.remove(aid);
        if (pkgaid != null) {
            if (PkgAIDAndAppAID.get(pkgaid).size() == 1) {
                PkgAIDAndAppAID.remove(pkgaid);
            } else {
                PkgAIDAndAppAID.get(pkgaid).remove(aid);
            }
        }
        cardAppletMap.remove(aid);
    }

}
