package com.example.senzec.datingapp.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.activities.MainActivity;
import com.example.senzec.datingapp.models.User;
import com.example.senzec.datingapp.responsemodel.FindMatchResponse;
import com.example.senzec.datingapp.ui.TinderCardView;
import com.example.senzec.datingapp.ui.TinderStackLayout;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by power hashing on 4/15/2017.
 */

public class FindMatchFragment extends Fragment implements View.OnClickListener {
    View view;

    // region Constants
    private static final int STACK_SIZE = 4;
    // endregion

    // region Views
    private TinderStackLayout tinderStackLayout;
    // endregion

    // region Member Variables
    private String[] displayNames,displayAge, user_distance, avatarUrls;
    private int index = 0;
    ScrollView scroll;
    Button btn_like,btn_dislike,btn_damn;
    TextView findmatch_username;
    FindMatchFragmentCommunicator communicator;
    FindMatchResponse resource;
    String[] userId,userName,userDistance,photoUrl;

    public void setFindMatchFragmentCommunicator(FindMatchFragmentCommunicator communicator){
        this.communicator=communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        resource = (FindMatchResponse) getArguments().getSerializable("FindMatchResponse");
//        Log.d("Findmatch data:",resource.toString());
//        getFindmatchUserResponce();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_find_a_match,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        displayNames = getResources().getStringArray(R.array.display_names);
        displayAge = getResources().getStringArray(R.array.display_age);
        user_distance = getResources().getStringArray(R.array.user_distance);
        avatarUrls = getResources().getStringArray(R.array.avatar_urls);
//        btn_like_dislike=(Button)view.findViewById(R.id.btn_like_dislike);

        tinderStackLayout = (TinderStackLayout) view.findViewById(R.id.tsl);
        tinderStackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scroll.setVisibility(View.VISIBLE);
//                btn_like_dislike.setVisibility(View.GONE);

            }
        });
        scroll=(ScrollView)view.findViewById(R.id.scroll);

        TinderCardView tc;
        for(int i=index; index<i+STACK_SIZE; index++){
            tc = new TinderCardView(getActivity());
            tc.bind(getUser(index));
//            tc.bind(getUser1(index));
            tinderStackLayout.addCard(tc);
        }

        tinderStackLayout.getPublishSubject()
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if(integer == 1){
                            TinderCardView tc;
                            for(int i=index; index<i+(STACK_SIZE-1); index++){
                                tc = new TinderCardView(getActivity());
                                tc.bind(getUser(index));
//                                tc.bind(getUser1(index));
                                tinderStackLayout.addCard(tc);
                            }
                        }
                    }
                });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_damn = (Button) view.findViewById(R.id.btn_damn);
        btn_like = (Button) view.findViewById(R.id.btn_like);
        btn_dislike = (Button) view.findViewById(R.id.btn_dislike);
//        findmatch_username = (TextView) view.findViewById(R.id.findmatch_username);

        btn_damn.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_dislike.setOnClickListener(this);
    }

    // region Helper Methods
    private User getUser(int index){
        User user = new User();
        user.setAvatarUrl(avatarUrls[index]);
        user.setDisplayName(displayNames[index]);
        user.setDisplayAge(displayAge[index]);
        user.setUserDistance(user_distance[index]);
        return user;
    }

    private User getUser1(int index){
        User user = new User();
        user.setAvatarUrl(photoUrl[index]);
        user.setDisplayName(userName[index]);
        user.setDisplayAge(displayAge[index]);
        user.setUserDistance(user_distance[index]);
        return user;
    }

    private void getFindmatchUserResponce(){
        List<FindMatchResponse.Response> findmatch_user_responce = resource.response;
         userId = new String[findmatch_user_responce.size()];
         userName = new String[findmatch_user_responce.size()];
         userDistance = new String[findmatch_user_responce.size()];

        for(int i=0;i<findmatch_user_responce.size();i++){
            FindMatchResponse.Response responce = findmatch_user_responce.get(i);

            FindMatchResponse.User user_detail = responce.user;
            userId[i]=user_detail.id;
            userName[i]=user_detail.name;
            userDistance[i]=user_detail.distance;

            List<FindMatchResponse.Photos> userPhoto = responce.photos;
             photoUrl = new String[userPhoto.size()];
            for(int j =0;j<userPhoto.size();j++){
                FindMatchResponse.Photos photos=userPhoto.get(i);
                photoUrl[j]=photos.url;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_damn:
                break;
            case R.id.btn_like:
                break;
            case R.id.btn_dislike:
                break;
        }
    }

    public interface FindMatchFragmentCommunicator{
        void damnUser();
        void likeUser();
        void dislikeUser();
    }
}
