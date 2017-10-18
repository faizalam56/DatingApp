package com.example.senzec.datingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.fragments.FragmentChatDetails;

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

    public ChatAdapter(Context context) {

        this.context = context;

    }


    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_rank, tv_coin_name, tv_price, tv_symbol;


        public MyViewHolder(View view) {
            super(view);


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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentChatDetails fragmentChatDetails=new FragmentChatDetails();
                setFragment(fragmentChatDetails);
            }
        });

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
        return 11;
    }
}