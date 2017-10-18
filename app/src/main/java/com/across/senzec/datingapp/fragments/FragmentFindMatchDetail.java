package com.across.senzec.datingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.models.User;
import com.squareup.picasso.Picasso;

/**
 * Created by ravi on 19/7/17.
 */

public class FragmentFindMatchDetail extends Fragment {
    TextView tv_profile_name,tv_profile_age,tv_user_distance;
    ImageView iv;
    View view;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable("user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_find_match_detail,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_profile_name = (TextView) view.findViewById(R.id.tv_profile_name);
        tv_profile_age = (TextView) view.findViewById(R.id.tv_profile_age);
        tv_user_distance = (TextView) view.findViewById(R.id.tv_user_distance);
        iv = (ImageView) view.findViewById(R.id.iv);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothicb.ttf","gothicb.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        setUpImage(iv, user);
        setUpDisplayName(tv_profile_name, user);
        setUpDisplayAge(tv_profile_age,user);
        setUpUserDistance(tv_user_distance, user);
    }

    private void setUpImage(ImageView iv, User user) {
        String avatarUrl = user.getAvatarUrl();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(iv.getContext())
                    .load(avatarUrl)
                    .into(iv);
        }
    }

    private void setUpDisplayName(TextView tv, User user) {
        String displayName = user.getDisplayName();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayAge(TextView tv, User user) {
        String displayAge = user.getDisplayAge();
        if (!TextUtils.isEmpty(displayAge)) {
            tv.setText(displayAge);
        }
    }

    private void setUpUserDistance(TextView tv, User user) {
        String userdistance = user.getUserDistance();
        if (!TextUtils.isEmpty(userdistance)) {
            tv.setText(userdistance);
        }
    }
}
