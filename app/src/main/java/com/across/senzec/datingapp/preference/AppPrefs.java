package com.across.senzec.datingapp.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by power hashing on 4/20/2017.
 */


public class AppPrefs {

    private Context mContext;
    private static final String NAME_PREFS = "app_preferences";
    private static  SharedPreferences sharedPreferences;

    public AppPrefs(Context mContext) {
        this.mContext = mContext;
        sharedPreferences =  mContext.getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);
    }

    public static AppPrefs getInstance(Context mContext){
        AppPrefs appPrefs = new AppPrefs(mContext);
        return appPrefs;
    }

    public void putString(String key, String value){
        remove(key);
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void putBoolean(String key, boolean value){
        remove(key);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public void putInt(String key, int value){
        remove(key);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public void putLong(String key, long value){
        remove(key);
        sharedPreferences.edit().putLong(key, value).commit();
    }

//    public  void putFBname(String key,String value){
//        remove(key);
//        sharedPreferences.edit().putString(key, value).commit();
//    }
//
//    public  void putFBemail(String key,String value){
//        remove(key);
//        sharedPreferences.edit().putString(key, value).commit();
//    }
//
//    public  void putFBphone(String key,String value){
//        remove(key);
//        sharedPreferences.edit().putString(key, value).commit();
//    }
//
//    public  void putFBid(String key,String value){
//        remove(key);
//        sharedPreferences.edit().putString(key, value).commit();
//    }


    public String getString(String key){
        if (sharedPreferences.contains(key))
            return sharedPreferences.getString(key, null);
        return null;
    }

    public boolean getBoolean(String key){
        if (sharedPreferences.contains(key))
            return sharedPreferences.getBoolean(key, false);
        return false;
    }

    public int getInt(String key){
        if (sharedPreferences.contains(key))
            return sharedPreferences.getInt(key, 0);
        return 0;
    }

    public long getLong(String key){
        if (sharedPreferences.contains(key))
            return sharedPreferences.getLong(key, 0);
        return 0;
    }

    public void remove(String key){
        if (sharedPreferences.contains(key))
            sharedPreferences.edit().remove(key).commit();
    }


    /*
    *  get all preferences data with AppPrefs.getString(AppPrefs.Key.IS_LOGIN);
    *
    *  put all preferences with AppPrefs.putBoolean(AppPrefs.Key.IS_LOGIN, true);
    *
    *  remove AppPrefs.remove(AppPrefs.Key.IS_LOGIN);
    * */
}
