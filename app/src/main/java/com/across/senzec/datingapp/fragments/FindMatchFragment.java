package com.across.senzec.datingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.adapters.FindMatchAdapter;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.models.User;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by power hashing on 4/15/2017.
 */

public class FindMatchFragment extends Fragment {

    //////////******** New Find Match According to new Changes**********/////////////////////

    View view;

    // region Constants
    private static  int STACK_SIZE = 2;
    // endregion

    // region Member Variables
    private String[] displayNames,displayAge, user_distance, avatarUrls;
    private int index = 0;
    FindMatchFragmentCommunicator communicator;
    FindMatchResponse resource;
    String[] userId,userName,userDistance,photoUrl;
    RecyclerView rvUserRecycleList;
    FrameLayout fl_find_match,fl_no_find_match;
    LinearLayout lodingIndicator;

    public void setFindMatchFragmentCommunicator(FindMatchFragmentCommunicator communicator){
        this.communicator=communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = (FindMatchResponse) getArguments().getSerializable("FindMatchResponse");
        if(resource!=null) {
            STACK_SIZE = resource.response.size();
            Log.d("Findmatch data:", resource.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_find_a_match,container,false);

        lodingIndicator = (LinearLayout) view.findViewById(R.id.lodingIndicator);
        rvUserRecycleList = (RecyclerView)view.findViewById(R.id.idUserRecycleList);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fl_find_match = (FrameLayout) view.findViewById(R.id.fl_find_match);
        fl_no_find_match = (FrameLayout) view.findViewById(R.id.fl_no_find_match);
        if(resource!=null){
            fl_find_match.setVisibility(View.VISIBLE);
            fl_no_find_match.setVisibility(View.GONE);
        }else {
            fl_find_match.setVisibility(View.GONE);
            fl_no_find_match.setVisibility(View.VISIBLE);
        }
        FindMatchAdapter findMatchAdapter = new FindMatchAdapter(communicator,view.getContext(), resource,lodingIndicator);
        rvUserRecycleList.setAdapter(findMatchAdapter);


    }

    String userNameId;
    private User getUser(int index){

        User user = new User();
        if(resource!=null) {
            List<FindMatchResponse.Response> findmatch_user_responce = resource.response;
            if (findmatch_user_responce.size() > 0) {
                FindMatchResponse.Response responce = findmatch_user_responce.get(index);
                FindMatchResponse.User user_detail = responce.user;
                List<FindMatchResponse.Photos> userPhoto = responce.photos;
                if (userPhoto.size() > 0) {
                    FindMatchResponse.Photos photos = userPhoto.get(0);
                    user.setAvatarUrl(photos.url);
                }

                if(user_detail.name.contains(" ")){
                    user.setDisplayName(user_detail.name.substring(0,user_detail.name.indexOf(" ")));
                }else{
                    user.setDisplayName(user_detail.name);
                }

                user.setDisplayAge(findAgeFromDateTime(user_detail.dob));

                if(responce.distance.contains(".")){
                    user.setUserDistance(responce.distance.substring(0, responce.distance.indexOf('.')));
                }else{
                    user.setUserDistance(responce.distance);
                }

                user.setUserId(user_detail.username);
                System.out.println("User data: " + user);

                userNameId = user_detail.username;
            }
        }
        return user;
    }

    private User getUser1(int index){
        User user = new User();
        user.setAvatarUrl(avatarUrls[index]);
        user.setDisplayName(displayNames[index]);
        user.setDisplayAge(displayAge[index]);
        user.setUserDistance(user_distance[index]);
        System.out.println("User data: "+user);
        return user;
    }

    private String findAgeFromDateTime(String dobInTime){
        String date = getDate(dobInTime,"dd/MM/yyyy");

        String[] dateParts = date.split("/");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        String age = getAge(year,month,day);
        return age;
    }
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
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

    public interface FindMatchFragmentCommunicator{
        void checkFndMatchDetail(User user);
        void goToChat();
    }
}
