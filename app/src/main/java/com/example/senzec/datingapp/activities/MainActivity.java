package com.example.senzec.datingapp.activities;

/**
 * Created by power hashing on 4/14/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.senzec.datingapp.fragments.EditInfoFragment;
import com.example.senzec.datingapp.fragments.EventFragment;
import com.example.senzec.datingapp.fragments.FindMatchFragment;
import com.example.senzec.datingapp.fragments.FragmentChat;

import com.example.senzec.datingapp.fragments.FragmentImageSlider;
import com.example.senzec.datingapp.fragments.FragmentLikeEachOther;
import com.example.senzec.datingapp.fragments.FragmentNotification;
import com.example.senzec.datingapp.fragments.FutureFragment;
import com.example.senzec.datingapp.fragments.SettingFragment;
import com.example.senzec.datingapp.manager.App;
import com.example.senzec.datingapp.preference.AppPrefs;
import com.example.senzec.datingapp.requestmodel.FindMatchRequest;
import com.example.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.example.senzec.datingapp.requestmodel.RegisterUser;
import com.example.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.example.senzec.datingapp.responsemodel.ChatUserResponse;
import com.example.senzec.datingapp.responsemodel.EditInfoResponse;
import com.example.senzec.datingapp.responsemodel.FindMatchResponse;
import com.example.senzec.datingapp.responsemodel.NotificationResponse;
import com.example.senzec.datingapp.responsemodel.SettingDataResponse;
import com.example.senzec.datingapp.responsemodel.UpdateProfileResponse;
import com.example.senzec.datingapp.responsemodel.UserResponse;
import com.example.senzec.datingapp.services.GPSTracker;
import com.example.senzec.datingapp.ui.TinderCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

//import static com.example.senzec.datingapp.activities.LoginActivity.prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener ,
        EditInfoFragment.EditInfoFragmentCommunicator,
        SettingFragment.SettingFragmentCommunicator,
        FindMatchFragment.FindMatchFragmentCommunicator{

    DrawerLayout drawer;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    AppPrefs prefs;
    NavigationView leftNavigationView;
    Context mContext;
    ImageView iv_back,iv_logo_icon;
    TextView tv_title;
    Toolbar toolbar;
    FrameLayout frameFindMatch;
    Button btn_future,btn_present,btn_event;
    ProgressDialog progressDialog;
    private HashMap<String, String> userInfo;
    private APIInterface apiInterface;
    private String imageString;
    GPSTracker gpsTracker;
    Double latitude,longitude;
    FindMatchResponse resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = AppController.getInstance().getPrefs();
        this.mContext = this;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(prefs.getBoolean(App.Key.IS_LOGGED)){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            homeFragment();

        }else{
//            userInfo = (HashMap<String, String>) getIntent().getSerializableExtra("userInfo");
            landingFragment();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
//        resource = (FindMatchResponse) getIntent().getSerializableExtra("FindMatchResponse");
//        Log.d("Resource ins main act",resource.response.toString());
    }

    public  void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_logo_icon=(ImageView)findViewById(R.id.iv_logo_icon);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        frameFindMatch = (FrameLayout) findViewById(R.id.frame_find_match);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftNavigationView = (NavigationView) findViewById(R.id.nav_right_view);
        btn_present = (Button) findViewById(R.id.btn_present);
        btn_future = (Button) findViewById(R.id.btn_future);
        btn_event = (Button) findViewById(R.id.btn_event);

        iv_back.setOnClickListener(this);
        leftNavigationView.setNavigationItemSelectedListener(this);

        btn_future.setOnClickListener(this);
        btn_present.setOnClickListener(this);
        btn_event.setOnClickListener(this);
    }


    public void landingFragment() {

        EditInfoFragment editInfoFragment = new EditInfoFragment();

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("userInfo",userInfo);
//        editInfoFragment.setArguments(bundle);
        setFragment(editInfoFragment);
        mFragmentTransaction.addToBackStack("TAG");
        editInfoFragment.setEditInfoFragmentCommunicator(this);
        tv_title.setText("Edit Info");
        iv_logo_icon.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.back_small);
        leftNavigationView.setVisibility(View.INVISIBLE);


    }

    private void homeFragment(){
        FindMatchFragment findMatchFragment = new FindMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("FindMatchResponse",resource);
        findMatchFragment.setArguments(bundle);
        setFragment(findMatchFragment);
        mFragmentTransaction.addToBackStack("TAG");
        findMatchFragment.setFindMatchFragmentCommunicator(this);

        tv_title.setText("");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.VISIBLE);
        iv_logo_icon.setVisibility(View.VISIBLE);
        iv_logo_icon.setImageResource(R.mipmap.logo_icon);

        btn_present.setBackgroundResource(R.drawable.future);
        btn_present.setTextColor(Color.WHITE);
        btn_future.setBackgroundResource(R.drawable.present);
        btn_future.setTextColor(Color.BLACK);
        btn_event.setBackgroundResource(R.drawable.present);
        btn_event.setTextColor(Color.BLACK);
    }

    private void setFragment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment);
//        mFragmentTransaction.addToBackStack("TAG");
        mFragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        }

            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_openRight:
                drawer.openDrawer(GravityCompat.END);
                break;

            case R.drawable.back24:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
                    drawer.closeDrawer(GravityCompat.END);
                }

                int fragments = getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 1) {
                    finish();
                } else {
                    if (getFragmentManager().getBackStackEntryCount() > 1) {
                        getFragmentManager().popBackStack();
                    } else {
                        onBackPressed();
                    }
                }
              break;
            case R.id.btn_future:
                btn_future.setBackgroundResource(R.drawable.future);
                btn_future.setTextColor(Color.WHITE);
                btn_present.setBackgroundResource(R.drawable.present);
                btn_present.setTextColor(Color.BLACK);
                btn_event.setBackgroundResource(R.drawable.present);
                btn_event.setTextColor(Color.BLACK);

                FutureFragment futureFragment = new FutureFragment();
                setFragment(futureFragment);
                break;
            case R.id.btn_present:
                btn_present.setBackgroundResource(R.drawable.future);
                btn_present.setTextColor(Color.WHITE);
                btn_future.setBackgroundResource(R.drawable.present);
                btn_future.setTextColor(Color.BLACK);
                btn_event.setBackgroundResource(R.drawable.present);
                btn_event.setTextColor(Color.BLACK);

                FindMatchFragment findMatchFragment = new FindMatchFragment();
                setFragment(findMatchFragment);
                break;
            case R.id.btn_event:
                btn_event.setBackgroundResource(R.drawable.future);
                btn_event.setTextColor(Color.WHITE);
                btn_future.setBackgroundResource(R.drawable.present);
                btn_future.setTextColor(Color.BLACK);
                btn_present.setBackgroundResource(R.drawable.present);
                btn_present.setTextColor(Color.BLACK);

                EventFragment eventFragment = new EventFragment();
                setFragment(eventFragment);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_find_match) {
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callFindMatchApi();
                }
            }

        } else if (id == R.id.nav_chats) {
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callChatUserApi();
                }
            }

        } else if (id == R.id.nav_edit_info) {
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callEditInfoApi();
                }
            }

        } else if (id == R.id.nav_settings) {
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callSettingApi();
                }
            }
        } else if(id==R.id.nav_notification){
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callNotificationApi();
                }
            }
        } else if (id == R.id.nav_logout) {

            showLogoutDialog();
        }


        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void findMatchFragment(){
        FindMatchFragment findMatchFragment = new FindMatchFragment();
        setFragment(findMatchFragment);
        findMatchFragment.setFindMatchFragmentCommunicator(this);
        tv_title.setText("");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.VISIBLE);
        iv_logo_icon.setVisibility(View.VISIBLE);
        iv_logo_icon.setImageResource(R.mipmap.logo_icon);

        btn_present.setBackgroundResource(R.drawable.future);
        btn_present.setTextColor(Color.WHITE);
        btn_future.setBackgroundResource(R.drawable.present);
        btn_future.setTextColor(Color.BLACK);
        btn_event.setBackgroundResource(R.drawable.present);
        btn_event.setTextColor(Color.BLACK);
    }

    private void chatFragment(){
        FragmentChat fragmentChat = new FragmentChat();
        setFragment(fragmentChat);
        tv_title.setText("CHAT");
        iv_logo_icon.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.back_small);
    }

    private void editInfoFragment(){
        EditInfoFragment editInfoFragment = new EditInfoFragment();
        setFragment(editInfoFragment);
        editInfoFragment.setEditInfoFragmentCommunicator(this);
        tv_title.setText("EDIT INFO");
        iv_logo_icon.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.back_small);
    }

    private void settingFragment(){
        SettingFragment settingFragment = new SettingFragment();
        setFragment(settingFragment);
        settingFragment.setSettingFragmentCommunicator(this);
        tv_title.setText("SETTINGS");
        iv_logo_icon.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.back_small);
    }

    private void notificationFragment(){
        FragmentNotification notification = new FragmentNotification();
        setFragment(notification);
        tv_title.setText("NOTIFICATIONS");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.GONE);
    }
    private void showLogoutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to log out?");
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (prefs.getBoolean(App.Key.IS_LOGGED)) {
                    prefs.putBoolean(App.Key.IS_LOGGED,false);
                    LoginActivity.onLogoutClick(prefs, mContext, MainActivity.this, new LoginActivity.OnLogoutSuccess() {

                        @Override
                        public void onLogoutSuccess() {
                            Intent intentLogin = new Intent(mContext, LoginActivity.class);
                            intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentLogin);
                            finish();
                        }
                    });
                    new LoginActivity().onInstagramLogout();
                } else {

                }
                dialogInterface.cancel();
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();
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

    private void refreshFragment(){
        FragmentManager.BackStackEntry latestEntry = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount()-1);
        String fragmentNamebyTag = latestEntry.getName();

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentNamebyTag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void userRegister(RegisterUser parameter, final HashMap<String, String> photoParam) {
        loadingProgressbar("upload editInfo");
        /*
        String URl = "http://acrossapi.senzecit.in/guest/api/v1/user/register";
        Log.d("parameter: ",new JSONObject(parameter).toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URl, new JSONObject(parameter),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.cancel();
                        Log.d("jsonResponseUploadEdit",response.toString());
                        try {
                            if(response.getString("status").equalsIgnoreCase("true")){
                                String userToken = response.getString("response");
                                Log.d("userToken",userToken);
                                prefs.putString(App.Key.REGISTER_USER_TOKEN,userToken);
                                prefs.putBoolean(App.Key.IS_LOGGED,true);
                                dashBoardFragment();
                                new UploadImageAsyncTask(userToken,photoParam).execute();

                            } else if(response.getString("status").equalsIgnoreCase("false")){
                                String responceMsg = response.getString("response");
                                showErrorDialog(responceMsg);
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
                        showErrorDialog(obj.toString());
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

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);*/

