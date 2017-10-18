package com.across.senzec.datingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.preference.AppPrefs;

import com.across.senzec.datingapp.responsemodel.PostListResponse;
import com.across.senzec.datingapp.utils.CircleImageView;
import com.across.senzec.datingapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ravi on 21/9/17.
 */

public class PostFragment extends Fragment {

    View view;
    RecyclerView rv_listOfPost;
    PostFragmentCommunicator communicator;
    PostListResponse resource;
    EditText et_post_content;
    CircleImageView civ_loged_userImg;
    Button btn_post;
    AppPrefs prefs;

    public void setPostFragmentCommunicator(PostFragmentCommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = AppController.getInstance().getPrefs();
        resource = (PostListResponse) getArguments().getSerializable("FetchAllUserPostResponse");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_post,container,false);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_listOfPost.setLayoutManager(layoutManager);
        if(resource!=null) {
            PostListAdapter adapter = new PostListAdapter(getContext(),communicator,resource);
            rv_listOfPost.setAdapter(adapter);
        }
    }

    private void init(){
        rv_listOfPost = (RecyclerView) view.findViewById(R.id.rv_listOfPost);
        et_post_content = (EditText) view.findViewById(R.id.et_post_content);
        civ_loged_userImg = (CircleImageView) view.findViewById(R.id.civ_loged_userImg);
        btn_post = (Button) view.findViewById(R.id.btn_post);

        Picasso.with(getContext()).load(prefs.getString(Constants.TAG_PROFILE_PICTURE)).placeholder(R.drawable.profile).into(civ_loged_userImg);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = et_post_content.getText().toString();
                if(message.length()>0) {
                    communicator.sendPost(message);
                    et_post_content.setText("");
                }else{
                    Toast.makeText(getContext(),"Please write some post !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public interface PostFragmentCommunicator{
        void gotoPostCommentDetail(PostListResponse.Post post,PostListResponse.User user);
        void sendPost(String message);
    }
}

class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.MyViewHolder>{
    PostFragment.PostFragmentCommunicator communicator;
    PostListResponse resourse;
    Context context;
    public PostListAdapter(Context context,PostFragment.PostFragmentCommunicator communicator, PostListResponse resource){
        this.communicator = communicator;
        this.resourse = resource;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);
        return new MyViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        List<PostListResponse.Response> postListResponse = resourse.response;
        if(postListResponse.size()>0) {
            PostListResponse.Response response = postListResponse.get(position);

            String distance = response.distance;
            if(distance!=null) {
                if (distance.contains(".")) {
                    holder.user_distance_tv.setText(response.distance.substring(0, response.distance.indexOf('.')));
                } else {
                    holder.user_distance_tv.setText(response.distance);
                }
            }

            List<PostListResponse.Photos> userPhotos = response.photos;
            if (userPhotos.size() > 0) {
                PostListResponse.Photos photos = userPhotos.get(0);
                Picasso.with(context)
                        .load(photos.url)
                        .placeholder(R.drawable.profile)
                        .into(holder.civ_image);
            }

            final PostListResponse.User user = response.user;
            holder.display_name_tv.setText(user.name);
            holder.display_age_tv.setText(findAgeFromDateTime(user.dob));

            final PostListResponse.Post userPost = response.post;
            if(userPost.message.length()>70){
                holder.tv_message.setText(userPost.message.substring(0,70)+".....");
            }else {
                holder.tv_message.setText(userPost.message);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    communicator.gotoPostCommentDetail(userPost,user);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return resourse.response.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView civ_image;
        TextView display_name_tv,display_age_tv,user_distance_tv,tv_message;
        public MyViewHolder(View itemView) {
            super(itemView);
            civ_image = (CircleImageView) itemView.findViewById(R.id.civ_image);
            display_name_tv = (TextView) itemView.findViewById(R.id.display_name_tv);
            display_age_tv = (TextView) itemView.findViewById(R.id.display_age_tv);
            user_distance_tv = (TextView) itemView.findViewById(R.id.user_distance_tv);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        }
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
}