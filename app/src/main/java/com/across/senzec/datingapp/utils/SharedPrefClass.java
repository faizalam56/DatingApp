package com.across.senzec.datingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Divakar on 7/6/2017.
 */

public class SharedPrefClass {

    Context _context;
    private static final String TAG = SharedPrefClass.class.getSimpleName();
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;

    // Sharedpref file name
    private static final String PREF_NAME = "across_dating";

    private static final String LOGIN_KEY = "key";
    private static final String USER_QB= "signin_user_qb";
    private static final String USER_TOKEN= "user_token";
    private static final String LOGGED_USER= "logged_user";
    private static final String STORAGE_TYPE = "internal_camera";



    public SharedPrefClass(Context _context)
    {
        this._context = _context;
    }

    public void createLoginSession(String key)
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGIN_KEY, key);
        editor.commit();
    }
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<>();
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
         sharedPreferences.getString(LOGIN_KEY, null);

        user.put(LOGIN_KEY, sharedPreferences.getString(LOGIN_KEY, null));
        return user;
    }


    public void setUsrPwdForConversation(String user)
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_QB, user);
        editor.commit();
    }
    public String getUsrPwdForConversation()
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(USER_QB, null);
    }

    public void setUserToken(String userToken)
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_TOKEN, userToken);
        editor.commit();
    }
    public String getUserToken()
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(USER_TOKEN, null);
    }

   public void setLoggedUser(String loggedUser)
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGGED_USER, loggedUser);
        editor.commit();

    }
    public String getLoggedUser()
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(LOGGED_USER, null);
    }

    public String getStorageType()
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(STORAGE_TYPE, null);
    }
    public void setStorageType(String storage)
    {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STORAGE_TYPE, storage);
        editor.commit();

    }
}
