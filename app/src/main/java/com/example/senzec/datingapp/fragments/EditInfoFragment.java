package com.example.senzec.datingapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.test.mock.MockPackageManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.activities.LoginActivity;
import com.example.senzec.datingapp.activities.MainActivity;
import com.example.senzec.datingapp.controller.AppController;
import com.example.senzec.datingapp.instagram.InstagramApp;
import com.example.senzec.datingapp.instagram.InstagramSession;
import com.example.senzec.datingapp.lazyload.ImageLoader;
import com.example.senzec.datingapp.manager.App;
import com.example.senzec.datingapp.preference.AppPrefs;
import com.example.senzec.datingapp.requestmodel.RegisterUser;
import com.example.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.example.senzec.datingapp.services.GPSTracker;
import com.example.senzec.datingapp.utils.Constants;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by power hashing on 4/14/2017.
 */

public class EditInfoFragment extends Fragment implements View.OnClickListener ,FragmentBottomSheet.ImageChoosedListener{
    View view;
    RelativeLayout rl_pic1;
    private static int RESULT_LOAD_IMG1 = 1, RESULT_LOAD_IMG2 = 2, RESULT_LOAD_IMG3 = 3, RESULT_LOAD_IMG_BIG = 4, RESULT_LOAD_IMG_SMALL1 = 5, RESULT_LOAD_IMG_SMALL2 = 6;
    ImageView iv_pic1, iv_pic2, iv_pic3, imageView_small1, imageView_small2, imageView_big;
    String imgDecodableString;
    Uri selectedImage;
    Button btn_upload;
    String URL = "http://35.154.217.225/datingapp/index.php/Web_api/register_member";
    ProgressDialog progressDialog;
    Bitmap bitmap;
//    private AppPrefs prefs;
    GPSTracker gps;
    double latitude;
    double longitude;
    String macAddr;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private HashMap<String, String> userInfo;
    EditText et_first_name,et_my_work, et_favourite_restaurants, et_best_vacation_spot, et_most_recent_song, et_personality_i_follow, et_i_love_my_date;
    Spinner spnr_gender;
    TextView picker_date_of_birth;
    private int mYear, mMonth, mDay, mHour, mMinute;
    AppPrefs prefs;
    EditInfoFragmentCommunicator communicator;
    InstagramSession instagramSession;
    private InstagramApp mApp;

