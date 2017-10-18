package com.across.senzec.datingapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.fragments.EditInfoFragment;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.across.senzec.datingapp.requestmodel.RegisterUser;
import com.across.senzec.datingapp.requestmodel.SetTopicRequest;
import com.across.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.responsemodel.TopicListResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.across.senzec.datingapp.utils.Constants;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by ravi on 12/7/17.
 */

public class UserRegisterActivity extends AppCompatActivity implements EditInfoFragment.EditInfoFragmentCommunicator, View.OnClickListener {

    //QB

    static final String APP_ID = "59087";
    static final String AUTH_KEY = "wVESSN3M-g62fgh";
    static final String AUTH_SECRET = "gmx5pt3RAYTrr8L";
    static final String ACCOUNT_KEY = "ivbjtVyc3z861UGY4Hge";


    static final int REQUEST_CODE = 1000;

    ImageView iv_back,iv_logo_icon;
    TextView tv_title;
    Toolbar toolbar;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    AppPrefs prefs;
    private APIInterface apiInterface;
    private ProgressDialog progressDialog;
    GPSTracker gpsTracker;
    Double latitude,longitude;
    LinearLayout loadingIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        prefs = AppController.getInstance().getPrefs();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(savedInstanceState==null){
            EditInfoFragment editInfoFragment = new EditInfoFragment();
            setFragment(editInfoFragment,"home");
            mFragmentTransaction.addToBackStack("TAG");
            editInfoFragment.setEditInfoFragmentCommunicator(this);
            tv_title.setText("Edit Info");
            iv_logo_icon.setVisibility(View.GONE);
            iv_back.setVisibility(View.VISIBLE);
            iv_back.setImageResource(R.mipmap.back_small);
        }

        //QB
        InitializeFramework();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.frame_find_match).setVisibility(View.GONE);
        iv_logo_icon=(ImageView)findViewById(R.id.iv_logo_icon);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);

        iv_back.setOnClickListener(this);
    }

    private void setFragment(Fragment fragment,String tagName) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment,tagName);
//        mFragmentTransaction.addToBackStack("TAG");
        mFragmentTransaction.commit();

    }


    @Override
    public void userRegister(RegisterUser parameter, final ImageUploadRequest imageUploadRequest, final SetTopicRequest setTopicRequest) {

        loadingIndicator.setVisibility(View.VISIBLE);

        Call<UserResponse> call2 = apiInterface.getUserRegisterResponse(parameter);
        call2.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    Log.d("UserRegisterResponce:",resource.toString());
                    if(resource.status.equalsIgnoreCase("true")){
                        qbSignIn();
                        prefs.putBoolean(App.Key.IS_LOGGED,true);
                        prefs.putString(App.Key.REGISTER_USER_TOKEN,resource.response);
                        callphotoUploadApi(imageUploadRequest);
                        callSetTopicApi(setTopicRequest);
                        fetchUserFindMatch();
                    }
                }else{
                    loadingIndicator.setVisibility(View.GONE);
                    showErrorDialog("something went wrong from server!");
                }


            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                loadingIndicator.setVisibility(View.GONE);
                showErrorDialog(t.getMessage());
            }
        });
    }

    @Override
    public void updateEditInfo(UpdateProfileRequest parameter,SetTopicRequest setTopicRequest) {
        // user registration is to be done through UserRegisterActivity, here both activity add same fragment{EditInfoFragment},And here we implement
        // Commuicator interface, So due to this reason userRegister() method is overridded here.
    }

    private void fetchUserFindMatch() {
        if (hasGpsSensor()) {
            findGpsLocation();
            callFindMatchApi();
        }
    }

    private void callphotoUploadApi(ImageUploadRequest imageUploadRequest){
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        //////////////////// Upload Photos //////////////////////////////////////////////////
        Call<UserResponse> call3 = apiInterface.uploadPhoto(id,userToken,imageUploadRequest);
        call3.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
//                        Toast.makeText(getBaseContext(),"image uploaded",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void callSetTopicApi(SetTopicRequest setTopicRequest){
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);//"835c7c18b84afa56c49fe81492da46a598ddfdbfe0682cda832f731bb114e888";//
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<JsonObject> call6 = apiInterface.setTopic(userId,userToken,setTopicRequest);
        call6.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {


                JsonObject resource = response.body();
                Gson gson=new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {

                    } else {

                        TopicListResponse topicListResponse = gson.fromJson(resource.toString(), TopicListResponse.class);
                        System.out.println("topicListResponse " + topicListResponse);

                    }
                }else{

                    loadingIndicator.setVisibility(View.GONE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

                loadingIndicator.setVisibility(View.GONE);
                showErrorDialog(t.getMessage());
            }
        });
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
                        startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
                        finish();
                    } else {

                        FindMatchResponse findMatchResponse = gson.fromJson(resource.toString(), FindMatchResponse.class);
                        System.out.println("findMatchResponse " + findMatchResponse);
                        Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse", findMatchResponse);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else{

                    loadingIndicator.setVisibility(View.GONE);
                    showErrorDialog("something went wrong from server!");
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

                loadingIndicator.setVisibility(View.GONE);
                showErrorDialog(t.getMessage());
            }
        });

        /*Call<FindMatchResponse> call6 = apiInterface.findMatch(userId,userToken,findMatchRequest);
        call6.enqueue(new Callback<FindMatchResponse>() {
            @Override
            public void onResponse(Call<FindMatchResponse> call, retrofit2.Response<FindMatchResponse> response) {


                FindMatchResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
                        Log.d("resource...",resource.response.toString());
                        Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                        intent.putExtra("FindMatchResponse",resource);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else if(resource.status.equalsIgnoreCase("false")){

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
        });*/
    }
    private void findGpsLocation(){
        gpsTracker = new GPSTracker(UserRegisterActivity.this);
        if(gpsTracker.canGetLocation()){
            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    public boolean hasGpsSensor(){
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
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

    public void loadingProgressbar(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void qbSignIn(){
        final String user = prefs.getString(App.Key.ID_LOGGED);
        final String password = Constants.PASSWORD;

        QBUser qbUser = new QBUser(user, password);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                new SharedPrefClass(UserRegisterActivity.this).setUsrPwdForConversation(user);
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:

                Fragment home = mFragmentManager.findFragmentByTag("home");
                if(home!=null){
                    if(home.isVisible()){
                        UserRegisterActivity.this.finish();
                        LoginManager.getInstance().logOut();
                    }else{
                        super.onBackPressed();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment home = mFragmentManager.findFragmentByTag("home");
        if(home!=null) {
            if (home.isVisible()) {
                UserRegisterActivity.this.finish();
                LoginManager.getInstance().logOut();
            } else {
                super.onBackPressed();
            }
        }
    }
}
