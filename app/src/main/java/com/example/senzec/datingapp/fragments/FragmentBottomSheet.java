package com.example.senzec.datingapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.senzec.datingapp.R;

import com.example.senzec.datingapp.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import java.io.ByteArrayOutputStream;
import java.net.URI;
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

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private HashMap<String, String> userInfo;

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
        return view;
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_facebook_picture:
                getPosts();
                getDialog().dismiss();
                break;
            case R.id.tv_instagram_picture:

                getDialog().dismiss();
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

        }
        else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public interface ImageChoosedListener{
        void getImageUri(Uri imageUri, int imageType);
    }
}
