package com.dutchit.dutchit;

import org.json.JSONObject;

/**
 * Created by SooD on 11-02-2017.
 */

public class Globals {
    private static JSONObject gBill=null;
    private static String gUrl="";

    public static int getgAmount() {
        return gAmount;
    }

    public static void setgAmount(int gAmount) {
        Globals.gAmount = gAmount;
    }

    private static int gAmount = 0;
    public static JSONObject getBill() {
        return gBill;
    }

    public static void setBill(JSONObject bill) {
        Globals.gBill = bill;
    }

    public static String getMenuUrl() {
        return gMenuUrl;
    }

    public static void setMenuUrl(String gMenuUrl) {
        Globals.gMenuUrl = gMenuUrl;
    }

    public static String getBillUrl() {
        return gBillUrl;
    }

    public static void setBillUrl(String gBillUrl) {
        Globals.gBillUrl = gBillUrl;
    }

    private static String gMenuUrl="";
    private static String gBillUrl="";
    private static String gOutUrl="";
    private static String gUName="Foo";
    private static String gFBill="";
    private static int gTableN=-1;
    private static int gPId=-1;

    public static String getFBill() {
        return gFBill;
    }

    public static void setFBill(String gFBill) {
        Globals.gFBill = gFBill;
    }

    public static String getOutUrl() {
        return gOutUrl;
    }

    public static void setOutUrl(String gOutUrl) {
        Globals.gOutUrl = gOutUrl;
    }

    public static String getUrl() {
        return gUrl;
    }

    public static void setUrl(String gUrl) {
        Globals.gUrl = gUrl;
    }

    public static int getPId() {
        return gPId;
    }

    public static void setPId(int gPId) {
        Globals.gPId = gPId;
    }

    public static int getTableN() {
        return gTableN;
    }

    public static void setTableN(int gTableN) {
        Globals.gTableN = gTableN;
    }

    public static String getUName() {
        return gUName;
    }

    public static void setUName(String gUName) {
        Globals.gUName = gUName;
    }


}
