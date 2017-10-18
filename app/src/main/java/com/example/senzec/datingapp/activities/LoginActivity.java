package com.example.senzec.datingapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.tv.TvInputService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.api.APIClient;
import com.example.senzec.datingapp.api.APIInterface;
import com.example.senzec.datingapp.controller.AppController;
import com.example.senzec.datingapp.instagram.ApplicationData;
import com.example.senzec.datingapp.instagram.InstagramApp;
import com.example.senzec.datingapp.instagram.InstagramSession;
import com.example.senzec.datingapp.manager.App;
import com.example.senzec.datingapp.preference.AppPrefs;
import com.example.senzec.datingapp.requestmodel.FindMatchRequest;
import com.example.senzec.datingapp.requestmodel.UserRequest;
import com.example.senzec.datingapp.responsemodel.FindMatchResponse;
import com.example.senzec.datingapp.responsemodel.UserResponse;
import com.example.senzec.datingapp.services.GPSTracker;
import com.example.senzec.datingapp.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by power hashing on 4/14/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher, View.OnClickListener {

    TextView tv_facebook_promiss;
    private InstagramApp mApp;
    String txtPhoto;
    ProgressDialog progressDialog;
    private APIInterface apiInterface;
    GPSTracker gpsTracker;
    Double latitude,longitude;

    public interface OnLogoutSuccess {

        void onLogoutSuccess();
    }

    private static OnLogoutSuccess onLogoutSuccess;
    EditText et_1, et_2, et_3, et_4;
    Button btn_facebook_login, image_button_snapchat;
    private int whoHasFocus = 0;
    private LoginButton loginButton;
    private Context mContext;
    private CallbackManager callbackManager;
    public static final String KEY_LOGIN_TYPE = "login_type";
    private final String TAG = getClass().getSimpleName();
    public static AppPrefs prefs;
    private String txtId,firstName,lastName,email,birthday,gender;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
//                userInfoHashmap = mApp.getUserInfo();
                System.out.println("Inside instagram handler....");

//                checkFromseverIsUserExist(userInfoHashmap.get(Constants.TAG_USERID).toString());
                checkIsuserExist();
                prefs.putString(App.Key.LOGIN_TYPE_MEDIA,"Instagram");

            } else if (msg.what == InstagramApp.WHAT_ERROR) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        prefs = AppController.getInstance().getPrefs();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        this.mContext = this;
        init();
        initFacebookLogin();

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                mApp.fetchUserName(handler);

//                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        et_1 = (EditText) findViewById(R.id.et_1);
//        et_2 = (EditText) findViewById(R.id.et_2);
//        et_3 = (EditText) findViewById(R.id.et_3);
//        et_4 = (EditText) findViewById(R.id.et_4);
        image_button_snapchat = (Button) findViewById(R.id.image_button_snapchat);
        tv_facebook_promiss = (TextView) findViewById(R.id.tv_facebook_promiss);

        btn_facebook_login = (Button) findViewById(R.id.btn_facebook_login);
//        et_1.addTextChangedListener(this);
//        et_2.addTextChangedListener(this);
//        et_3.addTextChangedListener(this);
//        et_4.addTextChangedListener(this);
//        et_1.setOnFocusChangeListener(this);
//        et_2.setOnFocusChangeListener(this);
//        et_3.setOnFocusChangeListener(this);
//        et_4.setOnFocusChangeListener(this);
        btn_facebook_login.setOnClickListener(this);
        image_button_snapchat.setOnClickListener(this);
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();

                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    checkIsuserExist();
