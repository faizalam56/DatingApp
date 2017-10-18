package com.across.senzec.datingapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.requestmodel.UserRequest;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.across.senzec.datingapp.utils.Constants;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.wang.avi.AVLoadingIndicatorView;

import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import retrofit2.Call;
import retrofit2.Callback;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by power hashing on 4/14/2017.
 */

public class SplashActivity extends Activity {

    private static final int SPLASH_DISPLAY_TIME = 2000;
    private AppPrefs prefs;
    GPSTracker gpsTracker;
    Double latitude,longitude;
    ProgressDialog progressDialog;
    private APIInterface apiInterface;
    public static final int DIALOG_LOADING = 1;
    AVLoadingIndicatorView avi;
    LinearLayout loadongIndicator;
    private String android_device_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);
        loadongIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
        prefs = AppController.getInstance().getPrefs();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        FontChangeCrawler fontChanger = new FontChangeCrawler(this.getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
//        printhashkey();
        Constants.DEVICE_ID = getUdId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {

                /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();*/

                if (prefs.getBoolean(App.Key.IS_LOGGED)) {
                    Log.e(TAG, "onFinish: Continue with Login Id - " + prefs.getString(App.Key.ID_LOGGED));
                    if(checkInternetAndNetwork()){
                        checkIsuserExist();
                    }else{
                        showErrorDialog("Check Internet Connectivity");
                    }

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_TIME);
    }

    private void checkIsuserExist(){
//        loadingProgressbar();
        loadongIndicator.setVisibility(View.VISIBLE);

        String userId = prefs.getString(App.Key.ID_LOGGED);
        UserRequest userRequest=new UserRequest();
        userRequest.device_token = Constants.DEVICE_ID;
        userRequest.device_type = Constants.DEVICE_TYPE;

        Call<JsonObject> call1 = apiInterface.getUserResponse(userId,userRequest);
        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {         // here get status : false

                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        Toast.makeText(SplashActivity.this, "Its a new user", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                        finish();
                    } else {               // here get status : true
//
                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        prefs.putBoolean(App.Key.IS_LOGGED, true);
                        prefs.putString(App.Key.REGISTER_USER_TOKEN, userResponse.response);
                        qbSignIn();
                        fetchUserFindMatch();
                    }
                }else{

                    loadongIndicator.setVisibility(View.INVISIBLE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

                loadongIndicator.setVisibility(View.INVISIBLE);
                showErrorDialog(t.getMessage());
            }
        });
    }

    private void fetchUserFindMatch() {
            if (hasGpsSensor()) {
                if(turnGPSOn()) {
                    findGpsLocation();
                    callFindMatchApi();
                }
            }
    }

    private void callFindMatchApi(){

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);//"835c7c18b84afa56c49fe81492da46a598ddfdbfe0682cda832f731bb114e888";//
        String userId = prefs.getString(App.Key.ID_LOGGED);

        FindMatchRequest findMatchRequest=new FindMatchRequest();
        findMatchRequest.latitude=latitude;
        findMatchRequest.longitude=longitude;
        findMatchRequest.username=userId;

        Call<JsonObject> call6 = apiInterface.findMatch(userId,userToken,findMatchRequest);
        call6.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson=new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {

                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        System.out.println("userResponse " + userResponse);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {

                        FindMatchResponse findMatchResponse = gson.fromJson(resource.toString(), FindMatchResponse.class);
                        System.out.println("findMatchResponse " + findMatchResponse);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse", findMatchResponse);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else{

                    loadongIndicator.setVisibility(View.INVISIBLE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                loadongIndicator.setVisibility(View.INVISIBLE);
                showErrorDialog(t.getMessage());
            }
        });
    }
    private void findGpsLocation(){
        gpsTracker = new GPSTracker(SplashActivity.this);
        if(gpsTracker.canGetLocation()){
            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    public boolean hasGpsSensor(){
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }
    private boolean turnGPSOn(){
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            showErrorDialogToEnableGps("Please turn on GPS location!");

            return false;
        }else{
            return true;
        }
    }
    public void loadingProgressbar(){
        progressDialog = new ProgressDialog(this,R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }



    private void showErrorDialog(String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("ALERT");
        dialog.setMessage(message);
        dialog.setIcon(R.mipmap.logo_icon);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void showErrorDialogToEnableGps(String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("ALERT");
        dialog.setMessage(message);
        dialog.setIcon(R.mipmap.logo_icon);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SplashActivity.this.finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private boolean checkInternetAndNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if(wifiNetwork != null && wifiNetwork.isConnected() || mobileDataNetwork != null && mobileDataNetwork.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING:
                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //here we set layout of progress dialog
                dialog.setContentView(R.layout.custom_progress_dialog);
                dialog.setCancelable(true);

                return dialog;

            default:
                return null;
        }
    }

    private void qbSignIn(){
        String user = prefs.getString(App.Key.ID_LOGGED);
        String password = Constants.PASSWORD;
        QBUser qbUser = new QBUser(user,password);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {

            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                prefs.putString(App.Key.QB_ID,qbUser.getId().toString());
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    public void printhashkey(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.across.senzec.datingapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error : "+e,e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error : "+e,e);
        }

    }

    private String getUdId(){
        android_device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("DeviceId",android_device_id);
        return android_device_id;
    }

}

