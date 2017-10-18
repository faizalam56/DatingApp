package com.example.senzec.datingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.activities.MainActivity;
import com.example.senzec.datingapp.adapters.ChatAdapter;

/**
 * Created by power hashing on 4/17/2017.
 */

public class FragmentChat extends Fragment {
    View view;
    RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("CHATS");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_chat);
        chatAdapter = new ChatAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        for (int i = 0; i <= 10; i++) {
            recyclerView.setAdapter(chatAdapter);
        }
        return view;
    }
}
