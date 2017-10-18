package com.example.senzec.datingapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.utils.CircleImageView;

import java.util.zip.Inflater;


/**
 * Created by ravi on 21/6/17.
 */

public class FragmentNotification extends Fragment {

    View view;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_notification);
        NotificationAdapter adapter = new NotificationAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        for(int i=0;i<10;i++){
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}

class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(position%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#f0f0f0"));
        }
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//    }

    @Override
    public int getItemCount() {
        return 11;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView notificationTV,timeTV;
        public MyViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.iv_profile_pic);
            notificationTV = (TextView) itemView.findViewById(R.id.tv_notification_massage);
            timeTV = (TextView) itemView.findViewById(R.id.tv_time_notification);

        }
    }
}