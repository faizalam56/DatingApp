package com.across.senzec.datingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by senzec on 26/9/17.
 */


public class ConnectivityManagerClass {

    private static ConnectivityManagerClass connectivityManager = null;
    private ConnectivityManagerClass( ) {
    }

    public static ConnectivityManagerClass getInstance()
    {
        if (connectivityManager == null){
            synchronized(ConnectivityManagerClass.class){
                if (connectivityManager == null){
                    connectivityManager = new ConnectivityManagerClass();//instance will be created at request time
                }
            }
        }
        return connectivityManager;
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            //  Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            //  Toast.makeText(context, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;

    }
}