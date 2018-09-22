/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

/**
 *
 * @author Hitesh
 */
public class SCP02_I_Value_Interpreter {
    //B1-LSB
    //String Constant values are taken from GPCardSpec_v2.2.pdf 
    //Appendix E Secure Channel Protocol '02' Table E-1: Values of Parameter "i"
    private static final String B1_1 = "3 Secure Channel Keys";
    private static final String B1_0 = "1 Secure Channel base key";
    private static final String B2_1 = "C-MAC on unmodified APDU";
    private static final String B2_0 = "C-MAC on modified APDU";
    private static final String B3_1 = "Initiation mode explicit";
    private static final String B3_0 = "Initiation mode implicit";
    private static final String B4_1 = "ICV set to MAC over AID";
    private static final String B4_0 = "ICV set to zero";
    private static final String B5_1 = "ICV encryption for C-MAC session";
    private static final String B5_0 = "No ICV encryption";
    private static final String B6_1 = "R-MAC support";
    private static final String B6_0 = "No R-MAC support";
    private static final String B7_1 = "Well-known pseudo-random algorithm (card challenge)";
    private static final String B7_0 = "Unspecified card challenge generation method";
    
    public static String getInterpretation(byte iValue){
        String strInterpretation="";
        if((iValue&1)>0){
            strInterpretation+=B1_1+", ";
        }else{
            strInterpretation+=B1_0+", ";
        }
        if(((iValue>>1)&1)>0){
            strInterpretation+=B2_1+", ";
        }else{
            strInterpretation+=B2_0+", ";
        }
        if(((iValue>>2)&1)>0){
            strInterpretation+=B3_1+", ";
        }else{
            strInterpretation+=B3_0+", ";
        }
        if(((iValue>>3)&1)>0){
            strInterpretation+=B4_1+", ";
        }else{
            strInterpretation+=B4_0+", ";
        }
        if(((iValue>>4)&1)>0){
            strInterpretation+=B5_1+", ";
        }else{
            strInterpretation+=B5_0+", ";
        }
        if(((iValue>>5)&1)>0){
            strInterpretation+=B6_1+", ";
        }else{
            strInterpretation+=B6_0+", ";
        }
        if(((iValue>>6)&1)>0){
            strInterpretation+=B7_1;
        }else{
            strInterpretation+=B7_0;
        }
        return strInterpretation;
    }
//    public static void main(String[] args) {
//        System.out.println(getInterpretation(HexUtils.hex2bin("55")[0]));
//    }
}
