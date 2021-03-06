package com.across.senzec.datingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.font.FontChangeCrawler;

/**
 * Created by power hashing on 4/18/2017.
 */

public class FragmentChatDetails extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_chat_details,container,false);
//        ((MainActivity) getActivity()).getSupportActionBar().setIcon(R.mipmap.msg_icon_black);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }
}
