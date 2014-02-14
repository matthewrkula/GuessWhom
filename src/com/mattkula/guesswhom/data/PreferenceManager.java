package com.mattkula.guesswhom.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by matt on 2/9/14.
 */
public class PreferenceManager {
    public static final String PREFERENCES = "preferences";
    public static final String FIRST_NAME = "first_name";
    public static final String FIRST_NAME_DEFAULT = "NONE";
    public static final String PROFILE_ID = "profile_id";
    public static final String PROFILE_ID_DEFAULT = "-1";

    public static void setFirstName(Context c, String firstName){
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(FIRST_NAME, firstName);
        editor.commit();
    }

    public static String getFirstName(Context c) {
        return c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(FIRST_NAME, FIRST_NAME_DEFAULT);
    }

    public static void setProfileId(Context c, String profileId){
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(PROFILE_ID, profileId);
        editor.commit();
    }

    public static String getProfileId(Context c) {
        return c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(PROFILE_ID, PROFILE_ID_DEFAULT);
    }

    public static void setFadedMap(Context c, String gameId, int tag){
        Log.e("ASDF", String.format("Setting %s fadedMap to %d", gameId, tag));
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt(gameId, tag);
        editor.commit();
    }

    public static int getFadedMap(Context c, String gameId) {
        Log.e("ASDF", String.format("Getting %s fadedMap", gameId));
        return c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getInt(gameId, 0);
    }

    public static boolean isLoggedIn(Context c){
        return !getFirstName(c).equals(FIRST_NAME_DEFAULT);
    }

    public static void logout(Context c){
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(FIRST_NAME, FIRST_NAME_DEFAULT);
        editor.putString(PROFILE_ID, PROFILE_ID_DEFAULT);
        editor.commit();
    }
}