    public void setEditInfoFragmentCommunicator(EditInfoFragmentCommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(prefs.getBoolean(App.Key.IS_LOGGED)){
//
//        }else{
//            Bundle bundle = this.getArguments();
//            userInfo = (HashMap<String, String>) bundle.getSerializable("userInfo");
//            System.out.println("at on create User Info..."+userInfo.toString());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_info_new, container, false);
        prefs = AppController.getInstance().getPrefs();

        init();
        gpsTracker();
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_big) , Constants.RESULT_LOAD_IMG_BIG);
        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_small_1) ,Constants.RESULT_LOAD_IMG_SMALL_1);
        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_small_2) ,Constants.RESULT_LOAD_IMG_SMALL_2);
        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_pic_1) ,Constants.RESULT_LOAD_IMG_PIC_1);
        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_pic_2) ,Constants.RESULT_LOAD_IMG_PIC_2);
        findBigImageWidthHeight((FrameLayout) view.findViewById(R.id.lay_frame_pic_3) ,Constants.RESULT_LOAD_IMG_PIC_3);



    }

    String widthHeightImageBig;
    String widthHeightImageSmall1;
    String widthHeightImageSmall2;
    String widthHeightImagePic1;
    String widthHeightImagePic2;
    String widthHeightImagePic3;
    private void findBigImageWidthHeight(final FrameLayout frameLayout, final int imageType) {

        ViewTreeObserver observer = frameLayout .getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub

                switch (imageType){

                    case 1:
                        widthHeightImageBig =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                    case 2:
                        widthHeightImageSmall1 =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                    case 3:
                        widthHeightImageSmall2 =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                    case 4:
                        widthHeightImagePic1 =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                    case 5:
                        widthHeightImagePic2 =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                    case 6:
                        widthHeightImagePic3 =  frameLayout.getHeight() + "^" +  frameLayout.getWidth();
                        frameLayout .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        break;
                }


            }
        });

    }

    @Override
    public void getImageUri(Uri imageUri, int imageType) {
        switch (imageType){

            case 1:



                selectedImage = imageUri;
                ImageCropFunction(1,Integer.parseInt(widthHeightImageBig.substring(0, widthHeightImageBig.indexOf("^"))), Integer.parseInt(widthHeightImageBig.substring(widthHeightImageBig.indexOf("^")+1,widthHeightImageBig.length())));

                break;

            case 2:


                selectedImage = imageUri;
                ImageCropFunction(2,Integer.parseInt(widthHeightImageSmall1.substring(0, widthHeightImageSmall1.indexOf("^"))), Integer.parseInt(widthHeightImageSmall1.substring(widthHeightImageBig.indexOf("^")+1,widthHeightImageSmall1.length())));

                break;

            case 3:

                selectedImage = imageUri;
                ImageCropFunction(3,Integer.parseInt(widthHeightImageSmall2.substring(0, widthHeightImageSmall2.indexOf("^"))), Integer.parseInt(widthHeightImageSmall2.substring(widthHeightImageSmall2.indexOf("^")+1,widthHeightImageSmall2.length())));

                break;

            case 4:

                selectedImage = imageUri;
                ImageCropFunction(4,Integer.parseInt(widthHeightImagePic1.substring(0, widthHeightImagePic1.indexOf("^"))), Integer.parseInt(widthHeightImagePic1.substring(widthHeightImagePic1.indexOf("^")+1,widthHeightImagePic1.length())));

                break;

            case 5:


                selectedImage = imageUri;
                ImageCropFunction(5,Integer.parseInt(widthHeightImagePic2.substring(0, widthHeightImagePic2.indexOf("^"))), Integer.parseInt(widthHeightImagePic2.substring(widthHeightImagePic2.indexOf("^")+1, widthHeightImagePic2.length())));

                break;

            case 6:


                selectedImage = imageUri;
                ImageCropFunction(6,Integer.parseInt(widthHeightImagePic3.substring(0, widthHeightImagePic3.indexOf("^"))), Integer.parseInt(widthHeightImagePic3.substring(widthHeightImagePic3.indexOf("^")+1, widthHeightImagePic3.length())));

                break;
        }
    }

    Intent  CropIntent;
    public void ImageCropFunction(int imgeRequestCode, int width, int height) {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(selectedImage, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", width);
            CropIntent.putExtra("outputY", height);
            CropIntent.putExtra("aspectX", 4);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, imgeRequestCode);

        } catch (ActivityNotFoundException e) {

        }
    }


    public void gpsTracker() {
        gps = new GPSTracker(getActivity());
//
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public void init() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("EDIT INFO");


        et_first_name = (EditText) view.findViewById(R.id.et_first_name);
        picker_date_of_birth = (TextView) view.findViewById(R.id.picker_date_of_birth);
        spnr_gender = (Spinner) view.findViewById(R.id.spnr_gender);
        et_my_work = (EditText) view.findViewById(R.id.et_my_work);
//        et_my_work.setText(getActivity().getIntent().getExtras().getString("name"));


        et_favourite_restaurants = (EditText) view.findViewById(R.id.et_favourite_restaurants);
        et_best_vacation_spot = (EditText) view.findViewById(R.id.et_best_vacation_spot);
        et_most_recent_song = (EditText) view.findViewById(R.id.et_most_recent_song);
        et_personality_i_follow = (EditText) view.findViewById(R.id.et_personality_i_follow);
        et_i_love_my_date = (EditText) view.findViewById(R.id.et_i_love_my_date);

        btn_upload = (Button) view.findViewById(R.id.btn_upload);

        imageView_big = (ImageView) view.findViewById(R.id.imageView_big);
        imageView_small1 = (ImageView) view.findViewById(R.id.imageView_small1);
        imageView_small2 = (ImageView) view.findViewById(R.id.imageView_small2);
        iv_pic1 = (ImageView) view.findViewById(R.id.iv_pic1);
        iv_pic2 = (ImageView) view.findViewById(R.id.iv_pic2);
        iv_pic3 = (ImageView) view.findViewById(R.id.iv_pic3);

        //****Image load by using lazyload.
        new ImageLoader(getActivity()).DisplayImage(prefs.getString(Constants.TAG_PROFILE_PICTURE),imageView_big);
        //****Image load by using Picasso.
//        Picasso.with(getContext()).load(prefs.getString(Constants.TAG_PROFILE_PICTURE)).placeholder(R.drawable.profile).into(imageView_big);
        et_first_name.setText(prefs.getString(Constants.TAG_USERNAME));

        imageView_big.setOnClickListener(this);
        imageView_small1.setOnClickListener(this);
        imageView_small2.setOnClickListener(this);
        iv_pic1.setOnClickListener(this);
        iv_pic2.setOnClickListener(this);
        iv_pic3.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        picker_date_of_birth.setOnClickListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK && null != data) {
                selectedImage = data.getData();

                if (requestCode == 1) {

                    showCropImage(imageView_big, data);

                }else if (requestCode == 2) {

                    showCropImage(imageView_small1, data);

                }else if (requestCode == 3) {

                    showCropImage(imageView_small2, data);

                }else if (requestCode == 4 ) {

                    showCropImage(iv_pic1, data);

                }else if (requestCode == 5) {

                    showCropImage(iv_pic2, data);

                }else if (requestCode == 6) {

                    showCropImage(iv_pic3, data);
                }


            } else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showCropImage(ImageView imageView, Intent data) {

        Bundle bundle = data.getExtras();
        Bitmap bitmap =  bundle.getParcelable("data");
        imageView.setImageBitmap(bitmap);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_big:

                FragmentBottomSheet dialogFragment = new FragmentBottomSheet();

                dialogFragment.setCommunicator(this);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("imageType",  1);
                dialogFragment.setArguments(bundle1);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "Dialog");

                break;

            case R.id.imageView_small1:
                FragmentBottomSheet dialogFragmentSmall1 = new FragmentBottomSheet();

                dialogFragmentSmall1.setCommunicator(this);
                Bundle bundleSmall1 = new Bundle();
                bundleSmall1.putInt("imageType",  2);
                dialogFragmentSmall1.setArguments(bundleSmall1);
                dialogFragmentSmall1.show(getActivity().getSupportFragmentManager(), "Dialog");


