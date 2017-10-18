package com.across.senzec.datingapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by senzec on 28/7/17.
 */

public class ProgressClass {

    private static final String TAG = "ProgressClass";
    private static ProgressClass progressClass = null;
    private ProgressDialog progressDialog;

    private ProgressClass()
    {

    }
    public static ProgressClass getProgressInstance()
    {
        if(progressClass == null)
        {
            progressClass = new ProgressClass();
        }
        return progressClass;
    }

    public void startProgress(Context _context)
    {
        progressDialog = new ProgressDialog(_context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    public void stopProgress()
    {
        try {
            progressDialog.cancel();
        }catch (NullPointerException npe){
            Log.e(TAG, "#Error : "+npe, npe);
        }
    }
}
