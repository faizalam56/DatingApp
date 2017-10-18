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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.responsemodel.AllCommentOnPostResponse;
import com.across.senzec.datingapp.responsemodel.PostListResponse;
import com.across.senzec.datingapp.utils.CircleImageView;
import com.across.senzec.datingapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ravi on 22/9/17.
 */



public class FragmentPostCommentDetail extends Fragment {
    View view;
    RecyclerView rv_comment_list;
    TextView tv_post,tv_comment_message;
    PostListResponse.Post post;
    ImageView iv_send_comment;
    CircleImageView civ_loged_userImg;
    FragmentPostCommentDetailCommunicator communicator;
    AllCommentOnPostResponse resource;
    AppPrefs prefs;

    public void setFragmentPostCommentDetailCommunicator(FragmentPostCommentDetailCommunicator communicator){
        this.communicator = communicator;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post = (PostListResponse.Post) getArguments().getSerializable("userpost");
        resource = (AllCommentOnPostResponse) getArguments().getSerializable("comments");
        prefs = AppController.getInstance().getPrefs();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_comment_details,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_post = (TextView) view.findViewById(R.id.tv_post);
        tv_post.setText(post.message);

        civ_loged_userImg = (CircleImageView) view.findViewById(R.id.civ_loged_userImg);
        Picasso.with(getContext()).load(prefs.getString(Constants.TAG_PROFILE_PICTURE)).placeholder(R.drawable.profile).into(civ_loged_userImg);

        tv_comment_message = (TextView) view.findViewById(R.id.tv_comment_message);
        iv_send_comment = (ImageView) view.findViewById(R.id.iv_send_comment);
        iv_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = tv_comment_message.getText().toString();
                if(message.length()>0) {
                    communicator.createCommentOnPost(message);
                    tv_comment_message.setText("");
                }else{
                    Toast.makeText(view.getContext(), "Please first write some comments!" + message, Toast.LENGTH_LONG).show();
                }
            }
        });

        rv_comment_list = (RecyclerView) view.findViewById(R.id.rv_comment_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_comment_list.setLayoutManager(layoutManager);

        CommentDetailAdapter adapter = new CommentDetailAdapter(getContext(),resource);
        rv_comment_list.setAdapter(adapter);
    }

    public interface FragmentPostCommentDetailCommunicator{
        void createCommentOnPost(String message);
    }

}

class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.MyViewHolder>{
    AllCommentOnPostResponse resource;
    Context context;
    public CommentDetailAdapter(Context context,AllCommentOnPostResponse resource){
        this.resource = resource;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_detail_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        List<AllCommentOnPostResponse.Comments> commentsList = resource.response.comments;
        if(commentsList.size()>0){
            AllCommentOnPostResponse.Comments comments=commentsList.get(position);

            AllCommentOnPostResponse.User  user=comments.user;
            holder.tv_name.setText(user.name);

            List<AllCommentOnPostResponse.Photos> userPhotos = comments.photos;
            if (userPhotos.size() > 0) {
                AllCommentOnPostResponse.Photos photos = userPhotos.get(0);
                Picasso.with(context)
                        .load(photos.url)
                        .placeholder(R.drawable.profile)
                        .into(holder.civ_image);
            }


            AllCommentOnPostResponse.Comment comment = comments.comment;
            holder.tv_comments.setText(comment.message);
        }
    }

    @Override
    public int getItemCount() {
        if(resource.response.comments.size()>0){
            return resource.response.comments.size();
        }else {
            return 0;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView civ_image;
        TextView tv_name,tv_comments;
        public MyViewHolder(View itemView) {
            super(itemView);
            civ_image = (CircleImageView) itemView.findViewById(R.id.civ_image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
        }
    }
}