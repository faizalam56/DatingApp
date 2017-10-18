package com.example.senzec.datingapp.instagram;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.controller.AppController;

import static com.facebook.login.widget.ProfilePictureView.TAG;


public class AllMediaFiles extends Activity {
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";
//    private Handler handler = new Handler(new Callback() {
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (pd != null && pd.isShowing())
//                pd.dismiss();
//            if (msg.what == WHAT_FINALIZE) {
//                setImageGridAdapter();
//            } else {
//
//                Toast.makeText(context, "Check your network.",
//                        Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_media_list_files);
        gvAllImages = (GridView) findViewById(R.id.gvAllImages);
        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");

        context = AllMediaFiles.this;
        getAllMediaImages();
        gvAllImages.setAdapter(new MyGridListAdapter(context, imageThumbList));
    }

//    private void setImageGridAdapter() {
//        gvAllImages.setAdapter(new MyGridListAdapter(context, imageThumbList));
//    }


    private void getAllMediaImages() {
		pd = ProgressDialog.show(context, "", "Loading images...");


            String urlJsonArry = "https://api.instagram.com/v1/users/"
                    + userInfo.get(InstagramApp.TAG_USERID)
                    + "/media/recent/?client_id="
                    + ApplicationData.CLIENT_ID
                    + "&count="
                    + userInfo.get(InstagramApp.TAG_COUNTS);
            JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());

                            try {

                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject data_obj = (JSONObject) response.get(i);

                                    JSONObject images_obj = data_obj
                                            .getJSONObject(TAG_IMAGES);

                                    JSONObject thumbnail_obj = images_obj
                                            .getJSONObject(TAG_THUMBNAIL);

                                    String str_url = thumbnail_obj.getString(TAG_URL);
                                    imageThumbList.add(str_url);

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }

//                            hidepDialog();
                            pd.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
//                    hidepDialog();
                    pd.dismiss();
                }
            });


            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(req);


    }

//	private void getAllMediaImages() {
//		pd = ProgressDialog.show(context, "", "Loading images...");
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				int what = WHAT_FINALIZE;
//				try {
//					// URL url = new URL(mTokenUrl + "&code=" + code);
//					JSONParser jsonParser = new JSONParser();
//					JSONObject jsonObject = jsonParser
//							.getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
//									+ userInfo.get(InstagramApp.TAG_ID)
//									+ "/media/recent/?client_id="
//									+ ApplicationData.CLIENT_ID
//									+ "&count="
//									+ userInfo.get(InstagramApp.TAG_COUNTS));
//					JSONArray data = jsonObject.getJSONArray(TAG_DATA);
//					for (int data_i = 0; data_i < data.length(); data_i++) {
//						JSONObject data_obj = data.getJSONObject(data_i);
//
//						JSONObject images_obj = data_obj
//								.getJSONObject(TAG_IMAGES);
//
//						JSONObject thumbnail_obj = images_obj
//								.getJSONObject(TAG_THUMBNAIL);
//
//						// String str_height =
//						// thumbnail_obj.getString(TAG_HEIGHT);
//						//
//						// String str_width =
//						// thumbnail_obj.getString(TAG_WIDTH);
//
//						String str_url = thumbnail_obj.getString(TAG_URL);
//						imageThumbList.add(str_url);
//					}
//
//					System.out.println("jsonObject::" + jsonObject);
//
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					what = WHAT_ERROR;
//
//				}
//				// pd.dismiss();
//				handler.sendEmptyMessage(what);
//			}
//		}).start();
//	}
}
