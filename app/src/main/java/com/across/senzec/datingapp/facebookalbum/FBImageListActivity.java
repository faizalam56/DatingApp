package com.across.senzec.datingapp.facebookalbum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.instagram.JSONParser;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by zmq161 on 15/7/17.
 */
public class FBImageListActivity extends Activity {
    // Log tag
    private static final String TAG = FBImageListActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private List<FbImage> fbImageList = new ArrayList<>();

    private GridViewAdapter adapter;
    private GridView grid;
    String getImageFrom;
    String loginType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_main);

        grid = (GridView) findViewById(R.id.gridview);
        Bundle bundle = getIntent().getExtras();
        getImageFrom = (String) bundle.get("getImageFrom");
        loginType = (String) bundle.get("loginType");
        makeNetworkReq();

        adapter = new GridViewAdapter(this, fbImageList);
        grid.setAdapter(adapter);

    }


    private void readFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+"/FB");
        if(mediaStorageDir.exists()){
            File[]files=mediaStorageDir.listFiles();
            if(files.length>0){

            }
        }
    }
    private void makeNetworkReq(){
        if(getImageFrom.equalsIgnoreCase("Instagram")){
            getInsagramImages();
        }
        else if(getImageFrom.equalsIgnoreCase("Facebook")){
            getFbImages();
        }


    }

    private HashMap<String, String> userInfo;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";

    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    public AppPrefs prefs;
    private android.os.Handler handler;

    private void getInsagramImages(){
        loadingProgressbar();

        prefs = AppController.getInstance().getPrefs();
        final String loginId = (loginType==null)?prefs.getString(App.Key.ID_LOGGED):prefs.getString(App.Key.TEMP_INSTA_ID_LOGGED);

        handler = new android.os.Handler(new android.os.Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == InstagramApp.WHAT_FINALIZE) {
                      adapter.notifyDataSetChanged();
                } else if (msg.what == InstagramApp.WHAT_ERROR) {
                }
                return false;
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {


                try {
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = jsonParser
                            .getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
                                    + loginId
                                    + "/media/recent/?access_token="
                                    + prefs.getString(App.Key.INSTA_ACCESS_TOKEN));


                    System.out.println("jsonObject "+jsonObject.toString());
                    JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);

                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        String str_url = thumbnail_obj.getString(TAG_URL);

                        fbImageList.add(new FbImage(str_url));
                    }
                    handler.sendEmptyMessage(WHAT_FINALIZE);
                    System.out.println("jsonObject::" + jsonObject);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    handler.sendEmptyMessage(WHAT_ERROR);
                }
                if(pDialog != null && pDialog.isShowing())
                 pDialog.dismiss();

            }
        }).start();

    }

    private void getFbImages(){
        loadingProgressbar();
        System.out.println("AccessToken and ID  "+ AccessToken.getCurrentAccessToken()+" "+AccessToken.getCurrentAccessToken().getUserId());
        /*make API call*/

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(album),count");

        //https://graph.facebook.com/226949532466/albums
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),  //your fb AccessToken
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",//user id of login user
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("TAG", "Facebook Albums: " + response.toString());
                        try {
                            if (response.getError() == null) {
                                JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
                                    if(jaData.length()>0) {
                                        for (int i = 0; i < jaData.length(); i++) {//find no. of album using jaData.length()
                                            JSONObject joAlbum = jaData.getJSONObject(i); //convert perticular album into JSONObject
                                            GetFacebookImages(joAlbum.optString("id")); //find Album ID and get All Images from album
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),"You have not right to access albums!",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                        FBImageListActivity.this.finish();

                                    }

                                }
                            } else {
                                Log.d("Test", response.getError().toString());
                                if(pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                        }
                    }
                }
        ).executeAsync();
    }

    public void GetFacebookImages(final String albumId) {
        System.out.println("Facebook Albums: albumId "+albumId);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.v("TAG", "Facebook Photos response: " + response);
                        try {
                            if (response.getError() == null) {


                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");

//                                    JSONObject jsonObject=null;
                                    for (int i = 0; i < jaData.length(); i++)//Get no. of images {
                                    {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jsonArray=joAlbum.getJSONArray("images");

                                        JSONObject jsonObject=jsonArray.getJSONObject(0);

                                        fbImageList.add(new FbImage(jsonObject.optString("source")));
                                    }
                                    adapter.notifyDataSetChanged();
                                    if(pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                                }

                                //set your adapter here
                            } else {
                                Log.v("TAG", response.getError().toString());
                                if(pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                        }
                    }
                }).executeAsync();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private void loadingProgressbar(){
        pDialog = new ProgressDialog(this,R.style.MyTheme);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
