package com.across.senzec.datingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.activities.MainActivity;
import com.across.senzec.datingapp.adapters.ChatAdapter;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.responsemodel.ChatUserResponse;

/**
 * Created by power hashing on 4/17/2017.
 */

public class FragmentChat extends Fragment {
    View view;
    RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    ChatUserResponse resource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = (ChatUserResponse) getArguments().getSerializable("ChatUserResponse");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("CHATS");

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*for (int i = 0; i <= 10; i++) {
            recyclerView.setAdapter(chatAdapter);
        }*/
        if(resource.response==null){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_chat);
            chatAdapter = new ChatAdapter(getActivity(),resource);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(chatAdapter);
        }
    }
}