//                Intent galleryIntent5 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent5, RESULT_LOAD_IMG_SMALL1);
                break;
            case R.id.imageView_small2:
                FragmentBottomSheet dialogFragmentSmall2 = new FragmentBottomSheet();

                dialogFragmentSmall2.setCommunicator(this);
                Bundle bundleSmall2 = new Bundle();
                bundleSmall2.putInt("imageType",  3);
                dialogFragmentSmall2.setArguments(bundleSmall2);
                dialogFragmentSmall2.show(getActivity().getSupportFragmentManager(), "Dialog");


//                Intent galleryIntent6 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent6, RESULT_LOAD_IMG_SMALL2);
                break;

            case R.id.iv_pic1:

                FragmentBottomSheet pick1 = new FragmentBottomSheet();
                pick1.setCommunicator(this);
                Bundle bundle = new Bundle();
                bundle.putInt("imageType",  4);
                pick1.setArguments(bundle);
                pick1.show(getActivity().getSupportFragmentManager(), "Dialog");



//                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent, RESULT_LOAD_IMG1);
                break;
            case R.id.iv_pic2:
                FragmentBottomSheet pick2 = new FragmentBottomSheet();
                pick2.setCommunicator(this);

                Bundle bundle2 = new Bundle();
                bundle2.putInt("imageType",  5);
                pick2.setArguments(bundle2);
                pick2.show(getActivity().getSupportFragmentManager(), "Dialog");
//                Intent galleryIntent2 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent2, RESULT_LOAD_IMG2);
                break;
            case R.id.iv_pic3:
                FragmentBottomSheet pick3 = new FragmentBottomSheet();
                pick3.setCommunicator(this);

                Bundle bundle3 = new Bundle();
                bundle3.putInt("imageType",  6);
                pick3.setArguments(bundle3);
                pick3.show(getActivity().getSupportFragmentManager(), "Dialog");
