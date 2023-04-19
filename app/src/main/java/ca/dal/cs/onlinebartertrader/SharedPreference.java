package ca.dal.cs.onlinebartertrader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Functions as "storage" for the user; all classes can reference this to get various information
 * about the user, including their username, preferred search category, distance, and previously
 * read chat messages
 */

public class SharedPreference {
    static final String PREF_USERNAME = "username";
    static final String PREF_CATEGORY = "category";
    static final String PREF_DISTANCE = "distance";
    static final int PREF_READ = 1;
    static final int PREF_CURRENT = 2;

    private SharedPreference() {
    }

    //getters
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static int getRead(Context ctx) {
        return getSharedPreferences(ctx).getInt(String.valueOf(PREF_READ), 0);
    }

    public static int getPrefCurrent(Context ctx) {
        return getSharedPreferences(ctx).getInt(String.valueOf(PREF_CURRENT), 0);
    }

    public static String getUsername(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USERNAME, "");
    }

    public static String getCategory(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_CATEGORY, "");
    }

    public static int getDistance(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_DISTANCE, 20);
    }

    //setters
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }

    public static void setUsername(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USERNAME, userName);
        editor.apply();
    }

    public static void setPrefRead(Context ctx, int category) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(String.valueOf(PREF_READ), category);
        editor.apply();
    }

    public static void setPrefCurrent(Context ctx, int category) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(String.valueOf(PREF_CURRENT), category);
        editor.apply();
    }

    public static void setCategory(Context ctx, String category) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CATEGORY, category);
        editor.apply();
    }

    public static void setDistance(Context ctx, int distance) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_DISTANCE, distance);
        editor.apply();
    }


}
