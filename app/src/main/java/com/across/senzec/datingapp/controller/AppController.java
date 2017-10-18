package com.across.senzec.datingapp.controller;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;

import com.across.senzec.datingapp.utils.EnableMultiDex;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.across.senzec.datingapp.facebookalbum.LruBitmapCache;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.utils.Constants;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.quickblox.auth.session.QBSettings;

/**
 * Created by power hashing on 4/20/2017.
 */


public class AppController extends Application {

    public static final String TAG =AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController sInstance;
    private AppPrefs prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize the singleton
        sInstance = this;
        prefs = new AppPrefs(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        initializeQBFramework();

        new EnableMultiDex().getEnableMultiDexApp();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public ImageLoader getImageLoader() {

        getRequestQueue();
        if (mImageLoader == null) {

            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req passing request.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        // set max retries
        int maxRetries = 10;
        int socketTimeOut = 100 * 100 * 10;

        req.setRetryPolicy(new DefaultRetryPolicy(socketTimeOut, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized AppController getInstance() {
        return sInstance;
    }

    // Checking for all possible internet providers
    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public AppPrefs getPrefs() {
        return prefs;
    }

    private void initializeQBFramework() {

        QBSettings.getInstance().init(getApplicationContext(), Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constants.ACCOUNT_KEY);
    }
}
