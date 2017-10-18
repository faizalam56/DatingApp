package com.example.senzec.datingapp.responsemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ravi on 7/7/17.
 */

public class UpdateProfileResponse implements Serializable{
    public String status;
    public Response response;

    public class Response implements Serializable{
        public String id;
        public String username;
        public String qbid;
        public String name;
        public String email;
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
        List<Photos> photo;
    }

    public class Photos implements Serializable{
        public String id;
        public String name;
        public String url;
        public String created_at;
        public Pivot pivot;
    }

    public class Pivot implements Serializable{
        public String user_id;
        public String user_photo;
    }


}
