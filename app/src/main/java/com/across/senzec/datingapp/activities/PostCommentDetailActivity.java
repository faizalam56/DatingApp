package com.across.senzec.datingapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.fragments.FragmentPostCommentDetail;
import com.across.senzec.datingapp.instagram.ApplicationData;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.NewCommentRequest;

import com.across.senzec.datingapp.responsemodel.AllCommentOnPostResponse;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.responsemodel.NewCommentResponse;
import com.across.senzec.datingapp.responsemodel.PostListResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class PostCommentDetailActivity extends AppCompatActivity implements View.OnClickListener,
        FragmentPostCommentDetail.FragmentPostCommentDetailCommunicator{

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    ImageView iv_back,iv_logo_icon;
    TextView tv_title;
    Toolbar toolbar;
    PostListResponse.Post post;
    PostListResponse.User user;

    ProgressDialog progressDialog;

    private HashMap<String, String> userInfo;
    private APIInterface apiInterface;

    private String imageString;
    GPSTracker gpsTracker;
    Double latitude,longitude;
    FindMatchResponse resource;
    LinearLayout loadingIndicator;
    InstagramApp mApp;
    AppPrefs prefs;
    Context mContext;
    AllCommentOnPostResponse allCommentOnPostResponse;
    FrameLayout frameFindMatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment_detail);

        post= (PostListResponse.Post) getIntent().getSerializableExtra("UserPostDetail");
        user = (PostListResponse.User) getIntent().getSerializableExtra("userInfo");

        allCommentOnPostResponse = (AllCommentOnPostResponse) getIntent().getSerializableExtra("comments");

        prefs = AppController.getInstance().getPrefs();
        this.mContext = this;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
        init();
        if(savedInstanceState==null){
            FragmentPostCommentDetail fragmentPostCommentDetail = new FragmentPostCommentDetail();
            Bundle bundle = new Bundle();
            bundle.putSerializable("userpost",post);
            bundle.putSerializable("comments",allCommentOnPostResponse);
            fragmentPostCommentDetail.setArguments(bundle);
            setFragment(fragmentPostCommentDetail,"home");
            fragmentPostCommentDetail.setFragmentPostCommentDetailCommunicator(this);
            mFragmentTransaction.addToBackStack("TAG");
            if (user!=null) {
                tv_title.setText(user.name);
            }
            iv_logo_icon.setVisibility(View.GONE);
            iv_back.setVisibility(View.VISIBLE);
            iv_back.setImageResource(R.mipmap.back_small);
        }

        iv_back.setOnClickListener(this);
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_logo_icon=(ImageView)findViewById(R.id.iv_logo_icon);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        frameFindMatch = (FrameLayout) findViewById(R.id.frame_find_match);
        frameFindMatch.setVisibility(View.GONE);


    }
    private void setFragment(Fragment fragment, String tagName) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment,tagName);
//        mFragmentTransaction.addToBackStack("TAG");
        mFragmentTransaction.commit();

    }

    private void postCommentDetailsFragment(AllCommentOnPostResponse resource){
        FragmentPostCommentDetail fragmentPostCommentDetail = new FragmentPostCommentDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userpost",post);
        bundle.putSerializable("comments",resource);
        fragmentPostCommentDetail.setArguments(bundle);
        setFragment(fragmentPostCommentDetail,"home");
        fragmentPostCommentDetail.setFragmentPostCommentDetailCommunicator(this);
        mFragmentTransaction.addToBackStack("TAG");
        if (user!=null) {
            tv_title.setText(user.name);
        }
        iv_logo_icon.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setImageResource(R.mipmap.back_small);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                PostCommentDetailActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PostCommentDetailActivity.this.finish();
    }

    @Override
    public void createCommentOnPost(String message) {
        callCreateNewCommentPostApi(message);
    }

    private void callCreateNewCommentPostApi(String message){
        loadingIndicator.setVisibility(View.VISIBLE);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);
        String userId = prefs.getString(App.Key.ID_LOGGED);


        final NewCommentRequest newCommentRequest=new NewCommentRequest();
        newCommentRequest.message = message;
        newCommentRequest.post =post.id;


        Call<JsonObject> call15 = apiInterface.createNewComment(userId,userToken,newCommentRequest);
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
                        NewCommentResponse newCommentResponse = gson.fromJson(resource.toString(), NewCommentResponse.class);
                        Toast.makeText(PostCommentDetailActivity.this,"Comment sent successfully",Toast.LENGTH_SHORT).show();
//                        loadingIndicator.setVisibility(View.GONE);
                        callgetAllCommentListOnPostApi();
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

    private void callgetAllCommentListOnPostApi(){

//        loadingIndicator.setVisibility(View.VISIBLE);
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
                        AllCommentOnPostResponse commentOnPostResponse = gson.fromJson(resource.toString(), AllCommentOnPostResponse.class);
                        postCommentDetailsFragment(commentOnPostResponse);
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
}
