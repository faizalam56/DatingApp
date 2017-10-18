package com.across.senzec.datingapp.activities;

/**
 * Created by power hashing on 4/14/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.fragments.ConversationFragment;
import com.across.senzec.datingapp.fragments.EditInfoFragment;
import com.across.senzec.datingapp.fragments.FindMatchFragment;
import com.across.senzec.datingapp.fragments.FragmentChat;

import com.across.senzec.datingapp.fragments.FragmentFindMatchDetail;
import com.across.senzec.datingapp.fragments.FragmentNotification;
import com.across.senzec.datingapp.fragments.NewSettingFragment;
import com.across.senzec.datingapp.fragments.PostFragment;
import com.across.senzec.datingapp.fragments.SettingFragment;
import com.across.senzec.datingapp.instagram.ApplicationData;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.models.User;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.qbchat.ConversationActivity;
import com.across.senzec.datingapp.requestmodel.AllPostListRequest;
import com.across.senzec.datingapp.requestmodel.CreateEventRequest;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.across.senzec.datingapp.requestmodel.NewPostRequest;
import com.across.senzec.datingapp.requestmodel.RegisterUser;
import com.across.senzec.datingapp.requestmodel.SetTopicRequest;
import com.across.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.across.senzec.datingapp.requestmodel.UpdateSettingRequest;
import com.across.senzec.datingapp.responsemodel.AllCommentOnPostResponse;
import com.across.senzec.datingapp.responsemodel.ChatUserResponse;
import com.across.senzec.datingapp.responsemodel.EditInfoResponse;
import com.across.senzec.datingapp.responsemodel.FetchEventResponse;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.responsemodel.NewPostResponse;
import com.across.senzec.datingapp.responsemodel.NotificationResponse;
import com.across.senzec.datingapp.responsemodel.PostListResponse;
import com.across.senzec.datingapp.responsemodel.SettingDataResponse;
import com.across.senzec.datingapp.responsemodel.TopicListResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

//import static com.example.senzec.datingapp.activities.LoginActivity.prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener ,
        EditInfoFragment.EditInfoFragmentCommunicator,
        NewSettingFragment.SettingFragmentCommunicator,
        FindMatchFragment.FindMatchFragmentCommunicator,
        PostFragment.PostFragmentCommunicator{

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
    Button btn_present,btn_post;
    ProgressDialog progressDialog;

    private HashMap<String, String> userInfo;
    private APIInterface apiInterface;
    private String imageString;
    GPSTracker gpsTracker;
    Double latitude,longitude;
    FindMatchResponse resource;
    LinearLayout loadingIndicator;
    InstagramApp mApp;
    public static final String KEY_LOGIN_TYPE = "login_type";

    ArrayList<String> qbidList = new ArrayList<>();

    public  Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ConversationFragment.WHAT_FINALIZE) {
                loadingIndicator.setVisibility(View.GONE);

            } else if (msg.what == InstagramApp.WHAT_ERROR) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = AppController.getInstance().getPrefs();
        this.mContext = this;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        init();
        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        resource = (FindMatchResponse) getIntent().getSerializableExtra("FindMatchResponse");

        homeFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
//
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
        btn_post = (Button) findViewById(R.id.btn_post);

        iv_back.setOnClickListener(this);
        leftNavigationView.setNavigationItemSelectedListener(this);

        btn_present.setOnClickListener(this);
        btn_post.setOnClickListener(this);

        leftNavigationView.getMenu().getItem(0).setChecked(true);
    }


    public void landingFragment() {

        EditInfoFragment editInfoFragment = new EditInfoFragment();

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("userInfo",userInfo);
//        editInfoFragment.setArguments(bundle);
        setFragment(editInfoFragment,"edit_info");
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
        setFragment(findMatchFragment,"find_match");
        mFragmentTransaction.addToBackStack("TAG");
        findMatchFragment.setFindMatchFragmentCommunicator(this);

        tv_title.setText("");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.VISIBLE);
        iv_logo_icon.setVisibility(View.VISIBLE);
        iv_logo_icon.setImageResource(R.mipmap.logo_icon);

        btn_present.setBackgroundResource(R.drawable.future);
        btn_present.setTextColor(Color.WHITE);

        btn_post.setBackgroundResource(R.drawable.present);
        btn_post.setTextColor(Color.BLACK);
    }

    private void setFragment(Fragment fragment,String tagName) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment,tagName);
//        mFragmentTransaction.addToBackStack("TAG");
        mFragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        }else if(loadingIndicator.getVisibility() == View.VISIBLE){
            loadingIndicator.setVisibility(View.GONE);
        }else {

            Fragment home= mFragmentManager.findFragmentByTag("find_match");
            if(home!=null)
            {
                if(home.isVisible())
                {
                    //exit your application
                    MainActivity.this.finish();
                }else{
                    if(hasGpsSensor()){
                        findGpsLocation();
                        callFindMatchApi();
                    }
                }
            }else {
                super.onBackPressed();// optional depending on your needs
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

            case R.id.btn_present:

                if(hasGpsSensor()){
                    findGpsLocation();
                    callFindMatchApi();
                }
                break;
            case R.id.btn_post:
                if(hasGpsSensor()){
                    findGpsLocation();
//                    callEventApi();
//                    callPostApi();
                    callAllPostListApi();
                }
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
//                     callChatUserApi();
//                    setToken();
//                    conversationFragment();
                    chatFragment(new ChatUserResponse());
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
        } /*else if(id==R.id.nav_notification){
            if(checkInternetAndNetwork()){
                if(hasGpsSensor()){
                    findGpsLocation();
                    callNotificationApi();
                }
            }
        }*/ else if (id == R.id.nav_logout) {

            showLogoutDialog();
        }


        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private  void postFragment(PostListResponse resource){
        PostFragment postFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("FetchAllUserPostResponse",resource);
        postFragment.setArguments(bundle);
        setFragment(postFragment,"post");
        postFragment.setPostFragmentCommunicator(this);
        tv_title.setText("");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.VISIBLE);
        iv_logo_icon.setVisibility(View.VISIBLE);
        iv_logo_icon.setImageResource(R.mipmap.logo_icon);
        btn_post.setBackgroundResource(R.drawable.future);
        btn_post.setTextColor(Color.WHITE);

        btn_present.setBackgroundResource(R.drawable.present);
        btn_present.setTextColor(Color.BLACK);
        loadingIndicator.setVisibility(View.GONE);
    }


    private void findMatchFragment(FindMatchResponse resource){

        FindMatchFragment findMatchFragment = new FindMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("FindMatchResponse",resource);
        findMatchFragment.setArguments(bundle);
        setFragment(findMatchFragment,"find_match");
        findMatchFragment.setFindMatchFragmentCommunicator(this);
        tv_title.setText("");
        iv_back.setVisibility(View.GONE);
        frameFindMatch.setVisibility(View.VISIBLE);
        iv_logo_icon.setVisibility(View.VISIBLE);
        iv_logo_icon.setImageResource(R.mipmap.logo_icon);

        btn_present.setBackgroundResource(R.drawable.future);
        btn_present.setTextColor(Color.WHITE);

        btn_post.setBackgroundResource(R.drawable.present);
        btn_post.setTextColor(Color.BLACK);

        loadingIndicator.setVisibility(View.GONE);
    }

    private void chatFragment(ChatUserResponse resource){
        FragmentChat fragmentChat = new FragmentChat();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChatUserResponse",resource);
        fragmentChat.setArguments(bundle);
        setFragment(fragmentChat,"chat");
        tv_title.setText("CHAT");
        iv_logo_icon.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);
    }


    private void conversationFragment(){
        loadingIndicator.setVisibility(View.VISIBLE);
        ConversationFragment conversationFragment = new ConversationFragment();

        setFragment(conversationFragment,"conversation");
        conversationFragment.setConversationFragmentCommunicator(this);
        tv_title.setText("CHAT");
        iv_logo_icon.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);
    }
    private void editInfoFragment(EditInfoResponse resource){

        EditInfoFragment editInfoFragment = new EditInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("EditInfoResponse",resource);
        editInfoFragment.setArguments(bundle);
        setFragment(editInfoFragment,"edit_info");
        editInfoFragment.setEditInfoFragmentCommunicator(this);
        tv_title.setText("EDIT INFO");
        iv_logo_icon.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);

        loadingIndicator.setVisibility(View.GONE);
    }

    private void settingFragment(SettingDataResponse resource){

        NewSettingFragment settingFragment = new NewSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("SettingDataResponse",resource);
        settingFragment.setArguments(bundle);
        setFragment(settingFragment,"settings");
        settingFragment.setSettingFragmentCommunicator(this);
        tv_title.setText("SETTINGS");
        iv_logo_icon.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);

        loadingIndicator.setVisibility(View.GONE);
    }

    private void notificationFragment(NotificationResponse resource){

        FragmentNotification notification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putSerializable("NotificationResponse",resource);
        notification.setArguments(bundle);
        setFragment(notification,"notifications");
        tv_title.setText("NOTIFICATIONS");
        iv_logo_icon.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);

        loadingIndicator.setVisibility(View.GONE);
    }

    private void findamatchDetailFragment(){
        FragmentFindMatchDetail fragmentFindMatchDetail = new FragmentFindMatchDetail();
        setFragment(fragmentFindMatchDetail,"findmatch_detail");
        mFragmentTransaction.addToBackStack("TAG");
        tv_title.setText("Name of Matcher");
        iv_logo_icon.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        frameFindMatch.setVisibility(View.GONE);
        iv_back.setImageResource(R.mipmap.back_small);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentManager.popBackStack("TAG",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void showLogoutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to log out?");
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (prefs.getBoolean(App.Key.IS_LOGGED)) {
                    prefs.putBoolean(App.Key.IS_LOGGED,false);
                    if(prefs.getString(App.Key.LOGIN_TYPE_MEDIA).equalsIgnoreCase("instagram")){
                        Toast.makeText(MainActivity.this,"logout clicked insta",Toast.LENGTH_SHORT).show();
                        mApp.resetAccessToken();
                        Intent intentLogin = new Intent(mContext, LoginActivity.class);
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentLogin);
                        finish();
                    }else {
                        LoginActivity.onLogoutClick(prefs, mContext, MainActivity.this, new LoginActivity.OnLogoutSuccess() {

                            @Override
                            public void onLogoutSuccess() {
                                Intent intentLogin = new Intent(mContext, LoginActivity.class);
                                intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentLogin);
                                finish();
                            }
                        });

                    }
                } else {

                }
                dialogInterface.cancel();
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();
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
    public void userRegister(RegisterUser parameter, ImageUploadRequest imageUploadRequest, SetTopicRequest setTopicRequest) {
       // user registration is to be done through UserRegisterActivity, here both activity add same fragment{EditInfoFragment},And here we implement
       // Commuicator interface, So due to this reason userRegister() method is overridded here.
    }

    @Override
    public void updateEditInfo(UpdateProfileRequest updateProfileRequest, final SetTopicRequest setTopicRequest) {
        loadingIndicator.setVisibility(View.VISIBLE);
//        loadingProgressbar();
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        Call<JsonObject> call5 = apiInterface.updateProfile(id,userToken,updateProfileRequest);
        call5.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        loadingIndicator.setVisibility(View.GONE);
                    } else {
                        callSetTopicApi(setTopicRequest);
                        if(hasGpsSensor()){
                            findGpsLocation();
                            callFindMatchApi();

                        }

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

    @Override
    public void submitSettingDetails(UpdateSettingRequest updateSettingRequest, final SetTopicRequest setTopicRequest) {
        loadingIndicator.setVisibility(View.VISIBLE);
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        Call<UserResponse> call10 = apiInterface.updateSetting(id,userToken,updateSettingRequest);
        call10.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
                        callSetTopicApi(setTopicRequest);
                        if(hasGpsSensor()){
                            findGpsLocation();
                            callFindMatchApi();
                        }
                    }
                }else{
                    loadingIndicator.setVisibility(View.GONE);
                    showErrorDialog("something went wrong!");
                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                showErrorDialog(t.getMessage());
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void updateEvent(CreateEventRequest createEventRequest,String eventId) {

        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        Call<UserResponse> call14 = apiInterface.updateCreateEvent(id,eventId,userToken, createEventRequest);
        call14.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
                       /* Toast.makeText(MainActivity.this, "Event update", Toast.LENGTH_SHORT).show();*/
                    }
                }else{
//                    showErrorDialog("something went wrong!");
                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                showErrorDialog(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void createEvent(CreateEventRequest createEventRequest) {
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);


        Call<UserResponse> call13 = apiInterface.createEvent(id,userToken, createEventRequest);
        call13.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    if(resource.status.equalsIgnoreCase("true")){
                        /*Toast.makeText(MainActivity.this, "Event create", Toast.LENGTH_SHORT).show();*/
                    }
                }else{
//                    showErrorDialog("something went wrong!");
                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                showErrorDialog(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void deleteAccount() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to log out?");
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                delete();
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

    private void delete(){
        loadingIndicator.setVisibility(View.VISIBLE);
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        Call<UserResponse> call11 = apiInterface.deleteAccount(id,userToken);
        call11.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){
                    prefs.remove(App.Key.ID_LOGGED);
                    prefs.putBoolean(App.Key.IS_LOGGED,false);
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    loadingIndicator.setVisibility(View.GONE);
                }else {
                    showErrorDialog("something went wrong from server!");
                }


            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                t.printStackTrace();
                loadingIndicator.setVisibility(View.GONE);
            }
        });


    }


    @Override
    public void goToChat() {
        conversationFragment();
    }

    @Override
    public void checkFndMatchDetail(User user) {
//        findamatchDetailFragment();
        Intent intent = new Intent(MainActivity.this,MatchDetailActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
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
                if(resource!=null){
                    showErrorDialog("photo upload successfully");
                }else{
//                    showErrorDialog("something went wrong!");
                }
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void callFindMatchApi(){
        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        final FindMatchRequest findMatchRequest=new FindMatchRequest();
        findMatchRequest.latitude=latitude;
        findMatchRequest.longitude=longitude;
        findMatchRequest.username=userId;

        Call<JsonObject> call6 = apiInterface.findMatch(userId,userToken,findMatchRequest);
        call6.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        findMatchFragment(null);
                    } else {
                        FindMatchResponse findMatchResponse = gson.fromJson(resource.toString(), FindMatchResponse.class);
                        leftNavigationView.getMenu().getItem(0).setChecked(true);
                        findMatchFragment(findMatchResponse);

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

    /*private void callFindMatchApi(){
            loadingProgressbar("FetchMatchData");
            String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
            String userId = prefs.getString(App.Key.ID_LOGGED);

            FindMatchRequest findMatchRequest=new FindMatchRequest();
            findMatchRequest.latitude=latitude;
            findMatchRequest.longitude=longitude;
            findMatchRequest.username=userId;

            Call<Object> call6 = apiInterface.findMatch(userId,userToken,findMatchRequest);
            call6.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                    if(response.body() instanceof FindMatchResponse){
                        FindMatchResponse resource = (FindMatchResponse) response.body();
                        if(resource!=null){
                            System.out.println(resource);
                            if(resource.status.equalsIgnoreCase("true")){
                                findMatchFragment(resource);
                            }
                        }else{
                            showErrorDialog("something went wrong !");
                        }
                    }else if(response.body() instanceof FindMatchNoResponce){
                       *//* FindMatchNoResponce resource = (FindMatchNoResponce) response.body();
                        if(resource!=null){
                            System.out.println(resource);
                            if(resource.status.equalsIgnoreCase("true")){
                                findMatchFragment(resource);
                            }
                        }else{
                            showErrorDialog("something went wrong !");
                        }*//*
                    }

                    progressDialog.dismiss();

                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                    showErrorDialog(t.getMessage());
                }
            });
        }*/






    private void callAllPostListApi(){
        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        final AllPostListRequest allPostListRequest=new AllPostListRequest();
        allPostListRequest.latitude=latitude;
        allPostListRequest.longitude=longitude;
        allPostListRequest.username=userId;

        Call<JsonObject> call14 = apiInterface.fetchAllPostList(userId,userToken,allPostListRequest);
        call14.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        postFragment(null);
                    } else {
                        PostListResponse postListResponse = gson.fromJson(resource.toString(), PostListResponse.class);
                        postFragment(postListResponse);

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

    private void callgetAllCommentListOnPostApi(final PostListResponse.Post post, final PostListResponse.User user){

        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);
        String postid = post.id;

        Call<JsonObject> call13 = apiInterface.findCommentOnPost(userId,postid,userToken);
        call13.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        loadingIndicator.setVisibility(View.GONE);
                    } else {

                        AllCommentOnPostResponse fetchAllUserPostResponse = gson.fromJson(resource.toString(), AllCommentOnPostResponse.class);

                        Intent intent = new Intent(MainActivity.this,PostCommentDetailActivity.class);
                        intent.putExtra("UserPostDetail",post);
                        intent.putExtra("userInfo",user);
                        intent.putExtra("comments", fetchAllUserPostResponse);
                        startActivity(intent);
                        loadingIndicator.setVisibility(View.GONE);

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
                t.printStackTrace();
            }
        });
    }

    private void setToken(){
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        new SharedPrefClass(MainActivity.this).setUserToken(userToken);
        new SharedPrefClass(MainActivity.this).setLoggedUser(userId);
    }

    private void callChatUserApi(){
        // loadingProgressbar();

        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        new SharedPrefClass(MainActivity.this).setUserToken(userToken);
        new SharedPrefClass(MainActivity.this).setLoggedUser(userId);

        startActivity(new Intent(MainActivity.this, ConversationActivity.class));

/*        Call<JsonObject> call7 = apiInterface.chatUser(userId,userToken);
        call7.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if (resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    } else {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        ChatUserResponse chatUserResponse = gson.fromJson(resource.toString(), ChatUserResponse.class);
                        for(int i=0; i<chatUserResponse.response.size(); i++)
                        {
                            String strQBID = chatUserResponse.response.get(i).user.qbid.toString();
                            System.out.println(chatUserResponse.response.get(i).user.qbid.toString());
                        //  QbidModel qbidModel = new QbidModel(chatUserResponse.response.get(i).user.qbid.toString());
                           // qbidList.add(qbidModel);
                            qbidList.add(strQBID);
                        }
                        Bundle bundle1 = new Bundle();
                        bundle1.putStringArrayList("qbid_list", qbidList);
                        Intent intent1 = new Intent(MainActivity.this, ConversationActivity.class);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        //chatFragment(chatUserResponse);
                    }
                }else{
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    showErrorDialog("something went wrong from server!");
                }


            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                showErrorDialog(t.getMessage());
            }
        });*/
    }

    private void callEditInfoApi(){

        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<JsonObject> call4 = apiInterface.editInfo(userId,userToken);
        call4.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {

                        loadingIndicator.setVisibility(View.GONE);
                    } else {

                        EditInfoResponse editInfoResponse = gson.fromJson(resource.toString(), EditInfoResponse.class);

                        editInfoFragment(editInfoResponse);
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

    private void callSettingApi(){

        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<JsonObject> call9 = apiInterface.settingData(userId,userToken);
        call9.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        loadingIndicator.setVisibility(View.GONE);
                    } else {

                        SettingDataResponse settingDataResponse = gson.fromJson(resource.toString(), SettingDataResponse.class);
                        settingFragment(settingDataResponse);
//                        fetchEventDetailsApi(settingDataResponse);
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

    private void callNotificationApi(){
        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        Call<JsonObject> call8 = apiInterface.notificationList(userId,userToken);
        call8.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        loadingIndicator.setVisibility(View.GONE);
                    } else {
                        loadingIndicator.setVisibility(View.GONE);
                        NotificationResponse notificationResponse = gson.fromJson(resource.toString(), NotificationResponse.class);
                        notificationFragment(notificationResponse);

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

    private void findGpsLocation(){
        gpsTracker = new GPSTracker(MainActivity.this);
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
    public void gotoPostCommentDetail(PostListResponse.Post post,PostListResponse.User user) {
        callgetAllCommentListOnPostApi(post,user);

    }

    @Override
    public void sendPost(String message) {
        callCreateNewPostApi(message);
    }

    private void callCreateNewPostApi(String message){
        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);

        final NewPostRequest newPostRequest=new NewPostRequest();
        newPostRequest.message = message;

        Call<JsonObject> call15 = apiInterface.createNewPost(userId,userToken,newPostRequest);
        call15.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if(resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        UserResponse userResponse = gson.fromJson(resource.toString(), UserResponse.class);
                        loadingIndicator.setVisibility(View.GONE);
                    } else {
                        NewPostResponse findMatchResponse = gson.fromJson(resource.toString(), NewPostResponse.class);
                        Toast.makeText(MainActivity.this,"Post send successfully",Toast.LENGTH_SHORT).show();
                        loadingIndicator.setVisibility(View.GONE);
                        callAllPostListApi();
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

}