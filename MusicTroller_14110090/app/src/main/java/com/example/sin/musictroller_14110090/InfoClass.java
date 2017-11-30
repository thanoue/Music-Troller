package com.example.sin.musictroller_14110090;

import android.graphics.Bitmap;

/**
 * Created by Sin on 05/19/17.
 */

public class InfoClass {
    private static final InfoClass ourInstance = new InfoClass();

    public static InfoClass getInstance() {
        return ourInstance;
    }

    private InfoClass() {
    }

    public static int getPositop() {
        return positop;
    }

    public static void setPositop(int positop) {
        InfoClass.positop = positop;
    }

    static int positop =0;

    public static String getUser_id() {
        return user_id;
    }

    public static void setUser_id(String user_id) {
        InfoClass.user_id = user_id;
    }

    public static Bitmap getUser_avt() {
        return user_avt;
    }

    public static void setUser_avt(Bitmap user_avt) {
        InfoClass.user_avt = user_avt;
    }

    static String user_id ;static Bitmap user_avt;

    public static String getUser_name() {
        return user_name;
    }

    public static void setUser_name(String user_name) {
        InfoClass.user_name = user_name;
    }

    static String user_name;
    public static Bitmap getDisIMG() {
        return disIMG;
    }

    public static void setDisIMG(Bitmap disIMG) {
        InfoClass.disIMG = disIMG;
    }

    static Bitmap disIMG =null;

}
