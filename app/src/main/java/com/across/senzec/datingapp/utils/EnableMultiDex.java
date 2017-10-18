package com.across.senzec.datingapp.utils;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.across.senzec.datingapp.controller.AppController;

/**
 * Created by ravi on 26/9/17.
 */

public class EnableMultiDex extends MultiDexApplication {
    private static EnableMultiDex enableMultiDex;
    public static Context context;

    public EnableMultiDex(){
        enableMultiDex=this;
    }

    public static EnableMultiDex getEnableMultiDexApp() {
        return enableMultiDex;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
