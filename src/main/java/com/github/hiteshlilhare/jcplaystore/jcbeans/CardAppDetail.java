/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcbeans;

/**
 *
 * @author Hitesh
 */
public class CardAppDetail {

    // Needs to be updated based on the availability of newer version.
    private boolean latest;
    private boolean fresh;
    private String icFabricator;
    private String icSerialNumber;
    private String icType;
    //App Details
    private String aid;
    private String description;
    private String lifeCycleState;
    private String privileges;
    private String privilegesString;
    private String domainAID;
    //Container Pkg Details
    private String pkgAid;
    private String pkgVersion;
    private String pkgLifeCycleState;
    private String pkgDomainAID;

    //App release details
    private AppReleaseDetails appReleaseDetails;
    
    //Reference to Java Card Bean
    private final JavaCardBean javaCardBean;
    
    
    public CardAppDetail(JavaCardBean javaCardBean){
        this.javaCardBean = javaCardBean;
    }

    public AppReleaseDetails getAppReleaseDetails() {
        return appReleaseDetails;
    }

    public void setAppReleaseDetails(AppReleaseDetails appReleaseDetails) {
        this.appReleaseDetails = appReleaseDetails;
    }
    
    public String getCardATR(){
        return javaCardBean.getCardDetails().getATRString();
    }
    
    public boolean isEMVCard(){
        return javaCardBean.isEMV();
    }
    
    public void attachToInstalledAppPanel(){
        javaCardBean.setAttachedToInstalledAppPanel(true);
    }
    
    public void detachToInstalledAppPanel(){
        javaCardBean.setAttachedToInstalledAppPanel(false);
    }
    
    public String getCardReaderName(){
        return javaCardBean.getReaderName();
    }
    
    public CardAppDetail removeFromJavaCardBean(){
        return javaCardBean.removeCardAppDetail(aid);
    }
    
    public String getAIDToDelete(){
        return javaCardBean.getAIDToDelete(aid);
    }

    public String getIcFabricator() {
        return icFabricator;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
        if(!fresh && this.fresh){
            javaCardBean.removeAppDetail(aid);
        }
    }
    

    public void setIcFabricator(String icFabricator) {
        this.icFabricator = icFabricator;
    }

    public String getIcSerialNumber() {
        return icSerialNumber;
    }

    public void setIcSerialNumber(String icSerialNumber) {
        this.icSerialNumber = icSerialNumber;
    }

    public String getIcType() {
        return icType;
    }

    public void setIcType(String icType) {
        this.icType = icType;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(String lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public String getPrivilegesString() {
        return privilegesString;
    }

    public void setPrivilegesString(String privilegesString) {
        this.privilegesString = privilegesString;
    }

    public String getDomainAID() {
        return domainAID;
    }

    public void setDomainAID(String domainAID) {
        this.domainAID = domainAID;
    }
    
    /////////////////////////////////////////////////////Pkg Related///////////////////////
    public String getPkgAid() {
        return pkgAid;
    }

    public void setPkgAid(String pkgAid) {
        this.pkgAid = pkgAid;
    }

    public String getPkgVersion() {
        return pkgVersion;
    }

    public void setPkgVersion(String version) {
        this.pkgVersion = version;
    }

    public String getPkgLifeCycleState() {
        return pkgLifeCycleState;
    }

    public void setPkgLifeCycleState(String pkgLifeCycleState) {
        this.pkgLifeCycleState = pkgLifeCycleState;
    }

    public String getPkgDomainAID() {
        return pkgDomainAID;
    }

    public void setPkgDomainAID(String pkgDomainAID) {
        this.pkgDomainAID = pkgDomainAID;
    }

    @Override
    public String toString() {
        String str = "AID: " + aid + "\nDescription: " + description +"\nPKGID: " + pkgAid + "\n" + "";
        return str; //To change body of generated methods, choose Tools | Templates.
    }
    
}
