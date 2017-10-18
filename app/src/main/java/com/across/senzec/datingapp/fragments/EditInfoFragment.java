package com.across.senzec.datingapp.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.test.mock.MockPackageManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.activities.MainActivity;
import com.across.senzec.datingapp.activities.UserRegisterActivity;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.instagram.InstagramSession;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.multiselectspinner.KeyPairBoolData;
import com.across.senzec.datingapp.multiselectspinner.MultiSpinnerSearch;
import com.across.senzec.datingapp.multiselectspinner.SpinnerListener;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.ImageUploadRequest;
import com.across.senzec.datingapp.requestmodel.RegisterUser;
import com.across.senzec.datingapp.requestmodel.SetTopicRequest;
import com.across.senzec.datingapp.requestmodel.UpdateProfileRequest;
import com.across.senzec.datingapp.responsemodel.EditInfoResponse;
import com.across.senzec.datingapp.services.GPSTracker;
import com.across.senzec.datingapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by power hashing on 4/14/2017.
 */

public class EditInfoFragment extends Fragment implements View.OnClickListener ,FragmentBottomSheet.ImageChoosedListener{

    private static final String TAG = EditInfoFragment.class.getSimpleName();
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
    EditText et_first_name,et_my_work, et_favourite_restaurants, et_best_vacation_spot, et_most_recent_song, et_personality_i_follow, et_i_love_my_date,picker_date_of_birth;
    Spinner spnr_gender,spnr_topics;
    MultiSpinnerSearch multiSpinnerSearch;
//    TextView picker_date_of_birth;
    private int mYear, mMonth, mDay, mHour, mMinute;
    AppPrefs prefs;
    EditInfoFragmentCommunicator communicator;
    InstagramSession instagramSession;
    private InstagramApp mApp;
    EditInfoResponse resource;
    LinearLayout loadingIndicator;
    private String[] topics_name;
    List<KeyPairBoolData> dataArrayList;

    public void setEditInfoFragmentCommunicator(EditInfoFragmentCommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = AppController.getInstance().getPrefs();

        if(prefs.getBoolean(App.Key.IS_LOGGED)){
            resource = (EditInfoResponse) getArguments().getSerializable("EditInfoResponse");
            crop=1;
        }else{

        }
//        resource = (EditInfoResponse) getArguments().getSerializable("EditInfoResponse");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_info, container, false);

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


        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothicb.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));


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
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("outputX", width);
            CropIntent.putExtra("outputY", height);

            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);


            startActivityForResult(CropIntent, imgeRequestCode);

        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Error : "+e, e);
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
        picker_date_of_birth = (EditText) view.findViewById(R.id.picker_date_of_birth);
        spnr_gender = (Spinner) view.findViewById(R.id.spnr_gender);

        topics_name = getResources().getStringArray(R.array.topics_name);
        /*spnr_topics = (Spinner) view.findViewById(R.id.spnr_topics);
        topics_name = getResources().getStringArray(R.array.topics_name);
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,topics_name);
        spnr_topics.setAdapter(topicAdapter);*/

        multiSpinnerSearch = (MultiSpinnerSearch) view.findViewById(R.id.spnr_topics);
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.topics_name));
        dataArrayList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            dataArrayList.add(h);
        }
        System.out.println("Selected..."+dataArrayList.toString());
        multiSpinnerSearch.setItems(dataArrayList, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });
        multiSpinnerSearch.setLimit(2, new MultiSpinnerSearch.LimitExceedListener() {
            @Override
            public void onLimitListener(KeyPairBoolData data) {
                Toast.makeText(getContext(),
                        "Topic Selection Limit exceed ", Toast.LENGTH_LONG).show();
            }
        });


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

        loadingIndicator = (LinearLayout) view.findViewById(R.id.lodingIndicator);

        //****Image load by using lazyload.
//        new ImageLoader(getActivity()).DisplayImage(prefs.getString(Constants.TAG_PROFILE_PICTURE),imageView_big);
        //****Image load by using Picasso.
        Picasso.with(getContext()).load(prefs.getString(Constants.TAG_PROFILE_PICTURE)).placeholder(R.drawable.profile).into(imageView_big);

        String name = prefs.getString(Constants.TAG_FULL_NAME);
        if(name.contains(" ")){
            et_first_name.setText(name.substring(0,name.indexOf(" ")));
        }else{
            et_first_name.setText(name);
        }