//                                    checkFromseverIsUserExist(prefs.getString("Instragram_TagId"));
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    private void initFacebookLogin() {
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        // initialize facebook sdk
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_about_me", "user_location",
                "email", "user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e(TAG,"facebook login object: "+object.toString());
                                Log.e(TAG,"facebook login responce: "+response.toString());
                                // Application code
                                if (response.getError() != null) {
                                    // handle error
                                    Log.e(TAG, "onCompleted: facebook login " + response.getError());
                                } else {
                                    try {

                                        txtId = object.getString("id");
                                        System.out.println("Id of person is...."+txtId);
                                        try {
                                            URL imageURL = null;
                                            imageURL = new URL("https://graph.facebook.com/" + txtId + "/picture?type=large");
                                            Log.e("image URL", imageURL.toString());
                                            txtPhoto = imageURL.toString();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        if(object.has("first_name"))
                                            firstName = object.getString("first_name");
                                        if(object.has("last_name"))
                                            lastName = object.getString("last_name");
                                        if (object.has("email"))
                                            email = object.getString("email");
                                        if (object.has("birthday"))
                                            birthday = object.getString("birthday");
                                        if (object.has("gender"))
                                            gender = object.getString("gender");

//                                        prefs.putBoolean(App.Key.IS_LOGGED, true);
                                        Constants.USERID = txtId;
                                        prefs.putString(App.Key.ID_LOGGED, txtId);
                                        prefs.putString(App.Key.LOGIN_TYPE_MEDIA,"facebook");
                                        prefs.putString(Constants.TAG_PROFILE_PICTURE,txtPhoto);
                                        prefs.putString(Constants.TAG_USERNAME,firstName);
                                        prefs.putString(Constants.TAG_GENDER,gender);


                             //hashmap added by faiz
//                                        userInfoHashmap.put(Constants.TAG_USERID,txtId);
//                                        userInfoHashmap.put(Constants.TAG_PROFILE_PICTURE,txtPhoto);
//                                        userInfoHashmap.put(Constants.TAG_USERNAME,firstName);
//                                        userInfoHashmap.put(Constants.TAG_DOB,birthday);
//                                        userInfoHashmap.put(Constants.TAG_GENDER,gender);


//


                                        Log.e(TAG, "onCompleted: success " + object.optString("email"));

//                                        checkFromseverIsUserExist(txtId);
                                        checkIsuserExist();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                        intent.putExtra("userInfo",userInfoHashmap);
                                        startActivity(intent);
                                        finish();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "onCompleted: facebook response " + object.toString());
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,birthday,name,link,email,gender,locale");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "CANCEL", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(mContext, "Error" + e, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: ", e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (whoHasFocus) {
            case 1:
                et_1.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
                String text1 = s.toString().trim();
                if (text1.length() == 1)
                    et_2.requestFocus();
                break;
            case 2:
                et_2.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
                String text2 = s.toString().trim();
                if (text2.length() == 1)
                    et_3.requestFocus();
                break;
            case 3:
                et_3.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
                String text3 = s.toString().trim();
                if (text3.length() == 1)
                    et_4.requestFocus();
                break;
            case 4:
                et_4.getBackground().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
//            case R.id.et_1:
//                whoHasFocus = 1;
//                break;
//            case R.id.et_2:
//                whoHasFocus = 2;
//                break;
//            case R.id.et_3:
//                whoHasFocus = 3;
//                break;
//            case R.id.et_4:
//                whoHasFocus = 4;
//                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook_login:
//                loginButton.performClick();

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.image_button_snapchat:
                connectOrDisconnectUser();
                break;
        }
    }


    public static boolean onLogoutClick(AppPrefs prefs, Context mContext, FragmentActivity fragmentActivity, OnLogoutSuccess onLogoutSuccess) {
        LoginActivity.prefs = prefs;
        LoginActivity.onLogoutSuccess = onLogoutSuccess;
        onFacebookLogout(mContext);
        return true;
    }

    private static void onFacebookLogout(Context mContext) {
        try {
            LoginManager.getInstance().logOut();
            prefs.remove(App.Key.IS_LOGGED);
            prefs.remove(App.Key.ID_LOGGED);
            prefs.remove(KEY_LOGIN_TYPE);
            if (onLogoutSuccess != null) {
                onLogoutSuccess.onLogoutSuccess();
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "onFacebookLogout: ", e);
        }
    }

    public void onInstagramLogout(){
        mApp.resetAccessToken();
    }

//***********For Volly Request**********************************************
    /*private void checkFromseverIsUserExist(String tagId){

        loadingProgressbar("find user exist");
        String URL = "http://acrossapi.senzecit.in/guest/api/v1/is/user/"+tagId+"/exists";

        HashMap<String,String> parameter = new HashMap<String, String>();
        parameter.put("device_token","56789");
        parameter.put("device_type","android");
        System.out.println("JsonObject Request...."+new JSONObject(parameter).toString()+"\n"+"url..."+URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, new JSONObject(parameter),
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.hide();
                Log.d("jsonResponse",response.toString());
                System.out.println("jsonResponse...."+response.toString());
                try {
                    VolleyLog.v("Response:%n %s", response.toString());
                    if(response.getString("status").equalsIgnoreCase("true")){
                        prefs.putBoolean(App.Key.IS_LOGGED,true);
                    }else {
                        prefs.putBoolean(App.Key.IS_LOGGED,false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                VolleyLog.e("Error: ", error.getMessage());

                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("ErrorResponse: ",obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }*/

//**********************For Retrofit Request*************************************************
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
                    if(resource.status.equalsIgnoreCase("true")){
                        prefs.putBoolean(App.Key.IS_LOGGED,true);
                        prefs.putString(App.Key.REGISTER_USER_TOKEN,resource.response);
                        fetchUserFindMatch();
                    }else{
                        Toast.makeText(LoginActivity.this,"Its a new user",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }else{
                    showErrorDialog("something went wrong!");
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
        loadingProgressbar("FetchMatchData");
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
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse",resource);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    showErrorDialog("something went wrong!");
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
        gpsTracker = new GPSTracker(LoginActivity.this);
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

    public void clearSharedPrefrenceData(String sharedPreferenceName){
        SharedPreferences sharedPref = getSharedPreferences(sharedPreferenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.remove(key);
        editor.clear();
        editor.commit();

    }
}
