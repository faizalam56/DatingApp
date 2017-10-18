package com.across.senzec.datingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.responsemodel.ChatUserResponse;
import com.across.senzec.datingapp.utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by power hashing on 4/17/2017.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    ChatUserResponse resource;
    private Context context;

    public ChatAdapter(Context context, ChatUserResponse resource) {

        this.context = context;
        this.resource = resource;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_last_chatline;
        CircleImageView iv_profile_pic;
        ImageView iv_no_of_unreadChat;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            iv_profile_pic = (CircleImageView) view.findViewById(R.id.iv_profile_pic);
            tv_last_chatline = (TextView) view.findViewById(R.id.tv_last_chatline);
            iv_no_of_unreadChat = (ImageView) view.findViewById(R.id.iv_no_of_unreadChat);

        }
    }


//    public CoinListAdapter(List<CoinDetailsModel> coinList) {
//        this.coinList = coinList;
//    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_row, parent, false);


        return new MyViewHolder(itemView);
    }
    private void setFragment(Fragment fragment) {
        mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment).commit();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#f0f0f0"));
        }
        /*List<ChatUserResponse.Response> findmatch_user_responce = resource.response;
        if(findmatch_user_responce.size()>0) {
            ChatUserResponse.Response responce = findmatch_user_responce.get(position);
            ChatUserResponse.User user_detail = responce.user;
            holder.tv_name.setText(user_detail.name);

            List<ChatUserResponse.Photos> userPhoto = responce.photos;
            if (userPhoto.size() > 0) {
                ChatUserResponse.Photos photos = userPhoto.get(0);
                Picasso.with(context)
                        .load(photos.url)
                        .placeholder(R.drawable.profile)
                        .into(holder.iv_profile_pic);
            }
        }*/

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentChatDetails fragmentChatDetails=new FragmentChatDetails();
                setFragment(fragmentChatDetails);
            }
        });*/

    }

    private String getFormatedAmount(double amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private String getDate(long timeStamp) {
        DateFormat objFormatter = new SimpleDateFormat("EEE, dd-MMM-yyyy hh:mm a");
        objFormatter.setTimeZone(TimeZone.getTimeZone("IST"));

        Calendar objCalendar =
                Calendar.getInstance(TimeZone.getTimeZone("IST"));
        objCalendar.setTimeInMillis(timeStamp * 1000);//edit
        String result = objFormatter.format(objCalendar.getTime());
        objCalendar.clear();
        return result;
    }


    @Override
    public int getItemCount() {
        /*if(resource.response.size()==0){
            return 0;
        }
        return resource.response.size();*/
        return 10;
    }
}