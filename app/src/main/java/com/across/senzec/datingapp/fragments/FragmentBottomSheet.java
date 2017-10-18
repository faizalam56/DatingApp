package com.across.senzec.datingapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;

import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.facebookalbum.FBImageListActivity;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.instagram.ApplicationData;
import com.across.senzec.datingapp.instagram.InstagramApp;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by power hashing on 4/22/2017.
 */

public class FragmentBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    View view;
    TextView tv_cancel,tv_facebook_picture,tv_instagram_picture,tv_gallery_picture;
    Uri selectedImage;
    Bitmap bitmap;
    InstagramApp mApp;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private HashMap<String, String> userInfo;
    AppPrefs prefs;
    public EditInfoFragment mFragment;


    public void setCommunicator(EditInfoFragment editInfoFragment) {

        mFragment = editInfoFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bottom_sheet_buttons, container, false);
        init();
        userInfo = (HashMap<String, String>) getActivity().getIntent().getSerializableExtra("userInfo");

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = AppController.getInstance().getPrefs();
        mApp = new InstagramApp(getActivity(), ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

         final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == InstagramApp.WHAT_FINALIZE) {

                    int imageType2 = getArguments().getInt("imageType", 404);
                    Intent intent2 = new Intent(getActivity(), FBImageListActivity.class);
                    intent2.putExtra("getImageFrom", "instagram");
                    intent2.putExtra("loginType","temp");
                    startActivityForResult(intent2, imageType2);
                    mApp.resetAccessToken();

                } else if (msg.what == InstagramApp.WHAT_ERROR) {
                    Toast.makeText(getActivity(), "Check your network.",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                mApp.getTempInstaUserId(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        initFacebookLogin();
    }

    public void init() {
        tv_facebook_picture = (TextView) view.findViewById(R.id.tv_facebook_picture);
        tv_instagram_picture = (TextView) view.findViewById(R.id.tv_instagram_picture);
        tv_gallery_picture = (TextView) view.findViewById(R.id.tv_gallery_picture);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_facebook_picture.setOnClickListener(this);
        tv_instagram_picture.setOnClickListener(this);
        tv_gallery_picture.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }
    private void getPosts(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/posts", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.e(TAG,response.toString());
                    }
                }
        ).executeAsync();
    }

    public void openFolder(){
        Intent galleryIntent4 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/FB/");
        galleryIntent4.setDataAndType(uri, "*/*");

        int imageType = getArguments().getInt("imageType", 404);

        switch (imageType){
            case 1:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_BIG);
                break;
            case 2:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_SMALL_1);
                break;
            case 3:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_SMALL_2);
                break;
            case 4:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_1);
                break;
            case 5:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_2);
                break;
            case 6:
                startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_3);
                break;

            default:
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_facebook_picture:

                if(AccessToken.getCurrentAccessToken()!=null) {
                    int imageType1 = getArguments().getInt("imageType", 404);

                    Intent intent = new Intent(getActivity(), FBImageListActivity.class);
                    intent.putExtra("getImageFrom", "facebook");
                    startActivityForResult(intent, imageType1);
                }else{
                    loginButton.performClick();
                }
//                openFolder();
                break;
            case R.id.tv_instagram_picture:

                if(mApp.hasAccessToken()) {
                    int imageType2 = getArguments().getInt("imageType", 404);

                    Intent intent2 = new Intent(getActivity(), FBImageListActivity.class);
                    intent2.putExtra("getImageFrom", "instagram");
                    startActivityForResult(intent2, imageType2);
                }else{
                    mApp.authorize();
                }
                break;
            case R.id.tv_gallery_picture:
//                Intent galleryIntent4 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent4, RESULT_LOAD_IMG_BIG);
//                getDialog().dismiss();

                int imageType = getArguments().getInt("imageType", 404);
                Intent galleryIntent4 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                switch (imageType){
                    case 1:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_BIG);
                        break;
                    case 2:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_SMALL_1);
                        break;
                    case 3:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_SMALL_2);
                        break;
                    case 4:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_1);
                        break;
                    case 5:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_2);
                        break;
                    case 6:
                        startActivityForResult(galleryIntent4, Constants.RESULT_LOAD_IMG_PIC_3);
                        break;

                    default:
                        break;
                }
                break;
            case R.id.tv_cancel:
                getDialog().dismiss();
                break;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult "+requestCode +" "+resultCode);
        if (resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            if (requestCode == 1) {
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,1);

            }else if(requestCode == 2){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,2);

            }else if(requestCode == 3){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,3);

            }else if(requestCode == 4){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,4);

            }else if(requestCode == 5){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,5);

            }else if(requestCode == 6){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,6);

            }
            else if(requestCode == 10){
                getDialog().dismiss();
                mFragment.getImageUri(selectedImage,6);

            }

        }
        else {
//            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private LoginButton loginButton;
    private String txtId,firstName,lastName,email,birthday,gender,txtPhoto;;
    private CallbackManager callbackManager;
    private void initFacebookLogin() {
        loginButton = (LoginButton)view.findViewById(R.id.fb_login_button);
//        loginButton.setLoginBehavior(LoginBehavior.WEB_ONLY);
        // initialize facebook sdk
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile email,user_photos");

        /*loginButton.setReadPermissions(Arrays.asList("public_profile", "user_about_me", "user_location",
                "email", "user_birthday"));*/
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                    int imageType1 = getArguments().getInt("imageType", 404);
                    Intent intent = new Intent(getActivity(), FBImageListActivity.class);
                    intent.putExtra("getImageFrom", "facebook");
                    startActivityForResult(intent, imageType1);


            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "CANCEL", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getContext(), "Error" + e, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: ", e);
            }
        });
    }



    public interface ImageChoosedListener{
        void getImageUri(Uri imageUri, int imageType);
    }
}
