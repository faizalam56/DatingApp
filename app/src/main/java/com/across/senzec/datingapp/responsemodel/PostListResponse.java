package com.across.senzec.datingapp.responsemodel;



import java.io.Serializable;
import java.util.List;





public class PostListResponse implements Serializable{

    public String status;
    public List<Response> response;

    public class Response implements Serializable {
        public User user;
        public Post post;
        public List<Photos> photos;
        public String distance;
    }

    public class User implements Serializable{
        public String id;
        public String username;
        public String qbid;
        public String name;
        public String email;
        public String mobile;
        public String dob;
        public String gender;
        public String media;
        public String work;
        public String fav_res_bar;
        public String fav_vac_spot;
        public String most_rec_song_liked;
        public String personality_followed;
        public String i_did_love_my_date_to;
        public String code;
        public String status;
        public String created_at;
        public String updated_at;
        public String topic;


        public List<Photo> photo;
    }

    public class Post implements Serializable{
        public String id;
        public String message;
        public String privacy;
        public String status;
        public String user_id;

        public String created_at;
        public String updated_at;
    }

    public class Photo implements Serializable{
        public String id;
        public String created_at;
        public String name;
        public String updated_at;
        public String url;
        public Pivot pivot;
    }

    public class Photos implements Serializable{
        public String id;
        public String created_at;
        public String name;
        public String updated_at;
        public String url;
        public Pivot pivot;
    }

    public class Pivot implements Serializable{
        public String photo_id;
        public String user_id;
    }


}
