/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

import javax.smartcardio.ATR;
import pro.javacard.gp.GPData;

/**
 *
 * @author Hitesh
 */
public class CardDetails {

    /**
     * Freshness of bean
     */
    private boolean fresh;
    /**
     * Answer To Reset.
     */
    private ATR atr;
    /**
     * ATR string
     */
    private String strATR;
    /**
     * Card Production Life Cycle Data (CPLC data)
     */
    private GPData.CPLC cplc;
    //When issuer issues a card, issuers should set the IIN (issuer identification number) 
    //and CIN (card image number) in the card manager.
    /**
     * Issuer Identification Number
     */
    private byte[] inn;
    /**
     * Card Image Number
     */
    private byte[] cin;

    private byte[] cardData;

    private byte[] cardCapabilities;

    private byte[] keyInfo;

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public ATR getAtr() {
        return atr;
    }

    public String getATRString() {
        return strATR;
    }

    public void setAtr(ATR atr) {
        this.atr = atr;
    }

    public void setATRString(String strATR) {
        this.strATR = strATR;
    }

    public GPData.CPLC getCplc() {
        return cplc;
    }

    public void setCplc(GPData.CPLC cplc) {
        this.cplc = cplc;
    }

    public byte[] getInn() {
        return inn;
    }

    public void setInn(byte[] inn) {
        this.inn = inn;
    }

    public byte[] getCin() {
        return cin;
    }

    public void setCin(byte[] cin) {
        this.cin = cin;
    }

    public byte[] getCardData() {
        return cardData;
    }

    public void setCardData(byte[] cardData) {
        this.cardData = cardData;
    }

    public byte[] getCardCapabilities() {
        return cardCapabilities;
    }

    public void setCardCapabilities(byte[] cardCapabilities) {
        this.cardCapabilities = cardCapabilities;
    }

    public byte[] getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(byte[] keyInfo) {
        this.keyInfo = keyInfo;
    }

}
