package com.example.senzec.datingapp.responsemodel;

import java.io.Serializable;
import java.util.List;


public class EditInfoResponse implements Serializable{

    public String status;
    public Response response;

    public class Response implements Serializable{
     public User user;
     public Setting setting;
     public List<Photos> photos;
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

        public Setting setting;
        public List<Photo> photo;
    }

    public class Setting implements Serializable{
        public String user_id;
        public String current_loc_name;
        public String current_loc_lat;
        public String current_loc_lon;
        public String current_loc_distance;
        public String future_loc_name;
        public String future_loc_lat;
        public String future_loc_lon;
        public String future_loc_start;
        public String future_loc_end;
        public String future_loc_distance;
        public String age_range_start;
        public String age_range_end;
        public String show_me;
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
