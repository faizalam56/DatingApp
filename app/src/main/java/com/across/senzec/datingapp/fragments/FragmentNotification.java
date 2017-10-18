package com.across.senzec.datingapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.responsemodel.NotificationResponse;
import com.across.senzec.datingapp.responsemodel.UserResponse;
import com.across.senzec.datingapp.utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by ravi on 21/6/17.
 */

public class FragmentNotification extends Fragment {

    View view;
    RecyclerView recyclerView;
    NotificationResponse resource;
    private LayoutInflater layoutInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resource = (NotificationResponse) getArguments().getSerializable("NotificationResponse");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification,container,false);
        layoutInflater=inflater;

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(resource.response!=null){
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_notification);
            NotificationAdapter adapter = new NotificationAdapter(getContext(),resource);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
//            for(int i=0;i<10;i++){
//                recyclerView.setAdapter(adapter);
//            }
        }
    }
}

class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    Context context;
    NotificationResponse resource;
    AppPrefs prefs;
    APIInterface apiInterface;
    public NotificationAdapter(Context context,NotificationResponse resource){
        this.context = context;
        this.resource = resource;
        prefs = AppPrefs.getInstance(context);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if(position%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#f0f0f0"));
        }

        List<NotificationResponse.Response> notificationResponce = resource.response;
        if(notificationResponce.size()>0){
            NotificationResponse.Response responce = notificationResponce.get(position);

            holder.timeTV.setText(getDateTime(responce.time,"dd/MM/yyy hh:mm a"));

            final NotificationResponse.User user_details = responce.user;

            String notification_detail = user_details.name+" "+responce.type+" you.";
            holder.tv_notification_detail.setText(notification_detail);

            List<NotificationResponse.Photos> userPhoto = responce.photos;
            if (userPhoto.size() > 0) {
                NotificationResponse.Photos photos = userPhoto.get(0);
                Picasso.with(context)
                        .load(photos.url)
                        .placeholder(R.drawable.profile)
                        .into(holder.iv_profile_pic);
            }

            /// for dialog popup //////////////////////
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    FragmentNotificationPopupDialog fragment = new FragmentNotificationPopupDialog();
                      alertDialog(holder.iv_profile_pic.getDrawable(),user_details);
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        if(resource.response.size()==0){
            return 0;
        }
        return resource.response.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView iv_profile_pic;
        TextView notificationTV,timeTV,tv_notification_detail;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_profile_pic = (CircleImageView) itemView.findViewById(R.id.iv_profile_pic);
//            notificationTV = (TextView) itemView.findViewById(R.id.tv_notification_massage);
            timeTV = (TextView) itemView.findViewById(R.id.tv_time_notification);
            tv_notification_detail = (TextView) itemView.findViewById(R.id.tv_notification_detail);


        }
    }


    private String getDateTime(String dobInTime, String dateFormat)
    {

        Long milliSeconds = Long.parseLong(dobInTime)*1000;
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void setFragment(Fragment fragment) {
        mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment).commit();
    }

    private void alertDialog(Drawable drawable, final NotificationResponse.User user_details){

        Button btn_damn,btn_like,btn_dislike;
        TextView displayNameTextView,displayAgeTextView,userDistanceTextView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        final AlertDialog dialog = dialogBuilder.create();
        View dialogView = inflater.inflate(R.layout.fragment_notification_popup_dialog, null);

        ImageView iv= (ImageView) dialogView.findViewById(R.id.iv);
        displayNameTextView = (TextView)dialogView. findViewById(R.id.display_name_tv);
        displayAgeTextView = (TextView)dialogView. findViewById(R.id.display_age_tv);
        userDistanceTextView = (TextView)dialogView. findViewById(R.id.user_distance_tv);
        btn_damn = (Button) dialogView.findViewById(R.id.btn_damn);
        btn_like = (Button) dialogView.findViewById(R.id.btn_like);
        btn_dislike = (Button) dialogView.findViewById(R.id.btn_dislike);

        iv.setImageDrawable(drawable);
        if(user_details.name.contains(" ")){
            displayNameTextView.setText(user_details.name.substring(0,user_details.name.indexOf(" ")));
        }else{
            displayNameTextView.setText(user_details.name);
        }

        displayAgeTextView.setText(findAgeFromDateTime(user_details.dob));
        btn_damn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_damn_api(user_details);
                dialog.dismiss();
            }
        });
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_like_api(user_details);
                dialog.dismiss();
            }
        });
        btn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_dislike_api(user_details);
                dialog.dismiss();
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.show();
        dialog.setView(dialogView);
        dialog.show();
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

    private void call_like_api(NotificationResponse.User user_details){
        String userNameId = user_details.username;
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        //Call<UserResponse> call14 = apiInterface.likeRequest(id,userNameId,userToken);
        Call<UserResponse> call14 = apiInterface.likeRequestPut(id,userNameId,userToken);

        call14.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){

                }else{

                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void call_dislike_api(NotificationResponse.User user_details){
        String userNameId = user_details.username;
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        //Call<UserResponse> call15 = apiInterface.dislikeRequest(id,userNameId,userToken);
        Call<UserResponse> call15 = apiInterface.dislikeRequestPut(id,userNameId,userToken);

        call15.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){

                }else{

                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void call_damn_api(NotificationResponse.User user_details){
        String userNameId = user_details.username;
        String id = prefs.getString(App.Key.ID_LOGGED);
        String userToken = prefs.getString(App.Key.REGISTER_USER_TOKEN);

        //Call<UserResponse> call13 = apiInterface.damnRequest(id,userNameId,userToken);
        Call<UserResponse> call13 = apiInterface.damnRequestPut(id,userNameId,userToken);
        call13.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                UserResponse resource = response.body();
                if(resource!=null){

                }

            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                call.cancel();
                t.printStackTrace();
            }
        });
    }
}