//                Intent galleryIntent3 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent3, RESULT_LOAD_IMG3);
                break;


            case R.id.picker_date_of_birth:
                deteOfBirth();
                break;
            case R.id.btn_upload:
                if(et_first_name.getText().toString().trim().length()==0){
                    et_first_name.requestFocus();
                    Toast.makeText(getActivity(),"Please enter the name",Toast.LENGTH_SHORT).show();
                }
                else if(picker_date_of_birth.getText().toString().trim().length()==0){
                    picker_date_of_birth.requestFocus();
                    Toast.makeText(getActivity(),"Please enter the date of birth",Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadEditInfo();
                    if(prefs.getBoolean(App.Key.IS_LOGGED)){
                        communicator.updateEditInfo(getUpdateProfileParimeter());
                    }else{
                        communicator.userRegister(getparameter(),getPhotoDetails());
                    }

                }

                break;
        }
    }

    private void deteOfBirth(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        picker_date_of_birth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        Calendar d = Calendar.getInstance();

        datePickerDialog.updateDate(d.get(Calendar.YEAR),d.get(Calendar.MONTH),d.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    //************************************* Volly implementation *****************************************
    /*private void uploadEditIfo(){
        loadingProgressbar("upload editInfo");
        String URl = "http://acrossapi.senzecit.in/guest/api/v1/user/register";

        HashMap<String,String> parameter = new HashMap<String, String>();

        parameter.put("name",et_first_name.getText().toString());
        parameter.put("dob",picker_date_of_birth.getText().toString());
        parameter.put("gender",spnr_gender.getSelectedItem().toString());
        parameter.put("device_token","56789");
        parameter.put("device_type","android");
        parameter.put("fab_res_bar",et_favourite_restaurants.getText().toString());
        parameter.put("fab_vac_spot",et_best_vacation_spot.getText().toString());
        parameter.put("i_did_love_my_date_to",et_i_love_my_date.getText().toString());
        parameter.put("media","instagram");
        parameter.put("most_rec_song_liked",et_most_recent_song.getText().toString());
        parameter.put("personality_followed",et_personality_i_follow.getText().toString());
        parameter.put("qbid","56789");
        parameter.put("username",prefs.getString("Instragram_TagId"));
        parameter.put("work",et_my_work.getText().toString());
        parameter.put("email","faiz@senzecit.com");

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
                        prefs.putString(App.Key.REGISTER_USER_TOKEN,userToken);

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

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }*/



    private RegisterUser getparameter(){
        RegisterUser registerUser=new RegisterUser();
        registerUser.device_token="12345";
        registerUser.device_type="ios";
        registerUser.dob=picker_date_of_birth.getText().toString();
        registerUser.fav_res_bar=et_favourite_restaurants.getText().toString();
        registerUser.fav_vac_spot=et_best_vacation_spot.getText().toString();
        registerUser.gender=spnr_gender.getSelectedItem().toString();
        registerUser.i_did_love_my_date_to=et_i_love_my_date.getText().toString();
        registerUser.media=prefs.getString(App.Key.LOGIN_TYPE_MEDIA);
        registerUser.most_rec_song_liked=et_most_recent_song.getText().toString();
        registerUser.name=et_first_name.getText().toString();
        registerUser.personality_followed=et_personality_i_follow.getText().toString();
        registerUser.qbid="4568976";
        registerUser.username=prefs.getString(App.Key.ID_LOGGED);
        registerUser.work=et_my_work.getText().toString();
        registerUser.email="ahsy@gmail.com";

        return registerUser;
    }




    private HashMap<String,String> getPhotoDetails(){
        HashMap<String,String> parameter = new HashMap<String, String>();
        parameter.put("photo_big",imageView_big.toString());
        parameter.put("photo_small1",imageView_small1.toString());
        parameter.put("photo_small2",imageView_small2.toString());
        parameter.put("img1_pic1",iv_pic1.toString());
        parameter.put("img_pic2",iv_pic2.toString());
        parameter.put("img_pic3",iv_pic3.toString());

        return parameter;
    }

    private UpdateProfileRequest getUpdateProfileParimeter(){
        UpdateProfileRequest updateProfileRequest=new UpdateProfileRequest();
        updateProfileRequest.dob=picker_date_of_birth.getText().toString();
        updateProfileRequest.fav_res_bar=et_favourite_restaurants.getText().toString();
        updateProfileRequest.fav_vac_spot=et_best_vacation_spot.getText().toString();
        updateProfileRequest.gender=spnr_gender.getSelectedItem().toString();
        updateProfileRequest.i_did_love_my_date_to=et_i_love_my_date.getText().toString();
        updateProfileRequest.media=prefs.getString(App.Key.LOGIN_TYPE_MEDIA);
        updateProfileRequest.most_rec_song_liked=et_most_recent_song.getText().toString();
        updateProfileRequest.name=et_first_name.getText().toString();
        updateProfileRequest.personality_followed=et_personality_i_follow.getText().toString();
        updateProfileRequest.qbid="1235";
        updateProfileRequest.username=prefs.getString(App.Key.ID_LOGGED);
        updateProfileRequest.work=et_my_work.getText().toString();

        UpdateProfileRequest.Photos photos=updateProfileRequest.new Photos();
        photos.data="";
        photos.type="";

        List<UpdateProfileRequest.Photos> photosList=new ArrayList<>();
        photosList.add(photos);

        updateProfileRequest.photos=photosList;
        return updateProfileRequest;
    }


    public interface EditInfoFragmentCommunicator{
        void userRegister(RegisterUser parameter,HashMap<String,String> photoParam);
        void updateEditInfo(UpdateProfileRequest parameter);
    }
}
