package com.example.senzec.datingapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.adapters.CustomSwipeAdapter;

/**
 * Created by power hashing on 5/3/2017.
 */


public class FragmentImageSlider extends android.support.v4.app.Fragment {
    ViewPager viewPager;
    CustomSwipeAdapter adapter;
    View view;

    public FragmentImageSlider() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.image_slider, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        adapter = new CustomSwipeAdapter(this.getActivity());
        viewPager.setAdapter(adapter);
        return view;

    }



}