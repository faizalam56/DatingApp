package com.example.senzec.datingapp.activities;

import android.app.Activity;
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
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.api.APIClient;
import com.example.senzec.datingapp.api.APIInterface;
import com.example.senzec.datingapp.controller.AppController;
import com.example.senzec.datingapp.manager.App;
import com.example.senzec.datingapp.preference.AppPrefs;
import com.example.senzec.datingapp.requestmodel.FindMatchRequest;
import com.example.senzec.datingapp.requestmodel.UserRequest;
import com.example.senzec.datingapp.responsemodel.FindMatchResponse;
import com.example.senzec.datingapp.responsemodel.UserResponse;
import com.example.senzec.datingapp.services.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);
        prefs = AppController.getInstance().getPrefs();
        apiInterface = APIClient.getClient().create(APIInterface.class);
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
        loadingProgressbar("checkIsuserExist");

        String userId = prefs.getString(App.Key.ID_LOGGED);
        UserRequest userRequest=new UserRequest();
        userRequest.device_token="1234567";
        userRequest.device_type="android";

        Call<UserResponse> call1 = apiInterface.getUserResponse(userId,userRequest);
        call1.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    prefs.putBoolean(App.Key.IS_LOGGED,true);
                    prefs.putString(App.Key.REGISTER_USER_TOKEN,resource.response);
                    fetchUserFindMatch();
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }

    private void fetchUserFindMatch() {
            if (hasGpsSensor()) {
                findGpsLocation();
                callFindMatchApi();
            }
    }

    private void callFindMatchApi(){
        loadingProgressbar("FindhMatchData");
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);//"835c7c18b84afa56c49fe81492da46a598ddfdbfe0682cda832f731bb114e888";//
        String userId = prefs.getString(App.Key.ID_LOGGED);

        FindMatchRequest findMatchRequest=new FindMatchRequest();
        findMatchRequest.latitude=latitude;
        findMatchRequest.longitude=longitude;
        findMatchRequest.username=userId;

        Call<FindMatchResponse> call6 = apiInterface.findMatch(userId,userToken,findMatchRequest);
        call6.enqueue(new Callback<FindMatchResponse>() {
            @Override
            public void onResponse(Call<FindMatchResponse> call, retrofit2.Response<FindMatchResponse> response) {

                FindMatchResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
                        Log.d("resource...",resource.response.toString());
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse",resource);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();

            }
            @Override
            public void onFailure(Call<FindMatchResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }
    private void findGpsLocation(){
        gpsTracker = new GPSTracker(SplashActivity.this);
        if(gpsTracker.canGetLocation()){
            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    public boolean hasGpsSensor(){
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public void loadingProgressbar(String message){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
    private boolean checkInternetAndNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if(wifiNetwork != null && wifiNetwork.isConnected() || mobileDataNetwork != null && mobileDataNetwork.isConnected()){
            return true;
        }
        return false;
    }
}

