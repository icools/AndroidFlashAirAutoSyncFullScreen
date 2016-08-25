package com.htl.flashair.fullscreenphoto;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager {

    public static final String PREFS_NAME_COMMON_SETTINGS = "HTL_FLASH_CARD";

    public static SharedPreferences getSharedPreferences(Context ctx, String prefName) {
        return ctx.getSharedPreferences(prefName, 0);
    }

    public static SharedPreferences.Editor getSharedPreferencesEdit(Context ctx, String prefName) {
        return getSharedPreferences(ctx, prefName).edit();
    }

    private static final String PREF_LAST_PATH = "PREF_LAST_SELECTED_PATH" ;

    public static void setLastSelectPath(Context context, String path) {
        SharedPreferences.Editor editor = SettingManager.getSharedPreferencesEdit(context, PREFS_NAME_COMMON_SETTINGS);
        editor.putString(PREF_LAST_PATH, path);
        editor.commit();
    }
    public static String getLastSelectPath(Context context) {
        SharedPreferences preference = SettingManager.getSharedPreferences(context, PREFS_NAME_COMMON_SETTINGS);
        return preference.getString(PREF_LAST_PATH, null);
    }
}