//        et_first_name.setText(prefs.getString(Constants.TAG_FULL_NAME));
        et_first_name.setOnClickListener(this);
        imageView_big.setOnClickListener(this);

        imageView_small1.setOnClickListener(this);
        imageView_small2.setOnClickListener(this);
        iv_pic1.setOnClickListener(this);
        iv_pic2.setOnClickListener(this);
        iv_pic3.setOnClickListener(this);

        btn_upload.setOnClickListener(this);
        picker_date_of_birth.setOnClickListener(this);


        if(resource!=null){
            setEditInfoDetail();
        }
    }

    private void setEditInfoDetail(){
        EditInfoResponse.Response response = resource.response;
        EditInfoResponse.User user=response.user;
        List<EditInfoResponse.Photos>photos=response.photos;
        List<EditInfoResponse.Topics> topics = response.topics;
        int index=0;
        if(user.gender.equalsIgnoreCase("male")){
             index = 0;
        }else if(user.gender.equalsIgnoreCase("female")){
             index = 1;
        }

        if(user.name.contains(" ")){
            et_first_name.setText(user.name.substring(0,user.name.indexOf(" ")));
        }else{
            et_first_name.setText(user.name);
        }

        picker_date_of_birth.setText(getDate(user.dob,"dd/MM/yyyy"));
        spnr_gender.setSelection(index);
        et_my_work.setText(user.work);
        et_favourite_restaurants.setText(user.fav_res_bar);
        et_best_vacation_spot.setText(user.fav_vac_spot);
        et_most_recent_song.setText(user.most_rec_song_liked);
        et_personality_i_follow.setText(user.personality_followed);
        et_i_love_my_date.setText(user.i_did_love_my_date_to);

        if(topics!=null) {
            List<KeyPairBoolData> topicList = new ArrayList<>();
            for (int i = 0; i < topics.size(); i++) {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(i + 1);
                h.setName(topics.get(i).topic);
                h.setSelected(true);
                topicList.add(h);
            }
            multiSpinnerSearch.setItems(dataArrayList, -1, new SpinnerListener() {
                @Override
                public void onItemsSelected(List<KeyPairBoolData> items) {

                }
            });
        }

        ImageView[] imageView ={imageView_big,imageView_small1,imageView_small2,iv_pic1,iv_pic2,iv_pic3};
        if(photos!=null) {
            for (int i = 0; i < photos.size(); i++) {
                Picasso.with(getContext())
                        .load(photos.get(i).url)
                        .placeholder(R.drawable.add_img)
                        .into(imageView[i]);
            }
        }
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
//                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private String imageToBase64(Bitmap bitmap){
        //encode image to base64 string
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String s= Base64.encodeToString(b, Base64.DEFAULT);
        System.out.println("Base64 " + s);
        return s;
    }

    int crop=0;
    private void showCropImage(ImageView imageView, Intent data) {

        Bundle bundle = data.getExtras();
        Bitmap bitmap =  bundle.getParcelable("data");
        imageView.setImageBitmap(bitmap);

        if(crop==0) {
            ImageUploadRequest.Data data1 = imageUploadRequest.new Data();
            data1.data = imageToBase64(bitmap);
            data1.type = "base64";
            if (!dataList.contains(data1)) {
                dataList.add(data1);
            }
            imageUploadRequest.photos = dataList;
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        if(crop==1) {
            UpdateProfileRequest.Photos photos = updateProfileRequest.new Photos();
            photos.data = imageToBase64(bitmap);
            photos.type = "base64";
            if (!photosList.contains(photos)) {
                photosList.add(photos);
            }
            updateProfileRequest.photos = photosList;
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_first_name:
                et_first_name.setCursorVisible(true);
                break;
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
                else if(et_my_work.getText().toString().trim().length()==0){
                    picker_date_of_birth.requestFocus();
                    Toast.makeText(getActivity(),"Please enter the work",Toast.LENGTH_SHORT).show();
                }
                else{

                    if(communicator instanceof UserRegisterActivity){
                        communicator.userRegister(getparameter(),getPhotoDetails(),setTopicByUser());
                    }
                    else if(communicator instanceof MainActivity){
                        communicator.updateEditInfo(getUpdateProfileParimeter(),setTopicByUser());
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

                        picker_date_of_birth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

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
        registerUser.device_token=Constants.DEVICE_ID;
        registerUser.device_type=Constants.DEVICE_TYPE;
        registerUser.dob= String.valueOf(getDatetime(picker_date_of_birth.getText().toString()));//picker_date_of_birth.getText().toString();
        registerUser.fav_res_bar=et_favourite_restaurants.getText().toString();
        registerUser.fav_vac_spot=et_best_vacation_spot.getText().toString();
        registerUser.gender=getGender();
        registerUser.i_did_love_my_date_to=et_i_love_my_date.getText().toString();
        registerUser.media=prefs.getString(App.Key.LOGIN_TYPE_MEDIA);
        registerUser.most_rec_song_liked=et_most_recent_song.getText().toString();
        registerUser.name=et_first_name.getText().toString();
        registerUser.personality_followed=et_personality_i_follow.getText().toString();
//        registerUser.qbid=prefs.getString(App.Key.QB_ID);
        registerUser.username=prefs.getString(App.Key.ID_LOGGED);
        registerUser.work=et_my_work.getText().toString();
        registerUser.email="faiz@senzecit.com";
//        registerUser.topic = spnr_topics.getSelectedItem().toString() ;
//        registerUser.topic = CurrentSetTopic() ;

        return registerUser;
    }

    private String getGender(){
        String gender;
        if(spnr_gender.getSelectedItem().toString().equalsIgnoreCase("Male")){
           return gender = "male";
        }else if(spnr_gender.getSelectedItem().toString().equalsIgnoreCase("Female")){
           return gender = "female";
        }
        return null;
    }

    UpdateProfileRequest updateProfileRequest=new UpdateProfileRequest();
    List<UpdateProfileRequest.Photos> photosList=new ArrayList<>();
    private UpdateProfileRequest getUpdateProfileParimeter(){
        updateProfileRequest.dob= String.valueOf(getDatetime(picker_date_of_birth.getText().toString()));
        updateProfileRequest.fav_res_bar=et_favourite_restaurants.getText().toString();
        updateProfileRequest.fav_vac_spot=et_best_vacation_spot.getText().toString();
        updateProfileRequest.gender=getGender();
        updateProfileRequest.i_did_love_my_date_to=et_i_love_my_date.getText().toString();
        updateProfileRequest.media=prefs.getString(App.Key.LOGIN_TYPE_MEDIA);
        updateProfileRequest.most_rec_song_liked=et_most_recent_song.getText().toString();
        updateProfileRequest.name=et_first_name.getText().toString();
        updateProfileRequest.personality_followed=et_personality_i_follow.getText().toString();
//        updateProfileRequest.qbid=prefs.getString(App.Key.QB_ID);
        updateProfileRequest.username=prefs.getString(App.Key.ID_LOGGED);
        updateProfileRequest.work=et_my_work.getText().toString();
//        updateProfileRequest.topic = spnr_topics.getSelectedItem().toString();
//        updateProfileRequest.topic = CurrentSetTopic();

        UpdateProfileRequest.Photos photos=updateProfileRequest.new Photos();
        photos.data=prefs.getString(Constants.TAG_PROFILE_PICTURE);
        photos.type="url";

        photosList.add(photos);
        updateProfileRequest.photos=photosList;

        crop=1;
        return updateProfileRequest;
    }


    List<ImageUploadRequest.Data>dataList=new ArrayList<>();
    ImageUploadRequest imageUploadRequest = new ImageUploadRequest();
    private ImageUploadRequest getPhotoDetails(){

        ImageUploadRequest.Data data=imageUploadRequest.new Data();
        data.data=prefs.getString(Constants.TAG_PROFILE_PICTURE);
        data.type="url";

        dataList.add(data);
        imageUploadRequest.photos=dataList;

        crop=0;
        return imageUploadRequest;
    }

    SetTopicRequest setTopicRequest = new SetTopicRequest();
    private SetTopicRequest setTopicByUser(){
        setTopicRequest.topics = getSelectedTopic() ;
        return setTopicRequest;
    }

    private int getDatetime(String selectDate){
        String str_date=selectDate;
        SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " +date.getTime());
        return (int) (date.getTime()/1000);
    }

    private String getDate(String dobInTime, String dateFormat)
    {
        Long milliSeconds = Long.parseLong(dobInTime)*1000;
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private List<String> getSelectedTopic(){
        List<KeyPairBoolData> topicList = multiSpinnerSearch.getSelectedItems();
        String[] topicName = new String[topicList.size()];
        List<String> topic = new ArrayList<>();
        for(int i=0;i<topicList.size();i++){
            KeyPairBoolData data = topicList.get(i);
            topic.add(data.getName());

        }
        System.out.println("Top..."+topic);
        return topic;
    }
   /* private String CurrentSetTopic(){
        String name = selectedTopic();
        if(name.contains(",")){
            name = name.substring(0,name.indexOf(","));
            return name;
        }
        return name;
    }*/

    public interface EditInfoFragmentCommunicator{
        void userRegister(RegisterUser parameter, ImageUploadRequest imageUploadRequest, SetTopicRequest setTopicRequest);
        void updateEditInfo(UpdateProfileRequest parameter,SetTopicRequest setTopicRequest);
    }
}
