package com.across.senzec.datingapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.instagram.ApplicationData;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.requestmodel.UserRequest;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.across.senzec.datingapp.utils.Constants;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

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

    //QB
    static final String APP_ID = "59087";
    static final String AUTH_KEY = "wVESSN3M-g62fgh";
    static final String AUTH_SECRET = "gmx5pt3RAYTrr8L";
    static final String ACCOUNT_KEY = "ivbjtVyc3z861UGY4Hge";

    int REQUEST_READ_PHONE_STATE = 10;

    static final int REQUEST_CODE = 1000;

    TextView tv_facebook_promiss;
    private InstagramApp mApp;
    String txtPhoto;
    ProgressDialog progressDialog;
    private APIInterface apiInterface;
    GPSTracker gpsTracker;
    Double latitude,longitude;


    EditText et_1, et_2, et_3, et_4;
    Button btn_facebook_login, image_button_snapchat;
    private int whoHasFocus = 0;
    private LoginButton loginButton;
    private Context mContext;
    private CallbackManager callbackManager;
    public static final String KEY_LOGIN_TYPE = "login_type";
    private final String TAG = getClass().getSimpleName();
    public static AppPrefs prefs;
    private String txtId,firstName,lastName,email,birthday,gender,name;
    public static HashMap<String, String> userInfoHashmap;
    LinearLayout loadingIndicator;
    private static OnLogoutSuccess onLogoutSuccess;


    public interface OnLogoutSuccess {

        void onLogoutSuccess();
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                System.out.println("Inside instagram handler....");
                checkIsuserExist();
                prefs.putString(App.Key.LOGIN_TYPE_MEDIA,"instagram");

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
        prefs.putString(App.Key.ID_LOGGED, txtId);
        prefs.putString(App.Key.LOGIN_TYPE_MEDIA,"facebook");

        this.mContext = this;
//        Constants.DEVICE_ID = getDeviceId();
        init();
        initFacebookLogin();

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                mApp.fetchUserName(handler);

            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        //QB
        registrySession();
//        InitializeFramework();
        startFacebookLoginThread();
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
        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
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
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }


    protected void startFacebookLoginThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                initFacebookLogin();
            }
        }).start();
    }

    private void initFacebookLogin() {
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        // initialize facebook sdk
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile email,user_photos");

       /* loginButton.setReadPermissions(Arrays.asList("public_profile", "user_about_me", "user_location",
                "email", "user_birthday"));*/
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
                                        if (object.has("name"))
                                            name = object.getString("name");

//                                        prefs.putBoolean(App.Key.IS_LOGGED, true);
                                        Constants.USERID = txtId;
                                        prefs.putString(App.Key.ID_LOGGED, txtId);
                                        prefs.putString(App.Key.LOGIN_TYPE_MEDIA,"facebook");
                                        prefs.putString(Constants.TAG_PROFILE_PICTURE,txtPhoto);
                                        prefs.putString(Constants.TAG_USERNAME,firstName);
                                        prefs.putString(Constants.TAG_GENDER,gender);
                                        prefs.putString(Constants.TAG_FULL_NAME,name);

                                        Log.e(TAG, "onCompleted: success " + object.optString("email"));

                                        checkIsuserExist();

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
                loginButton.performClick();
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

            disconnectFromFacebook(mContext);
        } catch (Exception e) {
            Log.e("LoginActivity", "onFacebookLogout: ", e);
        }
    }

    public static void disconnectFromFacebook(Context context) {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();


            }
        }).executeAsync();
    }

    public  void onInstagramLogout(){
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
//        loadingProgressbar();
        loadingIndicator.setVisibility(View.VISIBLE);
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
                    if (!resource.get("status").getAsBoolean()) {

                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        qbSignUp();
                        qbSignIn();
                        startActivity(new Intent(getApplicationContext(), UserRegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        loadingIndicator.setVisibility(View.GONE);

                    } else {
                        //USER ALREADY REGISTERED
                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        prefs.putBoolean(App.Key.IS_LOGGED, true);
                        prefs.putString(App.Key.REGISTER_USER_TOKEN, userResponse.response);
                        qbSignIn();
                        fetchUserFindMatch();
                    }
                }else{

                    loadingIndicator.setVisibility(View.INVISIBLE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                loadingIndicator.setVisibility(View.INVISIBLE);
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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        FindMatchResponse findMatchResponse = gson.fromJson(resource.toString(), FindMatchResponse.class);
                        System.out.println("findMatchResponse " + findMatchResponse);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse", findMatchResponse);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    loadingIndicator.setVisibility(View.INVISIBLE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                loadingIndicator.setVisibility(View.INVISIBLE);
                showErrorDialog(t.getMessage());
            }
        });
    }
    private void findGpsLocation(){
        gpsTracker = new GPSTracker(LoginActivity.this);
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

    public void loadingProgressbar(){
        progressDialog = new ProgressDialog(this,R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        /*progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);*/
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

    private void qbSignUp(){
        final String user = prefs.getString(App.Key.ID_LOGGED);
        String password = Constants.PASSWORD;

        QBUser qbUser = new QBUser(user, password);
        qbUser.setFullName(prefs.getString(Constants.TAG_FULL_NAME));
        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                prefs.putString(App.Key.QB_ID,qbUser.getId().toString());

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void qbSignIn(){
        final String user = prefs.getString(App.Key.ID_LOGGED);
        final String password = Constants.PASSWORD;

        QBUser qbUser = new QBUser(user, password);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                prefs.putString(App.Key.QB_ID,qbUser.getId().toString());

                new SharedPrefClass(LoginActivity.this).setUsrPwdForConversation(user);
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });

    }

    private String getDeviceId(){

        String device_id="";
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            device_id = telephonyManager.getDeviceId();
        }
        return device_id;
    }


    private void registrySession() {

        //Creamos la sesion
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });

    }
    private void InitializeFramework() {

        //Inicializamos el servicio
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                break;
        }
    }



}