//********Retrofit********************

        Call<UserResponse> call2 = apiInterface.getUserRegisterResponse(parameter);
        call2.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    Log.d("UserRegisterResponce:",resource.toString());
                    if(resource.status.equalsIgnoreCase("true")){
                        prefs.putBoolean(App.Key.IS_LOGGED,true);
                        prefs.putString(App.Key.REGISTER_USER_TOKEN,resource.response);
                        homeFragment();
                        new UploadImageAsyncTask(photoParam).execute();
                    }
                }else{
                    showErrorDialog("User Register Failed");
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

    @Override
    public void updateEditInfo(UpdateProfileRequest updateProfileRequest) {

        loadingProgressbar("upload editInfo");
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);


        /*UpdateProfileRequest updateProfileRequest=new UpdateProfileRequest();
        updateProfileRequest.dob="23";
        updateProfileRequest.fav_res_bar="23";
        updateProfileRequest.fav_vac_spot="23";
        updateProfileRequest.gender="male";
        updateProfileRequest.i_did_love_my_date_to="ad";
        updateProfileRequest.media="instagram";
        updateProfileRequest.most_rec_song_liked="fd";
        updateProfileRequest.name="sdfsdf";
        updateProfileRequest.personality_followed="sd";
        updateProfileRequest.qbid="1235";
        updateProfileRequest.username="101056677180941";
        updateProfileRequest.work="sds";

        UpdateProfileRequest.Photos photos=updateProfileRequest.new Photos();
        photos.data="";
        photos.type="";

        List<UpdateProfileRequest.Photos> photosList=new ArrayList<>();
        photosList.add(photos);

        updateProfileRequest.photos=photosList;
*/
        Call<UpdateProfileResponse> call5 = apiInterface.updateProfile(id,userToken,updateProfileRequest);
        call5.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, retrofit2.Response<UpdateProfileResponse> response) {

                Object resource = response.body();
                System.out.println(resource);
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, resource.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void submitSettingDetails() {

    }

    @Override
    public void deleteAccount() {

    }

    @Override
    public void damnUser() {

    }

    @Override
    public void likeUser() {

    }

    @Override
    public void dislikeUser() {

    }

    private void uploadPhoto(HashMap<String,String> photoparam){
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        ImageUploadRequest imageUploadRequest=new ImageUploadRequest();
        ImageUploadRequest.Data data=imageUploadRequest.new Data();
        data.data=photoparam.get("photo_big");
        data.type="url";

        List<ImageUploadRequest.Data> datas=new ArrayList<>();
        datas.add(data);
        imageUploadRequest.photos=datas;

        Call<UserResponse> call3 = apiInterface.uploadPhoto(id,userToken,imageUploadRequest);
        call3.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                System.out.println(resource);
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, resource.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    private void callFindMatchApi(){
        loadingProgressbar("FetchMatchData");
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
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
                    System.out.println(resource);
                    if(resource.status.equalsIgnoreCase("true")){
                        findMatchFragment();
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

    private void callChatUserApi(){
        loadingProgressbar("Chat with");

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<ChatUserResponse> call7 = apiInterface.chatUser(userId,userToken);
        call7.enqueue(new Callback<ChatUserResponse>() {
            @Override
            public void onResponse(Call<ChatUserResponse> call, retrofit2.Response<ChatUserResponse> response) {

                ChatUserResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    if(resource.status.equalsIgnoreCase("true")){
                        chatFragment();
                    }
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ChatUserResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }

    private void callEditInfoApi(){
        loadingProgressbar("Editinfo");

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<EditInfoResponse> call4 = apiInterface.editInfo(userId,userToken);
        call4.enqueue(new Callback<EditInfoResponse>() {
            @Override
            public void onResponse(Call<EditInfoResponse> call, retrofit2.Response<EditInfoResponse> response) {

                EditInfoResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    if(resource.status.equalsIgnoreCase("true")){
                       editInfoFragment();
                    }
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();

            }
            @Override
            public void onFailure(Call<EditInfoResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }

    private void callSettingApi(){
        loadingProgressbar("Settings");

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<SettingDataResponse> call9 = apiInterface.settingData(userId,userToken);
        call9.enqueue(new Callback<SettingDataResponse>() {
            @Override
            public void onResponse(Call<SettingDataResponse> call, retrofit2.Response<SettingDataResponse> response) {

                SettingDataResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    if(resource.status.equalsIgnoreCase("true")){
                        settingFragment();
                    }
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<SettingDataResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });
    }

    private void callNotificationApi(){
        loadingProgressbar("Notification");

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<NotificationResponse> call8 = apiInterface.notificationList(userId,userToken);
        call8.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, retrofit2.Response<NotificationResponse> response) {

                NotificationResponse resource = response.body();
                if(resource!=null){
                    System.out.println(resource);
                    if(resource.status.equalsIgnoreCase("true")){
                        notificationFragment();
                    }
                }else{
                    showErrorDialog("something went wrong !");
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                call.cancel();
                progressDialog.dismiss();
            }
        });
    }

    private void findGpsLocation(){
        gpsTracker = new GPSTracker(MainActivity.this);
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

    private boolean checkInternetAndNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataNetwork = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if(wifiNetwork != null && wifiNetwork.isConnected() || mobileDataNetwork != null && mobileDataNetwork.isConnected()){
            return true;
        }
        return false;
    }

    private void imageToBase64(){
        //encode image to base64 string
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String s= Base64.encodeToString(b, Base64.DEFAULT);
        System.out.println("Base64 " + s);
        imageString=s;
        //decode base64 string to image
//        s="/9j/4AAQSkZJRgABAQAASABIAAD/4QBYRXhpZgAATU0AKgAAAAgAAgESAAMAAAAB\\nAAEAAIdpAAQAAAABAAAAJgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAALjqAD\\nAAQAAAABAAAJkAAAAAD/7QA4UGhvdG9zaG9wIDMuMAA4QklNBAQAAAAAAAA4QklN\\nBCUAAAAAABDUHYzZjwCyBOmACZjs+EJ+/8AAEQgJkAuOAwEiAAIRAQMRAf/EAB8A\\nAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAAB\\nfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYn\\nKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeI\\niYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh\\n4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYH\\nCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRC\\nkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZX\\nWFlaY2RlZmdonAC0UU3rQAnWijpTSaYBSgUCn4oASkJpTxTBzSAAMmndKOlNJoAaSe9J1petL0pgH\\nSmEilJpMZoAAKd0oxgUwmgQGnKKAtO9qADPammlpKBiUtJ1p6jNAgAp9A44ppoGJ\\nRikpSe1ACGikJzxSg4oA/9k=";
        b = Base64.decode(s, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView imageView= (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(decodedImage);
    }

    class UploadImageAsyncTask extends AsyncTask<Void,Void,Void>{

        HashMap<String,String> photoParam;
        public UploadImageAsyncTask(HashMap<String,String> photo){
            this.photoParam = photo;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            uploadPhoto(photoParam);
            return null;
        }
    }


}
