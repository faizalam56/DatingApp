package com.example.senzec.datingapp.requestmodel;

import java.util.List;


public class UpdateProfileRequest {
    public String dob;
    public String fav_res_bar;
    public String fav_vac_spot;
    public String gender;
    public String i_did_love_my_date_to;
    public String media;
    public String most_rec_song_liked;
    public String name;
    public String personality_followed;
    public String qbid;
    public String username;
    public String work;

    public List<Photos> photos;

    public class Photos{
        public String data;
        public String type;
    }
}
